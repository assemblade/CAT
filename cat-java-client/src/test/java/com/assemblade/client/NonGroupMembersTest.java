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

import com.assemblade.client.model.Group;
import com.assemblade.client.model.User;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class NonGroupMembersTest extends AbstractApiTest {
    @Test
    public void getNonGroupMembers_userNotInGroup() throws ClientException {
        User user = users.addUser(createUser("user", "User Name", "user@assemblade.com", null, "password"));
        Group group = groups.addGroup(createGroup("group", "group description"));

        List<User> nonGroupMemberList = groups.getNonGroupMembers(group);

        assertTrue(nonGroupMemberList.contains(user));
    }

    @Test
    public void getNonGroupMembers_userInGroup() throws ClientException {
        User user = users.addUser(createUser("user", "User Name", "user@assemblade.com", null, "password"));
        Group group = groups.addGroup(createGroup("group", "group description"));

        groups.addMemberToGroup(createGroupMember(group, user));

        List<User> nonGroupMemberList = groups.getNonGroupMembers(group);

        assertFalse(nonGroupMemberList.contains(user));
    }
}
