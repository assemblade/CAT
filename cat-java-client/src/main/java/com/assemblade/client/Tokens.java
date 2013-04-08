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
import org.codehaus.jackson.type.TypeReference;

import java.util.List;

public class Tokens extends AbstractClient {
    public Tokens(Authentication authentication) {
        super(authentication);
    }

    public List<Authentication> getAllAccessTokens() throws ClientException {
        return get("/token/all", new TypeReference<List<Authentication>>() {});
    }

    public Authentication getAccessToken() throws ClientException {
        return get("/token", new TypeReference<Authentication>() {});
    }

    public void deleteAccessToken(Authentication token) throws ClientException {
        delete("/token/" + token.getToken());
    }
}
