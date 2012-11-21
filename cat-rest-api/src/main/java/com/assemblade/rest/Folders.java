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
import com.assemblade.opendj.AssembladeErrorCode;
import com.assemblade.opendj.StorageException;
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

    public Folders(PropertyManager propertyManager) {
        this.propertyManager = propertyManager;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRootFolders() {
        try {
            List<Folder> folders = new ArrayList<Folder>();
            for (com.assemblade.server.model.Folder folder : propertyManager.getRootFolders()) {
                folders.add(map(folder));
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
    @Path("{parentId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getChildFolders(@PathParam("parentId") String parentId) {
        try {
            List<Folder> folders = new ArrayList<Folder>();
            for (com.assemblade.server.model.Folder folder : propertyManager.getFolders(parentId)) {
                folders.add(map(folder));
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

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addRootFolder(Folder folder) {
        try {
            folder = map(propertyManager.addFolder(map(folder)));
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
        com.assemblade.server.model.Folder newFolder = map(folder);
        newFolder.setParentId(parentId);
        try {
            folder = map(propertyManager.addFolder(newFolder));
            return Response.ok(folder).build();
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
            folder = map(propertyManager.updateFolder(propertyManager.getFolder(folderId), map(folder)));
            return Response.ok(folder).build();

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

    private Folder map(com.assemblade.server.model.Folder folder) {
        Folder mapperFolder = new Folder();
        mapperFolder.setId(folder.getId());
        mapperFolder.setName(folder.getName());
        mapperFolder.setDescription(folder.getDescription());
        mapperFolder.setParentId(folder.getParentId());
        return mapperFolder;
    }

    private com.assemblade.server.model.Folder map(Folder folder) {
        com.assemblade.server.model.Folder mappedFolder = new com.assemblade.server.model.Folder();
        mappedFolder.setId(folder.getId());
        mappedFolder.setName(folder.getName());
        mappedFolder.setDescription(folder.getDescription());
        mappedFolder.setParentId(folder.getParentId());
        return mappedFolder;
    }
}
