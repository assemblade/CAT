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
package com.assemblade.server.configuration;

import com.assemblade.opendj.StorageException;
import com.assemblade.opendj.model.Configuration;
import com.assemblade.server.users.UserManager;

import java.util.List;

public class ConfigurationManager {
    private final UserManager userManager;


    public ConfigurationManager(UserManager userManager) {
        this.userManager = userManager;
    }

    public List<Configuration> getAuthenticationPolicies() throws StorageException {
        return  userManager.getUserSession().getConfigurationItems("(objectClass=ds-cfg-authentication-policy)");
    }

    public void addConfiguration(Configuration configuration) throws StorageException {
        userManager.getUserSession().add(configuration);
    }

    public Configuration updateConfiguration(Configuration configuration) throws StorageException {
        userManager.getUserSession().update(configuration);
        return userManager.getUserSession().get(configuration);
    }

    public void deleteConfiguration(String name) throws StorageException {
        userManager.getUserSession().delete("cn=" + name + ",cn=Password Policies,cn=config", false);
    }
}
