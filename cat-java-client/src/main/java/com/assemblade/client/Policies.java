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
import com.assemblade.client.model.AuthenticationPolicy;
import com.assemblade.client.model.User;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.util.URIUtil;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Policies extends AbstractClient {
    public Policies(Authentication authentication) {
        super(authentication);
    }

    public List<AuthenticationPolicy> getAuthenticationPolicies() throws ClientException {
        return get("/policies", new TypeReference<List<AuthenticationPolicy>>() {});
    }

    public AuthenticationPolicy addAuthenticationPolicy(AuthenticationPolicy policy) throws ClientException {
        return add("/policies", policy, new TypeReference<AuthenticationPolicy>() {});
    }

    public AuthenticationPolicy updateAuthenticationPolicy(AuthenticationPolicy policy) throws ClientException {
        try {
            return add("/policies/name/" + URIUtil.encode(policy.getName(), URI.allowed_fragment), policy, new TypeReference<AuthenticationPolicy>() {});
        } catch (URIException e) {
            throw new CallFailedException("Failed to encode request path", e);
        }
    }

    public void deleteAuthenticationPolicy(AuthenticationPolicy policy) throws ClientException {
        try {
            delete("/policies/name/" + URIUtil.encode(policy.getName(), URI.allowed_fragment));
        } catch (URIException e) {
            throw new CallFailedException("Failed to encode request path", e);
        }
    }
}
