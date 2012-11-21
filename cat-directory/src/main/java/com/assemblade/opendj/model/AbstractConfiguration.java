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

import com.assemblade.opendj.AssembladeErrorCode;
import com.assemblade.opendj.LdapUtils;
import com.assemblade.opendj.StorageException;
import com.assemblade.opendj.authentication.policy.LdapPassthroughAuthenticationPolicy;
import com.assemblade.opendj.authentication.policy.PasswordPolicy;
import com.assemblade.opendj.authentication.validation.AttributeValuePasswordValidator;
import com.assemblade.opendj.authentication.validation.CharacterSetPasswordValidator;
import com.assemblade.opendj.authentication.validation.DictionaryPasswordValidator;
import com.assemblade.opendj.authentication.validation.LengthBasedPasswordValidator;
import com.assemblade.opendj.authentication.validation.RepeatedCharactersPasswordValidator;
import com.assemblade.opendj.authentication.validation.SimilarityBasedPasswordValidator;
import com.assemblade.opendj.authentication.validation.UniqueCharactersPasswordValidator;
import org.apache.commons.lang.StringUtils;
import org.opends.server.types.Attribute;
import org.opends.server.types.AttributeType;
import org.opends.server.types.DN;
import org.opends.server.types.DirectoryException;
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

public abstract class AbstractConfiguration implements Configuration, Serializable {
    private static final long serialVersionUID = 1L;

    protected String dn;
    protected String name;

    private static Map<String, Configuration> configurationItems = new HashMap<String, Configuration>();

    static {
        configurationItems.put("ds-cfg-password-policy", new PasswordPolicy());
        configurationItems.put("ds-cfg-ldap-pass-through-authentication-policy", new LdapPassthroughAuthenticationPolicy());
        configurationItems.put("ds-cfg-attribute-value-password-validator", new AttributeValuePasswordValidator());
        configurationItems.put("ds-cfg-character-set-password-validator", new CharacterSetPasswordValidator());
        configurationItems.put("ds-cfg-dictionary-password-validator", new DictionaryPasswordValidator());
        configurationItems.put("ds-cfg-length-based-password-validator", new LengthBasedPasswordValidator());
        configurationItems.put("ds-cfg-repeated-characters-password-validator", new RepeatedCharactersPasswordValidator());
        configurationItems.put("ds-cfg-similarity-based-password-validator", new SimilarityBasedPasswordValidator());
        configurationItems.put("ds-cfg-unique-characters-password-validator", new UniqueCharactersPasswordValidator());
    }

    public static ConfigurationDecorator getDecorator(Entry currentEntry) {
        for (String objectClass : currentEntry.getObjectClasses().values()) {
            if (configurationItems.containsKey(objectClass.toLowerCase())) {
                return configurationItems.get(objectClass.toLowerCase()).getDecorator();
            }
        }
        return null;
    }

    public AbstractConfiguration() {
    }

    public AbstractConfiguration(String dn) {
        this.dn = dn;
    }

    @Override
    public DN getDN() throws StorageException {
        try {
            return DN.decode(getDn());
        } catch (DirectoryException e) {
            throw new StorageException(AssembladeErrorCode.ASB_9999);
        }
    }

    @Override
    public Map<ObjectClass, String> getObjectClasses() {
        Map<ObjectClass, String> objectClasses = new HashMap<ObjectClass, String>();

        return objectClasses;
    }

    @Override
    public Collection<String> getAttributeNames() {
        return new ArrayList<String>(Arrays.asList("cn", "ds-cfg-java-class", "ds-cfg-enabled"));
    }

    @Override
    public Map<AttributeType, List<Attribute>> getUserAttributes() {
        Map<AttributeType, List<Attribute>> attributeMap = new HashMap<AttributeType, List<Attribute>>();

        LdapUtils.addSingleValueAttributeToMap(attributeMap, "cn", name);
        LdapUtils.addSingleValueAttributeToMap(attributeMap, "ds-cfg-java-class", getJavaClass());

        return attributeMap;
    }

    @Override
    public List<Modification> getModifications(Entry currentEntry) {
        List<Modification> modifications = new ArrayList<Modification>();

        return modifications;
    }

    @Override
    public String getRDN() {
        return "cn=" + name;
    }

    @Override
    public String getDn() {
        if (StringUtils.isNotEmpty(dn)) {
            return dn;
        }
        return getRDN() + "," + getRootDn();
    }

    @Override
    public boolean requiresRename(Entry currentEntry) {
        return !StringUtils.equals(name, LdapUtils.getSingleAttributeStringValue(currentEntry.getAttribute("cn")));
    }

    @Override
    public boolean requiresUpdate(Entry currentEntry) {
        return false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    protected abstract class Decorator<T extends AbstractConfiguration> implements ConfigurationDecorator<T> {
        @Override
        public T decorate(Entry entry) {
            T configuration = newInstance();

            configuration.name = LdapUtils.getSingleAttributeStringValue(entry.getAttribute("cn"));

            return configuration;
        }
    }
}
