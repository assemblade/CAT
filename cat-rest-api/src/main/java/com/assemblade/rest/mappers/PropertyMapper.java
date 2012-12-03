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

import com.assemblade.client.model.Property;
import com.assemblade.opendj.StorageException;
import com.assemblade.server.properties.PropertyManager;

public class PropertyMapper {
    private final PropertyManager propertyManager;
    private final FolderMapper folderMapper;

    public PropertyMapper(PropertyManager propertyManager, FolderMapper folderMapper) {
        this.propertyManager = propertyManager;
        this.folderMapper = folderMapper;
    }

    public Property toClient(com.assemblade.server.model.Property serverProperty) throws StorageException {
        Property clientProperty = new Property();
        clientProperty.setFolder(folderMapper.toClient(propertyManager.getFolder(serverProperty.getParentId())));
        clientProperty.setUrl(clientProperty.getFolder().getUrl() + "/properties/id/" + serverProperty.getId());
        clientProperty.setId(serverProperty.getId());
        clientProperty.setName(serverProperty.getName());
        clientProperty.setDescription(serverProperty.getDescription());
        clientProperty.setValue(serverProperty.getValue());

        return clientProperty;
    }

    public com.assemblade.server.model.Property toServer(Property clientProperty) throws StorageException {
        com.assemblade.server.model.Property serverProperty = new com.assemblade.server.model.Property();
        serverProperty.setParentDn(propertyManager.getFolder(clientProperty.getFolder().getId()).getDn());
        serverProperty.setId(clientProperty.getId());
        serverProperty.setName(clientProperty.getName());
        serverProperty.setDescription(clientProperty.getDescription());
        serverProperty.setValue(clientProperty.getValue());

        return serverProperty;
    }
}
