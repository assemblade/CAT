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
import com.assemblade.shell.Context;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddGroupCommand implements Command {
    private Pattern pattern = Pattern.compile("^\\s*([^,]*),([^,]*)");

    @Override
    public CommandStatus run(Context context, String parameters) {
        Matcher commandMatcher = pattern.matcher(parameters);
        String name = null;
        String description = null;

//        try {
            if (commandMatcher.matches()) {
                name = commandMatcher.group(1);
                description = commandMatcher.group(2);
            } else {
                context.println();
                name = context.readLine("Enter group name []: ");
                description = context.readLine("Enter group description []: ");
            }


        System.out.println("Name = " + name);

            Groups groups = new Groups(context.getAuthenticationProcessor().getAuthentication());
            Group group = new Group();
            group.setName(name);
            group.setDescription(description);
//            groups.addGroup(group);
//        } catch (ClientException e) {
//            e.printStackTrace();
//        }

        return CommandStatus.Continue;
    }
}
