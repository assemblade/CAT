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
import org.opends.server.types.Entry;

public class UniqueCharactersPasswordValidator extends PasswordValidator {
    @Override
    public String getJavaClass() {
        return "org.opends.server.extensions.UniqueCharactersPasswordValidator";
    }

    @Override
    public ConfigurationDecorator<UniqueCharactersPasswordValidator> getDecorator() {
        return new Decorator();
    }

    private class Decorator extends PasswordValidator.Decorator<UniqueCharactersPasswordValidator> {
        @Override
        public UniqueCharactersPasswordValidator newInstance() {
            return new UniqueCharactersPasswordValidator();
        }

        @Override
        public UniqueCharactersPasswordValidator decorate(Entry entry) {
            UniqueCharactersPasswordValidator validator = super.decorate(entry);

            return validator;
        }
    }

}
