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
import com.assemblade.opendj.permissions.AttributePermissions;
import com.assemblade.opendj.permissions.EntryPermissions;
import com.assemblade.opendj.permissions.PermissionsFactory;
import org.apache.commons.lang.StringUtils;
import org.opends.server.core.DirectoryServer;
import org.opends.server.types.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class LdapUtils {
    public static void createSingleEntryModification(List<Modification> modifications, Entry currentEntry, String attributeName, String value) {
        AttributeType type = DirectoryServer.getAttributeType(attributeName);
        AttributeBuilder builder = new AttributeBuilder(type);
        if (currentEntry.hasAttribute(type)) {
            if (StringUtils.isEmpty(value)) {
                modifications.add(new Modification(ModificationType.DELETE, builder.toAttribute()));
            } else {
                String oldValue = getSingleAttributeStringValue(currentEntry.getAttribute(attributeName));
                if (!StringUtils.equals(oldValue, value)) {
                    builder.add(value);
                    modifications.add(new Modification(ModificationType.REPLACE, builder.toAttribute()));
                }
            }
        } else {
            if (StringUtils.isNotEmpty(value)) {
                builder.add(value);
                modifications.add(new Modification(ModificationType.ADD, builder.toAttribute()));
            }
        }
    }

    public static void createMultipleEntryModifications(List<Modification> modifications, String attributeName, Collection<String> deleteEntries, Collection<String> addEntries) {
        AttributeType type = DirectoryServer.getAttributeType(attributeName);
        if (deleteEntries.size() > 0) {
            AttributeBuilder builder = new AttributeBuilder(type);
            for (String deleteEntry : deleteEntries) {
                builder.add(deleteEntry);
            }
            modifications.add(new Modification(ModificationType.DELETE, builder.toAttribute()));
        }
        if (addEntries.size() > 0) {
            AttributeBuilder builder = new AttributeBuilder(type);
            for (String addEntry : addEntries) {
                builder.add(addEntry);
            }
            modifications.add(new Modification(ModificationType.ADD, builder.toAttribute()));
        }
    }

    public static void createMultipleEntryModification(List<Modification> modifications, Entry currentEntry, String attributeName, Collection<String> entries) {
        AttributeType type = DirectoryServer.getAttributeType(attributeName);
        AttributeBuilder builder = new AttributeBuilder(type);
        if (currentEntry.hasAttribute(type)) {
            if (entries.size() > 0) {
                for (String addEntry : entries) {
                    builder.add(addEntry);
                }
                modifications.add(new Modification(ModificationType.REPLACE, builder.toAttribute()));
            } else {
                modifications.add(new Modification(ModificationType.DELETE, builder.toAttribute()));
            }
        } else {
            if (entries.size() > 0) {
                for (String addEntry : entries) {
                    builder.add(addEntry);
                }
                modifications.add(new Modification(ModificationType.ADD, builder.toAttribute()));
            }
        }
    }

    public static Modification createMultipleEntryAddModification(String attributeName, String value) {
        AttributeType type = DirectoryServer.getAttributeType(attributeName);
        AttributeBuilder builder = new AttributeBuilder(type);
        builder.add(value);
        return new Modification(ModificationType.ADD, builder.toAttribute());
    }

    public static Modification createMultipleEntryDeleteModification(String attributeName, String value) {
        AttributeType type = DirectoryServer.getAttributeType(attributeName);
        AttributeBuilder builder = new AttributeBuilder(type);
        builder.add(value);
        return new Modification(ModificationType.DELETE, builder.toAttribute());
    }

    public static void addSingleValueAttributeToMap(Map<AttributeType, List<Attribute>> attributeMap, String name, String value) {
        if (StringUtils.isNotEmpty(value)) {
            AttributeType type = DirectoryServer.getAttributeType(name.toLowerCase());
            AttributeBuilder builder = new AttributeBuilder(type);
            if (StringUtils.isNotEmpty(value)) {
                builder.add(value);
            }
            List<Attribute> attributeList = new ArrayList<Attribute>();
            attributeList.add(builder.toAttribute());
            attributeMap.put(type, attributeList);
        }
    }

    public static void addMultipleValueAttributeToMap(Map<AttributeType, List<Attribute>> attributeMap, String name, List<String> values) {
        if (values.size() > 0) {
            AttributeType type = DirectoryServer.getAttributeType(name.toLowerCase());
            AttributeBuilder builder = new AttributeBuilder(type);
            for (String value : values) {
                builder.add(value);
            }
            List<Attribute> attributeList = new ArrayList<Attribute>();
            attributeList.add(builder.toAttribute());
            attributeMap.put(type, attributeList);
        }
    }

    public static void addMultipleValueAttributeToMap(Map<AttributeType, List<Attribute>> attributeMap, String name, String... values) {
        if (values.length > 0) {
            AttributeType type = DirectoryServer.getAttributeType(name.toLowerCase());
            AttributeBuilder builder = new AttributeBuilder(type);
            for (String value : values) {
                builder.add(value);
            }
            List<Attribute> attributeList = new ArrayList<Attribute>();
            attributeList.add(builder.toAttribute());
            attributeMap.put(type, attributeList);
        }
    }

    public static void addSingleValueAttributeToMap(Map<AttributeType, List<Attribute>> attributeMap, String name, boolean value) {
        AttributeType type = DirectoryServer.getAttributeType(name.toLowerCase());
        AttributeBuilder builder = new AttributeBuilder(type);
        builder.add(value ? "true" : "false");
        List<Attribute> attributeList = new ArrayList<Attribute>();
        attributeList.add(builder.toAttribute());
        attributeMap.put(type, attributeList);
    }

    public static void addSingleValueAttributeToMap(Map<AttributeType, List<Attribute>> attributeMap, String name, byte[] value) {
        AttributeType type = DirectoryServer.getAttributeType(name.toLowerCase());
        AttributeBuilder builder = new AttributeBuilder(type);
        builder.add(AttributeValues.create(type, new ByteStringBuilder().append(value).toByteString()));
        List<Attribute> attributeList = new ArrayList<Attribute>();
        attributeList.add(builder.toAttribute());
        attributeMap.put(type, attributeList);
    }

    public static void applyDefaultProperties(AbstractStorable storable, Entry entry) {
        storable.setId(getSingleAttributeStringValue(entry.getAttribute("entryuuid")));
    }

    public static String getSingleAttributeStringValue(List<Attribute> attributes) {
        if ((attributes != null) && (attributes.size() == 1)) {
            for (AttributeValue value :	attributes.get(0)) {
                return value.toString();
            }
        }
        return null;
    }

    public static boolean getSingleAttributeBooleanValue(List<Attribute> attributes) {
        if ((attributes != null) && (attributes.size() == 1)) {
            for (AttributeValue value :	attributes.get(0)) {
                return value.toString().equals("true");
            }
        }
        return false;
    }

    public static byte[] getSingleAttributeByteValue(List<Attribute> attributes) {
        if ((attributes != null) && (attributes.size() == 1)) {
            for (AttributeValue value :	attributes.get(0)) {
                return value.getValue().toByteArray();
            }
        }
        return null;
    }

    public static Collection<String> getMultipleAttributeStringValues(List<Attribute> attributes) {
        List<String> values = new ArrayList<String>();

        if (attributes != null) {
            for (Attribute attribute : attributes) {
                for (AttributeValue value : attribute) {
                    values.add(value.toString());
                }
            }
        }

        return values;
    }

    public static EntryPermissions getEntryPermissions(Collection<Attribute> attributes) {
        for (Attribute attribute : attributes) {
            List<String> values = new ArrayList<String>();
            for (AttributeValue value : attribute) {
                values.add(value.getValue().toString());
            }
            if (attribute.getName().startsWith("aclRights;entryLevel")) {
                return PermissionsFactory.createEntryPermissions(values.get(0));
            }
        }
        return new EntryPermissions(false, false, false, false, false);
    }

    public static AttributePermissions getAttributePermissions(Collection<Attribute> attributes, String attributeName) {
        for (Attribute attribute : attributes) {
            List<String> values = new ArrayList<String>();
            for (AttributeValue value : attribute) {
                values.add(value.getValue().toString());
            }
            if (attribute.getName().startsWith("aclRights;attributeLevel"))  {
                String[] parts = attribute.getName().split(";");
                AttributePermissions permissions = PermissionsFactory.createAttributePermissions(parts[2], values.get(0));
                if (permissions.getAttributeName().equals(attributeName)) {
                    return permissions;
                }
            }
        }
        return null;
    }

}
