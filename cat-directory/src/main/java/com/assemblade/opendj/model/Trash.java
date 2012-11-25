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
package com.assemblade.opendj.model;

import com.assemblade.opendj.LdapUtils;
import com.assemblade.opendj.SequenceNumberGenerator;
import org.opends.server.core.DirectoryServer;
import org.opends.server.types.Attribute;
import org.opends.server.types.AttributeType;
import org.opends.server.types.Entry;
import org.opends.server.types.ObjectClass;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Trash extends AbstractStorable implements Serializable {
	private static final long serialVersionUID = 1L;

	private static Map<ObjectClass, String> objectClasses = new HashMap<ObjectClass, String>();

	static {
		objectClasses.put(DirectoryServer.getObjectClass("top"), "top");
		objectClasses.put(DirectoryServer.getObjectClass("assemblade-trash"), "assemblade-trash");
	}
	
	private String id;

    public Trash() {
	}

	public Trash(String parentDn) {
		id = SequenceNumberGenerator.getNextSequenceNumber();
		this.parentDn = parentDn;
	}
	
	public String constructDn() {
		return "cn=" + id + "," + getParentDn();
	}

    @Override
    public String getRDN() {
        throw new UnsupportedOperationException();
    }

    public String getSearchFilter() {
		return "(objectClass=assemblade-trash)";
	}
	
	public Collection<String> getAttributeNames() {
        Collection<String> attributeNames = super.getAttributeNames();
        attributeNames.addAll(Arrays.asList("cn"));
        return attributeNames;
	}
	
	public Map<ObjectClass, String> getObjectClasses() {
		return objectClasses;
	}

	public Map<AttributeType, List<Attribute>> getUserAttributes() {
		Map<AttributeType, List<Attribute>> attributeMap = new HashMap<AttributeType, List<Attribute>>();
        LdapUtils.addSingleValueAttributeToMap(attributeMap, "cn", id);
		return attributeMap;
	}

	public StorableDecorator<Trash> getDecorator() {
        return new Decorator();
	}

    private class Decorator extends AbstractStorable.Decorator<Trash> {
        @Override
        public Trash newInstance() {
            return new Trash();
        }

        @Override
        public Trash decorate(Entry entry) {
            Trash trash = super.decorate(entry);
            trash.parentDn = entry.getDN().getParent().toString();
            trash.id = LdapUtils.getSingleAttributeStringValue(entry.getAttribute("cn"));
            return trash;
        }
    }
}
