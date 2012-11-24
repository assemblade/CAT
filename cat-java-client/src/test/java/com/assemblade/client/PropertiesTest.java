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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PropertiesTest extends AbstractApiTest {
    private Folders folders;
    private Properties test;
    private Folder folder;

    @Before
    public void setup() throws Exception {
        Authentication authentication  = login.login("admin", "password");
        folders = new Folders(authentication);
        test = new Properties(authentication);
        folder = folders.addRootFolder(createFolder("folder", "description"));
    }

    @After
    public void teardown() throws ClientException {
        for (Folder folder : folders.getRootFolders()) {
            folders.deleteFolder(folder);
        }
    }

    @Test
    public void addPropertyTest() throws ClientException {
        Property property = createProperty(folder, "property1", "property1 description", "value1");
        property = test.addProperty(property);

        assertNotNull(property);
        assertNotNull(property.getId());
        assertEquals("property1", property.getName());
        assertEquals("property1 description", property.getDescription());
        assertEquals("value1", property.getValue());

        List<Property> properties = test.getProperties(folder);

        assertEquals(1, properties.size());
        assertNotNull(properties.get(0).getId());
        assertEquals("property1", properties.get(0).getName());
        assertEquals("property1 description", properties.get(0).getDescription());
        assertEquals("value1", properties.get(0).getValue());
    }

    @Test
    public void getPropertiesTest() throws ClientException {
        test.addProperty(createProperty(folder, "property1", "property1 description", "value1"));

        List<Property> properties = test.getProperties(folder);

        assertEquals(1, properties.size());
    }

    @Test
    public void updatePropertyTest_rename() throws ClientException {
        Property property = test.addProperty(createProperty(folder, "property1", "property1 description", "value1"));

        property.setName("property2");

        Property updatedProperty = test.updateProperty(property);

        assertEquals("property2", updatedProperty.getName());

        List<Property> properties = test.getProperties(folder);

        assertEquals("property2", properties.get(0).getName());
    }

    @Test
    public void updatePropertyTest_move() throws ClientException {
        Property property = test.addProperty(createProperty(folder, "property1", "property1 description", "value1"));

        Folder newFolder = folders.addRootFolder(createFolder("folder2", "folder 2 description"));

        property.setFolder(newFolder);

        Property updatedProperty = test.updateProperty(property);

        assertEquals("folder2", updatedProperty.getFolder().getName());

        List<Property> properties = test.getProperties(folder);

        assertEquals(0, properties.size());

        properties = test.getProperties(newFolder);

        assertEquals(1, properties.size());

        assertEquals("folder2", properties.get(0).getFolder().getName());
    }

    @Test
    public void updatePropertyTest_changeDescription() throws ClientException {
        Property property = test.addProperty(createProperty(folder, "property1", "property1 description", "value1"));

        property.setDescription("changed description");

        Property updatedProperty = test.updateProperty(property);

        assertEquals("changed description", updatedProperty.getDescription());

        List<Property> properties = test.getProperties(folder);

        assertEquals("changed description", properties.get(0).getDescription());
    }

    @Test
    public void updatePropertyTest_changeValue() throws ClientException {
        Property property = test.addProperty(createProperty(folder, "property1", "property1 description", "value1"));

        property.setValue("new value");

        Property updatedProperty = test.updateProperty(property);

        assertEquals("new value", updatedProperty.getValue());

        List<Property> properties = test.getProperties(folder);

        assertEquals("new value", properties.get(0).getValue());
    }
}
