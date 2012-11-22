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

    public List<User> getAllUsers() {
        GetMethod get = new GetMethod(baseUrl + "/users");
        try {
            if (executeMethod(get) == 200) {
                try {
                    return mapper.readValue(get.getResponseBodyAsStream(), new TypeReference<List<User>>(){});
                } catch (IOException e) {
                }
            }
        } finally {
            get.releaseConnection();
        }
        return new ArrayList<User>();
    }

    public User getAuthenticatedUser() {
        GetMethod get = new GetMethod(baseUrl + "/users/current");
        try {
            if (executeMethod(get) == 200) {
                try {
                    return mapper.readValue(get.getResponseBodyAsStream(), User.class);
                } catch (IOException e) {
                }
            }
        } finally {
            get.releaseConnection();
        }
        return null;
    }

    public User addUser(User user) {
        PostMethod post = new PostMethod(baseUrl + "/users");
        try {
            try {
                post.setRequestEntity(new StringRequestEntity(mapper.writeValueAsString(user), "application/json", null));
                if (executeMethod(post) == 200) {
                    return mapper.readValue(post.getResponseBodyAsStream(), User.class);
                }
            } catch (IOException e) {
            }
        } finally {
            post.releaseConnection();
        }
        return null;
    }

    public User updateUser(User user) {
        PutMethod put = new PutMethod(baseUrl + "/users");
        try {
            try {
                put.setRequestEntity(new StringRequestEntity(mapper.writeValueAsString(user), "application/json", null));
                if (executeMethod(put) == 200) {
                    return mapper.readValue(put.getResponseBodyAsStream(), User.class);
                }
            } catch (IOException e) {
            }
        } finally {
            put.releaseConnection();
        }
        return null;
    }

    public boolean deleteUser(String userId) {
        try {
            DeleteMethod delete = new DeleteMethod(baseUrl + "/users/" + URIUtil.encode(userId, URI.allowed_fragment));
            try {
                return executeMethod(delete) == 204;
            } finally {
                delete.releaseConnection();
            }
        } catch (URIException e) {
        }
        return false;
    }
}
