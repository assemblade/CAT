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
package com.assemblade.opendj.model;

import com.assemblade.opendj.LdapUtils;
import com.assemblade.opendj.SequenceNumberGenerator;
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

    public static final String ACCESS_TOKEN_ROOT = "ou=accesstokens,dc=assemblade,dc=com";
    public static final SecureRandom secureRandom = new SecureRandom();

    private String token;
    private String uid;
    private String secret;
    private String ipAddress;

    public static AccessToken createWithToken(String token) {
        AccessToken accessToken = new AccessToken();
        accessToken.token = token;
        return accessToken;
    }

    public AccessToken() {
    }

    public AccessToken(String uid) {
        this.token = Base64.encode(SequenceNumberGenerator.getNextSequenceNumber().getBytes());
        this.uid = uid;
        this.secret = new BigInteger(130, secureRandom).toString();
    }

    public AccessToken(String uid, String ipAddress) {
        this(uid);
        this.ipAddress = ipAddress;
    }

    @Override
    public String getRDN() {
        return "asb-token=" + token;
    }

    @Override
    protected String getRootDn() {
        return ACCESS_TOKEN_ROOT;
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
        attributeNames.addAll(new ArrayList<String>(Arrays.asList("asb-token", "uid", "asb-secret", "ipNetworkNumber")));
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

        if (StringUtils.isNotEmpty(ipAddress)) {
            LdapUtils.addSingleValueAttributeToMap(attributeMap, "ipNetworkNumber", ipAddress);
        }
        return attributeMap;
    }

    public String getUid() {
        return uid;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getToken() {
        return token;
    }

    public String getSecret() {
        return secret;
    }

    private class Decorator extends AbstractStorable.Decorator<AccessToken> {
        @Override
        public AccessToken newInstance() {
            return new AccessToken();
        }

        @Override
        public AccessToken decorate(Entry entry) {
            AccessToken token = super.decorate(entry);

            token.token = LdapUtils.getSingleAttributeStringValue(entry.getAttribute("asb-token"));
            token.uid = LdapUtils.getSingleAttributeStringValue(entry.getAttribute("uid"));
            token.secret = LdapUtils.getSingleAttributeStringValue(entry.getAttribute("asb-secret"));
            token.ipAddress = LdapUtils.getSingleAttributeStringValue(entry.getAttribute("ipnetworknumber"));

            return token;
        }
    }
}
