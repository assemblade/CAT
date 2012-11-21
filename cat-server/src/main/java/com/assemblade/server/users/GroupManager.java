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

import com.assemblade.opendj.StorageException;
import com.assemblade.server.model.Group;
import com.assemblade.server.model.GroupUser;
import com.assemblade.server.model.User;
import com.assemblade.server.model.UserNotInGroup;

import java.text.MessageFormat;
import java.util.List;

public class GroupManager {
    private static final MessageFormat usersInGroupFilterFormat = new MessageFormat("(&(objectClass=inetOrgPerson)(isMemberOf={0}))");

    private final UserManager userManager;
	
	public GroupManager(UserManager userManager) {
		this.userManager = userManager;
	}

    public Group addGroup(String groupName, String description) throws StorageException {
    	Group group = new Group(groupName, description);
    	userManager.getUserSession().add(group);
    	
    	Group adminGroup = new Group(group.getAdminGroupDn());
    	userManager.getUserSession().add(adminGroup);

		Group groupAdminGroup = userManager.getUserSession().get(new Group(Group.GROUP_ADMIN_DN));
    	groupAdminGroup.addMember(adminGroup);
    	userManager.getUserSession().update(groupAdminGroup);

    	return userManager.getUserSession().get(group);
    }

    public Group updateGroup(Group group) throws StorageException {
        userManager.getUserSession().update(group);
        return userManager.getUserSession().get(group);
    }

    public void deleteGroup(String groupId) throws StorageException {
		Group group = userManager.getUserSession().getByEntryId(new Group(), groupId);
		
		Group adminGroup = userManager.getUserSession().get(new Group(group.getAdminGroupDn()));
		if (adminGroup != null) {
			Group groupAdminGroup = userManager.getUserSession().get(new Group(Group.GROUP_ADMIN_DN));
			groupAdminGroup.deleteMember(adminGroup);
	    	userManager.getUserSession().update(groupAdminGroup);
		}
		
		userManager.getUserSession().delete(group, true);
    }
    
    public User addUserToGroup(String groupDn, String userDn) throws StorageException {
		Group group = userManager.getUserSession().get(new Group(groupDn));
		if (group != null) {
			User user = userManager.getUserSession().get(new User(userDn));
			if (user != null) {
		    	group.addMember(user);
		    	userManager.getUserSession().update(group);
		    	return user;
			}
		}
		return null;
    }

    public User setUserAdministrativeRights(String groupDn, String userDn, boolean administrator) throws StorageException {
		Group group = userManager.getUserSession().get(new Group(groupDn));
		User user = userManager.getUserSession().get(new User(userDn));

        Group adminGroup = userManager.getUserSession().get(new Group(group.getAdminGroupDn()));
		if (administrator) {
			adminGroup.addMember(user);
		} else {
			adminGroup.deleteMember(user);
		}
    	userManager.getUserSession().update(adminGroup);
    	
    	return user;
    }

    public void removeGroupAdministrativeRightsFromUser(String groupDn, String userDn) throws StorageException {
		Group group = userManager.getUserSession().get(new Group(groupDn));
		User user = userManager.getUserSession().get(new User(userDn));
        Group adminGroup = userManager.getUserSession().get(new Group(group.getAdminGroupDn()));
        adminGroup.deleteMember(user);
    	userManager.getUserSession().update(adminGroup);
    }

    public void removeUserFromGroup(String groupDn, String userDn) throws StorageException {
		Group group = userManager.getUserSession().get(new Group(groupDn));
		User user = userManager.getUserSession().get(new User(userDn));

		group.deleteMember(user);
    	userManager.getUserSession().update(group);
    }

    public List<Group> getGroups() throws StorageException {
		return userManager.getUserSession().search(new Group(), Group.ROOT, false).getEntries();
    }
    
    public List<GroupUser> getListOfUsersInGroup(String groupId) throws StorageException {
        Group group = userManager.getUserSession().getByEntryId(new Group(), groupId);
		return userManager.getUserSession().search(new GroupUser(group.getDn()), User.ROOT, false).getEntries();
    }
    
    public List<UserNotInGroup> getListOfUsersNotInGroup(String groupId) throws StorageException {
        Group group = userManager.getUserSession().getByEntryId(new Group(), groupId);
        return userManager.getUserSession().search(new UserNotInGroup(group.getDn()), User.ROOT, false).getEntries();
    }
}
