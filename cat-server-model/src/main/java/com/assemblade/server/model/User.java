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

import com.assemblade.opendj.Session;
import com.assemblade.opendj.model.StorableDecorator;
import org.opends.server.types.Entry;

public class User extends AbstractUser {
	private static final long serialVersionUID = 1L;

	public StorableDecorator<User> getDecorator() {
        return new Decorator();
	}

	@Override
	public String toString() {
		return "User [userId=" + userId + ", fullName=" + fullName + ", emailAddress=" + emailAddress + "]";
	}

	public boolean recordChanges() {
		return false;
	}
	
    protected class Decorator extends AbstractUser.Decorator<User> {
        @Override
        public User newInstance() {
            return new User();
        }

        @Override
        public User decorate(Session session, Entry entry) {
            User user = super.decorate(session, entry);
            return user;
        }
    }
}
