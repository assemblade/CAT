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

import java.io.Serializable;

public enum AssembladeErrorCode implements Serializable {
	
	ASB_0001("ASB_0001", "Failed to start the storage server due to a configuration problem"),
	ASB_0002("ASB_0002", "Failed to start the storage server due to an initialization problem"),
	ASB_0003("ASB_0003", "Failed to add an entry"),
	ASB_0004("ASB_0004", "Failed to get a session for a user"),
	ASB_0005("ASB_0005", "Failed to update an entry"),
	ASB_0006("ASB_0006", "Failed to get an entry"),
	ASB_0007("ASB_0007", "Failed to delete an entry"),
    ASB_0008("ASB_0008", "Failed to rename an entry"),
    ASB_0009("ASB_0009", "Failed to move an entry"),
    ASB_0010("ASB_0010", "Search failed"),
    ASB_0011("ASB_0011", "Failed to change password"),
    ASB_0012("ASB_0012", "Failed to encode a URL"),
    ASB_0013("ASB_0013", "Storable object is invalid"),
    ASB_0014("ASB_0014", "Failed to access a remote user"),
    ASB_0015("ASB_0015", "Failed to delete an access token"),
    ASB_9999("ASB_9999", "General error");
    ;

	private final String code;
	private final String description;

	private AssembladeErrorCode(String code, String description) {
		this.code = code;
		this.description = description;
	}

	public String getCode() {
		return code;
	}
	
	public String getDescription() {
		return description;
	}
}
