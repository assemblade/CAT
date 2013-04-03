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

import com.assemblade.client.model.LdapPassthroughPolicy;
import com.assemblade.client.model.PasswordPolicy;
import com.assemblade.opendj.AssembladeErrorCode;
import com.assemblade.opendj.StorageException;
import com.assemblade.rest.mappers.AuthenticationPolicyMapper;
import com.assemblade.server.configuration.ConfigurationManager;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/policies")
public class Policies {
    private final ConfigurationManager configurationManager;
    private final AuthenticationPolicyMapper authenticationPolicyMapper;

    public Policies(ConfigurationManager configurationManager, AuthenticationPolicyMapper authenticationPolicyMapper) {
        this.configurationManager = configurationManager;
        this.authenticationPolicyMapper = authenticationPolicyMapper;
    }

    @GET
    @Path("/local")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLocalPasswordPolicy() {
        try {
            return Response.ok().entity(authenticationPolicyMapper.toClient(configurationManager.getLocalUserPasswordPolicy())).build();
        } catch (StorageException e) {
            if ((e.getErrorCode() == AssembladeErrorCode.ASB_0006) || (e.getErrorCode() == AssembladeErrorCode.ASB_0010)) {
                return Response.status(Response.Status.NOT_FOUND).build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        }
    }

    @GET
    @Path("/remote")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRemoteAuthenticationPolicy() {
        try {
            return Response.ok().entity(authenticationPolicyMapper.toClient(configurationManager.getRemoteUserAuthenticationPolicy())).build();
        } catch (StorageException e) {
            if ((e.getErrorCode() == AssembladeErrorCode.ASB_0006) || (e.getErrorCode() == AssembladeErrorCode.ASB_0010)) {
                return Response.status(Response.Status.NOT_FOUND).build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        }
    }

    @PUT
    @Path("/local")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateLocalPasswordPolicy(PasswordPolicy policy) {
        try {
            return Response.ok().entity(authenticationPolicyMapper.toClient(configurationManager.updateLocalUserPasswordPolicy(authenticationPolicyMapper.toServer(policy)))).build();
        } catch (StorageException e) {
            if (e.getErrorCode() == AssembladeErrorCode.ASB_0003) {
                return Response.status(Response.Status.CONFLICT).build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        }
    }

    @PUT
    @Path("/remote")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateRemoteAuthenticationPolicy(LdapPassthroughPolicy policy) {
        try {
            return Response.ok().entity(authenticationPolicyMapper.toClient(configurationManager.updateRemoteUserAuthenticationPolicy(authenticationPolicyMapper.toServer(policy)))).build();
        } catch (StorageException e) {
            if (e.getErrorCode() == AssembladeErrorCode.ASB_0003) {
                return Response.status(Response.Status.CONFLICT).build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        }
    }

//    @DELETE
//    @Path("/name/{policyName}")
//    @Consumes(MediaType.APPLICATION_JSON)
//    public Response deleteAuthenticationPolicy(@PathParam("policyName")String policyName) {
//        try {
//            configurationManager.deleteConfiguration(policyName);
//            return Response.noContent().build();
//        } catch (StorageException e) {
//            if (e.getErrorCode() == AssembladeErrorCode.ASB_0003) {
//                return Response.status(Response.Status.CONFLICT).build();
//            } else {
//                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
//            }
//        }
//    }
}
