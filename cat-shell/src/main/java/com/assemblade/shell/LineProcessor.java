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
package com.assemblade.shell;

import com.assemblade.client.ClientException;
import com.assemblade.client.Users;
import com.assemblade.client.model.Authentication;
import com.assemblade.client.model.User;
import com.assemblade.shell.commands.AdminCommand;
import com.assemblade.shell.commands.Command;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LineProcessor {
    private final static Pattern commandPattern = Pattern.compile("^\\s*([list|add|edit|delete]*)\\s*(.*)$");

    private static final Map<String, Class> commands = new HashMap<String, Class>();

    static {
        commands.put("admin", AdminCommand.class);
    }

    public void processLine(Authentication authentication, String line) {
        Matcher commandMatcher = commandPattern.matcher(line);
        if (commandMatcher.matches()) {
            String command = commandMatcher.group(1);

            System.out.println("command = " + command);
            System.out.println("parameters = " + commandMatcher.group(2));

            if (commands.containsKey(command)) {

            }

        }
//        if (line.equals("list users")) {
//            Users users = new Users(authentication);
//            try {
//                List<User> userList = users.getUsers();
//                for (User user : userList) {
//                    System.out.println(user.getUserId() + "," + user.getFullName() + "," + user.getEmailAddress());
//                }
//            } catch (ClientException e) {
//                e.printStackTrace();
//            }
//        }

    }
}
