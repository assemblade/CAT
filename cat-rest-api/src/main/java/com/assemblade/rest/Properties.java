package com.assemblade.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/properties")
public class Properties {
    @GET
    public String getRoot() {
        return "root";
    }

    @GET
    @Path("{folder: [a-zA-Z\\s/]*}")
    public String get(@PathParam("folder") String subResources) {
        return subResources;
    }
}
