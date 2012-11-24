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
package com.assemblade.client.scenarios.folders;

import com.assemblade.client.AbstractApiTest;
import com.assemblade.client.ClientException;
import com.assemblade.client.Folders;
import com.assemblade.client.Groups;
import com.assemblade.client.Policies;
import com.assemblade.client.Users;
import com.assemblade.client.model.Authentication;
import com.assemblade.client.model.AuthenticationPolicy;
import com.assemblade.client.model.Folder;
import com.assemblade.client.model.Group;
import com.assemblade.client.model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AdminCanAssignReadAndWritePermissionsToUsers extends AbstractApiTest {
    private Users users;
    private Groups groups;
    private Folders folders;

    @Before
    public void setup() throws ClientException {
        Authentication authentication = login.login("admin", "password");
        users = new Users(authentication);
        groups = new Groups(authentication);
        folders = new Folders(authentication);
    }

    @After
    public void teardown() throws ClientException {
        for (Folder folder : folders.getRootFolders()) {
            folders.deleteFolder(folder);
        }
        for (Group group : groups.getAllGroups()) {
            if (group.isDeletable()) {
                groups.deleteGroup(group);
            }
        }
        for (User user : users.getAllUsers()) {
            if (user.isDeletable()) {
                users.deleteUser(user);
            }
        }
    }

    @Test
    public void adminCanAssignReadPermissionsToAUser() throws ClientException {

    }
}