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
package com.assemblade.server.model;

import com.assemblade.opendj.AssembladeErrorCode;
import com.assemblade.opendj.LdapUtils;
import com.assemblade.opendj.Session;
import com.assemblade.opendj.StorageException;
import com.assemblade.opendj.model.AbstractStorable;
import com.assemblade.opendj.permissions.EntryPermissions;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opends.server.core.DirectoryServer;
import org.opends.server.types.Attribute;
import org.opends.server.types.AttributeType;
import org.opends.server.types.AttributeValue;
import org.opends.server.types.DN;
import org.opends.server.types.DirectoryException;
import org.opends.server.types.Entry;
import org.opends.server.types.Modification;
import org.opends.server.types.ObjectClass;
import org.opends.server.types.RDN;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public abstract class AbstractUser extends AbstractStorable {
    private static final long serialVersionUID = 1L;
    private Log log = LogFactory.getLog(AbstractUser.class);

    public static final String ROOT = "ou=users,dc=assemblade,dc=com";

    private static final String DEFAULT_SEARCH_STRING = "(objectClass=inetOrgPerson)";

    protected String userId;
    protected String fullName;
    protected String password;
    protected String emailAddress;
    protected String searchFilter = DEFAULT_SEARCH_STRING;
    protected boolean remoteUser;
    protected String remoteDn;
    protected boolean globalAdministrator;
    protected boolean groupAdministrator;

    @Override
    public String getRDN() {
        return "uid=" + userId;
    }

    @Override
    public String getParentDn() {
        return User.ROOT;
    }

    @Override
    public String getSearchFilter() {
        return searchFilter;
    }

    @Override
    public Collection<String> getAttributeNames() {
        Collection<String> attributeNames = super.getAttributeNames();
        attributeNames.addAll(Arrays.asList("uid", "cn", "userPassword", "mail", "isMemberOf", "aclRights", "pwdPolicySubEntry"));
        return attributeNames;
    }

    @Override
    public Map<ObjectClass, String> getObjectClasses() {
        Map<ObjectClass, String> objectClasses = super.getObjectClasses();
        objectClasses.put(DirectoryServer.getObjectClass("inetorgperson"), "inetOrgPerson");
        return objectClasses;
    }

    @Override
    public Map<AttributeType, List<Attribute>> getUserAttributes() {
        Map<AttributeType, List<Attribute>> attributeMap = super.getUserAttributes();

        LdapUtils.addSingleValueAttributeToMap(attributeMap, "uid", userId);
        LdapUtils.addSingleValueAttributeToMap(attributeMap, "cn", fullName);
        LdapUtils.addSingleValueAttributeToMap(attributeMap, "sn", userId);
        LdapUtils.addSingleValueAttributeToMap(attributeMap, "mail", emailAddress);
        if (StringUtils.isNotEmpty(password)) {
            LdapUtils.addSingleValueAttributeToMap(attributeMap, "userPassword", password);
        }
        if (remoteUser && StringUtils.isNotEmpty(remoteDn)) {
            LdapUtils.addSingleValueAttributeToMap(attributeMap, "description", remoteDn);
        }
        return attributeMap;
    }

    @Override
    public Map<AttributeType, List<Attribute>> getOperationalAttributes() {
        Map<AttributeType, List<Attribute>> attributeMap = super.getOperationalAttributes();

        if (remoteUser) {
            LdapUtils.addSingleValueAttributeToMap(attributeMap, "ds-pwp-password-policy-dn", "cn=Remote User Authentication Policy,cn=Password Policies,cn=config");
        }

        return attributeMap;
    }

    @Override
    public boolean requiresRename(Session session, Entry currentEntry) throws StorageException {
        return !StringUtils.equals(userId, LdapUtils.getSingleAttributeStringValue(currentEntry.getAttribute("uid")));
    }

    @Override
    public boolean requiresUpdate(Session session, Entry currentEntry) throws StorageException {
        User user = new User().getDecorator().decorate(session, currentEntry);
        return !StringUtils.equals(fullName, user.fullName) || !StringUtils.equals(emailAddress, user.emailAddress) || (remoteUser != user.remoteUser);
    }

    @Override
    public List<Modification> getModifications(Session session, Entry currentEntry) throws StorageException {
        List<Modification> modifications = super.getModifications(session, currentEntry);

        User currentUser = (User)getDecorator().decorate(session, currentEntry);

        if (!StringUtils.equals(currentUser.getFullName(), fullName)) {
            LdapUtils.createSingleEntryModification(modifications, currentEntry, "cn", fullName);
        }

        if (!StringUtils.equals(currentUser.getEmailAddress(), emailAddress)) {
            LdapUtils.createSingleEntryModification(modifications,currentEntry, "mail", emailAddress);
        }
        return modifications;
    }

    public String getPassword() {
        return password;
    }

    public String getFullName() {
        return fullName;
    }

    public String getUserId() {
        return userId;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    @Override
    public String toString() {
        return "User [userId=" + userId + ", fullName=" + fullName + ", emailAddress=" + emailAddress + "]";
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public boolean isGlobalAdministrator() {
        return globalAdministrator;
    }

    public void setGlobalAdministrator(boolean globalAdministrator) {
        this.globalAdministrator = globalAdministrator;
    }

    public boolean isGroupAdministrator() {
        return groupAdministrator;
    }

    public void setGroupAdministrator(boolean groupAdministrator) {
        this.groupAdministrator = groupAdministrator;
    }

    public boolean isRemoteUser() {
        return remoteUser;
    }

    public void setRemoteUser(boolean remoteUser) {
        this.remoteUser = remoteUser;
    }

    public String getRemoteDn() {
        return remoteDn;
    }

    public void setRemoteDn(String remoteDn) {
        this.remoteDn = remoteDn;
    }

    protected abstract class Decorator<T extends AbstractUser> extends AbstractStorable.Decorator<T> {
        @Override
        public T decorate(Session session, Entry entry) throws StorageException {
            T user = super.decorate(session, entry);
            user.userId = LdapUtils.getSingleAttributeStringValue(entry.getAttribute("uid"));
            user.fullName = LdapUtils.getSingleAttributeStringValue(entry.getAttribute("cn"));
            user.password = LdapUtils.getSingleAttributeStringValue(entry.getAttribute("userPassword"));
            user.emailAddress = LdapUtils.getSingleAttributeStringValue(entry.getAttribute("mail"));
            List<org.opends.server.types.Attribute> attributes = entry.getOperationalAttribute(DirectoryServer.getAttributeType("ismemberof"));
            if (attributes != null) {
                for (Attribute attribute : attributes) {
                    for (AttributeValue value : attribute) {
                        try {
                            String groupDn = DN.decode(value.getValue()).toString();
                            if (groupDn.toString().equals(Group.GLOBAL_ADMIN_DN)) {
                                user.globalAdministrator = true;
                            } else if (groupDn.equals(Group.GROUP_ADMIN_DN)) {
                                user.groupAdministrator = true;
                            }
                        } catch (DirectoryException e) {
                            log.error("Failed to set administrator flags on user", e);
                            throw new StorageException(AssembladeErrorCode.ASB_9999);
                        }
                    }
                }
            }
            String authenticationPolicy = LdapUtils.getSingleAttributeStringValue(entry.getAttribute("pwdpolicysubentry"));
            user.remoteUser = StringUtils.isNotEmpty(authenticationPolicy) && authenticationPolicy.equals("cn=Remote User Authentication Policy,cn=Password Policies,cn=config");
            user.remoteDn = LdapUtils.getSingleAttributeStringValue(entry.getAttribute("description"));
            EntryPermissions permissions = LdapUtils.getEntryPermissions(entry.getAttributes());
            user.writable = permissions.canWrite();
            user.deletable = permissions.canDelete();
            return user;
        }
    }
}
