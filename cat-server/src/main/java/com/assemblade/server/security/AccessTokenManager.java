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
package com.assemblade.server.security;

import com.assemblade.opendj.BindStatus;
import com.assemblade.opendj.DirectoryService;
import com.assemblade.opendj.StorageException;
import com.assemblade.opendj.model.AccessToken;
import com.assemblade.server.model.User;

import java.text.MessageFormat;
import java.util.List;

public class AccessTokenManager {
    private static final String USER_SEARCH_FILTER = "(&(objectClass=inetOrgPerson)(uid={0}))";
    private static MessageFormat userFilterFormat = new MessageFormat(USER_SEARCH_FILTER);

    private final DirectoryService directoryService;

    public AccessTokenManager(DirectoryService directoryService) {
        this.directoryService = directoryService;
    }

    public AccessToken requestAccessToken(String username, String password) throws StorageException, BadCredentialsException, ChangePasswordException {
        User user = getUser(username);

        if (user == null) {
            throw new BadCredentialsException();
        }

        BindStatus bindStatus = directoryService.bind(user.getDn(), password);

        if (bindStatus == BindStatus.BadCredentials) {
            throw new BadCredentialsException();
        } else if (bindStatus == BindStatus.PasswordChangeNeeded) {
            throw new ChangePasswordException();
        }

        List<AccessToken> tokens = directoryService.getAdminSession().search(new AccessToken(), AccessToken.ACCESS_TOKEN_ROOT, "(&(objectClass=inetOrgPerson)(uid={0}))");
        for (AccessToken token : tokens) {
            directoryService.getAdminSession().delete(token);
        }

        AccessToken token = new AccessToken(username);

        directoryService.getAdminSession().add(token);

        return token;
    }

    public AccessToken getExistingAccessToken(String token) throws StorageException {
        return directoryService.getAdminSession().get(AccessToken.createWithToken(token));
    }

    private User getUser(String userName) {
        User user = null;
        try {
            String userFilter = userFilterFormat.format(new Object[] {userName});

            user = directoryService.getAdminSession().getByEntryDnAndFilter(new User(), User.ROOT, userFilter);
        } catch (StorageException e) {
        }
        return user;
    }
}
