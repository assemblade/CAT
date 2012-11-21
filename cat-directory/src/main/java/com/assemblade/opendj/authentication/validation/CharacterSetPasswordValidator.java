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

package com.assemblade.opendj.authentication.validation;

import com.assemblade.opendj.model.ConfigurationDecorator;
import org.opends.server.core.DirectoryServer;
import org.opends.server.types.Entry;
import org.opends.server.types.ObjectClass;

import java.util.Map;

public class CharacterSetPasswordValidator extends PasswordValidator {
    @Override
    public String getJavaClass() {
        return "org.opends.server.extensions.CharacterSetPasswordValidator";
    }

    @Override
    public ConfigurationDecorator<CharacterSetPasswordValidator> getDecorator() {
        return new Decorator();
    }

    @Override
    public Map<ObjectClass, String> getObjectClasses() {
        Map<ObjectClass, String> objectClasses = super.getObjectClasses();

        objectClasses.put(DirectoryServer.getObjectClass("ds-cfg-character-set-password-validator"), "ds-cfg-character-set-password-validator");

        return objectClasses;
    }

    private class Decorator extends PasswordValidator.Decorator<CharacterSetPasswordValidator> {
        @Override
        public CharacterSetPasswordValidator newInstance() {
            return new CharacterSetPasswordValidator();
        }

        @Override
        public CharacterSetPasswordValidator decorate(Entry entry) {
            CharacterSetPasswordValidator validator = super.decorate(entry);

            return validator;
        }
    }
}
