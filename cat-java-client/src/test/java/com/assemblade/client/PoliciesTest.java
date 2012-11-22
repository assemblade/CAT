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

public class PoliciesTest extends AbstractApiTest {
    private Policies test;

    @Before
    public void setup() throws Exception {
        Authentication authentication  = login.login("admin", "password");
        test = new Policies(authentication);
    }

    @Test
    public void getAuthenticationPoliciesTest() {
        List<AuthenticationPolicy> policies = test.getAuthenticationPolicies();

        System.out.println();
    }

    @Test
    public void deleteAuthenticationPolicy() {
        PasswordPolicy policy = new PasswordPolicy();
        policy.setName("Change on reset");
        test.addAuthenticationPolicy(policy);
        test.deleteAuthenticationPolicy("Change on reset");
    }
}
