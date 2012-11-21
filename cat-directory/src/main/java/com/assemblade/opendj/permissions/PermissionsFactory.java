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
package com.assemblade.opendj.permissions;

public class PermissionsFactory {
	public static EntryPermissions createEntryPermissions(String permissionsString) {
		String[] permissionArray = permissionsString.split(",");
		
		boolean add = false;
		boolean delete = false;
		boolean read = false;
		boolean write = false;
		boolean proxy = false;
		
		for (String permission : permissionArray) {
			String[] parts = permission.split(":");
			
			if (parts.length == 2) {
				if (parts[0].equals("add")) {
					add = parts[1].equals("1");
				} else if (parts[0].equals("delete")) {
					delete = parts[1].equals("1");
				} else if (parts[0].equals("read")) {
					read = parts[1].equals("1");
				} else if (parts[0].equals("write")) {
					write = parts[1].equals("1");
				} else if (parts[0].equals("proxy")) {
					proxy = parts[1].equals("1");
				}
			}
		}
		
		EntryPermissions permissions = new EntryPermissions(add, delete, read, write, proxy);
		
		return permissions;
	}

	public static AttributePermissions createAttributePermissions(String attributeName, String permissionsString) {
		String[] permissionArray = permissionsString.split(",");
		
		boolean search = false;
		boolean read = false;
		boolean compare = false;
		boolean write = false;
		boolean selfWriteAdd = false;
		boolean selfWriteDelete = false;
		boolean proxy = false;
		
		for (String permission : permissionArray) {
			String[] parts = permission.split(":");
			
			if (parts.length == 2) {
				if (parts[0].equals("search")) {
					search = parts[1].equals("1");
				} else if (parts[0].equals("read")) {
					read = parts[1].equals("1");
				} else if (parts[0].equals("compare")) {
					compare = parts[1].equals("1");
				} else if (parts[0].equals("write")) {
					write = parts[1].equals("1");
				} else if (parts[0].equals("selfwrite_add")) {
					selfWriteAdd = parts[1].equals("1");
				} else if (parts[0].equals("selfwrite_delete")) {
					selfWriteDelete = parts[1].equals("1");
				} else if (parts[0].equals("proxy")) {
					proxy = parts[1].equals("1");
				}
			}
		}
		
		AttributePermissions permissions = new AttributePermissions(attributeName, search, read, compare, write, selfWriteAdd, selfWriteDelete, proxy);
		
		return permissions;
	}
}
