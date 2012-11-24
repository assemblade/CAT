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
import com.assemblade.server.users.UserManager;

import java.util.ArrayList;
import java.util.List;

public class FolderMapper {
    private final UserManager userManager;
    private final GroupMapper groupMapper;

    public FolderMapper(UserManager userManager, GroupMapper groupMapper) {
        this.userManager = userManager;
        this.groupMapper = groupMapper;
    }

    public Folder toClient(com.assemblade.server.model.Folder folder) {
        Folder mapperFolder = new Folder();
        mapperFolder.setId(folder.getId());
        mapperFolder.setName(folder.getName());
        mapperFolder.setDescription(folder.getDescription());
        mapperFolder.setParentId(folder.getParentId());
        mapperFolder.setAddable(folder.isAddable());
        mapperFolder.setWritable(folder.isWritable());
        mapperFolder.setDeletable(folder.isDeletable());
        List<Group> readGroups = new ArrayList<Group>();
        for (String groupDn : folder.getReadGroups()) {
            try {
                readGroups.add(groupMapper.toClient(userManager.getUserSession().get(new com.assemblade.server.model.Group(groupDn))));
            } catch (StorageException e) {
            }
        }
        if (readGroups.size() > 0) {
            mapperFolder.setReadGroups(readGroups);
        }
        List<Group> writeGroups = new ArrayList<Group>();
        for (String groupDn : folder.getWriteGroups()) {
            try {
                writeGroups.add(groupMapper.toClient(userManager.getUserSession().get(new com.assemblade.server.model.Group(groupDn))));
            } catch (StorageException e) {
            }
        }
        if (writeGroups.size() > 0) {
            mapperFolder.setWriteGroups(writeGroups);
        }
        return mapperFolder;
    }

    public com.assemblade.server.model.Folder toServer(Folder folder) throws StorageException {
        com.assemblade.server.model.Folder mappedFolder = new com.assemblade.server.model.Folder();
        if (folder.getParentId() != null) {
            mappedFolder.setParentDn(userManager.getUserSession().dnFromId(folder.getParentId()));
        }
        mappedFolder.setId(folder.getId());
        mappedFolder.setName(folder.getName());
        mappedFolder.setDescription(folder.getDescription());
        mappedFolder.setParentId(folder.getParentId());
        if (folder.getReadGroups() != null) {
            for (Group readGroup : folder.getReadGroups()) {
                mappedFolder.getReadGroups().add(userManager.getUserSession().dnFromId(readGroup.getId()));
            }
        }
        if (folder.getWriteGroups() != null) {
            for (Group writeGroup : folder.getWriteGroups()) {
                mappedFolder.getWriteGroups().add(userManager.getUserSession().dnFromId(writeGroup.getId()));
            }
        }
        return mappedFolder;
    }


}