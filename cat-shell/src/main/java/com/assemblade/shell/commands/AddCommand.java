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

import com.assemblade.shell.Context;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddCommand implements Command {
    private CommandFactory commandFactory;
    private Pattern commandPattern;

    public AddCommand() {
        commandFactory = new AddCommandFactory();
        commandPattern = Pattern.compile(commandFactory.getCommandRegex());
    }
    @Override
    public CommandStatus run(Context context, String body) {
        Matcher commandMatcher = commandPattern.matcher(body);
        if (commandMatcher.matches()) {
            String command = commandMatcher.group(1);

            Command commandInstance = commandFactory.get(command);

            if (commandInstance == null) {
                System.err.println("Did not understand command: " + command);
            } else {
                return commandInstance.run(context, commandMatcher.group(2));
            }
        }

        return CommandStatus.Continue;
    }
}
