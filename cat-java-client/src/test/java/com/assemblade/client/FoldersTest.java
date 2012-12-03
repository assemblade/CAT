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
package com.assemblade.client;

import com.assemblade.client.model.Folder;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class FoldersTest extends AbstractApiTest {
    @Test
    public void addRootFolderTest() throws ClientException {
        Folder folder = createFolder("folder1", "folder1 description", "properties");
        folder = folders.addRootFolder(folder);

        assertNotNull(folder);
        assertNotNull(folder.getId());
        assertNull(folder.getParent());
        assertEquals("folder1", folder.getName());
        assertEquals("folder1 description", folder.getDescription());
        assertEquals("properties", folder.getTemplate());
        assertTrue(folder.isAddable());
        assertTrue(folder.isWritable());
        assertTrue(folder.isDeletable());

        List<Folder> folderList = folders.getRootFolders();

        assertEquals(1, folderList.size());

        assertTrue(folderList.contains(folder));

        folder = folderList.get(folderList.indexOf(folder));

        assertNotNull(folder);
        assertNotNull(folder.getId());
        assertNull(folder.getParent());
        assertEquals("folder1", folder.getName());
        assertEquals("folder1 description", folder.getDescription());
        assertEquals("properties", folder.getTemplate());
        assertTrue(folder.isAddable());
        assertTrue(folder.isWritable());
        assertTrue(folder.isDeletable());
    }

    @Test
    public void getFolderTest() throws ClientException {
        Folder folder = folders.addRootFolder(createFolder("folder1", "folder1 description", "properties"));

        folder = folders.getFolder(folder.getUrl());

        assertNotNull(folder);
        assertNotNull(folder.getId());
        assertNull(folder.getParent());
        assertEquals("folder1", folder.getName());
        assertEquals("folder1 description", folder.getDescription());
        assertEquals("properties", folder.getTemplate());
        assertTrue(folder.isAddable());
        assertTrue(folder.isWritable());
        assertTrue(folder.isDeletable());
    }

    @Test
    public void getRootFoldersTest() throws ClientException {
        folders.addRootFolder(createFolder("folder1", "folder1 description", "properties"));

        List<Folder> folders = this.folders.getRootFolders();

        assertEquals(1, folders.size());
    }

    @Test
    public void addChildFolderTest() throws ClientException {
        Folder rootFolder = folders.addRootFolder(createFolder("folder1", "folder1 description", "properties"));

        Folder childFolder = folders.addChildFolder(rootFolder, createFolder("folder2", "folder2 description", "properties"));

        assertNotNull(childFolder);
        assertEquals(rootFolder.getId(), childFolder.getParent().getId());
        assertEquals("folder2", childFolder.getName());
        assertEquals("folder2 description", childFolder.getDescription());
        assertEquals("properties", childFolder.getTemplate());
        assertTrue(childFolder.isAddable());
        assertTrue(childFolder.isWritable());
        assertTrue(childFolder.isDeletable());

        List<Folder> folderList = folders.getChildFolders(rootFolder);

        assertEquals(1, folderList.size());

        assertTrue(folderList.contains(childFolder));

        childFolder = folderList.get(folderList.indexOf(childFolder));

        assertEquals(rootFolder.getId(), childFolder.getParent().getId());
        assertEquals("folder2", childFolder.getName());
        assertEquals("folder2 description", childFolder.getDescription());
        assertEquals("properties", childFolder.getTemplate());
        assertTrue(childFolder.isAddable());
        assertTrue(childFolder.isWritable());
        assertTrue(childFolder.isDeletable());
    }

    @Test
    public void getChildFoldersTest() throws ClientException {
        Folder rootFolder = folders.addRootFolder(createFolder("folder1", "folder1 description", "properties"));

        Folder childFolder = folders.addChildFolder(rootFolder, createFolder("folder2", "folder2 description", "properties"));

        List<Folder> childFolders = folders.getChildFolders(rootFolder);

        assertEquals(1, childFolders.size());

        assertTrue(childFolders.contains(childFolder));
    }

    @Test
    public void updateFolderTest_rename() throws ClientException {
        Folder folder = folders.addRootFolder(createFolder("folder1", "folder1 description", "properties"));

        folder.setName("folder2");

        Folder updatedFolder = folders.updateFolder(folder);

        assertEquals(folder.getId(), updatedFolder.getId());
        assertEquals("folder2", updatedFolder.getName());

        List<Folder> folderList = this.folders.getRootFolders();

        folder = folderList.get(folderList.indexOf(folder));

        assertEquals("folder2", folder.getName());
    }

    @Test
    public void updateFolderTest_changeDescription() throws Exception {
        Folder folder = folders.addRootFolder(createFolder("folder1", "folder1 description", "properties"));

        folder.setDescription("changed description");

        Folder updatedFolder = folders.updateFolder(folder);

        assertEquals(folder.getId(), updatedFolder.getId());
        assertEquals("changed description", updatedFolder.getDescription());

        List<Folder> folderList = this.folders.getRootFolders();

        folder = folderList.get(folderList.indexOf(folder));

        assertEquals("changed description", folder.getDescription());
    }

    @Test
    public void deleteFolderTest() throws ClientException {
        Folder folder = folders.addRootFolder(createFolder("folder1", "folder1 description", "properties"));

        assertEquals(1, folders.getRootFolders().size());

        folders.deleteFolder(folder);

        assertEquals(0, folders.getRootFolders().size());
    }
}
