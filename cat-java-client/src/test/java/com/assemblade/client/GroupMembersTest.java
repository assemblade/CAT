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
import com.assemblade.client.model.GroupMember;
import com.assemblade.client.model.User;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class GroupMembersTest extends AbstractApiTest {
    @Test
    public void addMemberToGroupTest() throws ClientException {
        User user = users.addUser(createUser("user", "User Name", "user@assemblade.com", null, "password"));
        Group group = groups.addGroup(createGroup("group", "group description"));

        GroupMember groupMember = createGroupMember(group, user);

        groupMember = groups.addMemberToGroup(groupMember);

        assertNotNull(groupMember);

        List<GroupMember> groupMemberList = groups.getGroupMembers(group);

        assertTrue(groupMemberList.contains(groupMember));
    }

    @Test
    public void getGroupMembersTest() throws Exception {
        User user = users.addUser(createUser("user", "User Name", "user@assemblade.com", null, "password"));
        Group group = groups.addGroup(createGroup("group", "group description"));

        groups.addMemberToGroup(createGroupMember(group, user));

        List<GroupMember> groupMemberList = groups.getGroupMembers(group);

        assertEquals(1, groupMemberList.size());
    }

    @Test
    public void removeGroupMemberTest() throws ClientException {
        User user = users.addUser(createUser("user", "User Name", "user@assemblade.com", null, "password"));
        Group group = groups.addGroup(createGroup("group", "group description"));

        GroupMember groupMember = createGroupMember(group, user);

        groupMember = groups.addMemberToGroup(groupMember);

        List<GroupMember> groupMemberList = groups.getGroupMembers(group);

        assertEquals(1, groupMemberList.size());

        groups.removeMemberFromGroup(groupMember);

        groupMemberList = groups.getGroupMembers(group);

        assertEquals(0, groupMemberList.size());
    }
}
