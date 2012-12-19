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

import com.assemblade.opendj.SequenceNumberGenerator;
import com.assemblade.opendj.StorageException;
import com.assemblade.server.model.Group;
import com.assemblade.server.model.GroupMember;
import com.assemblade.server.model.User;

import java.text.MessageFormat;
import java.util.List;

public class GroupManager {
    private static final MessageFormat searchFilterFormat = new MessageFormat("(&(objectClass=inetOrgPerson)(!(isMemberOf={0})))");

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
        group.setGroupId(SequenceNumberGenerator.getNextSequenceNumber());
    	userManager.getUserSession().add(group);
    	return userManager.getUserSession().get(group);
    }

    public Group getGroup(String groupId) throws StorageException {
        return userManager.getUserSession().getByEntryDn(new Group(), userManager.getUserSession().dnFromId(groupId));
    }

    public Group getGroup(Group group) throws StorageException {
        return userManager.getUserSession().get(group);
    }

    public Group updateGroup(Group group) throws StorageException {
        group.setGroupId(userManager.getUserSession().getByEntryId(group, group.getId()).getGroupId());
        userManager.getUserSession().update(group);
        return userManager.getUserSession().get(group);
    }

    public void deleteGroup(String groupId) throws StorageException {
		Group group = userManager.getUserSession().getByEntryId(new Group(), groupId);
        userManager.getUserSession().delete(group);
    }

    public GroupMember addMemberToGroup(GroupMember groupMember) throws StorageException {
        groupMember.getGroup().setGroupId(userManager.getUserSession().getByEntryId(groupMember.getGroup(), groupMember.getGroup().getId()).getGroupId());
        groupMember.getGroup().addMember(groupMember);
        userManager.getUserSession().update(groupMember.getGroup());
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
		return userManager.getUserSession().search(new Group(), Group.ROOT, false);
    }
    
    public List<GroupMember> getListOfUsersInGroup(String groupId) throws StorageException {
        Group group = userManager.getUserSession().getByEntryId(new Group(), groupId);
        GroupMember groupMember = new GroupMember();
        groupMember.setGroup(group);
		return userManager.getUserSession().search(groupMember, User.ROOT, false);
    }
    
    public List<User> getListOfUsersNotInGroup(String groupId) throws StorageException {
        String groupDn = userManager.getUserSession().dnFromId(groupId);
        String searchFilter = searchFilterFormat.format(new Object[] {groupDn});
        return userManager.getUserSession().search(new User(), User.ROOT, searchFilter);
    }
}
