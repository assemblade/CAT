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

import com.assemblade.client.model.AuthenticationPolicy;
import com.assemblade.opendj.AssembladeErrorCode;
import com.assemblade.opendj.StorageException;
import com.assemblade.opendj.model.Configuration;
import com.assemblade.rest.mappers.AuthenticationPolicyMapper;
import com.assemblade.server.configuration.ConfigurationManager;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/policies")
public class Policies {
    private final ConfigurationManager configurationManager;
    private final AuthenticationPolicyMapper authenticationPolicyMapper;

    public Policies(ConfigurationManager configurationManager, AuthenticationPolicyMapper authenticationPolicyMapper) {
        this.configurationManager = configurationManager;
        this.authenticationPolicyMapper = authenticationPolicyMapper;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAuthenticationPolicies() {
        List<AuthenticationPolicy> policies = new ArrayList<AuthenticationPolicy>();
        try {
            for (Configuration configuration : configurationManager.getAuthenticationPolicies()) {
                policies.add(authenticationPolicyMapper.toClient(configuration));
            }
            GenericEntity<List<AuthenticationPolicy>> entity = new GenericEntity<List<AuthenticationPolicy>>(policies) {};
            return Response.ok(entity).build();
        } catch (StorageException e) {
            if ((e.getErrorCode() == AssembladeErrorCode.ASB_0006) || (e.getErrorCode() == AssembladeErrorCode.ASB_0010)) {
                return Response.status(Response.Status.NOT_FOUND).build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        }
    }

    @GET
    @Path("/name/{policyName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAuthenticationPolicy(@PathParam("policyName") String policyName) {
        try {
            return Response.ok().entity(authenticationPolicyMapper.toClient(configurationManager.getAuthenticationPolicy(policyName))).build();
        } catch (StorageException e) {
            if ((e.getErrorCode() == AssembladeErrorCode.ASB_0006) || (e.getErrorCode() == AssembladeErrorCode.ASB_0010)) {
                return Response.status(Response.Status.NOT_FOUND).build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addAuthenticationPolicy(AuthenticationPolicy policy) {
        try {
            return Response.ok().entity(authenticationPolicyMapper.toClient(configurationManager.addConfiguration(authenticationPolicyMapper.toServer(policy)))).build();
        } catch (StorageException e) {
            if (e.getErrorCode() == AssembladeErrorCode.ASB_0003) {
                return Response.status(Response.Status.CONFLICT).build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        }
    }

    @PUT
    @Path("/name/{policyName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateAuthenticationPolicy(@PathParam("policyName") String policyName, AuthenticationPolicy policy) {
        try {
            return Response.ok().entity(authenticationPolicyMapper.toClient(configurationManager.updateConfiguration(authenticationPolicyMapper.toServer(policy)))).build();
        } catch (StorageException e) {
            if (e.getErrorCode() == AssembladeErrorCode.ASB_0003) {
                return Response.status(Response.Status.CONFLICT).build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        }
    }

    @DELETE
    @Path("/name/{policyName}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteAuthenticationPolicy(@PathParam("policyName")String policyName) {
        try {
            configurationManager.deleteConfiguration(policyName);
            return Response.noContent().build();
        } catch (StorageException e) {
            if (e.getErrorCode() == AssembladeErrorCode.ASB_0003) {
                return Response.status(Response.Status.CONFLICT).build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        }
    }
}
