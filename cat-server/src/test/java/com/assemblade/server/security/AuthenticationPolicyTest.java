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
package com.assemblade.server.security;

import com.assemblade.opendj.OpenDJTestRunner;
import com.assemblade.opendj.model.authentication.policy.PasswordPolicy;
import com.assemblade.server.AbstractUserManagementTest;
import com.assemblade.server.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(OpenDJTestRunner.class)
public class AuthenticationPolicyTest extends AbstractUserManagementTest {
    @Test
    public void adminCanAssignAPasswordPolicyToAUser() throws Exception {
        userLogin("admin");

        PasswordPolicy policy = new PasswordPolicy();
        policy.setName("Force Change");
        policy.setForceChangeOnReset(true);
        userManager.getUserSession().add(policy);

        User user = new User();
        user.setUserId("user");
        user.setFullName("User Name");
        user.setPassword("changeme");
        user.setAuthenticationPolicy("Force Change");

        user = userManager.addUser(user);

        assertEquals("Force Change", user.getAuthenticationPolicy());

        userManager.deleteUser(user.getId());
        userManager.getUserSession().delete(policy.getDn(), false);
    }
}
