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
package com.assemblade.server.properties;

import com.assemblade.opendj.AssembladeErrorCode;
import com.assemblade.opendj.StorageException;
import com.assemblade.server.model.Folder;
import com.assemblade.server.model.Property;
import com.assemblade.server.users.UserManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

public class PropertyManager {
	private final UserManager userManager;
	
	public PropertyManager(UserManager userManager) {
		this.userManager = userManager;
	}

	public Folder addFolder(Folder folder) throws StorageException {
        folder.setOwner(userManager.getAuthenticatedUserDn());
        userManager.getUserSession().add(folder);
        return userManager.getUserSession().getByEntryDn(new Folder(), folder.getDn());
	}

    public Folder getFolder(String folderId) throws StorageException {
        return userManager.getUserSession().getByEntryDn(new Folder(), userManager.getUserSession().dnFromId(folderId));
    }
	
	public Folder updateFolder(Folder updatedFolder) throws StorageException {
        updatedFolder.setOwner(userManager.getAuthenticatedUserDn());

        userManager.getUserSession().update(updatedFolder);

        return userManager.getUserSession().get(updatedFolder);
	}

    public List<Folder> getRootFolders() throws StorageException {
        return userManager.getUserSession().search(new Folder(), Folder.FOLDER_ROOT, false);
    }

    public List<Folder> getAllFolders() throws StorageException {
        return userManager.getUserSession().search(new Folder(), Folder.FOLDER_ROOT, true);
    }

    public List<Folder> getFolders(String parentId) throws StorageException {
        Folder parentFolder = userManager.getUserSession().getByEntryId(new Folder(), parentId);
        return userManager.getUserSession().search(parentFolder, parentFolder.getDn(), false);
    }

    public void deleteFolder(String folderId) throws StorageException {
        Folder folder = userManager.getUserSession().getByEntryId(new Folder(), folderId);
		userManager.getUserSession().delete(folder, true);
	}

    public List<Property> getProperties(String folderId) throws StorageException {
        Folder folder = userManager.getUserSession().getByEntryId(new Folder(), folderId);
        return userManager.getUserSession().search(new Property(), folder.getDn(), false);
    }

	public Property addProperty(Property property) throws StorageException {
		userManager.getUserSession().add(property);

        return userManager.getUserSession().get(property);
	}

    public Property getProperty(String propertyId) throws StorageException {
        return userManager.getUserSession().getByEntryId(new Property(), propertyId);
    }


    public Property updateProperty(Property updated) throws StorageException {
        userManager.getUserSession().update(updated);
        return userManager.getUserSession().get(updated);
	}

    public void deleteProperty(String propertyId) throws StorageException {
        Property property = userManager.getUserSession().getByEntryId(new Property(), propertyId);
        userManager.getUserSession().delete(property, true);
    }
}
