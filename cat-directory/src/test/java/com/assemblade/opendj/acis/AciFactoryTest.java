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

import com.assemblade.opendj.AbstractDirectoryServiceTest;
import com.assemblade.opendj.OpenDJTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

public class AciFactoryTest {
	String aciText = "(targetscope=\"subtree\")(version 3.0; acl \"read\"; allow(read,search,compare,export) userdn!=\"ldap:///uid=admin,ou=users,dc=assemblade,dc=com\" AND groupdn=\"ldap:///ou=users,dc=assemblade,dc=com||ldap:///cn=globaladmin,ou=groups,dc=assemblade,dc=com\";)";

    @Test
	public void parseAci() throws Exception {
		AccessControlItem aci = AciFactory.parse(aciText);

        String actual = aci.toString();

        assertEquals(actual, aciText);
	}
	

}
