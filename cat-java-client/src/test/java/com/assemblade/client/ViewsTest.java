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
import com.assemblade.client.model.View;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ViewsTest extends AbstractApiTest {
    @Test
    public void addViewTest() throws ClientException {
        Folder folder1 = folders.addRootFolder(createFolder("folder1", "folder1 description"));
        Folder folder2 = folders.addRootFolder(createFolder("folder2", "folder2 description"));

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
}
