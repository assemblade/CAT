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

public class User {
    private String userId;
    private String fullName;
    private String emailAddress;
    private boolean globalAdministrator;
    private boolean groupAdministrator;
    private String authenticationPolicy;
    private String password;
    private boolean writable;
    private boolean deletable;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public boolean isGlobalAdministrator() {
        return globalAdministrator;
    }

    public void setGlobalAdministrator(boolean globalAdministrator) {
        this.globalAdministrator = globalAdministrator;
    }

    public boolean isGroupAdministrator() {
        return groupAdministrator;
    }

    public void setGroupAdministrator(boolean groupAdministrator) {
        this.groupAdministrator = groupAdministrator;
    }

    public String getAuthenticationPolicy() {
        return authenticationPolicy;
    }

    public void setAuthenticationPolicy(String authenticationPolicy) {
        this.authenticationPolicy = authenticationPolicy;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isWritable() {
        return writable;
    }

    public void setWritable(boolean writable) {
        this.writable = writable;
    }

    public boolean isDeletable() {
        return deletable;
    }

    public void setDeletable(boolean deletable) {
        this.deletable = deletable;
    }
}
