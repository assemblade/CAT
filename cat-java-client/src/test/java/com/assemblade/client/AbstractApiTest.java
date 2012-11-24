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
import com.assemblade.client.model.AuthenticationPolicy;
import com.assemblade.client.model.Folder;
import com.assemblade.client.model.Group;
import com.assemblade.client.model.Property;
import com.assemblade.client.model.User;
import org.junit.After;
import org.junit.Before;

public class AbstractApiTest {
    protected static final String baseUrl = "http://localhost:11080/cat-rest-api";
    protected Login login;
    protected Policies policies;
    protected Users users;
    protected Groups groups;
    protected Folders folders;
    protected Properties properties;

    @Before
    public void initialise_client_for_admin() throws ClientException {
        login = new Login(baseUrl);
        Authentication authentication = login.login("admin", "password");
        policies = new Policies(authentication);
        users = new Users(authentication);
        groups = new Groups(authentication);
        folders = new Folders(authentication);
        properties = new Properties(authentication);
    }

    @After
    public void delete_everything() throws ClientException {
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
        for (AuthenticationPolicy policy : policies.getAuthenticationPolicies()) {
            if (!policy.getName().startsWith("Default")) {
                policies.deleteAuthenticationPolicy(policy);
            }
        }
    }

    protected Group createGroup(String name, String description) {
        Group group = new Group();
        group.setName(name);
        group.setDescription(description);

        return group;
    }

    protected Folder createFolder(String name, String description) {
        Folder folder = new Folder();
        folder.setName(name);
        folder.setDescription(description);

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

}
