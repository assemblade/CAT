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

import com.assemblade.opendj.model.StorableDecorator;

import java.text.MessageFormat;

public class UserNotInGroup extends AbstractUser {
	private static final long serialVersionUID = 1L;

	private static final MessageFormat searchFilterFormat = new MessageFormat("(&(objectClass=inetOrgPerson)(!(isMemberOf={0})))");

	private String groupDn;
	
	public UserNotInGroup() {
	}
	
	public UserNotInGroup(String groupDn) {
		super();
		this.groupDn = groupDn;
	}
	
	public String getSearchFilter() {
		return searchFilterFormat.format(new Object[] {groupDn});
	}

	public StorableDecorator getDecorator() {
        return new Decorator();
	}

    private class Decorator extends AbstractUser.Decorator<UserNotInGroup> {
        @Override
        public UserNotInGroup newInstance() {
            return new UserNotInGroup(groupDn);
        }
    }
}
