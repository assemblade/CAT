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
import com.assemblade.server.model.GroupUser;
import com.assemblade.server.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(OpenDJTestRunner.class)
public class GroupManagerTest extends AbstractUserManagementTest {

	@Test
	public void adminCanSeeUsers() throws Exception {
		userLogin("admin");
		userManager.addUser(new User("test1", "Test1", "test1@example.com", "password"));

		List<User> users = userManager.getUsers();
		
		assertEquals(2, users.size());
	}
	
	@Test
	public void adminHasAccessToAllGroups() throws Exception {
		userLogin("admin");

		List<Group> groups = groupManager.getGroups();
		
		assertEquals(2, groups.size());
	}
	
	@Test
	public void adminCanAddGroup() throws Exception {
		userLogin("admin");

		Group expected = groupManager.addGroup("Test1", "Test1 Description");

		assertEquals(3, groupManager.getGroups().size());

		Group actual = userManager.getUserSession().get(expected);
		
		assertEquals("Test1", actual.getName());
		assertEquals("Test1 Description", actual.getDescription());
	}
	
	@Test
	public void adminCanDeleteGroup() throws Exception {
		userLogin("admin");

		Group expected = groupManager.addGroup("Test1", "Test1 Description");

		groupManager.deleteGroup(expected.getId());
		
		assertEquals(2, groupManager.getGroups().size());
		
		assertFalse(userManager.getUserSession().exists(expected));
	}
	
	@Test(expected=StorageException.class)
	public void adminCannotDeleteAdminGroup() throws Exception {
		userLogin("admin");

        Group adminGroup = userManager.getUserSession().getByEntryDn(new Group(), "cn=globaladmin,ou=groups,dc=assemblade,dc=com");

		groupManager.deleteGroup(adminGroup.getId());
	}
	
	@Test
	public void adminCanGiveAdminRightsToUser() throws Exception {
		userLogin("admin");

		User user = userManager.addUser(new User("test1", "Test1", "test1@example.com", "password"));

		groupManager.addUserToGroup(Group.GLOBAL_ADMIN_DN, user.getDn());
		
		userLogin("test1");
		
		assertEquals(2, groupManager.getGroups().size());
	}
	
	@Test
	public void adminCanAddUserToGroup() throws Exception {
		userLogin("admin");

		Group group = groupManager.addGroup("Group1", "Group1 Description");
		User user = userManager.addUser(new User("test1", "Test1", "test1@example.com", "password"));
		
		groupManager.addUserToGroup(group.getDn(), user.getDn());

		List<GroupUser> groupUsers = groupManager.getListOfUsersInGroup(group.getId());
		
		assertEquals(1, groupUsers.size());
		assertEquals("test1", groupUsers.get(0).getUserId());
		assertFalse(groupUsers.get(0).isAdministrator());
	}

	@Test
	public void adminCanRemoveUserFromGroup() throws Exception {
		userLogin("admin");

		Group group = groupManager.addGroup("Group1", "Group1 Description");
		User user = userManager.addUser(new User("test1", "Test1", "test1@example.com", "password"));
		
		groupManager.addUserToGroup(group.getDn(), user.getDn());

		List<GroupUser> groupUsers = groupManager.getListOfUsersInGroup(group.getId());
		
		assertEquals(1, groupUsers.size());
		
		groupManager.removeUserFromGroup(group.getDn(), user.getDn());

		groupUsers = groupManager.getListOfUsersInGroup(group.getId());
		
		assertEquals(0, groupUsers.size());
	}
	
	@Test
	public void adminCanGiveGroupAdminRightsToUser() throws Exception {
		userLogin("admin");

		Group group = groupManager.addGroup("Group1", "Group1 Description");
		
		User user = userManager.addUser(new User("test1", "Test1", "test1@example.com", "password"));
		
		groupManager.addUserToGroup(group.getDn(), user.getDn());
		groupManager.setUserAdministrativeRights(group.getDn(), user.getDn(), true);

		List<GroupUser> groupUsers = groupManager.getListOfUsersInGroup(group.getId());
		
		assertEquals(1, groupUsers.size());
		assertEquals("test1", groupUsers.get(0).getUserId());
		assertTrue(groupUsers.get(0).isAdministrator());
	}

	@Test
	public void adminCanRemoveGroupAdminRightsFromUser() throws Exception {
		userLogin("admin");

		Group group = groupManager.addGroup("Group1", "Group1 Description");
		
		User user = userManager.addUser(new User("test1", "Test1", "test1@example.com", "password"));
		
		groupManager.addUserToGroup(group.getDn(), user.getDn());
		groupManager.setUserAdministrativeRights(group.getDn(), user.getDn(), true);

		List<GroupUser> groupUsers = groupManager.getListOfUsersInGroup(group.getId());
		
		assertEquals(1, groupUsers.size());
		assertEquals("test1", groupUsers.get(0).getUserId());
		assertTrue(groupUsers.get(0).isAdministrator());
		
		groupManager.setUserAdministrativeRights(group.getDn(), user.getDn(), false);

		groupUsers = groupManager.getListOfUsersInGroup(group.getId());

		assertEquals(1, groupUsers.size());
		assertEquals("test1", groupUsers.get(0).getUserId());
		assertFalse(groupUsers.get(0).isAdministrator());
	}

	@Test
	public void userCannotSeeAdminGroup() throws Exception {
		userLogin("admin");

		userManager.addUser(new User("test1", "Test1", "test1@example.com", "password"));
		
		userLogin("test1");

        List<Group> groups = groupManager.getGroups();

		assertEquals(1, groups.size());
	}

	@Test
	public void userCannotSeeGroup() throws Exception {
		userLogin("admin");

		userManager.addUser(new User("test1", "Test1", "test1@example.com", "password"));
		groupManager.addGroup("Group1", "Group1 Description");
		
		userLogin("test1");
		
		assertEquals(1, groupManager.getGroups().size());
	}

	@Test
	public void userCannotAddItselfToGroup() throws Exception {
		userLogin("admin");

		Group group = groupManager.addGroup("Group1", "Group1 Description");
		User user = userManager.addUser(new User("test1", "Test1", "test1@example.com", "password"));
		
		userLogin("test1");

		groupManager.addUserToGroup(group.getDn(), user.getDn());
		
		userLogin("admin");
		
		assertEquals(0, groupManager.getListOfUsersInGroup(group.getId()).size());
	}
	
	@Test
	public void userInGroupCanSeeGroup() throws Exception {
		userLogin("admin");

		User user = userManager.addUser(new User("test1", "Test1", "test1@example.com", "password"));
		Group group = groupManager.addGroup("Group1", "Group1 Description");

        groupManager.addUserToGroup(group.getDn(), user.getDn());

        userLogin("test1");

        List<Group> groups = groupManager.getGroups();

        assertEquals(2, groups.size());
        assertEquals("Group1", groups.get(1).getName());
	}

    @Test
    public void userInOneGroupCannotSeeOtherGroup() throws Exception {
        userLogin("admin");

        User user = userManager.addUser(new User("test1", "Test1", "test1@example.com", "password"));
        Group group = groupManager.addGroup("Group1", "Group1 Description");

        groupManager.addUserToGroup(group.getDn(), user.getDn());

        groupManager.addGroup("Group2", "Group2 Description");

        userLogin("test1");

        List<Group> groups = groupManager.getGroups();

        assertEquals(2, groups.size());

        assertEquals("Group1", groups.get(1).getName());
    }

    @Test
	public void userWithGroupAdminRightsCanSeeGroup() throws Exception {
		userLogin("admin");

		Group group = groupManager.addGroup("Group1", "Group1 Description");
		
		User user = userManager.addUser(new User("test1", "Test1", "test1@example.com", "password"));
		
		groupManager.addUserToGroup(group.getDn(), user.getDn());
		groupManager.setUserAdministrativeRights(group.getDn(), user.getDn(), true);
		
		userLogin("test1");
		
		List<Group> groups = groupManager.getGroups();
		
		assertEquals(2, groups.size());
		assertEquals("Group1", groups.get(1).getName());
	}

	@Test
	public void userWithGroupAdminRightsIsMarkedAsAdministratorOfGroup() throws Exception {
		userLogin("admin");

		Group group = groupManager.addGroup("Group1", "Group1 Description");
		
		User user = userManager.addUser(new User("test1", "Test1", "test1@example.com", "password"));
		
		groupManager.addUserToGroup(group.getDn(), user.getDn());
		groupManager.setUserAdministrativeRights(group.getDn(), user.getDn(), true);
		
		List<GroupUser> groupUsers = groupManager.getListOfUsersInGroup(group.getId());
		
		assertTrue(groupUsers.get(0).isAdministrator());
	}
	
	@Test
	public void userWithGroupAdminRightsCannotSeeOtherGroup() throws Exception {
		userLogin("admin");

		Group group = groupManager.addGroup("Group1", "Group1 Description");
		groupManager.addGroup("Group2", "Group1 Description");
		
		User user = userManager.addUser(new User("test1", "Test1", "test1@example.com", "password"));
		
		groupManager.addUserToGroup(group.getDn(), user.getDn());
		groupManager.setUserAdministrativeRights(group.getDn(), user.getDn(), true);
		
		userLogin("test1");
		
		List<Group> groups = groupManager.getGroups();
		
		assertEquals(2, groups.size());
		assertEquals("Group1", groups.get(1).getName());
	}

	@Test
	public void userWithGroupAdminRightsOfOneGroupDoesNotHaveAdminRightsOfOtherGroupsTheyAreAMemberOf() throws Exception {
		userLogin("admin");

		Group group1 = groupManager.addGroup("Group1", "Group1 Description");
		Group group2 = groupManager.addGroup("Group2", "Group1 Description");
		
		User user = userManager.addUser(new User("test1", "Test1", "test1@example.com", "password"));
		
		groupManager.addUserToGroup(group1.getDn(), user.getDn());
		groupManager.setUserAdministrativeRights(group1.getDn(), user.getDn(), true);
		groupManager.addUserToGroup(group2.getDn(), user.getDn());
		
		List<GroupUser> groupUsers = groupManager.getListOfUsersInGroup(group2.getId());
		
		assertFalse(groupUsers.get(0).isAdministrator());
	}

	@Test
	public void userWithGroupAdminRightsCanGetListOfUsers() throws Exception {
		userLogin("admin");

		Group group = groupManager.addGroup("Group1", "Group1 Description");
		User user = userManager.addUser(new User("test1", "Test1", "test1@example.com", "password"));
		
		groupManager.addUserToGroup(group.getDn(), user.getDn());
		groupManager.setUserAdministrativeRights(group.getDn(), user.getDn(), true);

		userLogin("test1");
		
		List<User> users = userManager.getUsers();
		
		assertEquals(2, users.size());
	}
	
	@Test
	public void userWithGroupAdminRightsCanAddUserToGroup() throws Exception {
		userLogin("admin");

		Group group = groupManager.addGroup("Group1", "Group1 Description");
		User user1 = userManager.addUser(new User("test1", "Test1", "test1@example.com", "password"));
		User user2 = userManager.addUser(new User("test2", "Test2", "test2@example.com", "password"));

		groupManager.addUserToGroup(group.getDn(), user1.getDn());
		groupManager.setUserAdministrativeRights(group.getDn(), user1.getDn(), true);

		userLogin("test1");
		
		groupManager.addUserToGroup(group.getDn(), user2.getDn());
		
		assertEquals(2, groupManager.getListOfUsersInGroup(group.getId()).size());
	}
	
	@Test
	public void userWithGroupAdminRightsCanRemoveUserFromGroup() throws Exception {
		userLogin("admin");

		Group group = groupManager.addGroup("Group1", "Group1 Description");
		User user1 = userManager.addUser(new User("test1", "Test1", "test1@example.com", "password"));
		User user2 = userManager.addUser(new User("test2", "Test2", "test2@example.com", "password"));

		groupManager.addUserToGroup(group.getDn(), user1.getDn());
		groupManager.setUserAdministrativeRights(group.getDn(), user1.getDn(), true);

		userLogin("test1");
		
		groupManager.addUserToGroup(group.getDn(), user2.getDn());
		
		assertEquals(2, groupManager.getListOfUsersInGroup(group.getId()).size());
		
		groupManager.removeUserFromGroup(group.getDn(), user2.getDn());
		
		assertEquals(1, groupManager.getListOfUsersInGroup(group.getId()).size());
	}
	
	@Test
	public void userWithGroupAdminRightsCanAssignGroupAdminRightsToAnotherUser() throws Exception {
		userLogin("admin");

		Group group = groupManager.addGroup("Group1", "Group1 Description");
		User user1 = userManager.addUser(new User("test1", "Test1", "test1@example.com", "password"));
		User user2 = userManager.addUser(new User("test2", "Test2", "test2@example.com", "password"));

		groupManager.addUserToGroup(group.getDn(), user1.getDn());
		groupManager.setUserAdministrativeRights(group.getDn(), user1.getDn(), true);

		userLogin("test1");
		
		groupManager.addUserToGroup(group.getDn(), user2.getDn());
		groupManager.setUserAdministrativeRights(group.getDn(), user2.getDn(), true);
		
		userLogin("test2");

		assertEquals(2, groupManager.getGroups().size());
		assertEquals(2, groupManager.getListOfUsersInGroup(group.getId()).size());
	}
}
