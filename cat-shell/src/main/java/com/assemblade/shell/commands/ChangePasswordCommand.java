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

import java.io.IOException;

public class ChangePasswordCommand implements Command {
    @Override
    public CommandStatus run(Context context, String parameters) {
        context.println();
        String oldPassword = context.readLine("Current password: ", '*');
        String newPassword = context.readLine("New password: ", '*');
        if (context.getAuthenticationProcessor().changePassword(oldPassword, newPassword)) {
            context.getAuthenticationProcessor().clearStoredAuthentication();
        }
        return CommandStatus.Continue;
    }
}
