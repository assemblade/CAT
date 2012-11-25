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
package com.assemblade.server.model;

import com.assemblade.opendj.OpenDJTestRunner;
import com.assemblade.server.AbstractUserManagementTest;
import com.assemblade.server.properties.PropertyManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

@RunWith(OpenDJTestRunner.class)
public class ViewTest extends AbstractUserManagementTest {
    private Group group1;
    private Group group2;
    private Group group3;

    private User user1;
    private User user2;
    private User user3;

    private PropertyManager propertyManager;

    private Folder root;


    @Before
    public void setup() throws Exception {
        userLogin("admin");
        group1 = addGroup("group1", "");
        group2 = addGroup("group2", "");
        group3 = addGroup("group3", "");
        user1 = addUser("user1", "User1", "user1@example.com", "password");
        user2 = addUser("user2", "User2", "user2@example.com", "password");
        user3 = addUser("user3", "User3", "user3@example.com", "password");
        groupManager.addUserToGroup(group1, user1);
        groupManager.addUserToGroup(group2, user2);
        groupManager.addUserToGroup(group3, user3);

        propertyManager = new PropertyManager(userManager);

        root = userManager.getUserSession().getByEntryDn(new Folder(), Folder.FOLDER_ROOT);

        Folder folder1 = propertyManager.addFolder(createFolder("folder1"));
        Folder folder2 = propertyManager.addFolder(createFolder("folder2", folder1));
        Folder folder3 = propertyManager.addFolder(createFolder("folder3"));
        Folder folder4 = propertyManager.addFolder(createFolder("folder4", folder3));
    }

    @Test
    public void adminCanAddView() throws Exception {
        userLogin("admin");
        View view = createView("view1");
        userManager.getUserSession().add(view);

        view = userManager.getUserSession().get(view);

        assertEquals("view1", view.getName());
    }

    @Test
    public void userCanAddView() throws Exception {
        userLogin("user1");

        View view = createView("view1");
        userManager.getUserSession().add(view);

        view = userManager.getUserSession().get(view);

        assertNotNull(view);
        assertEquals("view1", view.getName());
    }

    @Test
    public void differentUsersCanAddViewWithTheSameName() throws Exception {
        userLogin("user1");

        View view = createView("view1");
        userManager.getUserSession().add(view);

        userLogin("user2");

        view = createView("view1");
        userManager.getUserSession().add(view);
    }

    @Test
    public void userCannotSeeViewsCreatedByOtherUserIfNotGivenPrivileges() throws Exception {
        userLogin("user1");

        View view = createView("view1");
        userManager.getUserSession().add(view);

        userLogin("user2");

        view = userManager.getUserSession().get(view);

        assertNull(view);
    }

    @Test
    public void userCanSeeViewCreatedByOtherUserIfGivenReadPrivileges() throws Exception {

    }

    @Test
    public void userCanWriteToViewCreatedByOtherUserIfGivenWritePrivileges() throws Exception {

    }

    @Test
    public void viewCanHaveViewpoints() throws Exception {
        userLogin("admin");

        View view = createView("view1");

        String viewPoints = "/folder1,/folder1/folder2";

        view.setViewPoints(viewPoints);

        userManager.getUserSession().add(view);

        view = userManager.getUserSession().get(view);

        assertEquals(viewPoints, view.getViewPoints());
    }

    @Test
    public void viewCanChangeOrderOfViewPoints() throws Exception {
        userLogin("admin");

        View view = createView("view1");

        String viewPoints = "/folder1,/folder1/folder2";

        view.setViewPoints(viewPoints);

        userManager.getUserSession().add(view);

        view = userManager.getUserSession().get(view);

        assertEquals(viewPoints, view.getViewPoints());

        viewPoints = "/folder1/folder2,/folder1";

        view.setViewPoints(viewPoints);

        userManager.getUserSession().update(view);

        view = userManager.getUserSession().get(view);

        assertEquals(viewPoints, view.getViewPoints());
    }



    private View createView(String name) throws Exception {
        View view = new View();

        view.setName(name);
        view.setParentDn(View.ROOT);

        view.setOwner(userManager.getAuthenticatedUserDn());

        return view;
    }

    private Folder createFolder(String name) {
        Folder folder = new Folder();
        folder.setName(name);
        folder.setParentDn(Folder.FOLDER_ROOT);

        return folder;
    }

    private Folder createFolder(String name, AbstractFolder parent) {
        Folder folder = new Folder();
        folder.setName(name);
        folder.setParentDn(parent.getDn());
        folder.setParentId(parent.getId());

        return folder;
    }
}
