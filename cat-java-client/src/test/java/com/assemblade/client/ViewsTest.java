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

import com.assemblade.client.model.Folder;
import com.assemblade.client.model.Property;
import com.assemblade.client.model.View;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ViewsTest extends AbstractApiTest {
    private Folder folder1;
    private Folder folder2;
    private Property property1;
    private Property property2;
    private Property property2a;
    private Property property3;


    @Before
    public void setup() throws ClientException {
        folder1 = folders.addRootFolder(createFolder("folder1", "folder1 description"));
        folder2 = folders.addRootFolder(createFolder("folder2", "folder2 description"));

        property1 = properties.addProperty(createProperty(folder1, "property1", "description", "value1"));
        property2 = properties.addProperty(createProperty(folder1, "property2", "description", "value2"));

        property2a = properties.addProperty(createProperty(folder2, "property2", "description", "value2a"));
        property3 = properties.addProperty(createProperty(folder2, "property3", "description", "value3"));
    }

    @Test
    public void addViewTest() throws ClientException {
        View view = createView("view", "view description", folder1, folder2);

        view = views.addView(view);

        assertNotNull(view);
        assertEquals("view", view.getName());
        assertEquals("view description", view.getDescription());
        assertEquals(0, view.getFolders().indexOf(folder1));
        assertEquals(1, view.getFolders().indexOf(folder2));

        List<View> viewList = views.getViews();

        assertTrue(viewList.contains(view));

        view = viewList.get(viewList.indexOf(view));

        assertEquals("view", view.getName());
        assertEquals("view description", view.getDescription());
        assertEquals(0, view.getFolders().indexOf(folder1));
        assertEquals(1, view.getFolders().indexOf(folder2));
    }

    @Test
    public void getViewsTest() throws ClientException {
        View view = views.addView(createView("view", "view description", folder1, folder2));

        List<View> viewList = views.getViews();

        assertTrue(viewList.contains(view));
    }

    @Test
    public void getPropertiesTest() throws ClientException {
        View view = views.addView(createView("view", "view description", folder1, folder2));

        List<Property> properties = views.getProperties(view);

        assertEquals(3, properties.size());
        assertTrue(properties.contains(property1));
        assertTrue(properties.contains(property2a));
        assertTrue(properties.contains(property3));
    }
}
