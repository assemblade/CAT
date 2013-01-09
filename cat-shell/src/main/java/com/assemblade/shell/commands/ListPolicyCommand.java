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

public class ListPolicyCommand implements Command {
    private static final ListReportDefinition passwordPolicyReport;
    private static final ListReportDefinition passthroughPolicyReport;

    static {
        passwordPolicyReport = new ListReportDefinition("Password Policies");
        passwordPolicyReport.addColumn(new ListColumn("Name", 40, "getName"));
        passwordPolicyReport.addColumn(new ListColumn("Force Reset", 11, "isForceChangeOnReset"));
        passthroughPolicyReport = new ListReportDefinition("LDAP Passthrough Policies");
        passthroughPolicyReport.addColumn(new ListColumn("Name", 40, "getName"));
        passthroughPolicyReport.addColumn(new ListColumn("Primary Server", 30, "getPrimaryRemoteServer"));
        passthroughPolicyReport.addColumn(new ListColumn("Secondary Server", 30, "getSecondaryRemoteServer"));
        passthroughPolicyReport.addColumn(new ListColumn("Search Base", 30, "getSearchBase"));
        passthroughPolicyReport.addColumn(new ListColumn("Bind DN", 30, "getBindDn"));
    }

    @Override
    public CommandStatus run(Context context, String parameters) {
        Policies policies = new Policies(context.getAuthenticationProcessor().getAuthentication());
        try {
            List<AuthenticationPolicy> authenticationPolicies = policies.getAuthenticationPolicies();
            passwordPolicyReport.printHeader(context);
            for (AuthenticationPolicy policy : authenticationPolicies) {
                if (policy.getType() == "password") {
                    passwordPolicyReport.printLine(context, policy);
                }
            }
            passthroughPolicyReport.printHeader(context);
            for (AuthenticationPolicy policy : authenticationPolicies) {
                if (policy.getType() == "passthrough") {
                    passthroughPolicyReport.printLine(context, policy);
                }
            }
        } catch (ClientException e) {
            e.printStackTrace();
        }

        return CommandStatus.Continue;
    }
}
