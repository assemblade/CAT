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
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractCommandFactory implements CommandFactory {
    protected final Map<String, Class> commands = new HashMap<String, Class>();

    @Override
    public CommandStatus process(Context context, String input) {
        input = input.trim();
        if (StringUtils.isNotEmpty(input)) {
            Matcher matcher = Pattern.compile(getCommandRegex()).matcher(input);
            if (matcher.matches()) {
                String command = matcher.group(1);

                Command commandInstance = get(command);

                if (commandInstance == null) {
                    System.err.println("Did not understand: " + command);
                } else {
                    return commandInstance.run(context, matcher.group(2));
                }
            } else {
                System.err.println("Did not understand: " + input);
            }
        } else {
            printHelp(context);
        }
        return CommandStatus.Continue;
    }


    private Command get(String commandName) {
        Command command = null;
        if (commands.containsKey(commandName)) {
            Class commandClass = commands.get(commandName);
            try {
                command = (Command)commandClass.newInstance();
            } catch (InstantiationException e) {
            } catch (IllegalAccessException e) {
            }
        }
        return command;
    }

    private String getCommandRegex() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("\\s*([");
        boolean first = true;
        for (String command : commands.keySet()) {
            if (first) {
                first = false;
            } else {
                buffer.append('|');
            }
            buffer.append(command);
        }
        buffer.append("]*)\\s*(.*)");
        return buffer.toString();
    }

    private void printHelp(Context context) {
        context.println("Commands:");
        for (String command : commands.keySet()) {
            context.println("\t" + command);
        }
    }
}
