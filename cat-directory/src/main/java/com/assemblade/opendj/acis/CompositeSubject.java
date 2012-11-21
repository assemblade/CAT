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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CompositeSubject extends Subject {
    private static final Pattern compositePattern = Pattern.compile(subjectRemainder);
    private String operand;
    private Subject left;
    private Subject right;

    public CompositeSubject(Subject left, Subject right, String operand) {
        this.left = left;
        this.right = right;
        this.operand = operand;
    }

    public List<String> getDns() {
        return new ArrayList<String>();
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(left);
        buffer.append(' ');
        buffer.append(operand);
        buffer.append(right);
        return buffer.toString();
    }

    public static Subject parse(Subject left, String text) {
        Matcher compositeMatcher = compositePattern.matcher(text);
        if (compositeMatcher.find()) {
            String operand = compositeMatcher.group(1).trim();
            String right = compositeMatcher.group(2);
            if (StringUtils.isNotEmpty(operand) && (operand.equalsIgnoreCase("AND") || operand.equalsIgnoreCase("OR"))) {
                return new CompositeSubject(left, Subject.parse(right), operand);
            }
        }
        return null;
    }

}
