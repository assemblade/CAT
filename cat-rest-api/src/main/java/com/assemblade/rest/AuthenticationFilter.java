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
package com.assemblade.rest;

import com.assemblade.opendj.DirectoryService;
import com.assemblade.opendj.StorageException;
import com.assemblade.opendj.model.AccessToken;
import com.assemblade.server.model.User;
import com.assemblade.server.security.AccessTokenManager;
import com.assemblade.server.security.AuthenticatedUserHolder;
import org.apache.commons.lang.StringUtils;
import org.scribe.model.OAuthConstants;
import org.scribe.services.HMACSha1SignatureService;
import org.scribe.services.SignatureService;
import org.scribe.utils.OAuthEncoder;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthenticationFilter implements Filter {
    private final DirectoryService directoryService;
    private final AccessTokenManager accessTokenManager;
    protected final SignatureService signatureService = new HMACSha1SignatureService();

    public AuthenticationFilter(DirectoryService directoryService, AccessTokenManager accessTokenManager) {
        this.directoryService = directoryService;
        this.accessTokenManager = accessTokenManager;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = ((HttpServletRequest)servletRequest);

        String address = httpServletRequest.getRequestURI();

        if (address.endsWith("/login") || address.endsWith("/login/changepassword")) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            String token = httpServletRequest.getParameter(OAuthConstants.CONSUMER_KEY);
            if (StringUtils.isEmpty(token)) {
                ((HttpServletResponse)servletResponse).setStatus(401);
            } else {
                try {
                    AccessToken accessToken = accessTokenManager.getExistingAccessToken(token);
                    if (accessToken == null) {
                        ((HttpServletResponse)servletResponse).setStatus(401);
                    } else {
                        if (checkOauthAuthentication(httpServletRequest, accessToken)) {
                            User user = directoryService.getAdminSession().get(new User(accessToken.getUid(), null, null, null));

                            if (user == null) {
                                ((HttpServletResponse)servletResponse).setStatus(401);
                            } else {
                                AuthenticatedUserHolder.setUser(user);
                                filterChain.doFilter(servletRequest, servletResponse);
                            }
                        } else {
                            ((HttpServletResponse)servletResponse).setStatus(401);
                        }
                    }
                } catch (StorageException e) {
                    ((HttpServletResponse)servletResponse).setStatus(500);
                }
            }
        }
    }

    @Override
    public void destroy() {
    }

    private boolean checkOauthAuthentication(HttpServletRequest request, AccessToken accessToken) {
        String token = request.getParameter(OAuthConstants.CONSUMER_KEY);
        String nonce = request.getParameter(OAuthConstants.NONCE);
        String signMethod = request.getParameter(OAuthConstants.SIGN_METHOD);
        String timestamp = request.getParameter(OAuthConstants.TIMESTAMP);
        String version = request.getParameter(OAuthConstants.VERSION);
        String signature = request.getParameter(OAuthConstants.SIGNATURE);

        if (StringUtils.isEmpty(token) || StringUtils.isEmpty(nonce) || StringUtils.isEmpty(signMethod) || StringUtils.isEmpty(timestamp) || StringUtils.isEmpty(version) || StringUtils.isEmpty(signature)) {
            return false;
        }

        String verb = request.getMethod();
        String url = request.getRequestURL().toString();


        String queryString = request.getQueryString();
        queryString = queryString.substring(0, queryString.lastIndexOf('&'));

        String baseString = verb + "&" + OAuthEncoder.encode(url) + "&" + OAuthEncoder.encode(queryString);

        String generatedSignature = signatureService.getSignature(baseString, accessToken.getSecret(), "");

        return signature.equals(generatedSignature);
    }
}
