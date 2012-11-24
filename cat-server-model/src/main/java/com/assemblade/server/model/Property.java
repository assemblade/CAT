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
import com.assemblade.opendj.model.AbstractStorable;
import com.assemblade.opendj.model.StorableDecorator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.opends.server.core.DirectoryServer;
import org.opends.server.types.Attribute;
import org.opends.server.types.AttributeType;
import org.opends.server.types.Entry;
import org.opends.server.types.Modification;
import org.opends.server.types.ObjectClass;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Property extends AbstractStorable implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private static Map<ObjectClass, String> objectClasses = new HashMap<ObjectClass, String>();

	static {
		objectClasses.put(DirectoryServer.getObjectClass("assemblade-property"), "assemblade-property");
	}
	
    private String name;
	private String value;
	private String description;

	public Property() {
	}

	public Property(String dn) {
		this.dn = dn;
	}
	
	public Property(String parentDn, String name, String value, String description) {
		this.parentDn = parentDn;
		this.name = name;
		this.value = value;
		this.description = description;
	}

	public String getRootDn() {
        return parentDn;
	}

    @Override
    public String getRDN() {
        return "cn=" + name;
    }

    public String getSearchFilter() {
		return "(objectClass=assemblade-property)";
	}
	
	public Collection<String> getAttributeNames() {
        Collection<String> attributeNames = super.getAttributeNames();

        attributeNames.addAll(Arrays.asList("cn", "assemblade-value", "description", "aclRights"));

        return attributeNames;
	}

	public Map<ObjectClass, String> getObjectClasses() {
		return objectClasses;
	}

	public Map<AttributeType, List<Attribute>> getUserAttributes() {
		Map<AttributeType, List<Attribute>> attributeMap = new HashMap<AttributeType, List<Attribute>>();

        LdapUtils.addSingleValueAttributeToMap(attributeMap, "cn", name);
        LdapUtils.addSingleValueAttributeToMap(attributeMap, "assemblade-value", value);
        LdapUtils.addSingleValueAttributeToMap(attributeMap, "description", description);
		return attributeMap;
	}
	
	public StorableDecorator getDecorator() {
        return new Decorator();
	}

    public boolean requiresRename(Entry currentEntry) {
        return !StringUtils.equals(name, LdapUtils.getSingleAttributeStringValue(currentEntry.getAttribute("cn")));
    }

    @Override
    public boolean requiresUpdate(Entry currentEntry) {
        Property currentProperty = (Property)getDecorator().decorate(currentEntry);
        return !StringUtils.equals(description, currentProperty.getDescription()) || !StringUtils.equals(value, currentProperty.getValue());
    }

    public List<Modification> getModifications(Entry currentEntry) {
        List<Modification> modifications = super.getModifications(currentEntry);
        LdapUtils.createSingleEntryModification(modifications, currentEntry, "description", description);
        LdapUtils.createSingleEntryModification(modifications, currentEntry, "assemblade-value", value);
        return modifications;
    }

    public String getName() {
		return name;
	}

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
		return value;
	}

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
		return description;
	}

    public void setDescription(String description) {
        this.description = description;
    }

    public String getChangeLogDescription() {
        return "Property[name = " + name + ", value = " + value + "]";
    }

    private class Decorator extends AbstractStorable.Decorator<Property> {
        @Override
        public Property newInstance() {
            return new Property();
        }

        @Override
        public Property decorate(Entry entry) {
            Property property = super.decorate(entry);

            property.name = LdapUtils.getSingleAttributeStringValue(entry.getAttribute("cn"));
            property.value = LdapUtils.getSingleAttributeStringValue(entry.getAttribute("assemblade-value"));
            property.description = LdapUtils.getSingleAttributeStringValue(entry.getAttribute("description"));

            return property;
        }
    }
}
