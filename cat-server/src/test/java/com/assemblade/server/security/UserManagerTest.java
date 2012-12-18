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
package com.assemblade.server.security;

import com.assemblade.opendj.OpenDJTestRunner;
import com.assemblade.opendj.SearchResult;
import com.assemblade.server.AbstractUserManagementTest;
import com.assemblade.server.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(OpenDJTestRunner.class)
public class UserManagerTest extends AbstractUserManagementTest {

	@Test
	public void canAddUserPlainPasswordAndGetSessionForUser() throws Exception {
		userLogin("admin");

		addUser("test", "test user", "test@example.com", "password");
    	
		userLogin("test");

    	assertNotNull(userManager.getUserSession());
	}

	@Test
	public void canGetListOfLocalUsers() throws Exception {
		userLogin("admin");

		addUser("test1", "test1 user", "test1@example.com", "password");
		addUser("test2", "test2 user", "test2@example.com", "password");

		List<User> users = userManager.getUserSession().search(new User(), User.ROOT, false);
    	
    	assertEquals(3, users.size());
	}
}
