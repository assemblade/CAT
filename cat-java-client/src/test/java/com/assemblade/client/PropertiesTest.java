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
import static org.junit.Assert.assertTrue;

public class PropertiesTest extends AbstractApiTest {
    private Folder folder;

    @Before
    public void setup() throws Exception {
        folder = folders.addRootFolder(createFolder("folder", "description", "properties"));
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
        property = properties.addProperty(property);

        assertNotNull(property);
        assertNotNull(property.getId());
        assertEquals("property1", property.getName());
        assertEquals("property1 description", property.getDescription());
        assertEquals("value1", property.getValue());

        List<Property> propertyList = properties.getProperties(folder);

        assertEquals(1, propertyList.size());
        assertTrue(propertyList.contains(property));

        property = propertyList.get(propertyList.indexOf(property));

        assertNotNull(property);
        assertNotNull(property.getId());
        assertEquals("property1", property.getName());
        assertEquals("property1 description", property.getDescription());
        assertEquals("value1", property.getValue());
    }

    @Test
    public void getPropertyTest() throws ClientException {
        Property property = properties.addProperty(createProperty(folder, "property1", "property1 description", "value1"));

        property = properties.getProperty(property.getUrl());

        assertNotNull(property);
        assertNotNull(property.getId());
        assertEquals("property1", property.getName());
        assertEquals("property1 description", property.getDescription());
        assertEquals("value1", property.getValue());
    }

    @Test
    public void getPropertiesTest() throws ClientException {
        properties.addProperty(createProperty(folder, "property1", "property1 description", "value1"));

        List<Property> propertyList = properties.getProperties(folder);

        assertEquals(1, propertyList.size());
    }

    @Test
    public void updatePropertyTest_rename() throws ClientException {
        Property property = properties.addProperty(createProperty(folder, "property1", "property1 description", "value1"));

        property.setName("property2");

        property = properties.updateProperty(property);

        assertEquals("property2", property.getName());

        List<Property> propertyList = properties.getProperties(folder);

        property = propertyList.get(propertyList.indexOf(property));

        assertEquals("property2", property.getName());
    }

    @Test
    public void updatePropertyTest_move() throws ClientException {
        Property property = properties.addProperty(createProperty(folder, "property1", "property1 description", "value1"));

        Folder newFolder = folders.addRootFolder(createFolder("folder2", "folder 2 description", "properties"));

        property.setFolder(newFolder);

        property = properties.updateProperty(property);

        assertEquals("folder2", property.getFolder().getName());

        List<Property> propertyList = properties.getProperties(folder);

        assertEquals(0, propertyList.size());

        propertyList = properties.getProperties(newFolder);

        assertEquals(1, propertyList.size());

        property = propertyList.get(propertyList.indexOf(property));

        assertEquals("folder2", property.getFolder().getName());
    }

    @Test
    public void updatePropertyTest_changeDescription() throws ClientException {
        Property property = properties.addProperty(createProperty(folder, "property1", "property1 description", "value1"));

        property.setDescription("changed description");

        Property updatedProperty = properties.updateProperty(property);

        assertEquals("changed description", updatedProperty.getDescription());

        List<Property> propertyList = properties.getProperties(folder);

        property = propertyList.get(propertyList.indexOf(property));

        assertEquals("changed description", property.getDescription());
    }

    @Test
    public void updatePropertyTest_changeValue() throws ClientException {
        Property property = properties.addProperty(createProperty(folder, "property1", "property1 description", "value1"));

        property.setValue("new value");

        Property updatedProperty = properties.updateProperty(property);

        assertEquals("new value", updatedProperty.getValue());

        List<Property> propertyList = properties.getProperties(folder);

        property = propertyList.get(propertyList.indexOf(property));

        assertEquals("new value", property.getValue());
    }

    @Test
    public void deletePropertyTest() throws ClientException {
        Property property = properties.addProperty(createProperty(folder, "property1", "property1 description", "value1"));

        properties.deleteProperty(property);

        List<Property> propertyList = properties.getProperties(folder);

        assertEquals(0, propertyList.size());
    }
}
