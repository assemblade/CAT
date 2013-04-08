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
package com.assemblade.rest.mappers;

import com.assemblade.client.model.GroupMember;
import com.assemblade.opendj.StorageException;
import com.assemblade.server.model.User;
import com.assemblade.server.security.AuthenticationHolder;

public class GroupMemberMapper {
    private final GroupMapper groupMapper;

    public GroupMemberMapper(GroupMapper groupMapper) {
        this.groupMapper = groupMapper;
    }

    public GroupMember toClient(com.assemblade.server.model.GroupMember serverGroupMember) {
        GroupMember clientGroupMember = new GroupMember();
        clientGroupMember.setUrl(AuthenticationHolder.getAuthentication().getBaseUrl() + "/groups/id/" + serverGroupMember.getGroup().getId() + "/members/id/" + serverGroupMember.getId());
        clientGroupMember.setId(serverGroupMember.getId());
        clientGroupMember.setGroup(groupMapper.toClient(serverGroupMember.getGroup()));
        clientGroupMember.setUserId(serverGroupMember.getUserId());
        clientGroupMember.setFullName(serverGroupMember.getFullName());
        clientGroupMember.setEmailAddress(serverGroupMember.getEmailAddress());
        clientGroupMember.setAdministrator(serverGroupMember.isAdministrator());
        clientGroupMember.setDeletable(serverGroupMember.isDeletable());

        return clientGroupMember;
    }

    public com.assemblade.server.model.GroupMember toServer(GroupMember clientGroupMember) throws StorageException {
        com.assemblade.server.model.GroupMember serverGroupMember = new com.assemblade.server.model.GroupMember();
        serverGroupMember.setId(clientGroupMember.getId());
        serverGroupMember.setGroup(groupMapper.toServer(clientGroupMember.getGroup()));
        serverGroupMember.setUserId(clientGroupMember.getUserId());
        serverGroupMember.setParentDn(User.ROOT);
        return serverGroupMember;
    }

}
