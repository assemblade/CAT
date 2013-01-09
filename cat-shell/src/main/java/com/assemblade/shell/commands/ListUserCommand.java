/*
 * Copyright 2013 Mike Adamson
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
import com.assemblade.client.Users;
import com.assemblade.client.model.User;
import com.assemblade.shell.Context;

public class ListUserCommand implements Command {
    private static final ListReportDefinition userReport;

    static {
        userReport = new ListReportDefinition("Users");
        userReport.addColumn(new ListColumn("User ID", 20, "getUserId"));
        userReport.addColumn(new ListColumn("Full Name", 30, "getFullName"));
        userReport.addColumn(new ListColumn("E-Mail Address", 30, "getEmailAddress"));
    }

    @Override
    public CommandStatus run(Context context, String parameters) {
        Users users = new Users(context.getAuthenticationProcessor().getAuthentication());

        userReport.printHeader(context);
        try {
            for (User user : users.getUsers()) {
                userReport.printLine(context, user);
            }
        } catch (ClientException e) {
            e.printStackTrace();
        }

        return CommandStatus.Continue;
    }
}
