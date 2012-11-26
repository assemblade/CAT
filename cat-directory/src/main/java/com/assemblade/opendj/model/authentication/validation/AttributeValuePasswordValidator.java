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

import com.assemblade.opendj.model.ConfigurationDecorator;
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

public class AttributeValuePasswordValidator extends PasswordValidator {

    private boolean testReversedPassword;

    public AttributeValuePasswordValidator() {
    }

    public AttributeValuePasswordValidator(String dn) {
        super(dn);
    }

    @Override
    public String getJavaClass() {
        return "org.opends.server.extensions.AttributeValuePasswordValidator";
    }

    @Override
    public Map<ObjectClass, String> getObjectClasses() {
        Map<ObjectClass, String> objectClasses = super.getObjectClasses();

        objectClasses.put(DirectoryServer.getObjectClass("ds-cfg-attribute-value-password-validator"), "ds-cfg-attribute-value-password-validator");

        return objectClasses;
    }

    @Override
    public Collection<String> getAttributeNames() {
        Collection<String> attributeNames = super.getAttributeNames();

        attributeNames.addAll(Arrays.asList("ds-cfg-test-reversed-password", "ds-cfg-match-attribute"));

        return attributeNames;
    }

    @Override
    public Map<AttributeType, List<Attribute>> getUserAttributes() {
        Map<AttributeType, List<Attribute>> attributeMap = super.getUserAttributes();

        LdapUtils.addSingleValueAttributeToMap(attributeMap, "ds-cfg-test-reversed-password", testReversedPassword);

        return attributeMap;
    }



    @Override
    public ConfigurationDecorator<AttributeValuePasswordValidator> getDecorator() {
        return new Decorator();
    }

    private class Decorator extends PasswordValidator.Decorator<AttributeValuePasswordValidator> {
        @Override
        public AttributeValuePasswordValidator newInstance() {
            return new AttributeValuePasswordValidator();
        }

        @Override
        public AttributeValuePasswordValidator decorate(Entry entry) {
            AttributeValuePasswordValidator validator = super.decorate(entry);

            validator.testReversedPassword = LdapUtils.getSingleAttributeBooleanValue(entry.getAttribute("ds-cfg-test-reversed-password"));

            return validator;
        }
    }

}
