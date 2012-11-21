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
    private Log log = LogFactory.getLog(PropertyManager.class);

	private final UserManager userManager;
	
	public PropertyManager(UserManager userManager) {
		this.userManager = userManager;
	}

//    public AbstractFolder addView(AbstractFolder view) throws StorageException {
//        try {
//            view.setOwner(userManager.getAuthenticatedUserDn());
//            userManager.getUserSession().add(view);
//            View newView = userManager.getUserSession().getByEntryDn(new View(), view.getDn());
//            newView.setParentId(view.getParentId());
//            return newView;
//        } catch (Exception e) {
//            log.error("Caught an exeception while adding view", e);
//        }
//        return view;
//    }
//
//    public void deleteView(String id) throws StorageException {
//        View view = userManager.getUserSession().getByEntryId(new View(), id);
//        log.info("Deleting view: " + view.getName());
//        userManager.getUserSession().delete(view, true);
//    }

	public Folder addFolder(Folder folder) throws StorageException {
        folder.setOwner(userManager.getAuthenticatedUserDn());
        userManager.getUserSession().add(folder);
        return userManager.getUserSession().getByEntryDn(new Folder(), folder.getDn());
	}

    public Folder getFolder(String folderId) throws StorageException {
        return userManager.getUserSession().getByEntryId(new Folder(), folderId);
    }
	
	public Folder updateFolder(Folder currentFolder, Folder updatedFolder) throws StorageException {
        if (!currentFolder.getParentId().equals(updatedFolder.getParentId())) {
            Folder newParent = userManager.getUserSession().getByEntryId(new Folder(), updatedFolder.getParentId());
            userManager.getUserSession().move(currentFolder.getDn(), currentFolder.getRDN(), newParent.getDn());
            updatedFolder = userManager.getUserSession().getByEntryId(new Folder(), currentFolder.getId());
            updatedFolder.setParentId(newParent.getId());
        }

		if (!currentFolder.getName().equals(updatedFolder.getName())) {
            userManager.getUserSession().rename(currentFolder.getDn(), updatedFolder.getRDN());
            updatedFolder = userManager.getUserSession().getByEntryId(new Folder(), currentFolder.getId());
            updatedFolder.setParentId(currentFolder.getParentId());
		}

		return updatedFolder;
	}

//    public List<AbstractFolder> getPropertyFolders(String parentId) throws StorageException {
//        List<AbstractFolder> folders = new ArrayList<AbstractFolder>();
//        Folder parentFolder = null;
//        if (StringUtils.isEmpty(parentId)) {
//            parentFolder = userManager.getUserSession().get(new Folder(Folder.FOLDER_ROOT));
//        } else {
//            parentFolder = userManager.getUserSession().getByEntryId(new Folder(), parentId);
//        }
//        if (parentFolder != null) {
//            for (Folder folder : userManager.getUserSession().search(parentFolder, parentFolder.getDn(), false)) {
//                folder.setParentId(parentId);
//                folders.add(folder);
//            }
//        }
//        return folders;
//    }

    public List<Folder> getRootFolders() throws StorageException {
        Folder parentFolder = userManager.getUserSession().get(new Folder(Folder.FOLDER_ROOT));
        if (parentFolder != null) {
            return userManager.getUserSession().search(parentFolder, parentFolder.getDn(), false).getEntries();
        } else {
            throw new StorageException(AssembladeErrorCode.ASB_0006);
        }
    }

    public List<Folder> getFolders(String parentId) throws StorageException {
        Folder parentFolder = userManager.getUserSession().getByEntryId(new Folder(), parentId);
        if (parentFolder != null) {
            return userManager.getUserSession().search(parentFolder, parentFolder.getDn(), false).getEntries();
        } else {
            throw new StorageException(AssembladeErrorCode.ASB_0006);
        }
    }

    public void deleteFolder(String folderId) throws StorageException {
        Folder folder = userManager.getUserSession().getByEntryId(new Folder(), folderId);
		userManager.getUserSession().delete(folder, true);
	}
	
	public Property addProperty(Property property) throws StorageException {
		userManager.getUserSession().add(property);

        return property;
	}
	
	public void updateProperty(Property property) throws StorageException {
		userManager.getUserSession().store(property);
	}

//	@SuppressWarnings("unchecked")
//	public List<Property> getProperties(String folder, long start, long end) throws StorageException {
//		log.info("Getting properties for folder: " + folder);
//
//		if (start == 0) {
//			ClientSessionHolder.getSession().setPropertyCookie(null);
//			ClientSessionHolder.getSession().setPageSize(0);
//		}
//		byte[] cookie = ClientSessionHolder.getSession().getPropertyCookie();
//		int pageSize = ClientSessionHolder.getSession().getPageSize();
//		
//		if (pageSize == 0) {
//			pageSize = (int)(end - start);
//			ClientSessionHolder.getSession().setPageSize(pageSize);
//		}
//
//		Session session = userManager.getUserSession();
//		
//		SearchResult result = session.search(folder == null ? ROOT : folder, PROPERTY_SEARCH_FILTER, "cn", false, false, pageSize, cookie, Property.getDecorator(), Property.getAttributeNames());
//		
//		ClientSessionHolder.getSession().setPropertyCookie(result.getCookie());
//		
//		return (List<Property>)result.getEntries();
//	}

//	@SuppressWarnings("unchecked")
//	public List<Property> getProperties(String folder) throws StorageException {
//		log.info("Getting properties for folder: " + folder);
//		return (List<Property>)userManager.getAdminSession().search(folder == null ? ROOT : folder, PROPERTY_SEARCH_FILTER, "cn", false, false, 0, null, Property.getDecorator(), Property.getAttributeNames()).getEntries();
//	}
}
