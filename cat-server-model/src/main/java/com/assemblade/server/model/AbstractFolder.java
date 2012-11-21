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
import com.assemblade.opendj.acis.AccessControlItem;
import com.assemblade.opendj.acis.AciFactory;
import com.assemblade.opendj.acis.CompositeSubject;
import com.assemblade.opendj.acis.Permission;
import com.assemblade.opendj.acis.PermissionSubject;
import com.assemblade.opendj.acis.Subject;
import com.assemblade.opendj.acis.Target;
import com.assemblade.opendj.model.AbstractStorable;
import com.assemblade.utils.localisation.Localiser;
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
import java.util.List;
import java.util.Map;

public abstract class AbstractFolder extends AbstractStorable {
    protected String name;
    protected String description;
    protected String owner;
    protected List<String> readGroups = new ArrayList<String>();
    protected List<String> writeGroups = new ArrayList<String>();
    protected boolean inherit = true;

    public abstract String getType();
    public abstract boolean getIsFolder();
    public abstract String getPermissionsAttributes();
    public abstract String getRootPermissions();

    public AbstractFolder() {
    }

    public AbstractFolder(String dn) {
        super(dn);
    }

    public AbstractFolder(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getRDN() {
        return "cn=" + name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDisplayName() {
        return Localiser.getInstance().translate(getName());
    }

    @Override
    public Map<ObjectClass, String> getObjectClasses() {
        Map<ObjectClass, String> objectClasses = super.getObjectClasses();
        objectClasses.put(DirectoryServer.getObjectClass("assemblade-folder"), "assemblade-folder");
        return objectClasses;
    }

    @Override
    public Collection<String> getAttributeNames() {
        Collection<String> attributeNames = super.getAttributeNames();

        attributeNames.addAll(Arrays.asList("cn", "description", "aclRights", "aci"));

        return attributeNames;
    }

    public Map<AttributeType, List<Attribute>> getUserAttributes() {
        Map<AttributeType, List<Attribute>> attributeMap = super.getUserAttributes();

        LdapUtils.addSingleValueAttributeToMap(attributeMap, "cn", name);
        LdapUtils.addSingleValueAttributeToMap(attributeMap, "description", description);

        return attributeMap;
    }


    public Map<AttributeType, List<Attribute>> getOperationalAttributes() {
        Map<AttributeType, List<Attribute>> attributeMap = super.getOperationalAttributes();

        LdapUtils.addMultipleValueAttributeToMap(attributeMap, "aci", generatePermissionAcis());

        return attributeMap;
    }

    public List<Modification> getModifications(Entry currentEntry) {
        List<Modification> modifications = super.getModifications(currentEntry);
        LdapUtils.createSingleEntryModification(modifications, currentEntry, "description", description);
        LdapUtils.createMultipleEntryReplaceModification(modifications, "aci", generatePermissionAcis());
        return modifications;
    }

    public boolean requiresRename(Entry currentEntry) {
        return !StringUtils.equals(name, LdapUtils.getSingleAttributeStringValue(currentEntry.getAttribute("cn")));
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public List<String> getReadGroups() {
        return readGroups;
    }

    public void setReadGroups(List<String> readGroups) {
        this.readGroups = readGroups;
    }

    public List<String> getWriteGroups() {
        return writeGroups;
    }

    public void setWriteGroups(List<String> writeGroups) {
        this.writeGroups = writeGroups;
    }

    public boolean isInherit() {
        return inherit;
    }

    public void setInherit(boolean inherit) {
        this.inherit = inherit;
    }

    protected List<String> generatePermissionAcis() {
        List<String> acis = new ArrayList<String>();

        if (!inherit) {
            //TODO The permissions could do with moving into a logic bean and having some serious testing round them
            List<Target> targets = new ArrayList<Target>();
            targets.add(new Target("targetscope", "=", getPermissionScope()));
            targets.add(new Target("targetattr", "=", getPermissionsAttributes()));

            List<String> allowGroups = new ArrayList<String>();
            if (CollectionUtils.isNotEmpty(readGroups)) {
                allowGroups.addAll(readGroups);
            }
            if (CollectionUtils.isNotEmpty(writeGroups)) {
                allowGroups.addAll(writeGroups);
            }
            allowGroups.add(Group.GLOBAL_ADMIN_DN);

            Subject ownerSubject = new PermissionSubject("userdn", Arrays.asList(owner), "!=");
            Subject allowedGroupsSubject = new PermissionSubject("groupdn", allowGroups, "!=");
            Subject composite = new CompositeSubject(ownerSubject, allowedGroupsSubject, "and");
            Permission permission = new Permission("deny", getRootPermissions(), composite);
            AccessControlItem aci = new AccessControlItem("deny", targets, Arrays.asList(permission));
            acis.add(aci.toString());

            Permission allowOwner = new Permission("allow", "read,search,compare,delete,export,add,write,import", new PermissionSubject("userdn", Arrays.asList(owner), "="));
            acis.add(new AccessControlItem("owner", targets, Arrays.asList(allowOwner)).toString());

            if (CollectionUtils.isNotEmpty(readGroups)) {
                Permission allowRead = new Permission("allow", "read,search,compare,export", new PermissionSubject("groupdn", readGroups, "="));
                AccessControlItem allowReadAci = new AccessControlItem("read", targets, Arrays.asList(allowRead));
                acis.add(allowReadAci.toString());
            }

            if (CollectionUtils.isNotEmpty(writeGroups)) {
                Permission allowWrite = new Permission("allow", "read,search,compare,delete,export,add,write,import", new PermissionSubject("groupdn", writeGroups, "="));
                AccessControlItem allowWriteAci = new AccessControlItem("write", targets, Arrays.asList(allowWrite));
                acis.add(allowWriteAci.toString());
            }
        }

        return acis;
    }

    private String getPermissionScope() {
        return getIsFolder() ? "subtree" : "base";
    }

    protected abstract class Decorator<T extends AbstractFolder> extends AbstractStorable.Decorator<T> {
        @Override
        public T decorate(Entry entry) {
            T folder = super.decorate(entry);

            folder.name = LdapUtils.getSingleAttributeStringValue(entry.getAttribute("cn"));
            folder.description = LdapUtils.getSingleAttributeStringValue(entry.getAttribute("description"));

            Collection<String> acis = LdapUtils.getMultipleAttributeStringValues(entry.getAttribute("aci"));

            if (acis.size() > 0) {
                folder.inherit = false;
                for (String aciText : acis) {
                    AccessControlItem aci = AciFactory.parse(aciText);
                    if (aci.getName().equals("owner")) {
                        folder.owner = aci.getPermissions().get(0).getSubject().getDns().get(0);
                    } else if (aci.getName().equals("read")) {
                        folder.readGroups.addAll(aci.getPermissions().get(0).getSubject().getDns());
                    } else if (aci.getName().equals("write")) {
                        folder.writeGroups.addAll(aci.getPermissions().get(0).getSubject().getDns());
                    }
                }
            }

            return folder;
        }
    }
}
