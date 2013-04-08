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

import com.assemblade.opendj.AssembladeErrorCode;
import com.assemblade.opendj.BindStatus;
import com.assemblade.opendj.DirectoryService;
import com.assemblade.opendj.StorageException;
import com.assemblade.server.model.AccessToken;
import com.assemblade.server.model.User;
import com.assemblade.server.users.UserManager;

import java.text.MessageFormat;
import java.util.List;

public class AccessTokenManager {
    private static final String USER_SEARCH_FILTER = "(&(objectClass=inetOrgPerson)(uid={0}))";
    private static MessageFormat userFilterFormat = new MessageFormat(USER_SEARCH_FILTER);

    private final DirectoryService directoryService;
    private final UserManager userManager;

    public AccessTokenManager(DirectoryService directoryService, UserManager userManager) {
        this.directoryService = directoryService;
        this.userManager = userManager;
    }

    public AccessToken requestAccessToken(String username, String password, String baseUrl) throws StorageException, BadCredentialsException, ChangePasswordException {
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

        List<AccessToken> tokens = directoryService.getAdminSession().search(new AccessToken(), AccessToken.ROOT, "(&(objectClass=inetOrgPerson)(uid={0}))");
        for (AccessToken token : tokens) {
            if (token.getType() == AccessToken.AccessTokenType.userLogin) {
                directoryService.getAdminSession().delete(token);
            }
        }

        AccessToken token = AccessToken.createAccessToken(user);
        token.setBaseUrl(baseUrl);
        token.setType(AccessToken.AccessTokenType.userLogin);

        directoryService.getAdminSession().add(token);

        return token;
    }

    public AccessToken requestAccessToken() throws StorageException {
        AccessToken token = AccessToken.createAccessToken(AuthenticationHolder.getAuthentication().getUser());
        token.setBaseUrl(AuthenticationHolder.getAuthentication().getBaseUrl());
        token.setType(AccessToken.AccessTokenType.persistent);

        directoryService.getAdminSession().add(token);

        return token;
    }

    public List<AccessToken> getAllAccessTokens() throws StorageException {
        return userManager.getUserSession().search(new AccessToken(), AccessToken.ROOT, false);
    }

    public AccessToken getAccessToken(String token) throws StorageException {
        AccessToken accessToken = new AccessToken();
        accessToken.setToken(token);
        return directoryService.getAdminSession().get(accessToken);
    }

    public void deleteAccessToken(String token) throws StorageException {
        AccessToken accessToken = getAccessToken(token);

        User user = AuthenticationHolder.getAuthentication().getUser();

        if (user.isGlobalAdministrator() || (user.getUserId().equals(accessToken.getUid()) && (accessToken.getType() == AccessToken.AccessTokenType.persistent))) {
            directoryService.getAdminSession().delete(accessToken);
        } else {
            throw new StorageException(AssembladeErrorCode.ASB_0015);
        }
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
