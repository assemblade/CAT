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

import com.assemblade.client.model.Folder;
import com.assemblade.client.model.Group;
import com.assemblade.opendj.StorageException;
import com.assemblade.server.properties.PropertyManager;
import com.assemblade.server.security.AuthenticationHolder;
import com.assemblade.server.users.UserManager;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class FolderMapper {
    private final UserManager userManager;
    private final PropertyManager propertyManager;
    private final GroupMapper groupMapper;

    public FolderMapper(UserManager userManager, PropertyManager propertyManager, GroupMapper groupMapper) {
        this.userManager = userManager;
        this.propertyManager = propertyManager;
        this.groupMapper = groupMapper;
    }

    public Folder toClient(com.assemblade.server.model.Folder serverFolder) throws StorageException {
        Folder clientFolder = new Folder();
        clientFolder.setUrl(AuthenticationHolder.getAuthentication().getBaseUrl() + "/folders/" + serverFolder.getId());
        clientFolder.setId(serverFolder.getId());
        clientFolder.setName(serverFolder.getName());
        clientFolder.setDescription(serverFolder.getDescription());
        if (!userManager.getUserSession().dnFromId(serverFolder.getParentId()).equals(com.assemblade.server.model.Folder.FOLDER_ROOT)) {
            clientFolder.setParent(toClient(propertyManager.getFolder(serverFolder.getParentId())));
        }
        clientFolder.setAddable(serverFolder.isAddable());
        clientFolder.setWritable(serverFolder.isWritable());
        clientFolder.setDeletable(serverFolder.isDeletable());
        List<Group> readGroups = new ArrayList<Group>();
        for (com.assemblade.server.model.Group group : serverFolder.getReadGroups()) {
            readGroups.add(groupMapper.toClient(group));
        }
        if (readGroups.size() > 0) {
            clientFolder.setReadGroups(readGroups);
        }
        List<Group> writeGroups = new ArrayList<Group>();
        for (com.assemblade.server.model.Group group : serverFolder.getWriteGroups()) {
            writeGroups.add(groupMapper.toClient(group));
        }
        if (writeGroups.size() > 0) {
            clientFolder.setWriteGroups(writeGroups);
        }
        return clientFolder;
    }

    public com.assemblade.server.model.Folder toServer(Folder folder) throws StorageException {
        com.assemblade.server.model.Folder mappedFolder = new com.assemblade.server.model.Folder();
        if (folder.getParent() == null) {
            mappedFolder.setParentDn(com.assemblade.server.model.Folder.FOLDER_ROOT);
        } else {
            mappedFolder.setParentDn(userManager.getUserSession().dnFromId(folder.getParent().getId()));
        }
        mappedFolder.setParentId(folder.getParent() == null ? null : folder.getParent().getId());
        mappedFolder.setId(folder.getId());
        mappedFolder.setName(folder.getName());
        mappedFolder.setDescription(folder.getDescription());
        if (folder.getReadGroups() != null) {
            for (Group readGroup : folder.getReadGroups()) {
                mappedFolder.getReadGroups().add(groupMapper.toServer(readGroup));
            }
        }
        if (folder.getWriteGroups() != null) {
            for (Group writeGroup : folder.getWriteGroups()) {
                mappedFolder.getWriteGroups().add(groupMapper.toServer(writeGroup));
            }
        }
        return mappedFolder;
    }


}
