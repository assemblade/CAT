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

import com.assemblade.client.CallFailedException;
import com.assemblade.client.ChangePasswordException;
import com.assemblade.client.Login;
import com.assemblade.client.model.Authentication;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Scanner;

public class AuthenticationProcessor {
    private File assembladeDirectory = new File(System.getProperty("user.home") + "/.assemblade");
    private String url;
    private Login login;

    public AuthenticationProcessor(String url) {
        this.url = url;
        this.login = new Login(url);
    }


    public Authentication authenticate() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Username: ");
        String username = scanner.nextLine();

        System.out.print("Password: ");
        String password = null;

        if (System.console() != null) {
            password = String.valueOf(System.console().readPassword());
        } else {
            password = scanner.nextLine();
        }

        try {
            Authentication authentication = login.login(username, password);
            if (authentication != null) {
                storeAuthentication(authentication);
                return authentication;
            } else {
                System.err.println("");
                System.err.println("Failed to authenticate");
                System.err.println("");
            }
        } catch (ChangePasswordException e) {
        } catch (CallFailedException e) {
            System.err.println("");
            System.err.println("Failed to authenticate");
            System.err.println("");
        }

        return null;
    }

    private void storeAuthentication(Authentication authentication) {
        if (!assembladeDirectory.exists()) {
            assembladeDirectory.mkdirs();
        }

        File authenticationFile = new File(assembladeDirectory.getAbsolutePath() + "/" + authentication.getToken());

        try {
            PrintWriter writer = new PrintWriter(authenticationFile);
            writer.println("url = " + authentication.getBaseUrl());
            writer.println("token = " + authentication.getToken());
            writer.println("secret = " + authentication.getSecret());
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private Authentication retrieveAuthentication() {
        if (assembladeDirectory.exists()) {
        }
        return null;
    }
}
