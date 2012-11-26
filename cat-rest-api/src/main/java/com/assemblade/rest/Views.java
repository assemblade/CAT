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

import com.assemblade.client.model.Property;
import com.assemblade.client.model.View;
import com.assemblade.opendj.AssembladeErrorCode;
import com.assemblade.opendj.StorageException;
import com.assemblade.rest.mappers.PropertyMapper;
import com.assemblade.rest.mappers.ViewMapper;
import com.assemblade.server.views.ViewManager;

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

@Path("/views")
public class Views {
    private final ViewManager viewManager;
    private final ViewMapper viewMapper;
    private final PropertyMapper propertyMapper;

    public Views(ViewManager viewManager, ViewMapper viewMapper, PropertyMapper propertyMapper) {
        this.viewManager = viewManager;
        this.viewMapper = viewMapper;
        this.propertyMapper = propertyMapper;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getViews() {
        List<View> views = new ArrayList<View>();
        try {
            for (com.assemblade.server.model.View view : viewManager.getViews()) {
                views.add(viewMapper.toClient(view));
            }
            return Response.ok(views).build();
        } catch (StorageException e) {
            if ((e.getErrorCode() == AssembladeErrorCode.ASB_0006) || (e.getErrorCode() == AssembladeErrorCode.ASB_0010)) {
                return Response.status(Response.Status.NOT_FOUND).build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        }
    }

    @GET
    @Path("{viewId}/properties")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProperties(@PathParam("viewId") String viewId) {
        List<Property> properties = new ArrayList<Property>();
        try {
            for (com.assemblade.server.model.Property property : viewManager.getProperties(viewId)) {
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
    public Response addView(View view) {
        try {
            return Response.ok().entity(viewMapper.toClient(viewManager.addView(viewMapper.toServer(view)))).build();
        } catch (StorageException e) {
            if (e.getErrorCode() == AssembladeErrorCode.ASB_0003) {
                return Response.status(Response.Status.CONFLICT).build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        }
    }

    @PUT
    @Path("{viewId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response editView(@PathParam("viewId") String viewId, View view) {
        try {
            return Response.ok(viewMapper.toClient(viewManager.updateView(viewMapper.toServer(view)))).build();

        } catch (StorageException e) {
            if (e.getErrorCode() == AssembladeErrorCode.ASB_0006) {
                return Response.status(Response.Status.NOT_FOUND).build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        }
    }

    @DELETE
    @Path("{viewId}")
    public Response deleteView(@PathParam("viewId") String viewId) {
        try {
            viewManager.deleteView(viewId);
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
