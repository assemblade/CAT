/*
 * Copyright 2013 Mike Adamson
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
import com.assemblade.server.security.AccessTokenManager;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/token")
public class Tokens {
    private final AccessTokenManager accessTokenManager;

    public Tokens(AccessTokenManager accessTokenManager) {
        this.accessTokenManager = accessTokenManager;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getToken() {
        try {
            AccessToken token = accessTokenManager.requestAccessToken();
            return Response.ok(new Authentication(token.getBaseUrl(), token.getToken(), token.getSecret())).build();
        } catch (StorageException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllTokens() {
        try {
            List<Authentication> tokens = new ArrayList<Authentication>();
            for (AccessToken token : accessTokenManager.getAllAccessTokens()) {
                tokens.add(new Authentication(token.getBaseUrl(), token.getToken(), token.getSecret()));
            }
            return Response.ok(tokens).build();
        } catch (StorageException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DELETE
    @Path("/{token}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteToken(@PathParam("token") String token) {
        try {
            accessTokenManager.deleteAccessToken(token);
        } catch (StorageException e) {
            if (e.getErrorCode() == AssembladeErrorCode.ASB_0006) {
                return Response.status(Response.Status.NOT_FOUND).build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        }
        return Response.noContent().build();
    }

}
