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
        Option help = new Option( "help", "print this message" );
        Option url = new Option( "url", true, "the url of the CAT rest API");

        options = new Options();
        options.addOption(help);
        options.addOption(url);

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
            String url = commandLine.getOptionValue("url");

            Context context = new Context();

            if (StringUtils.isNotEmpty(url)) {
                context.setUrl(url);
            }

            AuthenticationProcessor authenticationProcessor = new AuthenticationProcessor(context);

            if (StringUtils.isEmpty(url) && !authenticationProcessor.hasAuthentication()) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp( "cat-shell", options );
            } else {

                System.out.println();
                System.out.println();

                if (authenticationProcessor.hasAuthentication()) {
                    authenticationProcessor.welcomeUser();
                }

                try {
                    context.setAuthenticationProcessor(authenticationProcessor);
                    LineProcessor lineProcessor = new LineProcessor(context);
                    while (lineProcessor.processing());
                } catch (IOException e) {
                }

                System.out.println();
                System.out.println("bye!!");
                System.out.println();
            }
        }
    }

    public static void main(String[] args) {
        CatLauncher launcher = new CatLauncher(args);

        launcher.run();
    }
}
