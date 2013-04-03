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
package com.assemblade.client.scenarios.users;

import com.assemblade.client.AbstractApiTest;
import com.assemblade.client.ChangePasswordException;
import com.assemblade.client.ClientException;
import com.assemblade.client.model.Authentication;
import com.assemblade.client.model.PasswordPolicy;
import com.assemblade.client.model.User;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class UserWithForceResetPolicyHasToChangePasswordBeforeLoggingIn extends AbstractApiTest {
    @Test
    public void userCanChangeTheirPassword() throws ClientException {
        PasswordPolicy policy = policies.getLocalPasswordPolicy();
        policy.setForceChangeOnReset(true);
        policies.updateLocalPasswordPolicy(policy);

        User user = new User();
        user.setUserId("test");
        user.setFullName("Test User");
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
