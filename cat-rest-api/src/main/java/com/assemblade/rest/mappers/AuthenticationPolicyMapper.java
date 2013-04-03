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

import com.assemblade.client.model.LdapPassthroughPolicy;
import com.assemblade.client.model.PasswordPolicy;
import com.assemblade.server.security.AuthenticationHolder;

public class AuthenticationPolicyMapper {
    public PasswordPolicy toClient(com.assemblade.opendj.model.authentication.policy.PasswordPolicy policy) {
        PasswordPolicy passwordPolicy = new PasswordPolicy();
        passwordPolicy.setUrl(AuthenticationHolder.getAuthentication().getBaseUrl() + "/policies/local");
        passwordPolicy.setForceChangeOnReset(policy.isForceChangeOnReset());
        return passwordPolicy;
    }

    public LdapPassthroughPolicy toClient(com.assemblade.opendj.model.authentication.policy.LdapPassthroughAuthenticationPolicy policy) {
        LdapPassthroughPolicy ldapPassthroughPolicy = new LdapPassthroughPolicy();
        ldapPassthroughPolicy.setUrl(AuthenticationHolder.getAuthentication().getBaseUrl() + "/policies/remote");
        ldapPassthroughPolicy.setPrimaryRemoteServer(policy.getPrimaryRemoteServer());
        ldapPassthroughPolicy.setSearchBase(policy.getSearchBase());
        ldapPassthroughPolicy.setBindDn(policy.getBindDn());
        ldapPassthroughPolicy.setBindPassword(policy.getBindPassword());
        ldapPassthroughPolicy.setNameAttribute(policy.getNameAttribute());
        ldapPassthroughPolicy.setMailAttribute(policy.getMailAttribute());
        ldapPassthroughPolicy.setSearchAttribute(policy.getSearchAttribute());
        return ldapPassthroughPolicy;
    }

    public com.assemblade.opendj.model.authentication.policy.PasswordPolicy toServer(PasswordPolicy policy) {
        com.assemblade.opendj.model.authentication.policy.PasswordPolicy serverPasswordPolicy = new com.assemblade.opendj.model.authentication.policy.PasswordPolicy();
        serverPasswordPolicy.setForceChangeOnReset(policy.isForceChangeOnReset());
        return serverPasswordPolicy;
    }

    public com.assemblade.opendj.model.authentication.policy.LdapPassthroughAuthenticationPolicy toServer(LdapPassthroughPolicy policy) {
        com.assemblade.opendj.model.authentication.policy.LdapPassthroughAuthenticationPolicy serverLdapPassthroughPolicy = new com.assemblade.opendj.model.authentication.policy.LdapPassthroughAuthenticationPolicy();
        serverLdapPassthroughPolicy.setPrimaryRemoteServer(policy.getPrimaryRemoteServer());
        serverLdapPassthroughPolicy.setSearchBase(policy.getSearchBase());
        serverLdapPassthroughPolicy.setBindDn(policy.getBindDn());
        serverLdapPassthroughPolicy.setBindPassword(policy.getBindPassword());
        serverLdapPassthroughPolicy.setNameAttribute(policy.getNameAttribute());
        serverLdapPassthroughPolicy.setMailAttribute(policy.getMailAttribute());
        serverLdapPassthroughPolicy.setSearchAttribute(policy.getSearchAttribute());
        return serverLdapPassthroughPolicy;
    }
}
