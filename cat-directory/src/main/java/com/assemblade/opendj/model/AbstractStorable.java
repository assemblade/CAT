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

import com.assemblade.opendj.AssembladeErrorCode;
import com.assemblade.opendj.LdapUtils;
import com.assemblade.opendj.StorageException;
import com.assemblade.opendj.permissions.EntryPermissions;
import org.apache.commons.lang.StringUtils;
import org.opends.server.core.DirectoryServer;
import org.opends.server.types.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public abstract class AbstractStorable implements Storable, Serializable {
	private static final long serialVersionUID = 1L;

    protected String parentDn;
    protected String id;
    protected String parentId;
    protected boolean addable;
    protected boolean writable;
    protected boolean deletable;

    public AbstractStorable() {
    }

    public DN getDN() throws StorageException {
        try {
            return DN.decode(getDn());
        } catch (DirectoryException e) {
            throw new StorageException(AssembladeErrorCode.ASB_9999);
        }
    }

    public String getDn() {
        return getRDN() + "," + getParentDn();
    }

    public String getId() {
        return id;
    }

    @Override
    public String getParentDn() {
        return parentDn;
    }

    @Override
    public String getParentId() {
        return parentId;
    }

    @Override
    public String getSearchFilter() {
        return "(objectClass=*)";
    }

    @Override
    public Map<ObjectClass, String> getObjectClasses() {
        return new HashMap<ObjectClass, String>();
    }

    @Override
    public Collection<String> getAttributeNames() {
        return new ArrayList<String>(Arrays.asList("entryUUID"));
    }

    @Override
    public Map<AttributeType, List<Attribute>> getUserAttributes() {
        return new HashMap<AttributeType, List<Attribute>>();
    }

    @Override
    public List<Modification> getModifications(Entry currentEntry) {
		return new ArrayList<Modification>();
	}

    @Override
	public Map<AttributeType, List<Attribute>> getOperationalAttributes() {
		return new HashMap<AttributeType, List<Attribute>>();
	}

    @Override
    public boolean recordChanges() {
		return false;
	}
	
    public String getChangeLogDescription() {
        return "";
    }

    public boolean requiresRename(Entry currentEntry) {
        return false;
    }

    public boolean requiresMove(Entry currentEntry) {
        return !getParentDn().equals(currentEntry.getDN().getParent().toString());
    }

    public boolean requiresUpdate(Entry currentEntry) {
        return false;
    }

    public boolean isAddable() {
        return addable;
    }

    public boolean isWritable() {
        return writable;
    }

    public boolean isDeletable() {
        return deletable;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setParentDn(String parentDn) {
        this.parentDn = parentDn;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractStorable that = (AbstractStorable) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    protected abstract class Decorator<T extends AbstractStorable> implements StorableDecorator<T> {
        @Override
        public T decorate(Entry entry) {
            T storable = newInstance();

            storable.id = LdapUtils.getSingleAttributeStringValue(entry.getAttribute("entryuuid"));
            storable.parentDn = entry.getDN().getParent().toString();

            try {
                storable.parentId = LdapUtils.getSingleAttributeStringValue(DirectoryServer.getEntry(entry.getDN().getParent()).getAttribute("entryuuid"));
            } catch (DirectoryException e) {
            }

            EntryPermissions permissions = LdapUtils.getEntryPermissions(entry.getAttributes());
            if (permissions != null) {
                storable.addable = permissions.canAdd();
                storable.writable = permissions.canWrite();
                storable.deletable = permissions.canDelete();
            }

            return storable;
        }
    }
}
