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
package com.assemblade.opendj.model;

import com.assemblade.opendj.Session;
import com.assemblade.opendj.StorageException;
import org.opends.server.types.Attribute;
import org.opends.server.types.AttributeType;
import org.opends.server.types.DN;
import org.opends.server.types.Entry;
import org.opends.server.types.Modification;
import org.opends.server.types.ObjectClass;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface Storable {
    public boolean isValid(Session session) throws StorageException;
    public String getId();
    public String getParentId();
    public String getRDN();
	public String getDn();
    public DN getDN() throws StorageException;
	public String getParentDn();
	public String getSearchFilter();
	public Map<ObjectClass, String> getObjectClasses();
	public Collection<String> getAttributeNames();
	public StorableDecorator getDecorator();
	public Map<AttributeType, List<Attribute>> getUserAttributes();
	public Map<AttributeType, List<Attribute>> getOperationalAttributes();
	public List<Modification> getModifications(Session session, Entry currentEntry) throws StorageException;
	public boolean recordChanges();
    public String getChangeLogDescription();
    public boolean requiresRename(Session session, Entry currentEntry) throws StorageException;
    public boolean requiresMove(Session session, Entry currentEntry) throws StorageException;
    public boolean requiresUpdate(Session session, Entry currentEntry) throws StorageException;
    public boolean isAddable();
    public boolean isWritable();
    public boolean isDeletable();
}
