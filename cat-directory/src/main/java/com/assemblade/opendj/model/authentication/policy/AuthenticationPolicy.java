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

package com.assemblade.opendj.model.authentication.policy;

import com.assemblade.opendj.model.AbstractConfiguration;
import org.opends.server.core.DirectoryServer;
import org.opends.server.types.Entry;
import org.opends.server.types.ObjectClass;

import java.util.Map;

public abstract class AuthenticationPolicy extends AbstractConfiguration {
    private static final long serialVersionUID = 1L;

    private static final String ROOT_DN = "cn=Password Policies,cn=config";

    public AuthenticationPolicy() {
        super();
    }

    public AuthenticationPolicy(String dn) {
        super(dn);
    }

    @Override
    public String getRootDn() {
        return ROOT_DN;
    }

    @Override
    public String getSearchFilter() {
        return "(objectClass=ds-cfg-authentication-policy)";
    }

    @Override
    public Map<ObjectClass, String> getObjectClasses() {
        Map<ObjectClass, String> objectClasses = super.getObjectClasses();
        objectClasses.put(DirectoryServer.getObjectClass("ds-cfg-authentication-policy"), "ds-cfg-authentication-policy");
        return objectClasses;
    }

    protected abstract class Decorator<T extends AuthenticationPolicy> extends AbstractConfiguration.Decorator<T> {
        @Override
        public T decorate(Entry entry) {
            T policy = super.decorate(entry);
            return policy;
        }
    }
}
