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
import com.assemblade.client.model.User;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class UsersTest extends AbstractApiTest {
    @Test
    public void addUserTest() throws ClientException {
        User user = createUser("test", "Test User", "test@assemblade.com", null, "password");

        user = users.addUser(user);

        assertNotNull(user);
        assertEquals("test", user.getUserId());
        assertEquals("Test User", user.getFullName());
        assertEquals("test@assemblade.com", user.getEmailAddress());
        assertFalse(user.isGlobalAdministrator());
        assertFalse(user.isGroupAdministrator());
        assertTrue(user.isWritable());
        assertTrue(user.isDeletable());

        List<User> userList = users.getUsers();

        assertTrue(userList.contains(user));

        user = userList.get(userList.indexOf(user));

        assertEquals("test", user.getUserId());
        assertEquals("Test User", user.getFullName());
        assertEquals("test@assemblade.com", user.getEmailAddress());
        assertFalse(user.isGlobalAdministrator());
        assertFalse(user.isGroupAdministrator());
        assertTrue(user.isWritable());
        assertTrue(user.isDeletable());
    }

    @Test
    public void getUser() throws ClientException {
        User user = users.addUser(createUser("test", "Test User", "test@assemblade.com", null, "password"));

        user = users.getUser(user.getUrl());

        assertEquals("test", user.getUserId());
        assertEquals("Test User", user.getFullName());
        assertEquals("test@assemblade.com", user.getEmailAddress());
        assertFalse(user.isGlobalAdministrator());
        assertFalse(user.isGroupAdministrator());
        assertTrue(user.isWritable());
        assertTrue(user.isDeletable());
    }

    @Test
    public void getUsers() throws ClientException {
        List<User> userList = users.getUsers();

        assertEquals(1, userList.size());
    }

    @Test
    public void getAuthenticatedUserTest() throws ClientException {
        User user = users.getAuthenticatedUser();

        assertNotNull(user);
        assertEquals("admin", user.getUserId());
    }

    @Test
    public void updateUserTest_rename() throws ClientException {
        User user = users.addUser(createUser("test", "Test User", "test@assemblade.com", null, "password"));

        user.setUserId("test2");

        user = users.updateUser(user);

        assertNotNull(user);
        assertEquals("test2", user.getUserId());

        List<User> userList = users.getUsers();

        assertTrue(userList.contains(user));

        user = userList.get(userList.indexOf(user));

        assertEquals("test2", user.getUserId());
    }

    @Test
    public void updateUserTest_changeFullName() throws ClientException {
        User user = users.addUser(createUser("test", "Test User", "test@assemblade.com", null, "password"));

        user.setFullName("New Name");

        user = users.updateUser(user);

        assertNotNull(user);
        assertEquals("New Name", user.getFullName());

        List<User> userList = users.getUsers();

        assertTrue(userList.contains(user));

        user = userList.get(userList.indexOf(user));

        assertEquals("New Name", user.getFullName());
    }

    @Test
    public void updateUserTest_changeEmailAddress() throws ClientException {
        User user = users.addUser(createUser("test", "Test User", "test@assemblade.com", null, "password"));

        user.setEmailAddress("new@assemblade.com");

        user = users.updateUser(user);

        assertNotNull(user);
        assertEquals("new@assemblade.com", user.getEmailAddress());

        List<User> userList = users.getUsers();

        assertTrue(userList.contains(user));

        user = userList.get(userList.indexOf(user));

        assertEquals("new@assemblade.com", user.getEmailAddress());
    }
}
