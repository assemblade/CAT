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
import com.assemblade.client.model.Group;
import com.assemblade.client.model.GroupUser;
import com.assemblade.client.model.User;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Groups extends AbstractClient {
    public Groups(Authentication authentication) {
        super(authentication);
    }

    public List<Group> getAllGroups() {
        GetMethod get = new GetMethod(baseUrl + "/groups");
        try {
            if (executeMethod(get) == 200) {
                try {
                    return mapper.readValue(get.getResponseBodyAsStream(), new TypeReference<List<Group>>(){});
                } catch (IOException e) {
                }
            }
        } finally {
            get.releaseConnection();
        }
        return new ArrayList<Group>();
    }

    public List<GroupUser> getUsersInGroup(String groupId) {
        GetMethod get = new GetMethod(baseUrl + "/groups/" + groupId + "/members");
        try {
            if (executeMethod(get) == 200) {
                try {
                    return mapper.readValue(get.getResponseBodyAsStream(), new TypeReference<List<GroupUser>>(){});
                } catch (IOException e) {
                }
            }
        } finally {
            get.releaseConnection();
        }
        return new ArrayList<GroupUser>();
    }

    public List<User> getUsersNotInGroup(String groupId) {
        GetMethod get = new GetMethod(baseUrl + "/groups/" + groupId + "/nonmembers");
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

    public Group addGroup(Group group) {
        PostMethod post = new PostMethod(baseUrl + "/groups");
        try {
            try {
                post.setRequestEntity(new StringRequestEntity(mapper.writeValueAsString(group), "application/json", null));
                if (executeMethod(post) == 200) {
                    return mapper.readValue(post.getResponseBodyAsStream(), Group.class);
                }
            } catch (IOException e) {
            }
        } finally {
            post.releaseConnection();
        }
        return null;
    }

    public Group updateGroup(Group group) {
        PutMethod put = new PutMethod(baseUrl + "/groups");
        try {
            try {
                put.setRequestEntity(new StringRequestEntity(mapper.writeValueAsString(group), "application/json", null));
                if (executeMethod(put) == 200) {
                    return mapper.readValue(put.getResponseBodyAsStream(), Group.class);
                }
            } catch (IOException e) {
            }
        } finally {
            put.releaseConnection();
        }
        return null;
    }

    public boolean deleteGroup(Group group) {
        DeleteMethod delete = new DeleteMethod(baseUrl + "/groups/" + group.getId());
        try {
            return executeMethod(delete) == 204;
        } finally {
            delete.releaseConnection();
        }
    }

}
