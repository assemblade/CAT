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
import com.assemblade.opendj.Session;
import com.assemblade.opendj.StorageException;
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
import org.opends.server.types.DN;
import org.opends.server.types.DirectoryException;
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
    protected String template;
    protected List<Group> readGroups = new ArrayList<Group>();
    protected List<Group> writeGroups = new ArrayList<Group>();
    protected boolean inherit = true;

    public abstract boolean canHaveChildren();
    public abstract String getPermissionsAttributes();
    public abstract String getRootPermissions();

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

    @Override
    public Map<ObjectClass, String> getObjectClasses() {
        Map<ObjectClass, String> objectClasses = super.getObjectClasses();
        objectClasses.put(DirectoryServer.getObjectClass("asb-folder"), "asb-folder");
        return objectClasses;
    }

    @Override
    public Collection<String> getAttributeNames() {
        Collection<String> attributeNames = super.getAttributeNames();

        attributeNames.addAll(Arrays.asList("cn", "description", "asb-type", "aclRights", "aci"));

        return attributeNames;
    }

    public Map<AttributeType, List<Attribute>> getUserAttributes() {
        Map<AttributeType, List<Attribute>> attributeMap = super.getUserAttributes();

        LdapUtils.addSingleValueAttributeToMap(attributeMap, "cn", name);
        LdapUtils.addSingleValueAttributeToMap(attributeMap, "description", description);
        LdapUtils.addSingleValueAttributeToMap(attributeMap, "asb-type", template);

        return attributeMap;
    }


    public Map<AttributeType, List<Attribute>> getOperationalAttributes() {
        Map<AttributeType, List<Attribute>> attributeMap = super.getOperationalAttributes();

        LdapUtils.addMultipleValueAttributeToMap(attributeMap, "aci", generatePermissionAcis());

        return attributeMap;
    }

    public List<Modification> getModifications(Session session, Entry currentEntry) throws StorageException {
        List<Modification> modifications = super.getModifications(session, currentEntry);
        LdapUtils.createSingleEntryModification(modifications, currentEntry, "asb-type", template);
        LdapUtils.createSingleEntryModification(modifications, currentEntry, "description", description);
        LdapUtils.createMultipleEntryModification(modifications, currentEntry, "aci", generatePermissionAcis());
        return modifications;
    }

    public boolean requiresRename(Session session, Entry currentEntry) throws StorageException {
        return !StringUtils.equals(name, LdapUtils.getSingleAttributeStringValue(currentEntry.getAttribute("cn")));
    }

    @Override
    public boolean requiresUpdate(Session session, Entry currentEntry) throws StorageException {
        Folder currentFolder = new Folder().getDecorator().decorate(session, currentEntry);
        return !StringUtils.equals(template, currentFolder.getTemplate())
                || !StringUtils.equals(description, currentFolder.getDescription())
                || (inherit != currentFolder.inherit)
                || !CollectionUtils.isEqualCollection(readGroups, currentFolder.readGroups)
                || !CollectionUtils.isEqualCollection(writeGroups, currentFolder.writeGroups);
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public List<Group> getReadGroups() {
        return readGroups;
    }

    public void setReadGroups(List<Group> readGroups) {
        this.readGroups = readGroups;
    }

    public List<Group> getWriteGroups() {
        return writeGroups;
    }

    public void setWriteGroups(List<Group> writeGroups) {
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

            List<String> readGroupDns = new ArrayList<String>();
            List<String> writeGroupDns = new ArrayList<String>();
            List<String> allowGroupDns = new ArrayList<String>();
            if (CollectionUtils.isNotEmpty(readGroups)) {
                for (Group readGroup : readGroups) {
                    readGroupDns.add(readGroup.getDn());
                }
            }
            if (CollectionUtils.isNotEmpty(writeGroups)) {
                for (Group writeGroup : writeGroups) {
                    writeGroupDns.add(writeGroup.getDn());
                }
            }
            allowGroupDns.addAll(readGroupDns);
            allowGroupDns.addAll(writeGroupDns);
            allowGroupDns.add(Group.GLOBAL_ADMIN_DN);

            Subject ownerSubject = new PermissionSubject("userdn", Arrays.asList(owner), "!=");
            Subject allowedGroupsSubject = new PermissionSubject("groupdn", allowGroupDns, "!=");
            Subject composite = new CompositeSubject(ownerSubject, allowedGroupsSubject, "and");
            Permission permission = new Permission("deny", getRootPermissions(), composite);
            AccessControlItem aci = new AccessControlItem("deny", targets, Arrays.asList(permission));
            acis.add(aci.toString());

            Permission allowOwner = new Permission("allow", "read,search,compare,delete,export,add,write,import", new PermissionSubject("userdn", Arrays.asList(owner), "="));
            acis.add(new AccessControlItem("owner", targets, Arrays.asList(allowOwner)).toString());

            if (CollectionUtils.isNotEmpty(readGroupDns)) {
                Permission allowRead = new Permission("allow", "read,search,compare,export", new PermissionSubject("groupdn", readGroupDns, "="));
                AccessControlItem allowReadAci = new AccessControlItem("read", targets, Arrays.asList(allowRead));
                acis.add(allowReadAci.toString());
            }

            if (CollectionUtils.isNotEmpty(writeGroupDns)) {
                Permission allowWrite = new Permission("allow", "read,search,compare,delete,export,add,write,import", new PermissionSubject("groupdn", writeGroupDns, "="));
                AccessControlItem allowWriteAci = new AccessControlItem("write", targets, Arrays.asList(allowWrite));
                acis.add(allowWriteAci.toString());
            }
        }

        return acis;
    }

    private String getPermissionScope() {
        return canHaveChildren() ? "subtree" : "base";
    }

    protected abstract class Decorator<T extends AbstractFolder> extends AbstractStorable.Decorator<T> {
        @Override
        public T decorate(Session session, Entry entry) throws StorageException {
            T folder = super.decorate(session, entry);

            folder.name = LdapUtils.getSingleAttributeStringValue(entry.getAttribute("cn"));
            folder.description = LdapUtils.getSingleAttributeStringValue(entry.getAttribute("description"));
            folder.template = LdapUtils.getSingleAttributeStringValue(entry.getAttribute("asb-type"));

            Collection<String> acis = LdapUtils.getMultipleAttributeStringValues(entry.getAttribute("aci"));

            if (acis.size() > 0) {
                folder.inherit = false;
                for (String aciText : acis) {
                    AccessControlItem aci = AciFactory.parse(aciText);
                    if (aci.getName().equals("owner")) {
                        folder.owner = aci.getPermissions().get(0).getSubject().getDns().get(0);
                    } else if (aci.getName().equals("read")) {
                        for (String dn : aci.getPermissions().get(0).getSubject().getDns()) {
                            try {
                                folder.readGroups.add(session.getByEntryDn(new Group(), dn));
                            } catch (StorageException e) {
                            }
                        }
                    } else if (aci.getName().equals("write")) {
                        for (String dn : aci.getPermissions().get(0).getSubject().getDns()) {
                            try {
                                folder.writeGroups.add(session.getByEntryDn(new Group(), dn));
                            } catch (StorageException e) {
                            }
                        }
                    }
                }
            }

            return folder;
        }
    }
}
