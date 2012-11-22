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

    public List<AuthenticationPolicy> getAuthenticationPolicies() {
        GetMethod get = new GetMethod(baseUrl + "/policies");
        if (executeMethod(get) == 200) {
            try {
                return mapper.readValue(get.getResponseBodyAsStream(), new TypeReference<List<AuthenticationPolicy>>(){});
            } catch (IOException e) {
            }
        }
        return new ArrayList<AuthenticationPolicy>();
    }

    public AuthenticationPolicy addAuthenticationPolicy(AuthenticationPolicy policy) {
        PostMethod post = new PostMethod(baseUrl + "/policies");
        try {
            post.setRequestEntity(new StringRequestEntity(mapper.writeValueAsString(policy), "application/json", null));
            if (executeMethod(post) == 200) {
                return mapper.readValue(post.getResponseBodyAsStream(), AuthenticationPolicy.class);
            }
        } catch (IOException e) {
        }
        return null;
    }

    public AuthenticationPolicy updateAuthenticationPolicy(AuthenticationPolicy policy) {
        PutMethod put = new PutMethod(baseUrl + "/policies");
        try {
            put.setRequestEntity(new StringRequestEntity(mapper.writeValueAsString(policy), "application/json", null));
            if (executeMethod(put) == 200) {
                return mapper.readValue(put.getResponseBodyAsStream(), AuthenticationPolicy.class);
            }
        } catch (IOException e) {
        }
        return null;
    }

    public boolean deleteAuthenticationPolicy(String policyName) {
        try {
            DeleteMethod delete = new DeleteMethod(baseUrl + "/policies/" + URIUtil.encode(policyName, URI.allowed_fragment));
            return executeMethod(delete) == 204;
        } catch (URIException e) {
        }
        return false;
    }
}
