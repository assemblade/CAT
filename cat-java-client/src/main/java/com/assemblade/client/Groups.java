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
import com.assemblade.client.model.GroupMember;
import com.assemblade.client.model.User;
import org.codehaus.jackson.type.TypeReference;

import java.util.List;

public class Groups extends AbstractClient {
    public Groups(Authentication authentication) {
        super(authentication);
    }

    public List<Group> getAllGroups() throws ClientException {
        return get("/groups", new TypeReference<List<Group>>() {});
    }

    public Group getAdministratorGroup() throws ClientException {
        return get("/groups/administrator", new TypeReference<Group>() {});
    }

    public List<GroupMember> getGroupMembers(Group group) throws ClientException {
        return get("/groups/" + group.getId() + "/members", new TypeReference<List<GroupMember>>() {});
    }

    public GroupMember addMemberToGroup(GroupMember groupMember) throws ClientException {
        return add("/groups/" + groupMember.getGroup().getId() + "/members", groupMember, new TypeReference<GroupMember>() {});
    }

    public GroupMember editGroupMember(GroupMember groupMember) throws ClientException {
        return update("/groups/" + groupMember.getGroup().getId() + "/members/" + groupMember.getId(), groupMember, new TypeReference<GroupMember>() {});
    }

    public void removeMemberFromGroup(GroupMember groupMember) throws ClientException {
        delete("/groups/" + groupMember.getGroup().getId() + "/members/" + groupMember.getId());
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
