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

public interface AciPatterns {
    public static final String header = "\\(\\s*(?i)version(?-i)\\s*\\d\\.\\d\\s*;\\s*(?i)acl(?-i)\\s*\"(.*?)\"\\s*;\\s*";
    public static final String body = "\\G\\s*(\\w+)\\s*\\(([^()]+)\\)\\s*(.+?\"[)]*)\\s*;\\s*";
    public static final String target = "\\(\\s*(\\w+)\\s*(!?=)\\s*\"([^\"]+)\"\\s*\\)\\s*";
    public static final String subject = "^(\\w+)\\s*([!=<>]+)\\s*\"([^\"]+)\"\\s*";
    public static final String subjectRemainder = "^\\s*(\\w+)\\s*(.*)$";
    public static final String ldapUrl = "\\s*(ldap:///[^\\|]+)";
    public static final String ldapUrls = ldapUrl + "\\s*(\\|\\|\\s*" + ldapUrl + ")*";





}
