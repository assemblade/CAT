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
import com.assemblade.opendj.model.AbstractStorable;
import com.assemblade.opendj.model.StorableDecorator;
import org.opends.server.core.DirectoryServer;
import org.opends.server.types.Attribute;
import org.opends.server.types.AttributeType;
import org.opends.server.types.Entry;
import org.opends.server.types.ObjectClass;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class RememberMe extends AbstractStorable {
    private static final long serialVersionUID = 1L;

    public static final String REMEMBER_ME_ROOT = "ou=remembermetokens,dc=assemblade,dc=com";

    private String sequence;
    private String uid;
    private long expiryTime;

    private RememberMe() {
    }

    public RememberMe(String sequence) {
        this.sequence = sequence;
    }

    public RememberMe(String uid, long expiryTime) {
        this.sequence = SequenceNumberGenerator.getNextSequenceNumber();
        this.uid = uid;
        this.expiryTime = expiryTime;
    }

    @Override
    public String getRDN() {
        return "asb-sequence=" + sequence;
    }

    @Override
    public Map<ObjectClass, String> getObjectClasses() {
        Map<ObjectClass, String> objectClasses = super.getObjectClasses();
        objectClasses.put(DirectoryServer.getObjectClass("asb-remember-me"), "asb-remember-me");
        return objectClasses;
    }

    @Override
    public Collection<String> getAttributeNames() {
        Collection<String> attributeNames = super.getAttributeNames();
        attributeNames.addAll(Arrays.asList("asb-sequence", "uid", "asb-expiry-time"));
        return attributeNames;
    }

    @Override
    public StorableDecorator<RememberMe> getDecorator() {
        return new Decorator();
    }

    @Override
    public Map<AttributeType, List<Attribute>> getUserAttributes() {
        Map<AttributeType, List<Attribute>> attributeMap = super.getUserAttributes();

        LdapUtils.addSingleValueAttributeToMap(attributeMap, "asb-sequence", sequence);
        LdapUtils.addSingleValueAttributeToMap(attributeMap, "uid", uid);
        LdapUtils.addSingleValueAttributeToMap(attributeMap, "asb-expiry-time", Long.toString(expiryTime));

        return attributeMap;
    }

    public String getSequence() {
        return sequence;
    }

    public long getExpiryTime() {
        return expiryTime;
    }

    public String getUsername() {
        return uid;
    }

    private class Decorator extends AbstractStorable.Decorator<RememberMe> {
        @Override
        public RememberMe newInstance() {
            return new RememberMe();
        }

        @Override
        public RememberMe decorate(Session session, Entry entry) {
            RememberMe token = super.decorate(session, entry);

            token.sequence = LdapUtils.getSingleAttributeStringValue(entry.getAttribute("asb-sequence"));
            token.uid = LdapUtils.getSingleAttributeStringValue(entry.getAttribute("uid"));
            token.expiryTime = Long.parseLong(LdapUtils.getSingleAttributeStringValue(entry.getAttribute("asb-expiry-time")));

            return token;
        }
    }
}
