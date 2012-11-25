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
    private final UserManager userManager;

	public GroupManager(UserManager userManager) {
		this.userManager = userManager;
	}

    public Group getAdministratorGroup() throws StorageException {
        return userManager.getUserSession().getByEntryDn(new Group(), Group.GLOBAL_ADMIN_DN);
    }

    public Group getGroupAdministratorGroup() throws StorageException {
        return userManager.getUserSession().getByEntryDn(new Group(), Group.GROUP_ADMIN_DN);
    }

    public Group addGroup(Group group) throws StorageException {
    	userManager.getUserSession().add(group);
    	
    	Group adminGroup = createAdminGroup(group);
    	userManager.getUserSession().add(adminGroup);

		Group groupAdminGroup = getGroupAdministratorGroup();
    	groupAdminGroup.addMember(adminGroup);
    	userManager.getUserSession().update(groupAdminGroup);

    	return userManager.getUserSession().get(group);
    }

    public Group getGroup(Group group) throws StorageException {
        return userManager.getUserSession().get(group);
    }

    public Group updateGroup(Group group) throws StorageException {
        userManager.getUserSession().update(group);
        return userManager.getUserSession().get(group);
    }

    public void deleteGroup(String groupId) throws StorageException {
		Group group = userManager.getUserSession().getByEntryId(new Group(), groupId);
		
		Group adminGroup = userManager.getUserSession().get(createAdminGroup(group));
		if (adminGroup != null) {
			Group groupAdminGroup = getGroupAdministratorGroup();
			groupAdminGroup.deleteMember(adminGroup);
	    	userManager.getUserSession().update(groupAdminGroup);
		}
		
		userManager.getUserSession().delete(group, true);
    }
    
    public Group addUserToGroup(Group group, User user) throws StorageException {
    	group.addMember(user);
    	userManager.getUserSession().update(group);
        return userManager.getUserSession().get(group);
    }

    public void setUserAdministrativeRights(Group group, User user, boolean administrator) throws StorageException {
        Group adminGroup = userManager.getUserSession().get(createAdminGroup(group));
		if (administrator) {
			adminGroup.addMember(user);
		} else {
			adminGroup.deleteMember(user);
		}
    	userManager.getUserSession().update(adminGroup);
    }

    public Group removeUserFromGroup(Group group, User user) throws StorageException {
		group.deleteMember(user);
    	userManager.getUserSession().update(group);
        return userManager.getUserSession().get(group);
    }

    public List<Group> getGroups() throws StorageException {
		return userManager.getUserSession().search(new Group(), Group.ROOT, false).getEntries();
    }
    
    public List<GroupUser> getListOfUsersInGroup(String groupId) throws StorageException {
        Group group = userManager.getUserSession().getByEntryId(new Group(), groupId);
        GroupUser groupUser = new GroupUser();
        groupUser.setGroupDn(group.getDn());
		return userManager.getUserSession().search(groupUser, User.ROOT, false).getEntries();
    }
    
    public List<UserNotInGroup> getListOfUsersNotInGroup(String groupId) throws StorageException {
        Group group = userManager.getUserSession().getByEntryId(new Group(), groupId);
        UserNotInGroup userNotInGroup = new UserNotInGroup();
        userNotInGroup.setGroupDn(group.getDn());
        return userManager.getUserSession().search(userNotInGroup, User.ROOT, false).getEntries();
    }

    private Group createAdminGroup(Group group) {
        Group adminGroup = new Group();
        adminGroup.setParentDn(group.getDn());
        adminGroup.setName("admins");

        return adminGroup;
    }
}
