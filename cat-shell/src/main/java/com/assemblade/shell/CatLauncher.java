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

import com.assemblade.shell.commands.CommandStatus;
import com.assemblade.shell.commands.RootCommandFactory;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;

public class CatLauncher {
    private Options options;
    private CommandLine commandLine;

    private CatLauncher(String[] args) {
        Option help = new Option( "h", "help", true, "print this message" );
        Option url = new Option( "u", "url", true, "the url of the CAT rest API");
        Option script = new Option("s", "script", true, "path to a script file");
        Option output = new Option("o", "output", true, "path to output file");

        options = new Options();
        options.addOption(help);
        options.addOption(url);
        options.addOption(script);
        options.addOption(output);

        CommandLineParser parser = new GnuParser();
        try {
            commandLine = parser.parse( options, args );
        }
        catch( ParseException exp ) {
            System.err.println( "Parsing failed.  Reason: " + exp.getMessage() );
        }
    }

    private void run() {
        if (commandLine.hasOption("help")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( "cat-shell", options );
        } else {
            String url = commandLine.getOptionValue("u");
            String script = commandLine.getOptionValue("s");
            String output = commandLine.getOptionValue("o");

            Context context = new Context(url, script, output);

            AuthenticationProcessor authenticationProcessor = new AuthenticationProcessor(context);

            if (StringUtils.isEmpty(url) && !authenticationProcessor.hasAuthentication()) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp( "cat-shell", options );
            } else {
                context.println();
                System.out.println();

                if (authenticationProcessor.hasAuthentication()) {
                    authenticationProcessor.welcomeUser();
                }

                context.setAuthenticationProcessor(authenticationProcessor);

                RootCommandFactory commandFactory = new RootCommandFactory();

                CommandStatus status = CommandStatus.Continue;

                while (status == CommandStatus.Continue) {
                    context.getAuthenticationProcessor().authenticate();
                    status = commandFactory.process(context, context.readLine("> "));
                }

                System.out.println();
                System.out.println("bye!!");
                System.out.println();
            }
        }
    }

    public static void main(String[] args) {
        new CatLauncher(args).run();
    }
}
