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

import java.util.List;

public class AccessControlItem {
	private String name;
    private List<Target> targets;
	private List<Permission> permissions;
	
	public AccessControlItem(String name, List<Target> targets, List<Permission> permissions) {
		this.name = name;
        this.targets = targets;
        this.permissions = permissions;
	}
	
	public String getName() {
		return name;
	}
	
	public List<Permission> getBindRules() {
		return permissions;
	}
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
        for (Target target : targets) {
            buffer.append(target.toString());
        }
		buffer.append("(version 3.0; acl \"" + name + "\"; " );
		for (Permission rule : permissions) {
			buffer.append(rule.toString());
		}
		buffer.append(")");
		return buffer.toString();
	}
}
