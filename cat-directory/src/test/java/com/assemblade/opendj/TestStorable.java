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
package com.assemblade.opendj;

import com.assemblade.opendj.model.AbstractStorable;
import com.assemblade.opendj.model.StorableDecorator;
import org.opends.server.core.DirectoryServer;
import org.opends.server.types.Attribute;
import org.opends.server.types.AttributeType;
import org.opends.server.types.Entry;
import org.opends.server.types.Modification;
import org.opends.server.types.ObjectClass;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestStorable extends AbstractStorable implements Serializable {
	private static final long serialVersionUID = 1L;

    public String rdn;
	public String searchFilter;
	
	public transient Map<ObjectClass, String> objectClasses = new HashMap<ObjectClass, String>();
	public transient Map<AttributeType, List<Attribute>> userAttributes = new HashMap<AttributeType, List<Attribute>>();
	public transient Map<AttributeType, List<Attribute>> operationalAttributes = new HashMap<AttributeType, List<Attribute>>();
	public transient List<Modification> modifications = new ArrayList<Modification>();

    public boolean recordChanges;
	
	public transient Entry entry;
	
	public TestStorable() {
	}
	
	public String getRootDn() {
        throw new UnsupportedOperationException();
	}

    @Override
    public String getRDN() {
        return rdn;
    }

    public String getSearchFilter() {
		return searchFilter;
	}
	
	public Map<ObjectClass, String> getObjectClasses() {
		return objectClasses;
	}

	public Map<AttributeType, List<Attribute>> getUserAttributes() {
		return userAttributes;
	}

	public Map<AttributeType, List<Attribute>> getOperationalAttributes() {
		return operationalAttributes;
	}

	public void addObjectClasses(String... objectClasses) {
		for (String objectClass : objectClasses) {
			this.objectClasses.put(DirectoryServer.getObjectClass(objectClass.toLowerCase()), objectClass);
		}
	}
	
	public void addUserAttribute(String name, String value) {
		LdapUtils.addSingleValueAttributeToMap(userAttributes, name, value);
	}

	public void addOperationalAttribute(String name, String value) {
		LdapUtils.addSingleValueAttributeToMap(operationalAttributes, name, value);
	}

    public void addAcis(List<String> acis) {
        LdapUtils.addMultipleValueAttributeToMap(operationalAttributes, "aci", acis);
    }

	public List<Modification> getModifications() {
		return modifications;
	}

	public String getAttributeValueAsString(String attributeName) {
		List<Attribute> attributes = userAttributes.get(DirectoryServer.getAttributeType(attributeName.toLowerCase()));
		if ((attributes != null) && (attributes.size() > 0)) {
			return attributes.get(0).iterator().next().toString();
		}
		return null;
	}

	public Integer getAttributeValueAsInteger(String attributeName) {
		return null;
	}

	public Boolean getAttributeValueAsBoolean(String attributeName) {
		return null;
	}
	
	public Collection<String> getAttributeNames() {
		return Arrays.asList("+", "*", "aclRights");
	}
	
	public StorableDecorator getDecorator() {
        return new Decorator();
	}

	public boolean recordChanges() {
		return recordChanges;
	}

    private class Decorator extends AbstractStorable.Decorator<TestStorable> {
        @Override
        public TestStorable newInstance() {
            return new TestStorable();
        }

        @Override
        public TestStorable decorate(Session session, Entry entry) {
            TestStorable storable = new TestStorable();
            storable.entry = entry;
            return storable;
        }
    }
}
