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

package com.assemblade.opendj.authentication.policy;

import com.assemblade.opendj.model.ConfigurationDecorator;
import com.assemblade.opendj.LdapUtils;
import org.opends.server.core.DirectoryServer;
import org.opends.server.types.Attribute;
import org.opends.server.types.AttributeType;
import org.opends.server.types.Entry;
import org.opends.server.types.ObjectClass;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class LdapPassthroughAuthenticationPolicy extends AuthenticationPolicy {

    private String primaryRemoteServer = "localhost:1389";
    private String secondaryRemoteServer;
    private String searchBase;
    private String bindDn;
    private String bindPassword;
    private String mappingAttribute;

    @Override
    public String getJavaClass() {
        return "org.opends.server.extensions.LDAPPassThroughAuthenticationPolicyFactory";
    }

    @Override
    public Map<ObjectClass, String> getObjectClasses() {
        Map<ObjectClass, String> objectClasses = super.getObjectClasses();
        objectClasses.put(DirectoryServer.getObjectClass("ds-cfg-ldap-pass-through-authentication-policy"), "ds-cfg-ldap-pass-through-authentication-policy");
        return objectClasses;
    }

    @Override
    public Collection<String> getAttributeNames() {
        Collection<String> attributeNames = super.getAttributeNames();

        attributeNames.addAll(Arrays.asList(
                "ds-cfg-primary-remote-ldap-server",
                "ds-cfg-mapping-policy",
                "ds-cfg-use-password-caching",
                "ds-cfg-secondary-remote-ldap-server",
                "ds-cfg-mapped-attribute",
                "ds-cfg-mapped-search-bind-dn",
                "ds-cfg-mapped-search-bind-password",
                "ds-cfg-mapped-search-bind-password-property",
                "ds-cfg-mapped-search-bind-password-environment-variable",
                "ds-cfg-mapped-search-bind-password-file",
                "ds-cfg-mapped-search-base-dn",
                "ds-cfg-connection-timeout",
                "ds-cfg-trust-manager-provider",
                "ds-cfg-use-ssl",
                "ds-cfg-use-tcp-keep-alive",
                "ds-cfg-use-tcp-no-delay",
                "ds-cfg-ssl-protocol",
                "ds-cfg-ssl-cipher-suite",
                "ds-cfg-cached-password-storage-scheme",
                "ds-cfg-cached-password-ttl"
        ));
        return attributeNames;
    }

    @Override
    public Map<AttributeType, List<Attribute>> getUserAttributes() {
        Map<AttributeType, List<Attribute>> attributeMap = super.getUserAttributes();

        LdapUtils.addSingleValueAttributeToMap(attributeMap, "ds-cfg-mapping-policy", "mapped-search");
        LdapUtils.addSingleValueAttributeToMap(attributeMap, "ds-cfg-use-password-caching", "false");

        LdapUtils.addSingleValueAttributeToMap(attributeMap, "ds-cfg-primary-remote-ldap-server", primaryRemoteServer);
        LdapUtils.addSingleValueAttributeToMap(attributeMap, "ds-cfg-mapped-search-base-dn", searchBase);
        LdapUtils.addSingleValueAttributeToMap(attributeMap, "ds-cfg-mapped-search-bind-dn", bindDn);
        LdapUtils.addSingleValueAttributeToMap(attributeMap, "ds-cfg-mapped-search-bind-password", bindPassword);
        LdapUtils.addSingleValueAttributeToMap(attributeMap, "ds-cfg-mapped-attribute", mappingAttribute);

        return attributeMap;
    }

    @Override
    public ConfigurationDecorator getDecorator() {
        return new Decorator();
    }

    private class Decorator extends AuthenticationPolicy.Decorator<LdapPassthroughAuthenticationPolicy> {
        @Override
        public LdapPassthroughAuthenticationPolicy newInstance() {
            return new LdapPassthroughAuthenticationPolicy();
        }

        @Override
        public LdapPassthroughAuthenticationPolicy decorate(Entry entry) {
            LdapPassthroughAuthenticationPolicy policy = super.decorate(entry);

            policy.primaryRemoteServer = LdapUtils.getSingleAttributeStringValue(entry.getAttribute("ds-cfg-primary-remote-ldap-server"));
            policy.searchBase = LdapUtils.getSingleAttributeStringValue(entry.getAttribute("ds-cfg-mapped-search-base-dn"));
            policy.bindDn = LdapUtils.getSingleAttributeStringValue(entry.getAttribute("ds-cfg-mapped-search-bind-dn"));
            policy.bindPassword = LdapUtils.getSingleAttributeStringValue(entry.getAttribute("ds-cfg-mapped-search-bind-password"));
            policy.mappingAttribute = LdapUtils.getSingleAttributeStringValue(entry.getAttribute("ds-cfg-mapped-attribute"));

            return policy;
        }
    }

    public String getPrimaryRemoteServer() {
        return primaryRemoteServer;
    }

    public void setPrimaryRemoteServer(String primaryRemoteServer) {
        this.primaryRemoteServer = primaryRemoteServer;
    }

    public String getSecondaryRemoteServer() {
        return secondaryRemoteServer;
    }

    public void setSecondaryRemoteServer(String secondaryRemoteServer) {
        this.secondaryRemoteServer = secondaryRemoteServer;
    }

    public String getSearchBase() {
        return searchBase;
    }

    public void setSearchBase(String searchBase) {
        this.searchBase = searchBase;
    }

    public String getBindDn() {
        return bindDn;
    }

    public void setBindDn(String bindDn) {
        this.bindDn = bindDn;
    }

    public String getBindPassword() {
        return bindPassword;
    }

    public void setBindPassword(String bindPassword) {
        this.bindPassword = bindPassword;
    }

    public String getMappingAttribute() {
        return mappingAttribute;
    }

    public void setMappingAttribute(String mappingAttribute) {
        this.mappingAttribute = mappingAttribute;
    }
}
