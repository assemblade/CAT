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

import com.assemblade.opendj.Session;
import com.assemblade.opendj.StorageException;
import com.assemblade.opendj.model.StorableDecorator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.opends.server.core.DirectoryServer;
import org.opends.server.types.Entry;
import org.opends.server.types.ObjectClass;

import java.util.Map;

public class Folder extends AbstractFolder {
	private static final long serialVersionUID = 1L;
	
	public static final String FOLDER_ROOT = "ou=properties,dc=assemblade,dc=com";

    @Override
    public String getSearchFilter() {
		return "(objectClass=asb-folder)";
	}

    @Override
    public StorableDecorator<Folder> getDecorator() {
        return new Decorator();
	}

    @Override
    public Map<ObjectClass, String> getObjectClasses() {
        Map<ObjectClass, String> objectClasses = super.getObjectClasses();
        objectClasses.put(DirectoryServer.getObjectClass("asb-folder"), "asb-folder");
        return objectClasses;
    }

    @Override
    public boolean canHaveChildren() {
        return true;
    }

    @Override
    public String getPermissionsAttributes() {
        return "objectclass || entryUUID || cn || description || aci || aclRights";
    }

    @Override
    public String getRootPermissions() {
        return "read,search";
    }

    @Override
	public String toString() {
		return "Folder ["+ name +"]";
	}

    @Override
    public String getChangeLogDescription() {
        return "Folder[name = " + name + "]";
    }

    protected class Decorator extends AbstractFolder.Decorator<Folder> {
        @Override
        public Folder newInstance() {
            return new Folder();
        }

        @Override
        public Folder decorate(Session session, Entry entry) throws StorageException {
            return super.decorate(session, entry);
        }
    }
}
