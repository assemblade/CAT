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

import com.assemblade.client.ClientException;
import com.assemblade.client.Users;
import com.assemblade.client.model.Authentication;
import com.assemblade.client.model.User;

import java.util.List;

public class LineProcessor {
    private final AuthenticationProcessor authenticationProcessor;

    public LineProcessor(AuthenticationProcessor authenticationProcessor) {
        this.authenticationProcessor = authenticationProcessor;
    }

    public void processLine(Authentication authentication, String line) {
        if (line.equals("list users")) {
            Users users = new Users(authentication);
            try {
                List<User> userList = users.getUsers();
                for (User user : userList) {
                    System.out.println(user.getUserId() + "," + user.getFullName() + "," + user.getEmailAddress());
                }
            } catch (ClientException e) {
                e.printStackTrace();
            }
        }

    }
}
