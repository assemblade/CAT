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
import com.assemblade.client.model.Group;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class GroupsTest extends AbstractApiTest {
    @Test
    public void addGroupTest() throws ClientException {
        Group group = createGroup("group1", "group1 description");

        group = groups.addGroup(group);

        assertNotNull(group);
        assertNotNull(group.getId());
        assertEquals("group1", group.getName());
        assertEquals("group1 description", group.getDescription());
        assertEquals("group", group.getType());
        assertTrue(group.isDeletable());
        assertTrue(group.isWritable());

        List<Group> groupList = groups.getAllGroups();

        assertTrue(groupList.contains(group));

        group = groupList.get(groupList.indexOf(group));

        assertNotNull(group);
        assertNotNull(group.getId());
        assertEquals("group1", group.getName());
        assertEquals("group1 description", group.getDescription());
        assertEquals("group", group.getType());
        assertTrue(group.isDeletable());
        assertTrue(group.isWritable());
    }

    @Test
    public void getGroupTest() throws ClientException {
        Group group = groups.addGroup(createGroup("group1", "group1 description"));

        group = groups.getGroup(group.getUrl());

        assertNotNull(group);
        assertNotNull(group.getId());
        assertEquals("group1", group.getName());
        assertEquals("group1 description", group.getDescription());
        assertEquals("group", group.getType());
        assertTrue(group.isDeletable());
        assertTrue(group.isWritable());
    }

    @Test
    public void getAllGroupsTest() throws ClientException {
        List<Group> groupList = groups.getAllGroups();

        assertEquals(2, groupList.size());
    }

    @Test
    public void getAdministratorGroupTest() throws ClientException {
        Group group = groups.getAdministratorGroup();

        assertNotNull(group);
        assertEquals("Application Administrators", group.getName());
        assertEquals("admingroup", group.getType());
        assertFalse(group.isDeletable());
        assertFalse(group.isWritable());
    }

    @Test
    public void updateGroup_rename() throws ClientException {
        Group group = groups.addGroup(createGroup("group1", "group1 description"));

        group.setName("group2");

        Group updatedGroup = groups.updateGroup(group);

        assertEquals("group2", updatedGroup.getName());

        List<Group> groupList = groups.getAllGroups();

        assertTrue(groupList.contains(updatedGroup));

        updatedGroup = groupList.get(groupList.indexOf(updatedGroup));

        assertEquals("group2", updatedGroup.getName());
    }

    @Test
    public void updateGroup_edit() throws ClientException {
        Group group = groups.addGroup(createGroup("group1", "group1 description"));

        group.setDescription("changed description");

        Group updatedGroup = groups.updateGroup(group);

        assertEquals("changed description", updatedGroup.getDescription());

        List<Group> groupList = groups.getAllGroups();

        assertTrue(groupList.contains(updatedGroup));

        updatedGroup = groupList.get(groupList.indexOf(updatedGroup));

        assertEquals("changed description", updatedGroup.getDescription());
    }



}
