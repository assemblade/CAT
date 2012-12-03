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

import com.assemblade.client.model.User;
import com.assemblade.server.security.AuthenticationHolder;

public class UserMapper {
    public User toClient(com.assemblade.server.model.User serverUser) {
        User clientUser = new User();
        clientUser.setUrl(AuthenticationHolder.getAuthentication().getBaseUrl() + "/users/id/" + serverUser.getId());
        clientUser.setId(serverUser.getId());
        clientUser.setUserId(serverUser.getUserId());
        clientUser.setFullName(serverUser.getFullName());
        clientUser.setEmailAddress(serverUser.getEmailAddress());
        clientUser.setGlobalAdministrator(serverUser.isGlobalAdministrator());
        clientUser.setGroupAdministrator(serverUser.isGroupAdministrator());
        clientUser.setAuthenticationPolicy(serverUser.getAuthenticationPolicy());
        clientUser.setWritable(serverUser.isWritable());
        clientUser.setDeletable(serverUser.isDeletable());
        return clientUser;
    }

    public com.assemblade.server.model.User toServer(User clientUser) {
        com.assemblade.server.model.User serverUser = new com.assemblade.server.model.User();
        serverUser.setId(clientUser.getId());
        serverUser.setUserId(clientUser.getUserId());
        serverUser.setParentDn(com.assemblade.server.model.User.ROOT);
        serverUser.setFullName(clientUser.getFullName());
        serverUser.setEmailAddress(clientUser.getEmailAddress());
        serverUser.setAuthenticationPolicy(clientUser.getAuthenticationPolicy());
        serverUser.setPassword(clientUser.getPassword());
        return serverUser;
    }
}
