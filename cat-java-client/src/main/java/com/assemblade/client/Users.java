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

public class Users extends AbstractClient {
    public Users(Authentication authentication) {
        super(authentication);
    }

    public List<User> getAllUsers() throws ClientException {
        return get("/users", new TypeReference<List<User>>(){});
    }

    public User getAuthenticatedUser() throws ClientException {
        return get("/users/current", new TypeReference<User>() {});
    }

    public User addUser(User user) throws ClientException {
        return add("/users", user, new TypeReference<User>() {});
    }

    public User updateUser(User user) throws ClientException {
        try {
            return add("/users/" + URIUtil.encode(user.getUserId(), URI.allowed_fragment), user, new TypeReference<User>() {});
        } catch (URIException e) {
            throw new CallFailedException("Failed to encode request path", e);
        }
    }

    public void deleteUser(User user) throws ClientException {
        try {
            delete("/users/" + URIUtil.encode(user.getUserId(), URI.allowed_fragment));
        } catch (URIException e) {
            throw new CallFailedException("Failed to encode request path", e);
        }
    }
}
