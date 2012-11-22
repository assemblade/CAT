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
    private Folders test;

    @Before
    public void setup() throws Exception {
        Authentication authentication  = login.login("admin", "password");
        test = new Folders(authentication);
    }

    @After
    public void teardown() {
        for (Folder folder : test.getRootFolders()) {
            test.deleteFolder(folder);
        }
    }

    @Test
    public void addRootFolderTest() {
        Folder folder = createFolder("folder1", "folder1 description");
        folder = test.addRootFolder(folder);
        assertNotNull(folder);
        assertNotNull(folder.getId());
    }

    @Test
    public void getRootFoldersTest() {
        test.addRootFolder(createFolder("folder1", "folder1 description"));
        List<Folder> folders = test.getRootFolders();
        assertEquals(1, folders.size());
    }

    @Test
    public void deleteFolderTest() {
        Folder folder = test.addRootFolder(createFolder("folder1", "folder1 description"));
        assertEquals(1, test.getRootFolders().size());
        test.deleteFolder(folder);
        assertEquals(0, test.getRootFolders().size());
    }

    private Folder createFolder(String name, String description) {
        Folder folder = new Folder();
        folder.setName(name);
        folder.setDescription(description);
        return folder;
    }
}
