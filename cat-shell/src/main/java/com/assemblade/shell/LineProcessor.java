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

import com.assemblade.client.model.Authentication;
import com.assemblade.shell.commands.Command;
import com.assemblade.shell.commands.InteractiveCommandFactory;
import jline.console.ConsoleReader;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LineProcessor {
    private Context context;
    private Authentication authentication;
    private InteractiveCommandFactory commandFactory;
    private Pattern commandPattern;

    public LineProcessor(Context context) throws IOException {
        this.context = context;
        this.context.setConsoleReader(new ConsoleReader());
        commandFactory = new InteractiveCommandFactory();
        commandPattern = Pattern.compile(commandFactory.getCommandRegex());
    }

    public boolean processing() throws IOException {
        if (authentication == null) {
            authentication = context.getAuthenticationProcessor().authenticate(context.getConsoleReader());
        }
        Matcher commandMatcher = commandPattern.matcher(context.getConsoleReader().readLine("> "));
        if (commandMatcher.matches()) {
            String command = commandMatcher.group(1);
            String body = commandMatcher.group(2);

            Command commandInstance = commandFactory.get(command);

            if (commandInstance == null) {
                System.err.println("Did not understand command: " + command);
            } else {
                switch (commandInstance.run(context, body)) {
                    case Continue:
                        return true;
                    case NeedAuthentication:
                        authentication = null;
                        context.getAuthenticationProcessor().clearStoredAuthentication();
                        return true;
                    case Finish:
                        return false;
                }
            }
        }
        return true;
    }
}
