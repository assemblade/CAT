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
import com.assemblade.server.model.GroupMember;
import com.assemblade.server.model.User;
import com.assemblade.server.model.UserNotInGroup;

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

    public Group getGroup(String groupId) throws StorageException {
        return userManager.getUserSession().getByEntryId(new Group(), groupId);
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
        Group adminGroup = null;
        try {
            adminGroup = userManager.getUserSession().get(createAdminGroup(group));
        } catch (StorageException e) {
        }
        if (adminGroup != null) {
            Group groupAdminGroup = getGroupAdministratorGroup();
            groupAdminGroup.deleteMember(adminGroup);
            userManager.getUserSession().update(groupAdminGroup);
        }

        userManager.getUserSession().delete(group, true);
    }

    public GroupMember addMemberToGroup(GroupMember groupMember) throws StorageException {
        groupMember.getGroup().addMember(groupMember);
        userManager.getUserSession().update(groupMember.getGroup());
        if (groupMember.isAdministrator()) {
            return setGroupMemberAdministrativeRights(groupMember);
        } else {
            return userManager.getUserSession().get(groupMember);
        }
    }

    public GroupMember setGroupMemberAdministrativeRights(GroupMember groupMember) throws StorageException {
        Group adminGroup = userManager.getUserSession().get(createAdminGroup(groupMember.getGroup()));
		if (groupMember.isAdministrator()) {
			adminGroup.addMember(groupMember);
		} else {
			adminGroup.deleteMember(groupMember);
		}
    	userManager.getUserSession().update(adminGroup);
        return userManager.getUserSession().get(groupMember);
    }

    public void removeMemberFromGroup(String groupId, String memberId) throws StorageException {
        Group group = userManager.getUserSession().getByEntryId(new Group(), groupId);
        GroupMember member = new GroupMember();
        member.setGroup(group);
        member = userManager.getUserSession().getByEntryId(member, memberId);
        group.deleteMember(member);
    	userManager.getUserSession().update(group);
    }

    public List<Group> getGroups() throws StorageException {
		return userManager.getUserSession().search(new Group(), Group.ROOT, false).getEntries();
    }
    
    public List<GroupMember> getListOfUsersInGroup(String groupId) throws StorageException {
        Group group = userManager.getUserSession().getByEntryId(new Group(), groupId);
        GroupMember groupMember = new GroupMember();
        groupMember.setGroup(group);
		return userManager.getUserSession().search(groupMember, User.ROOT, false).getEntries();
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
