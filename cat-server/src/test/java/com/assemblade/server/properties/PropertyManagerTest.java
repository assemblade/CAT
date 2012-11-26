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
package com.assemblade.server.properties;

import com.assemblade.opendj.OpenDJTestRunner;
import com.assemblade.server.AbstractUserManagementTest;
import com.assemblade.server.model.Folder;
import com.assemblade.server.model.Group;
import com.assemblade.server.model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;


@RunWith(OpenDJTestRunner.class)
public class PropertyManagerTest extends AbstractUserManagementTest {
	private PropertyManager test;

    private Group group1;
    private Group group2;
    private Group group3;

    private User user1;
    private User user2;
    private User user3;

    @Before
	public void setup() throws Exception {
		test = new PropertyManager(userManager);
        userLogin("admin");
        group1 = addGroup("group1", "");
        group2 = addGroup("group2", "");
        group3 = addGroup("group3", "");
        user1 = addUser("user1", "User1", "user1@example.com", "password");
        user2 = addUser("user2", "User2", "user2@example.com", "password");
        user3 = addUser("user3", "User3", "user3@example.com", "password");
        groupManager.addMemberToGroup(createGroupMember(group1, user1));
        groupManager.addMemberToGroup(createGroupMember(group2, user2));
        groupManager.addMemberToGroup(createGroupMember(group3, user3));
    }

    @After
    public void tearDown() throws Exception {
        userLogin("admin");
        for (Folder folder : test.getRootFolders()) {
            test.deleteFolder(folder.getId());
        }
    }

	@Test
	public void administratorCanAddFolderUnderRoot() throws Exception {
		userLogin("admin");
		
		Folder folder = createFolder("folder1");

		test.addFolder(folder);
		
		List<Folder> folders = test.getRootFolders();
		
		assertEquals(1, folders.size());
		assertEquals("folder1", folders.get(0).getName());
	}

	@Test
	public void administratorCanDeleteFolderUnderRoot() throws Exception {
		userLogin("admin");
		
		Folder folder = createFolder("folder1");

		folder = test.addFolder(folder);
		
		List<Folder> folders = test.getRootFolders();
		
		assertEquals(1, folders.size());
		assertEquals("folder1", folders.get(0).getName());
		
		test.deleteFolder(folder.getId());

		folders = test.getRootFolders();
		
		assertEquals(0, folders.size());
	}
	
	@Test
	public void administratorCanAssignPermissionsToAFolder() throws Exception {
		userLogin("admin");
		
		Folder folder = createFolder("folder1", false, Arrays.asList(group1), Arrays.asList(group2));

		test.addFolder(folder);
		
		List<Folder> folders = test.getRootFolders();
		
		assertEquals(1, folders.size());
		assertEquals("folder1", folders.get(0).getName());
        assertEquals(1, folders.get(0).getReadGroups().size());
        assertEquals(group1, folders.get(0).getReadGroups().get(0));
        assertEquals(1, folders.get(0).getWriteGroups().size());
        assertEquals(group2, folders.get(0).getWriteGroups().get(0));
    }

    @Test
    public void userWithReadPermissionsCanReadFolder() throws Exception{
        userLogin("admin");

        Folder folder = createFolder("folder1", false, Arrays.asList(group1), null);

        test.addFolder(folder);

        userLogin("user1");

        List<Folder> folders = test.getRootFolders();

        assertEquals(1, folders.size());
    }

    @Test
    public void usersFromDifferentGroupsWithReadPermissionsCanReadFolder() throws Exception {
        userLogin("admin");

        Folder folder = createFolder("folder1", false, Arrays.asList(group1, group2), null);

        test.addFolder(folder);

        userLogin("user1");

        assertEquals(1, test.getRootFolders().size());

        userLogin("user2");

        assertEquals(1, test.getRootFolders().size());
    }

    @Test
    public void userWithWritePermissionsCanAddFolder() throws Exception {
        userLogin("admin");

        Folder folder1 = test.addFolder(createFolder("folder1", false, null, Arrays.asList(group1, group2)));

        userLogin("user1");

        Folder folder2 = test.addFolder(createFolder("folder2", folder1));

        List<Folder> folders = test.getFolders(folder1.getId());

        assertEquals(1, folders.size());

        assertEquals(folder2.getName(), folders.get(0).getName());

        userLogin("user2");

        folders = test.getFolders(folder1.getId());

        assertEquals(1, folders.size());

        assertEquals(folder2.getName(), folders.get(0).getName());
    }

    @Test
    public void userWithWritePermissionsDeleteFolder() throws Exception {
        userLogin("admin");

        Folder folder1 = createFolder("folder1", false, null, Arrays.asList(group1));

        folder1 = test.addFolder(folder1);

        userLogin("user1");

        Folder folder2 = createFolder("folder2", folder1);

        folder2 = test.addFolder(folder2);

        List<Folder> folders = test.getFolders(folder1.getId());

        assertEquals(1, folders.size());

        test.deleteFolder(folder2.getId());

        folders = test.getFolders(folder1.getId());

        assertEquals(0, folders.size());
    }

    @Test
    public void userCanCreateFolderWithoutInheritedPermissions() throws Exception {
        userLogin("admin");

        Folder folder1 = createFolder("folder1", false, null, Arrays.asList(group1, group2));

        folder1 = test.addFolder(folder1);

        userLogin("user1");

        Folder folder2 = createFolder("folder2", folder1, false, null, null);

        folder2 = test.addFolder(folder2);

        List<Folder> folders = test.getFolders(folder1.getId());

        assertEquals(1, folders.size());

        assertEquals(folder2.getName(), folders.get(0).getName());

        userLogin("user2");

        folders = test.getFolders(folder1.getId());

        assertEquals(0, folders.size());
    }

    @Test
    public void userCanRenameAFolder() throws Exception {
        userLogin("admin");

        Folder folder = createFolder("folder1", false, null, Arrays.asList(group1));

        folder = test.addFolder(folder);

        String id = folder.getId();

        userLogin("user1");

        Folder renamedFolder = test.getRootFolders().get(0);

        renamedFolder.setName("folder2");

        folder = test.updateFolder(renamedFolder);

        assertEquals(id, folder.getId());
        assertEquals("folder2", folder.getName());

        List<Folder> folders = test.getRootFolders();

        assertEquals(1, folders.size());

        assertEquals(folder.getName(), folders.get(0).getName());
        assertEquals(folder.getId(), folders.get(0).getId());
    }

    @Test
    public void userCanMoveAFolderToAnotherFolder() throws Exception {
        userLogin("admin");

        Folder folder1 = test.addFolder(createFolder("folder1", false, null, Arrays.asList(group1)));
        Folder folder2 = test.addFolder(createFolder("folder2", false, null, Arrays.asList(group1)));

        String id = folder1.getId();

        userLogin("user1");

        assertEquals(2, test.getRootFolders().size());
        assertEquals(0, test.getFolders(folder2.getId()).size());

        Folder movedFolder = test.getRootFolders().get(0);
        movedFolder.setParentDn(folder2.getDn());
        movedFolder.setParentId(folder2.getId());

        folder1 = test.updateFolder(movedFolder);

        assertEquals(id, folder1.getId());
        assertEquals(folder2.getDn(), folder1.getParentDn());

        assertEquals(1, test.getRootFolders().size());
        assertEquals(1, test.getFolders(folder2.getId()).size());

        assertEquals("folder1", test.getFolders(folder2.getId()).get(0).getName());
    }

    private Folder createFolder(String name) {
        Folder folder = new Folder();
        folder.setName(name);
        folder.setParentDn(Folder.FOLDER_ROOT);

        return folder;
    }

    private Folder createFolder(String name, boolean inherit, List<Group> readGroups, List<Group> writeGroups) {
        Folder folder = new Folder();
        folder.setName(name);
        folder.setParentDn(Folder.FOLDER_ROOT);
        folder.setInherit(inherit);
        folder.setReadGroups(readGroups);
        folder.setWriteGroups(writeGroups);

        return folder;
    }

    private Folder createFolder(String name, Folder parent) {
        Folder folder = new Folder();
        folder.setName(name);
        folder.setParentDn(parent.getDn());
        folder.setParentId(parent.getId());

        return folder;
    }

    private Folder createFolder(String name, Folder parent, boolean inherit, List<Group> readGroups, List<Group> writeGroups) {
        Folder folder = new Folder();
        folder.setName(name);
        folder.setParentDn(parent.getDn());
        folder.setParentId(parent.getId());
        folder.setInherit(inherit);
        folder.setReadGroups(readGroups);
        folder.setWriteGroups(writeGroups);

        return folder;
    }
}
