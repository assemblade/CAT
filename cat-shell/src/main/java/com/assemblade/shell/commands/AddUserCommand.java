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
import com.assemblade.client.Users;
import com.assemblade.client.model.User;
import com.assemblade.shell.Context;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddUserCommand implements Command {
    private Pattern pattern = Pattern.compile("^\\s*([^,]*),([^,]*),([^,]*),([^,]*),([^,]*)");

    @Override
    public CommandStatus run(Context context, String body) {
        Matcher commandMatcher = pattern.matcher(body);
        String userId = null;
        String fullName = null;
        String emailAddress = null;
        String authenticationPolicy = null;
        String password = null;

        try {
            if (commandMatcher.matches()) {
                userId = commandMatcher.group(1);
                fullName = commandMatcher.group(2);
                emailAddress = commandMatcher.group(3);
                authenticationPolicy = commandMatcher.group(4);
                password = commandMatcher.group(5);
            } else if (context.isInteractive()) {
                context.println();
                userId = context.readLine("Enter user Id []: ");
                fullName = context.readLine("Enter full name of user []: ");
                emailAddress = context.readLine("Enter user e-mail address []: ");
                authenticationPolicy  = context.readLine("Enter the authentication policy for the user []: ");
                password = context.readLine("Enter the password for the user []: ");
            }
            Users users = new Users(context.getAuthenticationProcessor().getAuthentication());
            User user = new User();
            user.setUserId(userId);
            user.setFullName(fullName);
            user.setEmailAddress(emailAddress);
            user.setAuthenticationPolicy(authenticationPolicy);
            user.setPassword(password);
            users.addUser(user);
        } catch (ClientException e) {
            e.printStackTrace();
        }

        return CommandStatus.Continue;
    }
}
