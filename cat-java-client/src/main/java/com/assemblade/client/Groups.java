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

    public List<Group> getAllGroups() throws ClientException {
        return get("/groups", new TypeReference<List<Group>>() {});
    }

    public List<GroupUser> getUsersInGroup(Group group) throws ClientException {
        return get("/groups/" + group.getId() + "/members", new TypeReference<List<GroupUser>>() {});
    }

    public List<User> getUsersNotInGroup(Group group) throws ClientException {
        return get("/groups/" + group.getId() + "/nonmembers", new TypeReference<List<User>>() {});
    }

    public Group addGroup(Group group) throws ClientException {
        return add("/groups", group, new TypeReference<Group>() {});
    }

    public Group updateGroup(Group group) throws ClientException {
        return update("/groups/" + group.getId(), group, new TypeReference<Group>() {});
    }

    public void deleteGroup(Group group) throws ClientException {
        delete("/groups/" + group.getId());
    }

}
