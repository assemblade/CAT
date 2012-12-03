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
import com.assemblade.client.model.Folder;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.scribe.model.OAuthConstants;
import org.scribe.services.HMACSha1SignatureService;
import org.scribe.services.SignatureService;
import org.scribe.services.TimestampService;
import org.scribe.services.TimestampServiceImpl;
import org.scribe.utils.OAuthEncoder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractClient {
    protected final ObjectMapper mapper = new ObjectMapper();
    protected final HttpClient client = new HttpClient(new MultiThreadedHttpConnectionManager());
    protected final String baseUrl;
    protected final TimestampService timestampService = new TimestampServiceImpl();
    protected final SignatureService signatureService = new HMACSha1SignatureService();
    protected final Authentication authentication;

    public AbstractClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.authentication = null;
    }

    public AbstractClient(Authentication authentication) {
        this.baseUrl = authentication.getBaseUrl();
        this.authentication = authentication;
    }

    protected <T> T get(String path, TypeReference<T> type) throws ClientException {
        return getFromUrl(baseUrl + path, type);
    }

    protected <T> T getFromUrl(String url, TypeReference<T> type) throws ClientException {
        GetMethod get = new GetMethod(url);
        try {
            int statusCode = executeMethod(get);
            if (statusCode == 200) {
                try {
                    return mapper.readValue(get.getResponseBodyAsStream(), type);
                } catch (IOException e) {
                    throw new CallFailedException("Failed to deserialize a response object", e);
                }
            } else {
                throw new InvalidStatusException(200, statusCode);
            }
        } finally {
            get.releaseConnection();
        }
    }

    protected <T> T add(String path, T object, TypeReference<T> type) throws ClientException {
        PostMethod post = new PostMethod(baseUrl + path);
        try {
            try {
                post.setRequestEntity(new StringRequestEntity(mapper.writeValueAsString(object), "application/json", null));
            } catch (IOException e) {
                throw new CallFailedException("Failed to serialize a request object", e);
            }
            int statusCode = executeMethod(post);
            if (statusCode == 200) {
                try {
                    return mapper.readValue(post.getResponseBodyAsStream(), type);
                } catch (IOException e) {
                    throw new CallFailedException("Failed to deserialize a response object", e);
                }
            } else {
                throw new InvalidStatusException(200, statusCode);
            }
        } finally {
            post.releaseConnection();
        }
    }

    protected <T> T update(String path, T object, TypeReference<T> type) throws ClientException {
        PutMethod put = new PutMethod(baseUrl + path);
        try {
            try {
                put.setRequestEntity(new StringRequestEntity(mapper.writeValueAsString(object), "application/json", null));
            } catch (IOException e) {
                throw new CallFailedException("Failed to serialize a request object", e);
            }
            int statusCode = executeMethod(put);
            if (statusCode == 200) {
                try {
                    return mapper.readValue(put.getResponseBodyAsStream(), type);
                } catch (IOException e) {
                    throw new CallFailedException("Failed to deserialize a response object", e);
                }
            } else {
                throw new InvalidStatusException(200, statusCode);
            }
        } finally {
            put.releaseConnection();
        }
    }

    protected void delete(String path) throws ClientException {
        DeleteMethod delete = new DeleteMethod(baseUrl + path);
        try {
            int statusCode = executeMethod(delete);
            if (statusCode != 204) {
                throw new InvalidStatusException(204, statusCode);
            }
        } finally {
            delete.releaseConnection();
        }
    }

    protected int executeMethod(HttpMethodBase method) throws CallFailedException {
        try {
            generateSignature(method);
            client.executeMethod(method);
            return method.getStatusCode();
        } catch (Exception e) {
            throw new CallFailedException("Method execution failed", e);
        }
    }

    private void generateSignature(HttpMethodBase method) throws URIException {
        if (authentication != null) {
            String verb = method.getName();
            String url = OAuthEncoder.encode(method.getURI().toString());

            List<NameValuePair> queryStrings = new ArrayList<NameValuePair>();
            queryStrings.add(new NameValuePair(OAuthConstants.CONSUMER_KEY, authentication.getToken()));
            queryStrings.add(new NameValuePair(OAuthConstants.NONCE, timestampService.getNonce()));
            queryStrings.add(new NameValuePair(OAuthConstants.SIGN_METHOD, signatureService.getSignatureMethod()));
            queryStrings.add(new NameValuePair(OAuthConstants.TIMESTAMP, timestampService.getTimestampInSeconds()));
            queryStrings.add(new NameValuePair(OAuthConstants.VERSION, "1.0"));
            method.setQueryString(queryStrings.toArray(new NameValuePair[]{}));

            String queryString = OAuthEncoder.encode(method.getQueryString());

            String baseString = verb + "&" + url + "&" + queryString;

            String signature = signatureService.getSignature(baseString, authentication.getSecret(), "");

            queryStrings.add(new NameValuePair(OAuthConstants.SIGNATURE, signature));
            method.setQueryString(queryStrings.toArray(new NameValuePair[]{}));
        }
    }
}
