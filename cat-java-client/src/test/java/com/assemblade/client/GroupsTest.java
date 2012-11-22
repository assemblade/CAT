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
package com.assemblade.client;

import com.assemblade.client.model.Authentication;
import com.assemblade.client.model.Group;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class GroupsTest extends AbstractApiTest {
    private Groups test;

    @Before
    public void setup() throws Exception {
        Authentication authentication  = login.login("admin", "password");
        test = new Groups(authentication);
    }

    @Test
    public void getAllGroupsTest() throws Exception {
        List<Group> groups = test.getAllGroups();

        System.out.println();
    }


}
