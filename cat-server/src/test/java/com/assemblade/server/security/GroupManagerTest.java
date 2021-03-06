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
package com.assemblade.server.security;

import com.assemblade.opendj.OpenDJTestRunner;
import com.assemblade.opendj.StorageException;
import com.assemblade.server.AbstractUserManagementTest;
import com.assemblade.server.model.Group;
import com.assemblade.server.model.GroupMember;
import com.assemblade.server.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(OpenDJTestRunner.class)
public class GroupManagerTest extends AbstractUserManagementTest {

	@Test
	public void adminCanSeeUsers() throws Exception {
		userLogin("admin");
		addUser("test1", "Test1", "test1@example.com", "password");

		List<User> users = userManager.getUsers();
		
		assertEquals(2, users.size());
	}
	
	@Test
	public void adminHasAccessToAllGroups() throws Exception {
		userLogin("admin");

		List<Group> groups = groupManager.getGroups();
		
		assertEquals(3, groups.size());
	}
	
	@Test
	public void adminCanAddGroup() throws Exception {
		userLogin("admin");

		Group expected = addGroup("Test1", "Test1 Description");

		assertEquals(4, groupManager.getGroups().size());

		Group actual = userManager.getUserSession().get(expected);
		
		assertEquals("Test1", actual.getName());
		assertEquals("Test1 Description", actual.getDescription());
	}
	
	@Test
	public void adminCanDeleteGroup() throws Exception {
		userLogin("admin");

		Group expected = addGroup("Test1", "Test1 Description");

		groupManager.deleteGroup(expected.getId());
		
		assertEquals(3, groupManager.getGroups().size());
	}
	
	@Test(expected=StorageException.class)
	public void adminCannotDeleteAdminGroup() throws Exception {
		userLogin("admin");

        Group adminGroup = groupManager.getAdministratorGroup();

		groupManager.deleteGroup(adminGroup.getId());
	}
	
	@Test
	public void adminCanGiveAdminRightsToUser() throws Exception {
		userLogin("admin");

		User user = addUser("test1", "Test1", "test1@example.com", "password");

		groupManager.addMemberToGroup(createGroupMember(groupManager.getAdministratorGroup(), user));
		
		userLogin("test1");
		
		assertEquals(3, groupManager.getGroups().size());
	}
	
	@Test
	public void adminCanAddUserToGroup() throws Exception {
		userLogin("admin");

		Group group = addGroup("Group1", "Group1 Description");
		User user = addUser("test1", "Test1", "test1@example.com", "password");
		
		groupManager.addMemberToGroup(createGroupMember(group, user));

		List<GroupMember> groupMembers = groupManager.getListOfUsersInGroup(group.getId());
		
		assertEquals(1, groupMembers.size());
		assertEquals("test1", groupMembers.get(0).getUserId());
		assertFalse(groupMembers.get(0).isAdministrator());
	}

	@Test
	public void adminCanRemoveUserFromGroup() throws Exception {
		userLogin("admin");

		Group group = addGroup("Group1", "Group1 Description");
		User user = addUser("test1", "Test1", "test1@example.com", "password");
		
		groupManager.addMemberToGroup(createGroupMember(group, user));

		List<GroupMember> groupMembers = groupManager.getListOfUsersInGroup(group.getId());
		
		assertEquals(1, groupMembers.size());
		
		groupManager.removeMemberFromGroup(group.getId(), user.getId());

		groupMembers = groupManager.getListOfUsersInGroup(group.getId());
		
		assertEquals(0, groupMembers.size());
	}
	
	@Test
	public void adminCanGiveGroupAdminRightsToUser() throws Exception {
		userLogin("admin");

		Group group = addGroup("Group1", "Group1 Description");
		
		User user = addUser("test1", "Test1", "test1@example.com", "password");

        groupManager.addMemberToGroup(createGroupMember(group, user));
        groupManager.addMemberToGroup(createGroupMember(groupManager.getGroupAdministratorGroup(), user));

		List<GroupMember> groupMembers = groupManager.getListOfUsersInGroup(group.getId());
		
		assertEquals(1, groupMembers.size());
		assertEquals("test1", groupMembers.get(0).getUserId());
		assertTrue(groupMembers.get(0).isAdministrator());
	}

	@Test
	public void adminCanRemoveGroupAdminRightsFromUser() throws Exception {
		userLogin("admin");

		Group group = addGroup("Group1", "Group1 Description");
		
		User user = addUser("test1", "Test1", "test1@example.com", "password");

        groupManager.addMemberToGroup(createGroupMember(group, user));
        groupManager.addMemberToGroup(createGroupMember(groupManager.getGroupAdministratorGroup(), user));

		List<GroupMember> groupMembers = groupManager.getListOfUsersInGroup(group.getId());
		
		assertEquals(1, groupMembers.size());
		assertEquals("test1", groupMembers.get(0).getUserId());
		assertTrue(groupMembers.get(0).isAdministrator());

        groupManager.removeMemberFromGroup(groupManager.getGroupAdministratorGroup().getId(), user.getId());

		groupMembers = groupManager.getListOfUsersInGroup(group.getId());

		assertEquals(1, groupMembers.size());
		assertEquals("test1", groupMembers.get(0).getUserId());
		assertFalse(groupMembers.get(0).isAdministrator());
	}

	@Test
	public void userCannotSeeGroups() throws Exception {
		userLogin("admin");

		addUser("test1", "Test1", "test1@example.com", "password");
		
		userLogin("test1");

        List<Group> groups = groupManager.getGroups();

		assertEquals(0, groups.size());
	}

	@Test
	public void userCannotSeeGroup() throws Exception {
		userLogin("admin");

		addUser("test1", "Test1", "test1@example.com", "password");
		Group group = addGroup("Group1", "Group1 Description");
		
		userLogin("test1");

        List<Group> groups = groupManager.getGroups();

        assertFalse(groups.contains(group));
	}

	@Test
	public void userInGroupCanSeeGroup() throws Exception {
		userLogin("admin");

		User user = addUser("test1", "Test1", "test1@example.com", "password");
		Group group = addGroup("Group1", "Group1 Description");

        groupManager.addMemberToGroup(createGroupMember(group, user));

        userLogin("test1");

        List<Group> groups = groupManager.getGroups();

        assertTrue(groups.contains(group));
	}

    @Test
    public void userInOneGroupCannotSeeOtherGroup() throws Exception {
        userLogin("admin");

        User user = addUser("test1", "Test1", "test1@example.com", "password");
        Group group1 = addGroup("Group1", "Group1 Description");

        groupManager.addMemberToGroup(createGroupMember(group1, user));

        Group group2 = addGroup("Group2", "Group2 Description");

        userLogin("test1");

        List<Group> groups = groupManager.getGroups();

        assertTrue(groups.contains(group1));
        assertFalse(groups.contains(group2));
    }

    @Test
	public void userWithGroupAdminRightsCanSeeGroup() throws Exception {
		userLogin("admin");

		Group group = addGroup("Group1", "Group1 Description");
		
		User user = addUser("test1", "Test1", "test1@example.com", "password");

        groupManager.addMemberToGroup(createGroupMember(group, user));
        groupManager.addMemberToGroup(createGroupMember(groupManager.getGroupAdministratorGroup(), user));

        userLogin("test1");
		
		List<Group> groups = groupManager.getGroups();

        assertTrue(groups.contains(group));
    }

	@Test
	public void userWithGroupAdminRightsIsMarkedAsAdministratorOfGroup() throws Exception {
		userLogin("admin");

		Group group = addGroup("Group1", "Group1 Description");
		
		User user = addUser("test1", "Test1", "test1@example.com", "password");

        groupManager.addMemberToGroup(createGroupMember(group, user));
        groupManager.addMemberToGroup(createGroupMember(groupManager.getGroupAdministratorGroup(), user));

        List<GroupMember> groupMembers = groupManager.getListOfUsersInGroup(group.getId());
		
		assertTrue(groupMembers.get(0).isAdministrator());
	}
	
	@Test
	public void userWithGroupAdminRightsCannotSeeOtherGroups() throws Exception {
		userLogin("admin");

		Group group = addGroup("Group1", "Group1 Description");
		addGroup("Group2", "Group1 Description");
		
		User user = addUser("test1", "Test1", "test1@example.com", "password");

        groupManager.addMemberToGroup(createGroupMember(group, user));
        groupManager.addMemberToGroup(createGroupMember(groupManager.getGroupAdministratorGroup(), user));

        userLogin("test1");
		
		List<Group> groups = groupManager.getGroups();
		
		assertEquals(1, groups.size());
		assertTrue(groups.contains(group));
	}

	@Test
	public void userWithGroupAdminRightsHasAdminRightsToAllGroupsTheyAreAMemberOf() throws Exception {
		userLogin("admin");

		Group group1 = addGroup("Group1", "Group1 Description");
		Group group2 = addGroup("Group2", "Group1 Description");
		
		User user = addUser("test1", "Test1", "test1@example.com", "password");

        groupManager.addMemberToGroup(createGroupMember(group1, user));
        groupManager.addMemberToGroup(createGroupMember(groupManager.getGroupAdministratorGroup(), user));

        groupManager.addMemberToGroup(createGroupMember(group2, user));

        List<GroupMember> groupMembers = groupManager.getListOfUsersInGroup(group2.getId());
		
		assertTrue(groupMembers.get(0).isAdministrator());
	}

	@Test
	public void userWithGroupAdminRightsCanGetListOfUsers() throws Exception {
		userLogin("admin");

		Group group = addGroup("Group1", "Group1 Description");
		User user = addUser("test1", "Test1", "test1@example.com", "password");

        groupManager.addMemberToGroup(createGroupMember(group, user));
        groupManager.addMemberToGroup(createGroupMember(groupManager.getGroupAdministratorGroup(), user));

        userLogin("test1");
		
		List<User> users = userManager.getUsers();
		
		assertEquals(2, users.size());
	}
	
	@Test
	public void userWithGroupAdminRightsCanAddUserToGroup() throws Exception {
		userLogin("admin");

		Group group = addGroup("Group1", "Group1 Description");
		User user1 = addUser("test1", "Test1", "test1@example.com", "password");
		User user2 = addUser("test2", "Test2", "test2@example.com", "password");

        groupManager.addMemberToGroup(createGroupMember(group, user1));
        groupManager.addMemberToGroup(createGroupMember(groupManager.getGroupAdministratorGroup(), user1));

        userLogin("test1");

        group = groupManager.getGroup(group);

        groupManager.addMemberToGroup(createGroupMember(group, user2));

        assertEquals(2, groupManager.getListOfUsersInGroup(group.getId()).size());
	}
	
	@Test
	public void userWithGroupAdminRightsCanRemoveUserFromGroup() throws Exception {
		userLogin("admin");

		Group group = addGroup("Group1", "Group1 Description");
		User user1 = addUser("test1", "Test1", "test1@example.com", "password");
		User user2 = addUser("test2", "Test2", "test2@example.com", "password");

        groupManager.addMemberToGroup(createGroupMember(group, user1));
        groupManager.addMemberToGroup(createGroupMember(groupManager.getGroupAdministratorGroup(), user1));

        userLogin("test1");

        groupManager.addMemberToGroup(createGroupMember(group, user2));

        assertEquals(2, groupManager.getListOfUsersInGroup(group.getId()).size());
		
		groupManager.removeMemberFromGroup(group.getId(), user2.getId());
		
		assertEquals(1, groupManager.getListOfUsersInGroup(group.getId()).size());
	}
	
	@Test
	public void userWithGroupAdminRightsCannotAssignGroupAdminRightsToAnotherUser() throws Exception {
		userLogin("admin");

		Group group = addGroup("Group1", "Group1 Description");
		User user1 = addUser("test1", "Test1", "test1@example.com", "password");
		User user2 = addUser("test2", "Test2", "test2@example.com", "password");

        groupManager.addMemberToGroup(createGroupMember(group, user1));
        groupManager.addMemberToGroup(createGroupMember(groupManager.getGroupAdministratorGroup(), user1));

        userLogin("test1");

        groupManager.addMemberToGroup(createGroupMember(group, user2));

        try {
            groupManager.addMemberToGroup(createGroupMember(groupManager.getGroupAdministratorGroup(), user2));
            fail();
        } catch (StorageException e) {
        }
	}
}
