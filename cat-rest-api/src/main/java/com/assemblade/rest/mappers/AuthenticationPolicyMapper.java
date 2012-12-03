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

import com.assemblade.client.model.AuthenticationPolicy;
import com.assemblade.client.model.LdapPassthroughPolicy;
import com.assemblade.client.model.PasswordPolicy;
import com.assemblade.opendj.model.Configuration;
import com.assemblade.server.security.AuthenticationHolder;

public class AuthenticationPolicyMapper {
    public AuthenticationPolicy toClient(Configuration configuration) {
        if (configuration instanceof com.assemblade.opendj.model.authentication.policy.PasswordPolicy) {
            com.assemblade.opendj.model.authentication.policy.PasswordPolicy serverPasswordPolicy = (com.assemblade.opendj.model.authentication.policy.PasswordPolicy)configuration;
            PasswordPolicy passwordPolicy = new PasswordPolicy();
            passwordPolicy.setUrl(AuthenticationHolder.getAuthentication().getBaseUrl() + "/policies/name/" + serverPasswordPolicy.getName());
            passwordPolicy.setName(serverPasswordPolicy.getName());
            passwordPolicy.setForceChangeOnReset(serverPasswordPolicy.isForceChangeOnReset());
            return passwordPolicy;
        } else {
            com.assemblade.opendj.model.authentication.policy.LdapPassthroughAuthenticationPolicy serverLdapPassthroughPolicy = (com.assemblade.opendj.model.authentication.policy.LdapPassthroughAuthenticationPolicy)configuration;
            LdapPassthroughPolicy ldapPassthroughPolicy = new LdapPassthroughPolicy();
            ldapPassthroughPolicy.setUrl(AuthenticationHolder.getAuthentication().getBaseUrl() + "/policies/name/" + ldapPassthroughPolicy.getName());
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

    public Configuration toServer(AuthenticationPolicy authenticationPolicy) {
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
