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
package com.assemblade.server.model;

import com.assemblade.opendj.LdapUtils;
import com.assemblade.opendj.SequenceNumberGenerator;
import com.assemblade.opendj.Session;
import com.assemblade.opendj.StorageException;
import com.assemblade.opendj.model.AbstractStorable;
import com.assemblade.opendj.model.StorableDecorator;
import org.apache.commons.lang.StringUtils;
import org.opends.server.core.DirectoryServer;
import org.opends.server.types.Attribute;
import org.opends.server.types.AttributeType;
import org.opends.server.types.Entry;
import org.opends.server.types.ObjectClass;
import org.opends.server.util.Base64;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class AccessToken extends AbstractStorable {
    private static final long serialVersionUID = 1L;

    public static final String ROOT = "ou=accesstokens,dc=assemblade,dc=com";
    public static final SecureRandom secureRandom = new SecureRandom();

    private String token;
    private String uid;
    private String secret;
    private String baseUrl;

    public enum AccessTokenType {
        userLogin,
        persistent
    }

    private AccessTokenType type;

    public static AccessToken createAccessToken(User user) {
        AccessToken token = new AccessToken();
        token.uid = user.getUserId();
        token.token = Base64.encode(SequenceNumberGenerator.getNextSequenceNumber().getBytes());
        token.secret = new BigInteger(130, secureRandom).toString();
        return token;
    }

    @Override
    public String getRDN() {
        return "asb-token=" + token;
    }

    @Override
    public String getParentDn() {
        return ROOT;
    }

    @Override
    public Map<ObjectClass, String> getObjectClasses() {
        Map<ObjectClass, String> objectClasses = super.getObjectClasses();
        objectClasses.put(DirectoryServer.getObjectClass("asb-access-token"), "asb-access-token");
        return objectClasses;
    }

    @Override
    public Collection<String> getAttributeNames() {
        Collection<String> attributeNames = super.getAttributeNames();
        attributeNames.addAll(new ArrayList<String>(Arrays.asList("asb-token", "uid", "asb-secret", "asb-baseurl", "asb-type")));
        return attributeNames;
    }

    @Override
    public StorableDecorator<AccessToken> getDecorator() {
        return new Decorator();
    }

    @Override
    public Map<AttributeType, List<Attribute>> getUserAttributes() {
        Map<AttributeType, List<Attribute>> attributeMap = super.getUserAttributes();

        LdapUtils.addSingleValueAttributeToMap(attributeMap, "asb-token", token);
        LdapUtils.addSingleValueAttributeToMap(attributeMap, "uid", uid);
        LdapUtils.addSingleValueAttributeToMap(attributeMap, "asb-secret", secret);
        LdapUtils.addSingleValueAttributeToMap(attributeMap, "asb-baseurl", baseUrl);
        LdapUtils.addSingleValueAttributeToMap(attributeMap, "asb-type", type.name());

        return attributeMap;
    }

    public String getUid() {
        return uid;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSecret() {
        return secret;
    }

    public AccessTokenType getType() {
        return type;
    }

    public void setType(AccessTokenType type) {
        this.type = type;
    }

    private class Decorator extends AbstractStorable.Decorator<AccessToken> {
        @Override
        public AccessToken newInstance() {
            return new AccessToken();
        }

        @Override
        public AccessToken decorate(Session session, Entry entry) throws StorageException {
            AccessToken token = super.decorate(session, entry);

            token.token = LdapUtils.getSingleAttributeStringValue(entry.getAttribute("asb-token"));
            token.uid = LdapUtils.getSingleAttributeStringValue(entry.getAttribute("uid"));
            token.secret = LdapUtils.getSingleAttributeStringValue(entry.getAttribute("asb-secret"));
            token.baseUrl = LdapUtils.getSingleAttributeStringValue(entry.getAttribute("asb-baseurl"));
            token.type = AccessTokenType.valueOf(LdapUtils.getSingleAttributeStringValue(entry.getAttribute("asb-type")));

            return token;
        }
    }
}
