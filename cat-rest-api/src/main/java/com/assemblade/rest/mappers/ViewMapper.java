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
import com.assemblade.client.model.View;
import com.assemblade.opendj.StorageException;
import com.assemblade.server.security.AuthenticationHolder;
import com.assemblade.server.users.UserManager;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

public class ViewMapper {
    private final UserManager userManager;
    private final FolderMapper folderMapper;

    public ViewMapper(UserManager userManager, FolderMapper folderMapper) {
        this.userManager = userManager;
        this.folderMapper = folderMapper;
    }

    public View toClient(com.assemblade.server.model.View serverView) {
        View clientView = new View();

        clientView.setUrl(AuthenticationHolder.getAuthentication().getBaseUrl() + "/views/" + serverView.getId());
        clientView.setId(serverView.getId());
        clientView.setName(serverView.getName());
        clientView.setDescription(serverView.getDescription());

        List<Folder> clientFolders = new ArrayList<Folder>();

        for (com.assemblade.server.model.Folder serverFolder : serverView.getFolders()) {
            clientFolders.add(folderMapper.toClient(serverFolder));
        }

        clientView.setFolders(clientFolders);

        return clientView;
    }

    public com.assemblade.server.model.View toServer(View clientView) throws StorageException {
        com.assemblade.server.model.View serverView = new com.assemblade.server.model.View();

        serverView.setId(clientView.getId());
        serverView.setName(clientView.getName());
        serverView.setParentDn(userManager.getViewsDn());
        serverView.setDescription(clientView.getDescription());

        List<com.assemblade.server.model.Folder> serverFolders = new ArrayList<com.assemblade.server.model.Folder>();

        for (Folder clientFolder : clientView.getFolders()) {
            serverFolders.add(folderMapper.toServer(clientFolder));
        }

        serverView.setFolders(serverFolders);

        return serverView;
    }
}
