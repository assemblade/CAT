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
import com.assemblade.client.model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ChangePasswordTest extends AbstractApiTest {
    private Policies policies;
    private Users users;

    @Before
    public void setup() throws Exception {
        Authentication authentication = login.login("admin", "password");
        policies = new Policies(authentication);

        for (AuthenticationPolicy policy : policies.getAuthenticationPolicies()) {
            if (!policy.getName().startsWith("Default")) {
                policies.deleteAuthenticationPolicy(policy);
            }
        }

        users = new Users(authentication);

        for (User user: users.getAllUsers()) {
            if (user.isDeletable()) {
                users.deleteUser(user);
            }
        }
    }

    @After
    public void teardown() throws Exception {
        for (User user: users.getAllUsers()) {
            if (user.isDeletable()) {
                users.deleteUser(user);
            }
        }
        for (AuthenticationPolicy policy : policies.getAuthenticationPolicies()) {
            if (!policy.getName().startsWith("Default")) {
                policies.deleteAuthenticationPolicy(policy);
            }
        }
    }

    @Test
    public void userCanChangeTheirPassword() throws ClientException {
        PasswordPolicy policy = new PasswordPolicy();
        policy.setName("Change on reset");
        policy.setForceChangeOnReset(true);
        policies.addAuthenticationPolicy(policy);

        User user = new User();
        user.setUserId("test");
        user.setFullName("Test User");
        user.setAuthenticationPolicy("Change On Reset");
        user.setPassword("changeme");
        user = users.addUser(user);

        assertNotNull(user);

        try {
            login.login("test", "changeme");
            fail();
        } catch (ChangePasswordException e) {
            boolean changed = login.changePassword("test", "changeme", "password");

            assertTrue(changed);
        }

        try {
            Authentication authentication = login.login("test", "password");
            assertNotNull(authentication);
        } catch (ChangePasswordException e) {
            fail();
        }
    }

}
