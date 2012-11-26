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

package com.assemblade.opendj.model.authentication.policy;

import com.assemblade.opendj.model.ConfigurationDecorator;
import com.assemblade.opendj.LdapUtils;
import org.opends.server.core.DirectoryServer;
import org.opends.server.types.Attribute;
import org.opends.server.types.AttributeType;
import org.opends.server.types.Entry;
import org.opends.server.types.ObjectClass;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class PasswordPolicy extends AuthenticationPolicy {
    private boolean forceChangeOnReset;

    public PasswordPolicy() {
        super();
    }

    public PasswordPolicy(String dn) {
        super(dn);
    }

    @Override
    public String getJavaClass() {
        return "org.opends.server.core.PasswordPolicyFactory";
    }

    @Override
    public Map<ObjectClass, String> getObjectClasses() {
        Map<ObjectClass, String> objectClasses = super.getObjectClasses();
        objectClasses.put(DirectoryServer.getObjectClass("ds-cfg-password-policy"), "ds-cfg-password-policy");
        return objectClasses;
    }

    @Override
    public Collection<String> getAttributeNames() {
        Collection<String> attributeNames = super.getAttributeNames();

        attributeNames.addAll(Arrays.asList(
                "ds-cfg-password-attribute",
                "ds-cfg-default-password-storage-scheme",
                "ds-cfg-account-status-notification-handler",
                "ds-cfg-allow-expired-password-changes",
                "ds-cfg-allow-multiple-password-values",
                "ds-cfg-allow-pre-encoded-passwords",
                "ds-cfg-allow-user-password-changes",
                "ds-cfg-deprecated-password-storage-scheme",
                "ds-cfg-expire-passwords-without-warning",
                "ds-cfg-force-change-on-add",
                "ds-cfg-force-change-on-reset",
                "ds-cfg-grace-login-count",
                "ds-cfg-idle-lockout-interval",
                "ds-cfg-last-login-time-attribute",
                "ds-cfg-last-login-time-format",
                "ds-cfg-lockout-duration",
                "ds-cfg-lockout-failure-count",
                "ds-cfg-lockout-failure-expiration-interval",
                "ds-cfg-max-password-age",
                "ds-cfg-max-password-reset-age",
                "ds-cfg-min-password-age",
                "ds-cfg-password-change-requires-current-password",
                "ds-cfg-password-expiration-warning-interval",
                "ds-cfg-password-generator",
                "ds-cfg-password-validator",
                "ds-cfg-previous-last-login-time-format",
                "ds-cfg-require-change-by-time",
                "ds-cfg-require-secure-authentication",
                "ds-cfg-require-secure-password-changes",
                "ds-cfg-skip-validation-for-administrators",
                "ds-cfg-state-update-failure-policy",
                "ds-cfg-password-history-count",
                "ds-cfg-password-history-duration"
        ));
        return attributeNames;
    }

    @Override
    public Map<AttributeType, List<Attribute>> getUserAttributes() {
        Map<AttributeType, List<Attribute>> attributeMap = super.getUserAttributes();

        LdapUtils.addSingleValueAttributeToMap(attributeMap, "ds-cfg-password-attribute", "userPassword");
        LdapUtils.addSingleValueAttributeToMap(attributeMap, "ds-cfg-default-password-storage-scheme", "cn=Salted SHA-512,cn=Password Storage Schemes,cn=config");
        LdapUtils.addSingleValueAttributeToMap(attributeMap, "ds-cfg-force-change-on-add", forceChangeOnReset);

        return attributeMap;
    }


    @Override
    public ConfigurationDecorator getDecorator() {
        return new Decorator();
    }

    private class Decorator extends AuthenticationPolicy.Decorator<PasswordPolicy> {
        @Override
        public PasswordPolicy newInstance() {
            return new PasswordPolicy();
        }

        @Override
        public PasswordPolicy decorate(Entry entry) {
            PasswordPolicy policy = super.decorate(entry);

            policy.forceChangeOnReset = LdapUtils.getSingleAttributeBooleanValue(entry.getAttribute("ds-cfg-force-change-on-add"));

            return policy;
        }
    }

    public boolean isForceChangeOnReset() {
        return forceChangeOnReset;
    }

    public void setForceChangeOnReset(boolean forceChangeOnReset) {
        this.forceChangeOnReset = forceChangeOnReset;
    }

}
