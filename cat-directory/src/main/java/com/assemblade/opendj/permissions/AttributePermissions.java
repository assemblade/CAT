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

import java.io.Serializable;

public class AttributePermissions implements Serializable {
	private static final long serialVersionUID = 1L;
	private String attributeName;
	private boolean search;
	private boolean read;
	private boolean compare;
	private boolean write;
	private boolean selfWriteAdd;
	private boolean selfWriteDelete;
	private boolean proxy;
	
	public AttributePermissions(String attributeName, boolean search, boolean read, boolean compare, boolean write, boolean selfWriteAdd, boolean selfWriteDelete, boolean proxy) {
		this.attributeName = attributeName;
		this.search = search;
		this.read = read;
		this.compare = compare;
		this.write = write;
		this.selfWriteAdd = selfWriteAdd;
		this.selfWriteDelete = selfWriteDelete;
		this.proxy = proxy;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public boolean canSearch() {
		return search;
	}

	public boolean canRead() {
		return read;
	}

	public boolean canCompare() {
		return compare;
	}

	public boolean canWrite() {
		return write;
	}

	public boolean canSelfWriteAdd() {
		return selfWriteAdd;
	}

	public boolean canSelfWriteDelete() {
		return selfWriteDelete;
	}

	public boolean canProxy() {
		return proxy;
	}

	
}
