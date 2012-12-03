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
import jline.Terminal;
import jline.console.ConsoleReader;

import java.io.IOException;
import java.util.Scanner;

public class InputProcessor {
    private AuthenticationProcessor authenticationProcessor;
    private LineProcessor lineProcessor;

    public InputProcessor(AuthenticationProcessor authenticationProcessor) {
        this.authenticationProcessor = authenticationProcessor;
        this.lineProcessor = new LineProcessor(authenticationProcessor);
    }

    public void readInput() {
        System.out.println();
        System.out.println();

        Authentication authentication = authenticationProcessor.authenticate();

        if (authentication != null) {
            System.out.println();

            try {
                ConsoleReader reader = new ConsoleReader();

                boolean exited = false;

                while (!exited) {
                    String line = reader.readLine("> ");

                    if (line.equals("exit")) {
                        exited = true;
                    } else {
                        lineProcessor.processLine(authentication, line);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

            System.out.println();
            System.out.println("bye!!");
            System.out.println();
        }
    }
}
