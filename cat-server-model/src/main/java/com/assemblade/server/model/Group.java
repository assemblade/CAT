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
import com.assemblade.opendj.acis.Permission;
import com.assemblade.opendj.acis.PermissionSubject;
import com.assemblade.opendj.acis.Subject;
import com.assemblade.opendj.acis.Target;
import com.assemblade.opendj.model.Storable;
import com.assemblade.opendj.model.StorableDecorator;
import org.apache.commons.collections.CollectionUtils;
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

public class Group extends AbstractFolder {
	private static final long serialVersionUID = 1L;

	public static final String ROOT = "ou=groups,dc=assemblade,dc=com";
	public static final String GLOBAL_ADMIN_DN = "cn=globaladmin,ou=groups,dc=assemblade,dc=com";
    public static final String GROUP_ADMIN_DN = "cn=groupadmin,cn=globaladmin,ou=groups,dc=assemblade,dc=com";
    public static final String USER_DN = "cn=user,ou=groups,dc=assemblade,dc=com";

    protected List<Storable> addMembers = new ArrayList<Storable>();
    protected List<Storable> deleteMembers = new ArrayList<Storable>();

    public Group() {
	}

	public Group(String dn) {
        super(dn);
	}
	
	public Group(String name, String description) {
        super(name, description);
	}

    public String getAdminGroupDn() {
        return "cn=admins," + getDn();
    }

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

    @Override
    public Map<AttributeType, List<Attribute>> getOperationalAttributes() {
        Map<AttributeType, List<Attribute>> attributeMap = new HashMap<AttributeType, List<Attribute>>();

        Subject groupsExclusion = new PermissionSubject("groupdn", Arrays.asList(GLOBAL_ADMIN_DN, "cn=admins," + getDn(), getDn()), "!=");
        Target allAttributes = new Target("targetattr", "=", "* || +");
        Permission deny = new Permission("deny", "all", groupsExclusion);
        AccessControlItem denyAci = new AccessControlItem("deny", Arrays.asList(allAttributes), Arrays.asList(deny));

        Subject groupsInclusion = new PermissionSubject("groupdn", Arrays.asList("cn=admins," + getDn()), "=");
        Target memberAttribute = new Target("targetattr", "=", "member");
        Permission allow = new Permission("allow", "write,delete", groupsInclusion);
        AccessControlItem allowAci = new AccessControlItem("allow", Arrays.asList(memberAttribute), Arrays.asList(allow));

        LdapUtils.addMultipleValueAttributeToMap(attributeMap, "aci", allowAci.toString(), denyAci.toString());

        return attributeMap;
    }

    @Override
    public Collection<String> getAttributeNames() {
        Collection<String> attributeNames = super.getAttributeNames();
        attributeNames.addAll(Arrays.asList("member", "isMemberOf"));
        return attributeNames;
    }

    @Override
    public List<Modification> getModifications(Entry currentEntry) {
        List<Modification> modifications = new ArrayList<Modification>();
        for (Storable newMember : addMembers) {
            modifications.add(LdapUtils.createMultipleEntryAddModification("member", newMember.getDn()));
        }
        for (Storable deleteMember : deleteMembers) {
            modifications.add(LdapUtils.createMultipleEntryDeleteModification("member", deleteMember.getDn()));
        }
        return modifications;
    }

    @Override
    public boolean requiresRename(Entry currentEntry) {
        return !getDecorator().decorate(currentEntry).getName().equals(name);
    }

    @Override
    public boolean requiresMove(Entry currentEntry) {
        return false;
    }

    @Override
    public boolean requiresUpdate(Entry currentEntry) {
        return CollectionUtils.isNotEmpty(addMembers) || CollectionUtils.isNotEmpty(deleteMembers);
    }

    @Override
    public String getPermissionsAttributes() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getRootPermissions() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getRDN() {
        return "cn=" + name;
    }

    @Override
    protected String getRootDn() {
        return ROOT;
    }

    @Override
    public boolean getIsFolder() {
        return false;
    }

    @Override
    public String getType() {
        if (getDn().equals(GLOBAL_ADMIN_DN)) {
            return "admingroup";
        } else if (getDn().equals(USER_DN)) {
            return "allusers";
        } else {
            return "group";
        }
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

    protected class Decorator extends AbstractFolder.Decorator<Group> {
        @Override
        public Group newInstance() {
            return new Group();
        }

        @Override
        public Group decorate(Entry entry) {
            Group group = super.decorate(entry);

            //TODO: This should be handled by an ACI
            if (group.getDn().equals(GLOBAL_ADMIN_DN) || group.getDn().equals(USER_DN)) {
                group.writable = false;
            }

            return group;
        }
    }
}
