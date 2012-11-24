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
import com.assemblade.client.model.Property;
import org.junit.Before;

public class AbstractApiTest {
    protected static final String baseUrl = "http://localhost:11080/cat-rest-api";
    protected Login login;

    @Before
    public void initialise_login() throws Exception {
        login = new Login(baseUrl);
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
