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
package com.assemblade.rest;

import com.assemblade.client.model.Folder;
import com.assemblade.client.model.Property;
import com.assemblade.opendj.AssembladeErrorCode;
import com.assemblade.opendj.StorageException;
import com.assemblade.rest.mappers.FolderMapper;
import com.assemblade.rest.mappers.PropertyMapper;
import com.assemblade.server.properties.PropertyManager;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/folders")
public class Folders {
    private final PropertyManager propertyManager;
    private final FolderMapper folderMapper;
    private final PropertyMapper propertyMapper;

    public Folders(PropertyManager propertyManager, FolderMapper folderMapper, PropertyMapper propertyMapper) {
        this.propertyManager = propertyManager;
        this.folderMapper = folderMapper;
        this.propertyMapper = propertyMapper;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRootFolders() {
        try {
            List<Folder> folders = new ArrayList<Folder>();
            for (com.assemblade.server.model.Folder folder : propertyManager.getRootFolders()) {
                folders.add(folderMapper.toClient(folder));
            }
            return Response.ok(folders).build();
        } catch (StorageException e) {
            if ((e.getErrorCode() == AssembladeErrorCode.ASB_0006) || (e.getErrorCode() == AssembladeErrorCode.ASB_0010)) {
                return Response.status(Response.Status.NOT_FOUND).build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        }
    }

    @GET
    @Path("{folderId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFolder(@PathParam("folderId") String folderId) {
        try {
            return Response.ok(folderMapper.toClient(propertyManager.getFolder(folderId))).build();
        } catch (StorageException e) {
            if ((e.getErrorCode() == AssembladeErrorCode.ASB_0006) || (e.getErrorCode() == AssembladeErrorCode.ASB_0010)) {
                return Response.status(Response.Status.NOT_FOUND).build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        }
    }

    @GET
    @Path("{folderId}/folders")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getChildFolders(@PathParam("folderId") String folderId) {
        try {
            List<Folder> folders = new ArrayList<Folder>();
            for (com.assemblade.server.model.Folder folder : propertyManager.getFolders(folderId)) {
                folders.add(folderMapper.toClient(folder));
            }
            return Response.ok(folders).build();
        } catch (StorageException e) {
            if ((e.getErrorCode() == AssembladeErrorCode.ASB_0006) || (e.getErrorCode() == AssembladeErrorCode.ASB_0010)) {
                return Response.status(Response.Status.NOT_FOUND).build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        }
    }

    @GET
    @Path("{folderId}/properties")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProperties(@PathParam("folderId") String folderId) {
        try {
            List<Property> properties = new ArrayList<Property>();
            for (com.assemblade.server.model.Property property : propertyManager.getProperties(folderId)) {
                properties.add(propertyMapper.toClient(property));
            }
            return Response.ok(properties).build();
        } catch (StorageException e) {
            if ((e.getErrorCode() == AssembladeErrorCode.ASB_0006) || (e.getErrorCode() == AssembladeErrorCode.ASB_0010)) {
                return Response.status(Response.Status.NOT_FOUND).build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addRootFolder(Folder folder) {
        try {
            folder = folderMapper.toClient(propertyManager.addFolder(folderMapper.toServer(folder)));
            return Response.ok().entity(folder).build();
        } catch (StorageException e) {
            if (e.getErrorCode() == AssembladeErrorCode.ASB_0003) {
                return Response.status(Response.Status.CONFLICT).build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        }
    }

    @POST
    @Path("{parentId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addChildFolder(@PathParam("parentId") String parentId, Folder folder) {
        try {
            com.assemblade.server.model.Folder parentFolder = propertyManager.getFolder(parentId);
            com.assemblade.server.model.Folder newFolder = folderMapper.toServer(folder);
            newFolder.setParentDn(parentFolder.getDn());
            folder = folderMapper.toClient(propertyManager.addFolder(newFolder));
            return Response.ok(folder).build();
        } catch (StorageException e) {
            if (e.getErrorCode() == AssembladeErrorCode.ASB_0003) {
                return Response.status(Response.Status.CONFLICT).build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        }
    }

    @POST
    @Path("{folderId}/properties")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addProperty(@PathParam("folderId") String folderId, Property property) {
        try {
            com.assemblade.server.model.Folder folder = propertyManager.getFolder(folderId);
            com.assemblade.server.model.Property serverProperty = propertyMapper.toServer(property);
            serverProperty.setParentDn(folder.getDn());
            property = propertyMapper.toClient(propertyManager.addProperty(serverProperty));
            return Response.ok(property).build();
        } catch (StorageException e) {
            if (e.getErrorCode() == AssembladeErrorCode.ASB_0003) {
                return Response.status(Response.Status.CONFLICT).build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        }
    }

    @PUT
    @Path("{folderId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response editFolder(@PathParam("folderId") String folderId, Folder folder) {
        try {
            folder = folderMapper.toClient(propertyManager.updateFolder(folderMapper.toServer(folder)));
            return Response.ok(folder).build();

        } catch (StorageException e) {
            if (e.getErrorCode() == AssembladeErrorCode.ASB_0006) {
                return Response.status(Response.Status.NOT_FOUND).build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        }
    }

    @PUT
    @Path("{folderId}/properties/{propertyId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response editProperty(@PathParam("folderId") String folderId, @PathParam("propertyId") String propertyId, Property property) {
        try {
            property = propertyMapper.toClient(propertyManager.updateProperty(propertyMapper.toServer(property)));
            return Response.ok(property).build();

        } catch (StorageException e) {
            if (e.getErrorCode() == AssembladeErrorCode.ASB_0006) {
                return Response.status(Response.Status.NOT_FOUND).build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        }
    }

    @DELETE
    @Path("{folderId}")
    public Response deleteFolder(@PathParam("folderId") String folderId) {
        try {
            propertyManager.deleteFolder(folderId);
        } catch (StorageException e) {
            if (e.getErrorCode() == AssembladeErrorCode.ASB_0006) {
                return Response.status(Response.Status.NOT_FOUND).build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        }
        return Response.noContent().build();
    }

    @DELETE
    @Path("{folderId}/properties/{propertyId}")
    public Response deleteProperty(@PathParam("folderId") String folderId, @PathParam("propertyId") String propertyId) {
        try {
            propertyManager.deleteProperty(propertyId);
        } catch (StorageException e) {
            if (e.getErrorCode() == AssembladeErrorCode.ASB_0006) {
                return Response.status(Response.Status.NOT_FOUND).build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        }
        return Response.noContent().build();
    }
}
