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
import com.assemblade.client.model.PasswordPolicy;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class PoliciesTest extends AbstractApiTest {
    @Test
    public void getAuthenticationPoliciesTest() throws ClientException {
        List<AuthenticationPolicy> policyList = policies.getAuthenticationPolicies();

        assertTrue(policyList.size() > 0);
    }

    @Test
    public void deleteAuthenticationPolicy() throws ClientException {
        PasswordPolicy policy = new PasswordPolicy();
        policy.setName("Change on reset");
        policies.addAuthenticationPolicy(policy);
        policies.deleteAuthenticationPolicy(policy);
    }
}
