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
package com.assemblade.client;

import com.assemblade.client.model.Authentication;
import com.assemblade.client.model.LdapPassthroughPolicy;
import com.assemblade.client.model.PasswordPolicy;
import org.codehaus.jackson.type.TypeReference;

public class Policies extends AbstractClient {
    public Policies(Authentication authentication) {
        super(authentication);
    }

    public PasswordPolicy getLocalPasswordPolicy() throws ClientException {
        return get("/policies/local", new TypeReference<PasswordPolicy>() {});
    }

    public LdapPassthroughPolicy getRemoteAuthenticationPolicy() throws ClientException {
        return get("/policies/remote", new TypeReference<LdapPassthroughPolicy>() {
        });
    }

    public PasswordPolicy updateLocalPasswordPolicy(PasswordPolicy policy) throws ClientException {
        return update("/policies/local", policy, new TypeReference<PasswordPolicy>() {});
    }

    public LdapPassthroughPolicy updateRemoteAuthenticationPolicy(LdapPassthroughPolicy policy) throws ClientException {
        return update("/policies/remote", policy, new TypeReference<LdapPassthroughPolicy>() {});
    }
}
