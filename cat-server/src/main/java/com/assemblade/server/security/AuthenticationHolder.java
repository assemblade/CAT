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

import com.assemblade.server.model.Authentication;
import com.assemblade.server.model.User;
import org.springframework.core.NamedInheritableThreadLocal;
import org.springframework.core.NamedThreadLocal;

public class AuthenticationHolder {
    private static final ThreadLocal<Authentication> authenticationHolder = new NamedThreadLocal<Authentication>("Authentication");
    private static final ThreadLocal<Authentication> inheritableAuthenticationHolder = new NamedInheritableThreadLocal<Authentication>("Authentication");

    public static void resetAuthentication() {
        authenticationHolder.remove();
        inheritableAuthenticationHolder.remove();
    }

    public static void setAuthentication(Authentication authentication) {
        setAuthentication(authentication, false);
    }

    public static void setAuthentication(Authentication authentication, boolean inheritable) {
        if (authentication == null) {
            resetAuthentication();
        }
        else {
            if (inheritable) {
                inheritableAuthenticationHolder.set(authentication);
                authenticationHolder.remove();
            }
            else {
                authenticationHolder.set(authentication);
                inheritableAuthenticationHolder.remove();
            }
        }
    }

    public static Authentication getAuthentication() {
        Authentication authentication = authenticationHolder.get();
        if (authentication == null) {
            authentication = inheritableAuthenticationHolder.get();
        }
        return authentication;
    }

}
