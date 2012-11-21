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

import org.apache.commons.lang.StringUtils;

import java.util.List;

public abstract class Subject implements AciPatterns {
    public abstract List<String> getDns();

    public static Subject parse(String rule) {
        if (StringUtils.isNotEmpty(rule)) {
            rule = rule.trim();
            char[] ruleArray = rule.toCharArray();

            if (rule.startsWith("(")) {
                int brackets = 0;

                for (int position = 0; position < ruleArray.length; position++) {
                    if (ruleArray[position] == '(') {
                        brackets++;
                    }
                    else if (ruleArray[position] == ')') {
                        brackets--;
                    }
                    if (brackets == 0) {
                        Subject subject = Subject.parse(rule.substring(1, position));
                        if (position < (ruleArray.length - 1)) {
                            return CompositeSubject.parse(subject, rule.substring(position + 1));
                        }
                        return subject;
                    }
                }
            } else {
                return PermissionSubject.parse(rule);
            }
        }
        return null;
    }
}
