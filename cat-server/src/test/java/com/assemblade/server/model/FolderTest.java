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

import com.assemblade.opendj.OpenDJTestRunner;
import com.assemblade.server.AbstractUserManagementTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(OpenDJTestRunner.class)
public class FolderTest extends AbstractUserManagementTest {
    private Group group1;
    private Group group2;
    private Group group3;

    private User user1;
    private User user2;
    private User user3;
    private Folder root;

    @Before
    public void setup() throws Exception {
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

        root = userManager.getUserSession().getByEntryDn(new Folder(), Folder.FOLDER_ROOT);
    }

    @Test
    public void folderIsAddedCorrectlyToRootWithInheritedPermissions() throws Exception {
        Folder folder = createFolder("folder", "folder description", root, true, new ArrayList<Group>(), new ArrayList<Group>());

        userManager.getUserSession().add(folder);

        Folder result = userManager.getUserSession().get(folder);

        assertEquals(folder.getDn(), result.getDn());
        assertEquals(folder.getName(), result.getName());
        assertEquals(folder.getDescription(), result.getDescription());
        assertEquals(folder.isInherit(), result.isInherit());
        assertEquals(folder.getViewPoint(), result.getViewPoint());
    }

    @Test
    public void folderIsAddedCorrectlyToRootWithoutInheritedPermissions() throws Exception {
        Folder folder = createFolder("folder", "folder description", root, false, Arrays.asList(group1, group2), Arrays.asList(group3));

        userManager.getUserSession().add(folder);

        Folder result = userManager.getUserSession().get(folder);

        assertEquals(folder.getDn(), result.getDn());
        assertEquals(folder.getName(), result.getName());
        assertEquals(folder.getDescription(), result.getDescription());
        assertEquals(folder.isInherit(), result.isInherit());
        assertEquals(folder.getViewPoint(), result.getViewPoint());

        assertEquals(2, result.getReadGroups().size());
        assertTrue(result.getReadGroups().contains(group1));
        assertTrue(result.getReadGroups().contains(group2));

        assertEquals(1, result.getWriteGroups().size());
        assertTrue(result.getWriteGroups().contains(group3));
    }

    @Test
    public void folderCanBeRenamed() throws Exception {
        Folder folder = createFolder("folder", "folder description", root, false, Arrays.asList(group1, group2), Arrays.asList(group3));

        userManager.getUserSession().add(folder);

        folder = userManager.getUserSession().get(folder);

        folder.setName("anotherFolder");

        userManager.getUserSession().update(folder);

        folder = userManager.getUserSession().get(folder);

        assertEquals("anotherFolder", folder.getName());
    }

    @Test
    public void folderCanBeMoved() throws Exception {
        Folder folder1 = createFolder("folder1", "folder1 description", root, false, Arrays.asList(group1, group2), Arrays.asList(group3));
        Folder folder2 = createFolder("folder2", "folder2 description", root, false, Arrays.asList(group1, group2), Arrays.asList(group3));

        userManager.getUserSession().add(folder1);
        userManager.getUserSession().add(folder2);

        folder1 = userManager.getUserSession().get(folder1);
        folder2 = userManager.getUserSession().get(folder2);

        folder1.setParentDn(folder2.getDn());
        folder1.setParentId(folder2.getId());

        userManager.getUserSession().update(folder1);

        folder1 = userManager.getUserSession().get(folder1);

        assertEquals("cn=folder1,cn=folder2,cn=properties,dc=assemblade,dc=com", folder1.getDn());
    }

    @Test
    public void folderCanChangeDescription() throws Exception {
        Folder folder = createFolder("folder", "folder description", root, false, Arrays.asList(group1, group2), Arrays.asList(group3));

        userManager.getUserSession().add(folder);

        folder = userManager.getUserSession().get(folder);

        folder.setDescription("changed description");

        userManager.getUserSession().update(folder);

        folder = userManager.getUserSession().get(folder);

        assertEquals("changed description", folder.getDescription());
    }

    @Test
    public void folderCanRemovePermissionsInheritance() throws Exception {
        Folder folder = createFolder("folder", "folder description", root);

        userManager.getUserSession().add(folder);

        folder = userManager.getUserSession().get(folder);

        assertTrue(folder.isInherit());

        folder.setInherit(false);
        folder.setOwner(userManager.getAuthenticatedUserDn());
        folder.setReadGroups(Arrays.asList(group1, group2));
        folder.setWriteGroups(Arrays.asList(group3));

        userManager.getUserSession().update(folder);

        folder = userManager.getUserSession().get(folder);

        assertFalse(folder.isInherit());

        assertEquals(2, folder.getReadGroups().size());
        assertTrue(folder.getReadGroups().contains(group1));
        assertTrue(folder.getReadGroups().contains(group2));

        assertEquals(1, folder.getWriteGroups().size());
        assertTrue(folder.getWriteGroups().contains(group3));
    }

    @Test
    public void folderCanSetPermissionsInheritance() throws Exception {
        Folder folder = createFolder("folder", "folder description", root, false, Arrays.asList(group1, group2), Arrays.asList(group3));

        userManager.getUserSession().add(folder);

        folder = userManager.getUserSession().get(folder);

        folder.setInherit(true);

        userManager.getUserSession().update(folder);

        folder = userManager.getUserSession().get(folder);

        assertTrue(folder.isInherit());
        assertNull(folder.getOwner());
        assertEquals(0, folder.getReadGroups().size());
        assertEquals(0, folder.getWriteGroups().size());
    }

    @Test
    public void folderCorrectlyStoresAndReturnsViewPoints() throws Exception {
        Folder folder1 = createFolder("folder1", "folder1 description", root);

        assertEquals("/folder1", folder1.getViewPoint());

        userManager.getUserSession().add(folder1);

        folder1 = userManager.getUserSession().get(folder1);

        assertEquals("/folder1", folder1.getViewPoint());

        Folder folder2 = createFolder("folder2", "folder2 description", folder1);

        assertEquals("/folder1/folder2", folder2.getViewPoint());

        userManager.getUserSession().add(folder2);

        folder2 = userManager.getUserSession().get(folder2);

        assertEquals("/folder1/folder2", folder2.getViewPoint());
    }

    private Folder createFolder(String name, String description, AbstractFolder parent) {
        Folder folder = new Folder();
        folder.setName(name);
        folder.setDescription(description);
        folder.setParentDn(parent.getDn());
        folder.setParentId(parent.getId());
        folder.setOwner(userManager.getAuthenticatedUserDn());

        return folder;
    }

    private Folder createFolder(String name, String description, AbstractFolder parent, boolean inherit, List<Group> readGroups, List<Group> writeGroups) {
        Folder folder = new Folder();
        folder.setName(name);
        folder.setDescription(description);
        folder.setParentDn(parent.getDn());
        folder.setParentId(parent.getId());
        folder.setInherit(inherit);
        folder.setReadGroups(readGroups);
        folder.setWriteGroups(writeGroups);
        folder.setOwner(userManager.getAuthenticatedUserDn());

        return folder;
    }

}
