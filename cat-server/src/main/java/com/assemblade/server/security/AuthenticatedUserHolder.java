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
package com.assemblade.server.security;

import com.assemblade.server.model.User;
import org.springframework.core.NamedInheritableThreadLocal;
import org.springframework.core.NamedThreadLocal;

public class AuthenticatedUserHolder {
    private static final ThreadLocal<User> userHolder = new NamedThreadLocal<User>("User");
    private static final ThreadLocal<User> inheritableUserHolder = new NamedInheritableThreadLocal<User>("User");

    public static void resetUser() {
        userHolder.remove();
        inheritableUserHolder.remove();
    }

    public static void setUser(User user) {
        setUser(user, false);
    }

    public static void setUser(User user, boolean inheritable) {
        if (user == null) {
            resetUser();
        }
        else {
            if (inheritable) {
                inheritableUserHolder.set(user);
                userHolder.remove();
            }
            else {
                userHolder.set(user);
                inheritableUserHolder.remove();
            }
        }
    }

    public static User getUser() {
        User user = userHolder.get();
        if (user == null) {
            user = inheritableUserHolder.get();
        }
        return user;
    }

}
