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
import com.assemblade.client.model.GroupMember;
import com.assemblade.client.model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class AdminCanAssignReadAndWritePermissionsToUsers extends AbstractApiTest {
    @Test
    public void adminCanAssignReadPermissionsToAUser() throws ClientException {
        User user = users.addUser(createUser("user", "User Name", "user@assemblade.com", null, "password"));
        Group group1 = groups.addGroup(createGroup("group1", "group1 description"));
        groups.addMemberToGroup(createGroupMember(group1, user));
        Group group2 = groups.addGroup(createGroup("group2", "group1 description"));
        groups.addMemberToGroup(createGroupMember(group2, user));

        Folder folder = folders.addRootFolder(createFolder("folder", "folder description", "properties"));

        folder.setReadGroups(Arrays.asList(group1));
        folder.setWriteGroups(Arrays.asList(group2));

        folder = folders.updateFolder(folder);

        assertEquals(group1, folder.getReadGroups().get(0));
        assertEquals(group2, folder.getWriteGroups().get(0));

        login("user", "password");

        List<Folder> folderList = folders.getFolders();

        assertEquals(1, folderList.size());
        assertTrue(folderList.contains(folder));
    }
}
