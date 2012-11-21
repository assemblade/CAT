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

public class Target {
    private String keyword;
    private String operator;
    private String expression;

    public Target(String keyword, String operator, String expression) {
        this.keyword = keyword;
        this.operator = operator;
        this.expression = expression;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append('(');
        buffer.append(keyword);
        buffer.append(operator);
        buffer.append('"');
        buffer.append(expression);
        buffer.append("\")");
        return buffer.toString();
    }
}
