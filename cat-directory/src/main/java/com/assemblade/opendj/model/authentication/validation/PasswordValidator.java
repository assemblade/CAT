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

package com.assemblade.opendj.model.authentication.validation;

import com.assemblade.opendj.model.AbstractConfiguration;
import com.assemblade.opendj.LdapUtils;
import org.opends.server.core.DirectoryServer;
import org.opends.server.types.Attribute;
import org.opends.server.types.AttributeType;
import org.opends.server.types.Entry;
import org.opends.server.types.ObjectClass;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public abstract class PasswordValidator extends AbstractConfiguration {
    private static final long serialVersionUID = 1L;

    private static final String ROOT_DN = "cn=Password Validators,cn=config";

    protected boolean enabled;

    public PasswordValidator() {
        super();
    }

    public PasswordValidator(String dn) {
        super(dn);
    }

    @Override
    public String getRootDn() {
        return ROOT_DN;
    }

    @Override
    public String getSearchFilter() {
        return "(objectClass=ds-cfg-password-validator)";
    }

    @Override
    public Map<ObjectClass, String> getObjectClasses() {
        Map<ObjectClass, String> objectClasses = super.getObjectClasses();
        objectClasses.put(DirectoryServer.getObjectClass("ds-cfg-password-validator"), "ds-cfg-password-validator");
        return objectClasses;
    }

    @Override
    public Collection<String> getAttributeNames() {
        Collection<String> attributeNames = super.getAttributeNames();
        attributeNames.addAll(Arrays.asList("ds-cfg-enabled"));
        return attributeNames;
    }

    @Override
    public Map<AttributeType, List<Attribute>> getUserAttributes() {
        Map<AttributeType, List<Attribute>> attributeMap = super.getUserAttributes();

        LdapUtils.addSingleValueAttributeToMap(attributeMap, "ds-cfg-enabled", enabled);

        return attributeMap;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    protected abstract class Decorator<T extends PasswordValidator> extends AbstractConfiguration.Decorator<T> {
        @Override
        public T decorate(Entry entry) {
            T validator = super.decorate(entry);

            validator.enabled = LdapUtils.getSingleAttributeBooleanValue(entry.getAttribute("ds-cfg-enabled"));

            return validator;
        }
    }
}
