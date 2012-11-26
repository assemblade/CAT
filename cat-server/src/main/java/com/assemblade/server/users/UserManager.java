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

import com.assemblade.opendj.DirectoryService;
import com.assemblade.opendj.Session;
import com.assemblade.opendj.StorageException;
import com.assemblade.server.model.Group;
import com.assemblade.server.model.User;
import com.assemblade.server.security.AuthenticationHolder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

public class UserManager {
	private Log log = LogFactory.getLog(UserManager.class);
	
	private final DirectoryService directoryService;

	public UserManager(DirectoryService directoryService) {
		this.directoryService = directoryService;
	}
	
	public User addUser(User user) throws StorageException {
        Session session = getUserSession();

		session.add(user);

        user = session.get(user);

        Group userGroup = session.getByEntryDn(new Group(), Group.USER_DN);

        userGroup.addMember(user);

        session.update(userGroup);

		return user;
	}
	
	public User updateUser(User user) throws StorageException {
		getUserSession().update(user);
		return getUserSession().get(user);
	}
	
	public void deleteUser(String userId) throws StorageException {
        User user = getUserSession().getByEntryId(new User(), userId);
        getUserSession().delete(user);
	}
	
	public List<User> getUsers() throws StorageException {
		return getUserSession().search(new User(), User.ROOT, false).getEntries();
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
}
