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
import com.assemblade.client.model.AuthenticationPolicy;
import com.assemblade.client.model.LdapPassthroughPolicy;
import com.assemblade.client.model.PasswordPolicy;
import com.assemblade.shell.Context;

import java.io.IOException;
import java.util.List;

public class ListPolicyCommand extends AbstractListCommand {
    private static final ListReportDefinition passwordPolicyReport;
    private static final ListReportDefinition passthroughPolicyReport;

    static {
        passwordPolicyReport = new ListReportDefinition("Password Policies");
        passwordPolicyReport.addColumn(new ListColumn("Name", 40, "getName"));
        passwordPolicyReport.addColumn(new ListColumn("Force Reset", 11, "isForceChangeOnReset"));
        passthroughPolicyReport = new ListReportDefinition("LDAP Passthrough Policies");
    }

    @Override
    public CommandStatus run(Context context, String parameters) {
        Policies policies = new Policies(context.getAuthenticationProcessor().getAuthentication());
        try {
            List<AuthenticationPolicy> authenticationPolicies = policies.getAuthenticationPolicies();
            context.getConsoleReader().println();
            context.getConsoleReader().println("Password Policies");
            context.getConsoleReader().println("----------------------------------------------------------");
            context.getConsoleReader().println("| Name                                     | Force Reset |");
            context.getConsoleReader().println("-------------------------------------------+--------------");
            for (AuthenticationPolicy policy : authenticationPolicies) {
                if (policy.getType() == "password") {
                    PasswordPolicy passwordPolicy = (PasswordPolicy)policy;
                    context.getConsoleReader().println("| " + pad(passwordPolicy.getName(), 40) + " | " + pad((passwordPolicy.isForceChangeOnReset() ? "yes" : "no"), 11) + " |");
                    context.getConsoleReader().println("-------------------------------------------+--------------");
                }
            }
            context.getConsoleReader().println();
            context.getConsoleReader().println("LDAP Passthrough Policies");
            context.getConsoleReader().println("----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
            context.getConsoleReader().println("| Name                                     | Primary Server                          | Secondary Server              | Search Base                    | Bind DN                        |");
            context.getConsoleReader().println("-------------------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------");
            for (AuthenticationPolicy policy : authenticationPolicies) {
                if (policy.getType() == "passthrough") {
                    LdapPassthroughPolicy ldapPolicy = (LdapPassthroughPolicy)policy;
                    context.getConsoleReader().println("| " + pad(ldapPolicy.getName(), 40) + " | " + pad(ldapPolicy.getPrimaryRemoteServer(), 30) + " |" + pad(ldapPolicy.getSecondaryRemoteServer(), 30) + " | " + pad(ldapPolicy.getSearchBase(), 30) + " | " + pad(ldapPolicy.getBindDn(), 30) + " |");
                    context.getConsoleReader().println("-------------------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------");
                }
            }
        } catch (ClientException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return CommandStatus.Continue;
    }
}
