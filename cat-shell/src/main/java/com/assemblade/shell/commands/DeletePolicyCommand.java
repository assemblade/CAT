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
import com.assemblade.client.model.PasswordPolicy;
import com.assemblade.shell.Context;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;

public class DeletePolicyCommand implements Command {
    @Override
    public CommandStatus run(Context context, String parameters) {
        String policyName = parameters.trim();
        if (StringUtils.isEmpty(policyName)) {
            policyName = context.readLine("Enter the name of the authentication policy to delete: ");
        }
        if (StringUtils.isNotEmpty(policyName)) {
            Policies policies = new Policies(context.getAuthenticationProcessor().getAuthentication());
            PasswordPolicy policy = new PasswordPolicy();
            policy.setName(policyName);
            try {
                policies.deleteAuthenticationPolicy(policy);
            } catch (ClientException e) {
                e.printStackTrace();
            }
        } else {
            context.println("\tDoing nothing");
        }
        return CommandStatus.Continue;
    }
}
