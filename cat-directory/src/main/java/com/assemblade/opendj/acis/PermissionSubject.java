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
package com.assemblade.opendj.acis;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PermissionSubject extends Subject {
    private static final Pattern permissionSubjectPattern = Pattern.compile(subject);

    private String key;
    private List<String> dns = new ArrayList<String>();
    private String operand;

    public PermissionSubject(String key, List<String> dns, String operand) {
        this.key = key;
        this.dns = dns;
        this.operand = operand;
    }

    public List<String> getDns() {
        return dns;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(' ');
        buffer.append(key);
        buffer.append(operand);
        buffer.append('"');
        boolean firstDn = true;
        for(String dn : dns) {
            if (!firstDn) {
                buffer.append("||");
            }
            buffer.append("ldap:///");
            buffer.append(dn);
            firstDn = false;
        }
        buffer.append('"');
        return buffer.toString();
    }

    public static Subject parse(String text) {
        Matcher permissionSubjectMatcher = permissionSubjectPattern.matcher(text);
        if (permissionSubjectMatcher.find()) {
            int endIndex = permissionSubjectMatcher.end();
            String keyword = permissionSubjectMatcher.group(1);
            String operand = permissionSubjectMatcher.group(2);
            String expression = permissionSubjectMatcher.group(3);
            List<String> dns = new ArrayList<String>();
            if (Pattern.matches(ldapUrls, expression)) {
                Matcher ldapURLMatcher = Pattern.compile(ldapUrl).matcher(expression);
                while (ldapURLMatcher.find()) {
                    dns.add(ldapURLMatcher.group(1).trim().substring(8));
                }
            }
            Subject permissionSubject = new PermissionSubject(keyword, dns, operand);
            if (endIndex < text.length()) {
                return CompositeSubject.parse(permissionSubject, text.substring(endIndex));
            }
            else {
                return permissionSubject;
            }
        }
        return null;
    }
}
