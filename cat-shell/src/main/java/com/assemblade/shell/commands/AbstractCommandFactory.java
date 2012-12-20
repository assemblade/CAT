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

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractCommandFactory implements CommandFactory {
    protected final Map<String, Class> commands = new HashMap<String, Class>();

    @Override
    public Command get(String commandName) {
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

    @Override
    public String getCommandRegex() {
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
}