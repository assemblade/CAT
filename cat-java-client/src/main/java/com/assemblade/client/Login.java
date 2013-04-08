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
package com.assemblade.client;

import com.assemblade.client.model.Authentication;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.IOException;

public class Login extends AbstractClient {
    public Login(String baseUrl) {
        super(baseUrl);
    }

    public Authentication login(String username, String password) throws ChangePasswordException, CallFailedException {
        GetMethod method = new GetMethod(baseUrl + "/login");
        try {
            method.setQueryString(new NameValuePair[]{new NameValuePair("username", username), new NameValuePair("password", password)});
            int status = executeMethod(method);
            if (status == 200) {
                try {
                    return mapper.readValue(method.getResponseBodyAsStream(), Authentication.class);
                } catch (IOException e) {
                }
            } else if (status == 403) {
                throw new ChangePasswordException();
            }
        } finally {
            method.releaseConnection();
        }
        return null;
    }

    public boolean changePassword(String username, String password, String newPassword) throws CallFailedException {
        PostMethod method = new PostMethod(baseUrl + "/login/changepassword");
        try {
            method.setQueryString(new NameValuePair[]{new NameValuePair("username", username), new NameValuePair("password", password), new NameValuePair("newpassword", newPassword)});
            return executeMethod(method) == 200;
        } finally {
            method.releaseConnection();
        }
    }
}
