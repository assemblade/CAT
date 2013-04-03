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

import java.io.Serializable;

public class LdapPassthroughPolicy implements Serializable {
    private static final long serialVersionUID = 5075640253300253277L;

    private String url;
    private String primaryRemoteServer;
    private String searchBase;
    private String bindDn;
    private String bindPassword;
    private String searchAttribute;
    private String nameAttribute;
    private String mailAttribute;

    public String getSearchAttribute() {
        return searchAttribute;
    }

    public void setSearchAttribute(String searchAttribute) {
        this.searchAttribute = searchAttribute;
    }

    public String getNameAttribute() {
        return nameAttribute;
    }

    public void setNameAttribute(String nameAttribute) {
        this.nameAttribute = nameAttribute;
    }

    public String getMailAttribute() {
        return mailAttribute;
    }

    public void setMailAttribute(String mailAttribute) {
        this.mailAttribute = mailAttribute;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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
}
