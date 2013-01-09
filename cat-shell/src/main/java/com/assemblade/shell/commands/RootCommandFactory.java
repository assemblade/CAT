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

public class RootCommandFactory extends AbstractCommandFactory {
    public RootCommandFactory() {
        commands.put("admin", AdminCommand.class);
        commands.put("add", AddCommand.class);
        commands.put("edit", EditCommand.class);
        commands.put("delete", DeleteCommand.class);
        commands.put("list", ListCommand.class);
        commands.put("logout", LogoutCommand.class);
        commands.put("exit", ExitCommand.class);
    }
}
