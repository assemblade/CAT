/*
 * Copyright 2013 Mike Adamson
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
import org.junit.Test;

import static junit.framework.Assert.assertNotNull;

public class TokensTest extends AbstractApiTest {
    @Test
    public void adminCanGetAnAccessToken() throws Exception {
        Authentication authentication = login.login("admin", "password");

        Tokens tokens = new Tokens(authentication);

        Authentication token = tokens.getAccessToken();

        assertNotNull(token);
    }

    @Test
    public void adminCanDeleteAnAccessToken() throws Exception {
        Authentication authentication = login.login("admin", "password");

        Tokens tokens = new Tokens(authentication);

        Authentication token = tokens.getAccessToken();

        tokens.deleteAccessToken(token);
    }
}
