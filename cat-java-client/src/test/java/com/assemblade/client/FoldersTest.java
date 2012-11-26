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

import com.assemblade.client.model.Authentication;
import com.assemblade.client.model.Folder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class FoldersTest extends AbstractApiTest {
    @Test
    public void addRootFolderTest() throws ClientException {
        Folder folder = createFolder("folder1", "folder1 description");
        folder = folders.addRootFolder(folder);

        assertNotNull(folder);
        assertNotNull(folder.getId());

        assertEquals(1, folders.getRootFolders().size());
    }

    @Test
    public void getRootFoldersTest() throws ClientException {
        folders.addRootFolder(createFolder("folder1", "folder1 description"));

        List<Folder> folders = this.folders.getRootFolders();

        assertEquals(1, folders.size());
    }

    @Test
    public void addChildFolderTest() throws ClientException {
        Folder rootFolder = folders.addRootFolder(createFolder("folder1", "folder1 description"));

        Folder childFolder = folders.addChildFolder(rootFolder, createFolder("folder2", "folder2 description"));

        assertNotNull(childFolder);
        assertEquals(rootFolder.getId(), childFolder.getParentId());

        assertEquals(1, folders.getChildFolders(rootFolder).size());
    }

    @Test
    public void getChildFoldersTest() throws ClientException {
        Folder rootFolder = folders.addRootFolder(createFolder("folder1", "folder1 description"));

        Folder childFolder = folders.addChildFolder(rootFolder, createFolder("folder2", "folder2 description"));

        List<Folder> childFolders = folders.getChildFolders(rootFolder);

        assertEquals(1, childFolders.size());
        assertEquals(childFolder.getId(), childFolders.get(0).getId());
        assertEquals(childFolder.getName(), childFolders.get(0).getName());
        assertEquals(childFolder.getDescription(), childFolders.get(0).getDescription());
    }

    @Test
    public void updateFolderTest_rename() throws ClientException {
        Folder folder = folders.addRootFolder(createFolder("folder1", "folder1 description"));

        folder.setName("folder2");

        Folder updatedFolder = folders.updateFolder(folder);

        assertEquals(folder.getId(), updatedFolder.getId());
        assertEquals("folder2", updatedFolder.getName());

        List<Folder> folders = this.folders.getRootFolders();

        assertEquals(1, folders.size());
        assertEquals("folder2", folders.get(0).getName());
    }

    @Test
    public void updateFolderTest_changeDescription() throws Exception {
        Folder folder = folders.addRootFolder(createFolder("folder1", "folder1 description"));

        folder.setDescription("changed description");

        Folder updatedFolder = folders.updateFolder(folder);

        assertEquals(folder.getId(), updatedFolder.getId());
        assertEquals("changed description", updatedFolder.getDescription());

        List<Folder> folders = this.folders.getRootFolders();

        assertEquals(1, folders.size());
        assertEquals("changed description", folders.get(0).getDescription());
    }

    @Test
    public void deleteFolderTest() throws ClientException {
        Folder folder = folders.addRootFolder(createFolder("folder1", "folder1 description"));

        assertEquals(1, folders.getRootFolders().size());

        folders.deleteFolder(folder);

        assertEquals(0, folders.getRootFolders().size());
    }
}
