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

import com.assemblade.opendj.LdapUtils;
import com.assemblade.opendj.SequenceNumberGenerator;
import com.assemblade.opendj.model.StorableDecorator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.opends.server.core.DirectoryServer;
import org.opends.server.types.Attribute;
import org.opends.server.types.AttributeType;
import org.opends.server.types.DN;
import org.opends.server.types.DirectoryException;
import org.opends.server.types.Entry;
import org.opends.server.types.Modification;
import org.opends.server.types.ObjectClass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class View extends AbstractFolder {
    private static final long serialVersionUID = 1L;

    private List<Folder> folders;

    public View() {
    }

    public String getType() {
        return "view";
    }

    @Override
    public boolean getIsFolder() {
        return false;
    }

    @Override
    public String getPermissionsAttributes() {
        return "objectclass || entryUUID || cn || description || asb-view-point || aci || aclRights";
    }

    @Override
    public String getRootPermissions() {
        return "read,search,add";
    }

    @Override
    public String getSearchFilter() {
        return "(objectClass=assemblade-view)";
    }

    public Collection<String> getAttributeNames() {
        Collection<String> attributeNames = super.getAttributeNames();
        attributeNames.addAll(Arrays.asList("asb-view-point"));
        return attributeNames;
    }

    @Override
    public Map<ObjectClass, String> getObjectClasses() {
        Map<ObjectClass, String> objectClasses = super.getObjectClasses();
        objectClasses.put(DirectoryServer.getObjectClass("assemblade-view"), "assemblade-view");
        return objectClasses;
    }

    @Override
    public Map<AttributeType, List<Attribute>> getUserAttributes() {
        Map<AttributeType, List<Attribute>> attributeMap = super.getUserAttributes();

        LdapUtils.addMultipleValueAttributeToMap(attributeMap, "asb-view-point", getViewPoints());

        return attributeMap;
    }

    public List<Modification> getModifications(Entry currentEntry) {
        List<Modification> modifications = super.getModifications(currentEntry);

        LdapUtils.createMultipleEntryModification(modifications, currentEntry, "asb-view-point", getViewPoints());

        return modifications;
    }

    @Override
    public StorableDecorator<View> getDecorator() {
        return new Decorator();
    }

    public boolean requiresMove(Entry currentEntry) {
        return false;
    }

    public boolean requiresUpdate(Entry currentEntry) {
        if (!super.requiresUpdate(currentEntry)) {
            View currentView = getDecorator().decorate(currentEntry);
            return !CollectionUtils.isEqualCollection(folders, currentView.folders);
        }
        return true;
    }

    public List<Folder> getFolders() {
        return folders;
    }

    public void setFolders(List<Folder> folders) {
        this.folders = folders;
    }

    private List<String> getViewPoints() {
        List<String> viewPoints = new ArrayList<String>();
        for (Folder folder : folders) {
            viewPoints.add(folder.getDn());
        }
        return viewPoints;
    }

    private class Decorator extends AbstractFolder.Decorator<View> {
        @Override
        public View newInstance() {
            return new View();
        }

        @Override
        public View decorate(Entry entry) {
            View view = super.decorate(entry);

            Collection<String> viewPoints = LdapUtils.getMultipleAttributeStringValues(entry.getAttribute("asb-view-point"));

            List<Folder> folders = new ArrayList<Folder>();

            StorableDecorator<Folder> decorator = new Folder().getDecorator();

            for (String viewPoint : viewPoints) {
                try {
                    folders.add(decorator.decorate(DirectoryServer.getEntry(DN.decode(viewPoint))));
                } catch (DirectoryException e) {
                }
            }

            view.setFolders(folders);
            return view;
        }
    }
}
