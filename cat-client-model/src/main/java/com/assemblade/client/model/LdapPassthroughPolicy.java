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
package com.assemblade.client.model;

public class LdapPassthroughPolicy extends AuthenticationPolicy {
    private String primaryRemoteServer;
    private String secondaryRemoteServer;
    private String searchBase;
    private String bindDn;
    private String bindPassword;
    private String mappingAttribute;

    @Override
    public String getType() {
        return "passthrough";
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
