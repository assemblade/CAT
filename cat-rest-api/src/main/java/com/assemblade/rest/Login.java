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
package com.assemblade.rest;

import com.assemblade.client.model.Authentication;
import com.assemblade.opendj.AssembladeErrorCode;
import com.assemblade.opendj.StorageException;
import com.assemblade.server.model.AccessToken;
import com.assemblade.server.model.User;
import com.assemblade.server.security.AccessTokenManager;
import com.assemblade.server.security.BadCredentialsException;
import com.assemblade.server.security.ChangePasswordException;
import com.assemblade.server.users.UserManager;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/login")
public class Login {
    private final AccessTokenManager accessTokenManager;
    private final UserManager userManager;

    public Login(AccessTokenManager accessTokenManager, UserManager userManager) {
        this.accessTokenManager = accessTokenManager;
        this.userManager = userManager;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@Context HttpServletRequest servletRequest, @QueryParam(value = "username") String username, @QueryParam(value = "password") String password) {
        try {
            String requestUrl = servletRequest.getRequestURL().toString();
            String baseUrl = requestUrl.substring(0, requestUrl.lastIndexOf('/'));
            AccessToken token = accessTokenManager.requestAccessToken(username, password, baseUrl);
            return Response.ok(new Authentication(baseUrl, token.getToken(), token.getSecret())).build();
        } catch (StorageException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } catch (BadCredentialsException e) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        } catch (ChangePasswordException e) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }

    @POST
    @Path("changepassword")
    public Response changePassword(@QueryParam(value = "username") String username, @QueryParam(value = "password") String password, @QueryParam("newpassword") String newPassword) {
        User user = new User();
        user.setUserId(username);
        try {
            userManager.getAdminSession().changePassword(user.getDn(), password, newPassword);
            return Response.ok().build();
        } catch (StorageException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
}
