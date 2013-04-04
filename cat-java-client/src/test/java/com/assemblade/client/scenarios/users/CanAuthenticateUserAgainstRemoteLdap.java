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
import com.assemblade.client.model.LdapPassthroughPolicy;
import com.assemblade.client.model.User;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class CanAuthenticateUserAgainstRemoteLdap extends AbstractApiTest {
    @Test
    public void userCanAuthenticateAgainstRemoteLdap() throws Exception {
        LdapPassthroughPolicy policy = policies.getRemoteAuthenticationPolicy();
        policy.setPrimaryRemoteServer("localhost:1389");
        policy.setSearchBase("ou=users,dc=example,dc=com");
        policy.setBindDn("cn=AdminUser");
        policy.setBindPassword("password");
        policy.setSearchAttribute("uid");
        policy.setNameAttribute("cn");
        policy.setMailAttribute("mail");

        policy = policies.updateRemoteAuthenticationPolicy(policy);

        assertEquals("uid", policy.getSearchAttribute());
        assertEquals("cn", policy.getNameAttribute());
        assertEquals("mail", policy.getMailAttribute());

        User user = createRemoteUser("test1");

        user = users.addUser(user);

        assertEquals("Test1 User", user.getFullName());
        assertEquals("test1@example.com", user.getEmailAddress());
        assertTrue(user.isRemoteUser());

        assertNotNull(login.login("test1", "password"));
    }
}
