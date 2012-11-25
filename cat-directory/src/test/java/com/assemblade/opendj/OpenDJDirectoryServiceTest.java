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
package com.assemblade.opendj;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@RunWith(OpenDJTestRunner.class)
public class OpenDJDirectoryServiceTest extends AbstractDirectoryServiceTest {
	@Test
	public void addAnEntryToOurRoot() throws Exception {
		TestStorable storable = new TestStorable();
		
		storable.rdn = "cn=test";
		storable.setParentDn("cn=properties,dc=assemblade,dc=com");
		storable.searchFilter = "(objectClass=assemblade-folder)";
		storable.addUserAttribute("cn", "test");
		storable.addObjectClasses("assemblade-folder");
		storable.addOperationalAttribute("assemblade-deleted", "false");

		Session session = directoryService.getSession(ADMIN_DN);
		
		session.add(storable);
		
		SearchResult<TestStorable> result = session.search(storable, "cn=properties,dc=assemblade,dc=com", false);
		
		assertEquals(1, result.size());
		assertTrue(result.get(0) instanceof TestStorable);
		
	}
}
