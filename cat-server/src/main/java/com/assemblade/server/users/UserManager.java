/*
 * Copyright 2012 Mike Adamson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.assemblade.server.users;

import com.assemblade.opendj.AssembladeErrorCode;
import com.assemblade.opendj.DirectoryService;
import com.assemblade.opendj.Session;
import com.assemblade.opendj.StorageException;
import com.assemblade.opendj.model.authentication.policy.AuthenticationPolicy;
import com.assemblade.opendj.model.authentication.policy.LdapPassthroughAuthenticationPolicy;
import com.assemblade.server.configuration.ConfigurationManager;
import com.assemblade.server.model.Group;
import com.assemblade.server.model.User;
import com.assemblade.server.model.Views;
import com.assemblade.server.security.AuthenticationHolder;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.forgerock.opendj.ldap.Attribute;
import org.forgerock.opendj.ldap.Connection;
import org.forgerock.opendj.ldap.DN;
import org.forgerock.opendj.ldap.ErrorResultException;
import org.forgerock.opendj.ldap.ErrorResultIOException;
import org.forgerock.opendj.ldap.LDAPConnectionFactory;
import org.forgerock.opendj.ldap.SearchResultReferenceIOException;
import org.forgerock.opendj.ldap.SearchScope;
import org.forgerock.opendj.ldap.responses.SearchResultEntry;
import org.forgerock.opendj.ldif.ConnectionEntryReader;

import java.util.List;

public class UserManager {
	private Log log = LogFactory.getLog(UserManager.class);
	
	private final DirectoryService directoryService;

	public UserManager(DirectoryService directoryService) {
		this.directoryService = directoryService;
	}
	
	public User addUser(final User user) throws StorageException {
        Session session = getUserSession();

        if (user.isRemoteUser()) {
            LdapPassthroughAuthenticationPolicy policy = (LdapPassthroughAuthenticationPolicy)session.getConfigurationByDn("cn=Remote User Authentication Policy," + AuthenticationPolicy.ROOT);

            String hostPort = policy.getPrimaryRemoteServer();
            int colonIndex = hostPort.lastIndexOf(":");
            String hostname = hostPort.substring(0, colonIndex);
            int port = Integer.parseInt(hostPort.substring(colonIndex + 1));

            LDAPConnectionFactory factory = new LDAPConnectionFactory(hostname, port);
            Connection connection = null;
            try {
                connection = factory.getConnection();
                connection.bind(policy.getBindDn(), policy.getBindPassword().toCharArray());
                String filter = "(" + policy.getSearchAttribute() + "=" + user.getUserId() + ")";
                ConnectionEntryReader reader = connection.search(policy.getSearchBase(), SearchScope.WHOLE_SUBTREE, filter, "+", "*");
                while (reader.hasNext()) {
                    if (!reader.isReference()) {
                        SearchResultEntry entry = reader.readEntry();
                        user.setRemoteDn(entry.getName().toString());
                        if (StringUtils.isNotEmpty(policy.getNameAttribute())) {
                            Attribute attribute = entry.getAttribute(policy.getNameAttribute());
                            if (attribute != null) {
                                user.setFullName(attribute.firstValueAsString());
                            }
                        }
                        if (StringUtils.isNotEmpty(policy.getMailAttribute())) {
                            Attribute attribute = entry.getAttribute(policy.getMailAttribute());
                            if (attribute != null) {
                                user.setEmailAddress(attribute.firstValueAsString());
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.error("Failed to get remote user information", e);
                throw new StorageException(AssembladeErrorCode.ASB_0014);
            } finally {
                if (connection != null) {
                    connection.close();
                }
            }
        }

		session.add(user);

        User addedUser = session.get(user);

        Group userGroup = session.getByEntryDn(new Group(), Group.USER_DN);

        userGroup.addMember(addedUser);

        session.update(userGroup);

        Views views = new Views();
        views.setParentDn(addedUser.getDn());

        session.add(views);

		return addedUser;
	}

    public User getUser(String userId) throws StorageException {
        return getUserSession().getByEntryDn(new User(), getUserSession().dnFromId(userId));
    }

	public User updateUser(User user) throws StorageException {
		getUserSession().update(user);
		return getUserSession().get(user);
	}
	
	public void deleteUser(String userId) throws StorageException {
        User user = getUserSession().getByEntryId(new User(), userId);
        getUserSession().delete(user, true);
	}
	
	public List<User> getUsers() throws StorageException {
		return getUserSession().search(new User(), User.ROOT, false);
	}

    public User getAuthenticatedUser() {
        return AuthenticationHolder.getAuthentication().getUser();
    }

    public Session getAdminSession() {
        return directoryService.getAdminSession();
    }

	public Session getUserSession() {
		Session session = null;
		try {
			session = directoryService.getSession(AuthenticationHolder.getAuthentication().getUser().getDn());
		} catch (Exception e) {
			log.error("Failed to get a user session", e);
		}
		return session;
	}

    public String getAuthenticatedUserDn() {
        return AuthenticationHolder.getAuthentication().getUser().getDn();
    }

    public String getViewsDn() {
        return "cn=views," + getAuthenticatedUserDn();
    }
}
