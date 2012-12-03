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

import com.assemblade.client.model.Group;
import com.assemblade.server.security.AuthenticationHolder;

public class GroupMapper {
    public Group toClient(com.assemblade.server.model.Group serverGroup) {
        Group clientGroup = new Group();
        clientGroup.setUrl(AuthenticationHolder.getAuthentication().getBaseUrl() + "/groups/id/" + serverGroup.getId());
        clientGroup.setId(serverGroup.getId());
        clientGroup.setName(serverGroup.getDisplayName());
        clientGroup.setDescription(serverGroup.getDescription());
        clientGroup.setWritable(serverGroup.isWritable());
        clientGroup.setDeletable(serverGroup.isDeletable());
        clientGroup.setType(serverGroup.getType());

        return clientGroup;
    }

    public com.assemblade.server.model.Group toServer(Group clientGroup) {
        com.assemblade.server.model.Group serverGroup = new com.assemblade.server.model.Group();
        serverGroup.setId(clientGroup.getId());
        serverGroup.setParentDn(com.assemblade.server.model.Group.ROOT);
        serverGroup.setName(clientGroup.getName());
        serverGroup.setDescription(clientGroup.getDescription());

        return serverGroup;
    }
}
