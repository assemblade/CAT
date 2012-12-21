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
import com.assemblade.shell.commands.CommandStatus;
import com.assemblade.shell.commands.InteractiveCommandFactory;
import jline.console.ConsoleReader;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LineProcessor {
    private Context context;
    private InteractiveCommandFactory commandFactory;

    public LineProcessor(Context context) throws IOException {
        this.context = context;
        this.context.setConsoleReader(new ConsoleReader());
        commandFactory = new InteractiveCommandFactory();
    }

    public boolean processing() throws IOException {
        context.getAuthenticationProcessor().authenticate();
        String line = context.getConsoleReader().readLine("> ");
        return commandFactory.process(context, line) == CommandStatus.Continue;
    }
}
