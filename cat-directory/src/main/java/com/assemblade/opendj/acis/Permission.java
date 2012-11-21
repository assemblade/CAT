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

public class Permission {
    private String permission;
    private String rights;
    private Subject subject;

    public Permission(String permission, String rights, Subject subject) {
        this.permission = permission;
        this.rights = rights;
        this.subject = subject;
    }

    public Subject getSubject() {
        return subject;
    }

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
        buffer.append(permission);
        buffer.append('(');
        buffer.append(rights);
        buffer.append(')');
        buffer.append(subject.toString());
        buffer.append(';');
        return buffer.toString();
	}
}
