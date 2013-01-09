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

import jline.console.ConsoleReader;

import java.io.IOException;
import java.io.PrintWriter;

public class Context {
    private String url;
    private AuthenticationProcessor authenticationProcessor;
    private ConsoleReader consoleReader;

    public Context(String url, String script, String output) {
        this.url = url;
        try {
            this.consoleReader = new ConsoleReader();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isInteractive() {
        return true;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public AuthenticationProcessor getAuthenticationProcessor() {
        return authenticationProcessor;
    }

    public void setAuthenticationProcessor(AuthenticationProcessor authenticationProcessor) {
        this.authenticationProcessor = authenticationProcessor;
    }

    public void setConsoleReader(ConsoleReader consoleReader) {
        this.consoleReader = consoleReader;
    }

    public String readLine(String prompt) {
        String line = null;
        try {
            line = consoleReader.readLine(prompt);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return line;
    }

    public String readLine(String prompt, Character mask) {
        String line = null;
        try {
            line = consoleReader.readLine(prompt, mask);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return line;
    }

    public void print(String text) {
        try {
            consoleReader.print(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void println() {
        try {
            consoleReader.println();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void println(String text) {
        try {
            consoleReader.println(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public PrintWriter getWriter() {
        return new PrintWriter(consoleReader.getOutput());
    }
}
