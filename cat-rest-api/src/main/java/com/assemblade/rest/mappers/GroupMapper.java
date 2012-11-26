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

public class GroupMapper {
    public Group toClient(com.assemblade.server.model.Group serverGroup) {
        Group group = new Group();

        group.setId(serverGroup.getId());
        group.setName(serverGroup.getDisplayName());
        group.setDescription(serverGroup.getDescription());
        group.setWritable(serverGroup.isWritable());
        group.setDeletable(serverGroup.isDeletable());
        group.setType(serverGroup.getType());

        return group;
    }

    public com.assemblade.server.model.Group toServer(Group group) {
        com.assemblade.server.model.Group serverGroup = new com.assemblade.server.model.Group();
        serverGroup.setId(group.getId());
        serverGroup.setParentDn(com.assemblade.server.model.Group.ROOT);
        serverGroup.setName(group.getName());
        serverGroup.setDescription(group.getDescription());

        return serverGroup;
    }
}
