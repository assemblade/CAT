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
import com.assemblade.client.model.Group;
import com.assemblade.client.model.GroupMember;
import com.assemblade.client.model.PasswordPolicy;
import com.assemblade.client.model.Property;
import com.assemblade.client.model.User;
import com.assemblade.client.model.View;
import org.junit.After;
import org.junit.Before;

import java.util.Arrays;

public class AbstractApiTest {
    protected static final String baseUrl = "http://localhost:11080/cat/api";
    protected Login login;
    protected Policies policies;
    protected Users users;
    protected Groups groups;
    protected Folders folders;
    protected Properties properties;
    protected Views views;

    @Before
    public void initialise_client_for_admin() throws ClientException {
        login("admin", "password");
    }

    @After
    public void delete_everything() throws ClientException {
        login("admin", "password");

        for (View view : views.getViews()) {
            views.deleteView(view);
        }

        for (Folder folder : folders.getRootFolders()) {
            folders.deleteFolder(folder);
        }
        for (Group group : groups.getAllGroups()) {
            if (group.isDeletable()) {
                groups.deleteGroup(group);
            }
        }
        for (User user : users.getUsers()) {
            if (user.isDeletable()) {
                users.deleteUser(user);
            }
        }

        PasswordPolicy policy = policies.getLocalPasswordPolicy();
        policy.setForceChangeOnReset(false);
        policies.updateLocalPasswordPolicy(policy);
    }

    protected void login(String userId, String password) throws ClientException  {
        login = new Login(baseUrl);
        Authentication authentication = login.login(userId, password);
        policies = new Policies(authentication);
        users = new Users(authentication);
        groups = new Groups(authentication);
        folders = new Folders(authentication);
        properties = new Properties(authentication);
        views = new Views(authentication);
    }

    protected User createUser(String userId, String fullName, String emailAddress, String password) {
        User user = new User();
        user.setUserId(userId);
        user.setFullName(fullName);
        user.setEmailAddress(emailAddress);
        user.setPassword(password);

        return user;
    }

    protected User createRemoteUser(String userId) {
        User user = new User();
        user.setUserId(userId);
        user.setRemoteUser(true);

        return user;
    }

    protected Group createGroup(String name, String description) {
        Group group = new Group();
        group.setName(name);
        group.setDescription(description);

        return group;
    }

    protected GroupMember createGroupMember(Group group, User user) {
        GroupMember groupMember = new GroupMember();
        groupMember.setGroup(group);
        groupMember.setId(user.getId());
        groupMember.setUserId(user.getUserId());

        return groupMember;
    }

    protected Folder createFolder(String name, String description, String template) {
        Folder folder = new Folder();
        folder.setName(name);
        folder.setDescription(description);
        folder.setTemplate(template);

        return folder;
    }

    protected Property createProperty(Folder folder, String name, String description, String value) {
        Property property = new Property();
        property.setFolder(folder);
        property.setName(name);
        property.setDescription(description);
        property.setValue(value);

        return property;
    }

    protected View createView(String name, String description, Folder... folders) {
        View view = new View();
        view.setName(name);
        view.setDescription(description);
        view.setFolders(Arrays.asList(folders));

        return view;
    }

}
