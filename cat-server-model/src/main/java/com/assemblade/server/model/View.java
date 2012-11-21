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

    private static final String VIEWS_ROOT = "cn=views,dc=assemblade,dc=com";

    private String sequence;
    private String viewPoints;

    public View() {
        sequence = SequenceNumberGenerator.getNextSequenceNumber();
        inherit = false;
    }

    @Override
    public String getRDN() {
        return "asb-sequence=" + sequence;
    }

    public String getType() {
        return "view";
    }

    public String getIcon() {
        return "classy/application.png";
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
    protected String getRootDn() {
        return VIEWS_ROOT;
    }

    @Override
    public String getSearchFilter() {
        return "(objectClass=assemblade-view)";
    }

    public Collection<String> getAttributeNames() {
        Collection<String> attributeNames = super.getAttributeNames();
        attributeNames.addAll(Arrays.asList("asb-sequence", "asb-view-point"));
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
        Map<AttributeType, List<Attribute>> attributeMap = new HashMap<AttributeType, List<Attribute>>();

        LdapUtils.addSingleValueAttributeToMap(attributeMap, "asb-sequence", sequence);
        LdapUtils.addSingleValueAttributeToMap(attributeMap, "cn", name);
        LdapUtils.addSingleValueAttributeToMap(attributeMap, "description", description);

        if (StringUtils.isNotEmpty(viewPoints)) {
            LdapUtils.addMultipleValueAttributeToMap(attributeMap, "asb-view-point", getViewPointDns());
        }

        return attributeMap;
    }

    public List<Modification> getModifications(Entry currentEntry) {
        View currentView = (View)getDecorator().decorate(currentEntry);
        List<Modification> modifications = new ArrayList<Modification>();

        if (!currentView.name.equals(name)) {
            LdapUtils.createSingleEntryModification(modifications, currentEntry, "cn", name);
        }
        if (!StringUtils.equals(currentView.description, description)) {
            LdapUtils.createSingleEntryModification(modifications, currentEntry, "description", description);
        }

        for (String viewPointDn : currentView.getViewPointDns()) {
            modifications.add(LdapUtils.createMultipleEntryDeleteModification("asb-view-point", viewPointDn));
        }

        for (String viewPointDn : getViewPointDns()) {
            modifications.add(LdapUtils.createMultipleEntryAddModification("asb-view-point", viewPointDn));
        }

        Collection<String> newAcis = generatePermissionAcis();

        LdapUtils.createMultipleEntryReplaceModification(modifications, "aci", newAcis);

        return modifications;
    }

    @Override
    public StorableDecorator<View> getDecorator() {
        return new Decorator();
    }

    @Override
    public boolean requiresRename(Entry currentEntry) {
        return false;
    }

    public boolean requiresMove(Entry currentEntry) {
        return false;
    }

    public boolean requiresUpdate(Entry currentEntry) {
        View current = getDecorator().decorate(currentEntry);
        return !StringUtils.equals(description, current.getDescription()) || !StringUtils.equals(viewPoints, current.viewPoints) || (inherit != current.inherit) || !CollectionUtils.isEqualCollection(readGroups, current.readGroups) || !CollectionUtils.isEqualCollection(writeGroups, current.writeGroups);
    }


    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public String getViewPoints() {
        return viewPoints;
    }

    public void setViewPoints(String viewPoints) {
        this.viewPoints = viewPoints;
    }

    public boolean recordChanges() {
        return false;
    }

    public boolean recordDeletions() {
        return false;
    }

    private List<String> getViewPointDns() {
        List<String> viewPointDns = new ArrayList<String>();

        for (String viewPoint : viewPoints.split(",")) {

            String[] rdns = viewPoint.split("/");

            String viewPointDn = Folder.FOLDER_ROOT;

            for (String rdn : rdns) {
                if (StringUtils.isNotEmpty(rdn)) {
                    viewPointDn = "cn=" + rdn + "," + viewPointDn;
                }
            }

            viewPointDns.add(viewPointDn);
        }
        return viewPointDns;
    }

    private class Decorator extends AbstractFolder.Decorator<View> {
        @Override
        public View newInstance() {
            return new View();
        }

        @Override
        public View decorate(Entry entry) {
            View view = super.decorate(entry);

            view.sequence = LdapUtils.getSingleAttributeStringValue(entry.getAttribute("asb-sequence"));
            Collection<String> viewPointDns = LdapUtils.getMultipleAttributeStringValues(entry.getAttribute("asb-view-point"));

            String viewPoints = "";

            for (String viewPointDn : viewPointDns) {
                String viewPoint = "";
                int index = viewPointDn.indexOf(Folder.FOLDER_ROOT);
                if (index > 0) {
                    viewPointDn = viewPointDn.substring(0, index - 1);
                    String[] rdns = viewPointDn.split(",");
                    for (String rdn : rdns) {
                        viewPoint = "/" + rdn.substring(rdn.indexOf("=") + 1) + viewPoint;
                    }
                }
                if (viewPoints.length() > 0) {
                    viewPoints += ",";
                }
                viewPoints += viewPoint;
            }

            view.viewPoints = viewPoints;
            return view;
        }
    }
}
