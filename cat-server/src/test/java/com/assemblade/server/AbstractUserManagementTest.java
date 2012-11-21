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
package com.assemblade.server;

import com.assemblade.opendj.AbstractDirectoryServiceTest;
import com.assemblade.opendj.StorageException;
import com.assemblade.server.configuration.ConfigurationManager;
import com.assemblade.server.model.Group;
import com.assemblade.server.model.User;
import com.assemblade.server.security.AuthenticatedUserHolder;
import com.assemblade.server.users.GroupManager;
import com.assemblade.server.users.UserManager;
import org.junit.Before;

public abstract class AbstractUserManagementTest extends AbstractDirectoryServiceTest {
	protected UserManager userManager;
	protected GroupManager groupManager;
    protected ConfigurationManager configurationManager;

	@Before
	public void initialize_user_management() throws Exception {
		userManager = new UserManager(directoryService);
		groupManager = new GroupManager(userManager);
        configurationManager = new ConfigurationManager(userManager);
	}

    @SuppressWarnings("serial")
	protected void userLogin(String userName) throws Exception {
        User user = directoryService.getAdminSession().get(new User(userName, null, null, null));
        AuthenticatedUserHolder.setUser(user);
    }
    
    protected User addLocalUser(String userName, String fullName, String emailAddress, String password) throws Exception {
		User user = new User(userName, fullName, emailAddress, password);
		userManager.addUser(user);
		return user;
    }
    
    protected Group addGroup(String groupName, String description) throws StorageException {
    	Group group = new Group(groupName, description);
    	userManager.getUserSession().add(group);
    	return group;
    }
    
    protected void addUserToGroup(Group group, User user) throws StorageException {
    	group.addMember(user);
    	userManager.getUserSession().update(group);
    }
    
    protected void removeUserFromGroup(Group group, User user) throws StorageException {
    	group.deleteMember(user);
    	userManager.getUserSession().update(group);
    }
    
    protected void addGroupToGroup(Group parent, Group child) throws StorageException {
    	parent.addMember(child);
    	userManager.getUserSession().update(parent);
    }
    
    protected void removeGroupFromGroup(Group parent, Group child) throws StorageException {
    	parent.deleteMember(child);
    	userManager.getUserSession().update(parent);
    }
}
