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

package com.assemblade.opendj.model.authentication.policy;

import com.assemblade.opendj.model.ConfigurationDecorator;
import com.assemblade.opendj.LdapUtils;
import org.apache.commons.lang.StringUtils;
import org.opends.server.core.DirectoryServer;
import org.opends.server.types.Attribute;
import org.opends.server.types.AttributeType;
import org.opends.server.types.Entry;
import org.opends.server.types.Modification;
import org.opends.server.types.ObjectClass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class LdapPassthroughAuthenticationPolicy extends AuthenticationPolicy {

    private String primaryRemoteServer = "localhost:1389";
    private String searchBase;
    private String searchAttribute = "uid";
    private String nameAttribute = "cn";
    private String mailAttribute = "mail";
    private String bindDn;
    private String bindPassword;

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
    public boolean requiresUpdate(Entry currentEntry) {
        return super.requiresUpdate(currentEntry)
            || !StringUtils.equals(getPrimaryRemoteServer(), LdapUtils.getSingleAttributeStringValue(currentEntry.getAttribute("ds-cfg-primary-remote-ldap-server")))
            || !StringUtils.equals(getSearchCriteria(), LdapUtils.getSingleAttributeStringValue(currentEntry.getAttribute("ds-cfg-mapped-search-base-dn")))
            || !StringUtils.equals(getBindDn(), LdapUtils.getSingleAttributeStringValue(currentEntry.getAttribute("ds-cfg-mapped-search-bind-dn")))
            || !StringUtils.equals(getBindPassword(), LdapUtils.getSingleAttributeStringValue(currentEntry.getAttribute("ds-cfg-mapped-search-bind-password")));
    }

    @Override
    public List<Modification> getModifications(Entry currentEntry) {
        List<Modification> modifications = super.getModifications(currentEntry);
        LdapUtils.createSingleEntryModification(modifications, currentEntry, "ds-cfg-primary-remote-ldap-server", getPrimaryRemoteServer());
        LdapUtils.createSingleEntryModification(modifications, currentEntry, "ds-cfg-mapped-search-base-dn", getSearchCriteria());
        LdapUtils.createSingleEntryModification(modifications, currentEntry, "ds-cfg-mapped-search-bind-dn", getBindDn());
        LdapUtils.createSingleEntryModification(modifications, currentEntry, "ds-cfg-mapped-search-bind-password", getBindPassword());

        return modifications;
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
            String searchCriteria = LdapUtils.getSingleAttributeStringValue(entry.getAttribute("ds-cfg-mapped-search-base-dn"));
            int commaIndex = searchCriteria.indexOf(',');
            policy.searchAttribute = searchCriteria.substring(0, commaIndex - 2);
            searchCriteria = searchCriteria.substring(commaIndex + 1);
            commaIndex = searchCriteria.indexOf(',');
            policy.nameAttribute = searchCriteria.substring(0, commaIndex - 2);
            searchCriteria = searchCriteria.substring(commaIndex + 1);
            commaIndex = searchCriteria.indexOf(',');
            policy.mailAttribute = searchCriteria.substring(0, commaIndex - 2);
            policy.searchBase = searchCriteria.substring(commaIndex + 1);
            policy.bindDn = LdapUtils.getSingleAttributeStringValue(entry.getAttribute("ds-cfg-mapped-search-bind-dn"));
            policy.bindPassword = LdapUtils.getSingleAttributeStringValue(entry.getAttribute("ds-cfg-mapped-search-bind-password"));

            return policy;
        }
    }

    public String getName() {
        return "Remote User Authentication Policy";
    }

    public String getPrimaryRemoteServer() {
        return primaryRemoteServer;
    }

    public void setPrimaryRemoteServer(String primaryRemoteServer) {
        this.primaryRemoteServer = primaryRemoteServer;
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

    public String getSearchAttribute() {
        return searchAttribute;
    }

    public void setSearchAttribute(String searchAttribute) {
        this.searchAttribute = searchAttribute;
    }

    public String getMailAttribute() {
        return mailAttribute;
    }

    public void setMailAttribute(String mailAttribute) {
        this.mailAttribute = mailAttribute;
    }

    public String getNameAttribute() {
        return nameAttribute;
    }

    public void setNameAttribute(String nameAttribute) {
        this.nameAttribute = nameAttribute;
    }

    private String getSearchCriteria() {
        return searchAttribute + "=a," + nameAttribute + "=b," + mailAttribute + "=c," + searchBase;
    }
}
