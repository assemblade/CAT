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
import com.assemblade.opendj.model.StorableDecorator;
import com.assemblade.opendj.permissions.EntryPermissions;
import org.opends.server.core.DirectoryServer;
import org.opends.server.types.Attribute;
import org.opends.server.types.AttributeValue;
import org.opends.server.types.Entry;

import java.text.MessageFormat;
import java.util.List;

public class GroupMember extends AbstractUser {
	private static final long serialVersionUID = 1L;
	
	private static final MessageFormat searchFilterFormat = new MessageFormat("(&(objectClass=inetOrgPerson)(isMemberOf={0}))");
	
	private Group group;
	private boolean administrator;

	public String getSearchFilter() {
		return searchFilterFormat.format(new Object[] {group.getDn()});
	}

	public StorableDecorator getDecorator() {
        return new Decorator();
	}
	
	public Group getGroup() {
		return group;
	}
	
	public void setGroup(Group group) {
		this.group = group;
	}
	
	public boolean isEnabled() {
		return true;
	}
	
	public boolean isAdministrator() {
		return administrator;
	}

	public void setAdministrator(boolean administrator) {
		this.administrator = administrator;
	}

    private class Decorator extends AbstractUser.Decorator<GroupMember> {
        @Override
        public GroupMember newInstance() {
            GroupMember groupMember = new GroupMember();
            groupMember.setGroup(group);
            return groupMember;
        }

        @Override
        public GroupMember decorate(Entry entry) {
            GroupMember member = super.decorate(entry);
            String adminGroupDn = "cn=admins," + group.getDn();
            List<Attribute> attributes = entry.getOperationalAttribute(DirectoryServer.getAttributeType("ismemberof"));
            if (attributes != null) {
                for (Attribute attribute : attributes) {
                    for (AttributeValue value : attribute) {
                        if (adminGroupDn.equals(value.getValue().toString())) {
                            member.administrator = true;
                        }
                    }
                }
            }
            EntryPermissions permissions = LdapUtils.getEntryPermissions(entry.getAttributes());
            if (permissions.canDelete()) {
                member.deletable = true;
            }
            return member;
        }
    }
}
