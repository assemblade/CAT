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

import com.assemblade.opendj.AbstractDirectoryServiceTest;
import com.assemblade.opendj.OpenDJTestRunner;
import com.assemblade.opendj.Session;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(OpenDJTestRunner.class)
public class GroupTest extends AbstractDirectoryServiceTest {
	private Session session;

	@Before
	public void setup() throws Exception {
		session = directoryService.getSession(ADMIN_DN);
	}

	@Test
	public void canGetGroup() throws Exception {
		User user = (User)session.get(new User("uid=admin,ou=users,dc=assemblade,dc=com"));
		
		Group result = (Group)session.get(new Group("globaladmin", null));
		
		assertEquals("globaladmin", result.getName());
	}
	
	@Test
	public void addNewGroupWithNoMembers() throws Exception {
		Group group = new Group("Database Administrators", "");
		
		session.add(group);
		
		Group result = (Group)session.get(new Group("Database Administrators", "Database administrators"));
		
		assertEquals("Database Administrators", result.getName());
	}
}
