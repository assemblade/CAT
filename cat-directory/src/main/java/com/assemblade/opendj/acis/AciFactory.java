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

public class AciFactory implements AciPatterns {
    private static Pattern headerPattern = Pattern.compile(header);
    private static Pattern targetPattern = Pattern.compile(target);
    private static Pattern bodyPattern = Pattern.compile(body);

    public static AccessControlItem parse(String aci) {
        String name = null;
        String targets = null;
        String rules = null;

        Matcher headerMatcher = headerPattern.matcher(aci);
        if (headerMatcher.find()) {
            targets = aci.substring(0, headerMatcher.start());
            name = headerMatcher.group(1);
            rules = aci.substring(headerMatcher.end());
        }

        List<Target> targetList = new ArrayList<Target>();

        Matcher targetMatcher = targetPattern.matcher(targets);
        while (targetMatcher.find()) {
            String keyword = targetMatcher.group(1);
            String operator = targetMatcher.group(2);
            String expression = targetMatcher.group(3);
            targetList.add(new Target(keyword, operator, expression));
        }

        List<Permission> ruleList = new ArrayList<Permission>();

        Matcher bodyMatcher = bodyPattern.matcher(rules);
        while (bodyMatcher.find()) {
            String permission = bodyMatcher.group(1);
            String rights = bodyMatcher.group(2);
            String rule = bodyMatcher.group(3);
            ruleList.add(new Permission(permission, rights, Subject.parse(rule)));
        }

        return new AccessControlItem(name, targetList, ruleList);
	}
}