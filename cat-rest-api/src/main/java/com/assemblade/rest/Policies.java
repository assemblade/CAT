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
import com.assemblade.client.model.LdapPassthroughPolicy;
import com.assemblade.client.model.PasswordPolicy;
import com.assemblade.opendj.AssembladeErrorCode;
import com.assemblade.opendj.StorageException;
import com.assemblade.opendj.model.Configuration;
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

    public Policies(ConfigurationManager configurationManager) {
        this.configurationManager = configurationManager;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAuthenticationPolicies() {
        List<AuthenticationPolicy> policies = new ArrayList<AuthenticationPolicy>();
        try {
            for (Configuration configuration : configurationManager.getAuthenticationPolicies()) {
                policies.add(map(configuration));
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

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addAuthenticationPolicy(AuthenticationPolicy policy) {
        try {
            configurationManager.addConfiguration(map(policy));
            return Response.ok().entity(policy).build();
        } catch (StorageException e) {
            if (e.getErrorCode() == AssembladeErrorCode.ASB_0003) {
                return Response.status(Response.Status.CONFLICT).build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateAuthenticationPolicy(AuthenticationPolicy policy) {
        try {
            policy = map(configurationManager.updateConfiguration(map(policy)));
            return Response.ok().entity(policy).build();
        } catch (StorageException e) {
            if (e.getErrorCode() == AssembladeErrorCode.ASB_0003) {
                return Response.status(Response.Status.CONFLICT).build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        }
    }

    @DELETE
    @Path("{policyName}")
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

    private AuthenticationPolicy map(Configuration configuration) {
        if (configuration instanceof com.assemblade.opendj.model.authentication.policy.PasswordPolicy) {
            com.assemblade.opendj.model.authentication.policy.PasswordPolicy serverPasswordPolicy = (com.assemblade.opendj.model.authentication.policy.PasswordPolicy)configuration;
            PasswordPolicy passwordPolicy = new PasswordPolicy();
            passwordPolicy.setName(serverPasswordPolicy.getName());
            passwordPolicy.setForceChangeOnReset(serverPasswordPolicy.isForceChangeOnReset());
            return passwordPolicy;
        } else {
            com.assemblade.opendj.model.authentication.policy.LdapPassthroughAuthenticationPolicy serverLdapPassthroughPolicy = (com.assemblade.opendj.model.authentication.policy.LdapPassthroughAuthenticationPolicy)configuration;
            LdapPassthroughPolicy ldapPassthroughPolicy = new LdapPassthroughPolicy();
            ldapPassthroughPolicy.setName(serverLdapPassthroughPolicy.getName());
            ldapPassthroughPolicy.setPrimaryRemoteServer(serverLdapPassthroughPolicy.getPrimaryRemoteServer());
            ldapPassthroughPolicy.setSecondaryRemoteServer(serverLdapPassthroughPolicy.getSecondaryRemoteServer());
            ldapPassthroughPolicy.setSearchBase(serverLdapPassthroughPolicy.getSearchBase());
            ldapPassthroughPolicy.setBindDn(serverLdapPassthroughPolicy.getBindDn());
            ldapPassthroughPolicy.setBindPassword(serverLdapPassthroughPolicy.getBindPassword());
            ldapPassthroughPolicy.setMappingAttribute(serverLdapPassthroughPolicy.getMappingAttribute());
            return ldapPassthroughPolicy;
        }
    }

    private Configuration map(AuthenticationPolicy authenticationPolicy) {
        if (authenticationPolicy instanceof PasswordPolicy) {
            com.assemblade.opendj.model.authentication.policy.PasswordPolicy serverPasswordPolicy = new com.assemblade.opendj.model.authentication.policy.PasswordPolicy();
            PasswordPolicy passwordPolicy = (PasswordPolicy)authenticationPolicy;
            serverPasswordPolicy.setName(passwordPolicy.getName());
            serverPasswordPolicy.setForceChangeOnReset(passwordPolicy.isForceChangeOnReset());
            return serverPasswordPolicy;
        } else {
            com.assemblade.opendj.model.authentication.policy.LdapPassthroughAuthenticationPolicy serverLdapPassthroughPolicy = new com.assemblade.opendj.model.authentication.policy.LdapPassthroughAuthenticationPolicy();
            LdapPassthroughPolicy ldapPassthroughPolicy = (LdapPassthroughPolicy)authenticationPolicy;
            serverLdapPassthroughPolicy.setName(ldapPassthroughPolicy.getName());
            serverLdapPassthroughPolicy.setPrimaryRemoteServer(ldapPassthroughPolicy.getPrimaryRemoteServer());
            serverLdapPassthroughPolicy.setSecondaryRemoteServer(ldapPassthroughPolicy.getSecondaryRemoteServer());
            serverLdapPassthroughPolicy.setSearchBase(ldapPassthroughPolicy.getSearchBase());
            serverLdapPassthroughPolicy.setBindDn(ldapPassthroughPolicy.getBindDn());
            serverLdapPassthroughPolicy.setBindPassword(ldapPassthroughPolicy.getBindPassword());
            serverLdapPassthroughPolicy.setMappingAttribute(ldapPassthroughPolicy.getMappingAttribute());
            return serverLdapPassthroughPolicy;
        }
    }
}
