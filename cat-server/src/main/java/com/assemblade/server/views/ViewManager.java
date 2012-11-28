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
package com.assemblade.server.views;

import com.assemblade.opendj.StorageException;
import com.assemblade.server.model.Folder;
import com.assemblade.server.model.Property;
import com.assemblade.server.model.View;
import com.assemblade.server.properties.PropertyManager;
import com.assemblade.server.users.UserManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewManager {
    private final UserManager userManager;
    private final PropertyManager propertyManager;

    public ViewManager(UserManager userManager, PropertyManager propertyManager) {
        this.userManager = userManager;
        this.propertyManager = propertyManager;
    }

    public View addView(View view) throws StorageException {
        userManager.getUserSession().add(view);
        return userManager.getUserSession().get(view);
    }

    public List<View> getViews() throws StorageException {
        return userManager.getUserSession().search(new View(), userManager.getViewsDn(), false).getEntries();
    }

    public View updateView(View view) throws StorageException {
        userManager.getUserSession().update(view);

        return userManager.getUserSession().get(view);
    }

    public void deleteView(String viewId) throws StorageException {
        View view = userManager.getUserSession().getByEntryId(new View(), viewId);
        userManager.getUserSession().delete(view, true);
    }

    public List<Property> getProperties(String viewId) throws StorageException {
        Map<String, Property> propertyMap = new HashMap<String, Property>();

        View view = userManager.getUserSession().getByEntryId(new View(), viewId);

        for (Folder folder : view.getFolders()) {
            for (Property property : propertyManager.getProperties(folder.getId())) {
                propertyMap.put(property.getName(), property);
            }
        }

        return new ArrayList<Property>(propertyMap.values());
    }
}
