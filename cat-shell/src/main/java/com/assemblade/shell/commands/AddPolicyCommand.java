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
package com.assemblade.shell.commands;

import com.assemblade.client.ClientException;
import com.assemblade.client.Policies;
import com.assemblade.client.model.LdapPassthroughPolicy;
import com.assemblade.client.model.PasswordPolicy;
import com.assemblade.shell.Context;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;

public class AddPolicyCommand implements Command {
    @Override
    public CommandStatus run(Context context, String parameters) {
        try {
            context.println();
            String name = context.readLine("Enter policy name []: ");
            String type = context.readLine("Enter policy type, 1-passthrough, 2-password [1]: ");

            Policies policies = new Policies(context.getAuthenticationProcessor().getAuthentication());

            if (StringUtils.equals(type, "2")) {
                String forceReset = context.readLine("Should password be changed on first use [yes|no]: ");

                PasswordPolicy policy = new PasswordPolicy();
                policy.setName(name);
                policy.setForceChangeOnReset(forceReset.equals("yes"));
                policies.addAuthenticationPolicy(policy);
            } else {
                String primaryRemoteServer = context.readLine("Enter the primary remote server url [localhost:389]: ");
                if (StringUtils.isEmpty(primaryRemoteServer)) {
                    primaryRemoteServer = "localhost:389";
                }
                String secondaryRemoteServer = context.readLine("Enter the secondary remote server url []: ");
                String searchBase = context.readLine("Enter the search base on the remote server []: ");
                String bindDn = context.readLine("Enter the bind DN on the remote server []: ");
                String bindPassword = context.readLine("Enter the bind password on the remote server []: ");
                String mappingAttribute = context.readLine("Enter the mapping attribute name [uid]: ");

                LdapPassthroughPolicy policy = new LdapPassthroughPolicy();
                policy.setName(name);
                policy.setPrimaryRemoteServer(primaryRemoteServer);
                policy.setSecondaryRemoteServer(secondaryRemoteServer);
                policy.setSearchBase(searchBase);
                policy.setBindDn(bindDn);
                policy.setBindPassword(bindPassword);
                policy.setMappingAttribute(mappingAttribute);

                policies.addAuthenticationPolicy(policy);
            }
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return CommandStatus.Continue;
    }
}
