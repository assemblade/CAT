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
import com.assemblade.client.Groups;
import com.assemblade.client.Users;
import com.assemblade.client.model.Group;
import com.assemblade.client.model.User;
import com.assemblade.shell.Context;

public class ListGroupCommand implements Command {
    private static final ListReportDefinition groupReport;

    static {
        groupReport = new ListReportDefinition("Groups");
        groupReport.addColumn(new ListColumn("Group Name", 30, "getName"));
        groupReport.addColumn(new ListColumn("Group Description", 60, "getDescription"));
    }

    @Override
    public CommandStatus run(Context context, String parameters) {
        Groups groups = new Groups(context.getAuthenticationProcessor().getAuthentication());

        groupReport.printHeader(context);
        try {
            for (Group group : groups.getAllGroups()) {
                groupReport.printLine(context, group);
            }
        } catch (ClientException e) {
            e.printStackTrace();
        }

        return CommandStatus.Continue;
    }
}
