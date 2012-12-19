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
import com.assemblade.opendj.acis.AccessControlItem;
import com.assemblade.opendj.acis.CompositeSubject;
import com.assemblade.opendj.acis.Permission;
import com.assemblade.opendj.acis.PermissionSubject;
import com.assemblade.opendj.acis.Subject;
import com.assemblade.opendj.acis.Target;
import com.assemblade.opendj.model.AbstractStorable;
import com.assemblade.opendj.model.Storable;
import com.assemblade.opendj.model.StorableDecorator;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Group extends AbstractStorable {
	private static final long serialVersionUID = 1L;

	public static final String ROOT = "ou=groups,dc=assemblade,dc=com";
	public static final String GLOBAL_ADMIN_DN = "cn=globaladmin,ou=groups,dc=assemblade,dc=com";
    public static final String GROUP_ADMIN_DN = "cn=groupadmin,ou=groups,dc=assemblade,dc=com";
    public static final String USER_DN = "cn=user,ou=groups,dc=assemblade,dc=com";

    private String groupId;
    private String name;
    private String description;
    protected List<Storable> addMembers = new ArrayList<Storable>();
    protected List<Storable> deleteMembers = new ArrayList<Storable>();

	public StorableDecorator<Group> getDecorator() {
        return new Decorator();
	}

    @Override
    public String getSearchFilter() {
        return "(objectClass=groupOfNames)";
    }

    @Override
    public Map<ObjectClass, String> getObjectClasses() {
        Map<ObjectClass, String> objectClasses = new HashMap<ObjectClass, String>();
        objectClasses.put(DirectoryServer.getObjectClass("groupofnames"), "groupOfNames");
        return objectClasses;
    }

    public Map<AttributeType, List<Attribute>> getUserAttributes() {
        Map<AttributeType, List<Attribute>> attributeMap = super.getUserAttributes();

        LdapUtils.addSingleValueAttributeToMap(attributeMap, "cn", groupId);
        LdapUtils.addSingleValueAttributeToMap(attributeMap, "description", encodeDescription());

        return attributeMap;
    }

    @Override
    public Map<AttributeType, List<Attribute>> getOperationalAttributes() {
        Map<AttributeType, List<Attribute>> attributeMap = new HashMap<AttributeType, List<Attribute>>();

        Subject groupsExclusion = new PermissionSubject("groupdn", Arrays.asList(GLOBAL_ADMIN_DN, getDn()), "!=");
        Target allAttributes = new Target("targetattr", "=", "* || +");
        Permission deny = new Permission("deny", "all", groupsExclusion);
        AccessControlItem denyAci = new AccessControlItem("deny", Arrays.asList(allAttributes), Arrays.asList(deny));

        Subject groupAdminInclusion = new PermissionSubject("groupdn", Arrays.asList(GROUP_ADMIN_DN), "=");
        Subject groupInclusion = new PermissionSubject("groupdn", Arrays.asList(getDn()), "=");
        Subject bothGroupsInclusion = new CompositeSubject(groupAdminInclusion, groupInclusion, "AND");
        Target memberAttribute = new Target("targetattr", "=", "member");
        Permission allow = new Permission("allow", "write,delete", bothGroupsInclusion);
        AccessControlItem allowAci = new AccessControlItem("allow", Arrays.asList(memberAttribute), Arrays.asList(allow));

        LdapUtils.addMultipleValueAttributeToMap(attributeMap, "aci", allowAci.toString(), denyAci.toString());

        return attributeMap;
    }

    @Override
    public Collection<String> getAttributeNames() {
        Collection<String> attributeNames = super.getAttributeNames();

        attributeNames.addAll(Arrays.asList("cn", "description", "aclRights", "aci", "member", "isMemberOf"));
        return attributeNames;
    }

    @Override
    public List<Modification> getModifications(Session session, Entry currentEntry) {
        List<Modification> modifications = super.getModifications(session, currentEntry);

        LdapUtils.createSingleEntryModification(modifications, currentEntry, "description", encodeDescription());

        for (Storable newMember : addMembers) {
            modifications.add(LdapUtils.createMultipleEntryAddModification("member", newMember.getDn()));
        }
        addMembers.clear();
        for (Storable deleteMember : deleteMembers) {
            modifications.add(LdapUtils.createMultipleEntryDeleteModification("member", deleteMember.getDn()));
        }
        deleteMembers.clear();
        return modifications;
    }

    @Override
    public boolean requiresUpdate(Session session, Entry currentEntry) {
        Group currentGroup = getDecorator().decorate(session, currentEntry);
        return !StringUtils.equals(name, currentGroup.getName()) || !StringUtils.equals(description, currentGroup.getDescription()) || CollectionUtils.isNotEmpty(addMembers) || CollectionUtils.isNotEmpty(deleteMembers);
    }

    @Override
    public String getRDN() {
        return "cn=" + groupId;
    }

    public String getType() {
        if (getDn().equals(GLOBAL_ADMIN_DN)) {
            return "admins";
        } else if (getDn().equals(GROUP_ADMIN_DN)) {
            return "groupadmins";
        } else if (getDn().equals(USER_DN)) {
            return "users";
        } else {
            return "group";
        }
	}

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
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

    public void addMember(Storable member) {
        addMembers.add(member);
    }

    public void deleteMember(Storable member) {
        deleteMembers.add(member);
    }

    @Override
	public String toString() {
		return "Group [dn=" + getDn() + "]";
	}

    protected class Decorator extends AbstractStorable.Decorator<Group> {
        @Override
        public Group newInstance() {
            return new Group();
        }

        @Override
        public Group decorate(Session session, Entry entry) {
            Group group = super.decorate(session, entry);
            group.groupId = LdapUtils.getSingleAttributeStringValue(entry.getAttribute("cn"));
            String nameDescription = LdapUtils.getSingleAttributeStringValue(entry.getAttribute("description"));
            if (nameDescription.contains("/")) {
                group.name = nameDescription.substring(0, nameDescription.indexOf('/'));
                group.description = nameDescription.substring(nameDescription.indexOf('/') + 1, nameDescription.length());
            }
            group.writable = group.getType().equals("group");
            return group;
        }
    }

    private String encodeDescription() {
        if (StringUtils.isNotEmpty(description)) {
            return name + "/" + description;
        } else {
            return name;
        }
    }
}
