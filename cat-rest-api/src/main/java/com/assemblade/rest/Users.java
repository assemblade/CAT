package com.assemblade.rest;

import com.assemblade.client.model.User;
import com.assemblade.opendj.AssembladeErrorCode;
import com.assemblade.opendj.StorageException;
import com.assemblade.rest.mappers.UserMapper;
import com.assemblade.server.users.UserManager;

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

@Path("/users")
public class Users {
    private final UserManager userManager;
    private final UserMapper userMapper;

    public Users(UserManager userManager, UserMapper userMapper) {
        this.userManager = userManager;
        this.userMapper = userMapper;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsers() {
        List<User> users = new ArrayList<User>();
        try {
            for (com.assemblade.server.model.User user : userManager.getUsers()) {
                users.add(userMapper.toClient(user));
            }
            return Response.ok(users).build();
        } catch (StorageException e) {
            if ((e.getErrorCode() == AssembladeErrorCode.ASB_0006) || (e.getErrorCode() == AssembladeErrorCode.ASB_0010)) {
                return Response.status(Response.Status.NOT_FOUND).build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        }
    }

    @GET
    @Path("/current")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAuthenticatedUser() {
        return Response.ok(userMapper.toClient(userManager.getAuthenticatedUser())).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addUser(User user) {
        try {
            return Response.ok().entity(userMapper.toClient(userManager.addUser(userMapper.toServer(user)))).build();
        } catch (StorageException e) {
            if (e.getErrorCode() == AssembladeErrorCode.ASB_0003) {
                return Response.status(Response.Status.CONFLICT).build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        }
    }

    @PUT
    @Path("{userId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response editUser(@PathParam("userId") String userId, User user) {
        try {
            return Response.ok(userMapper.toClient(userManager.updateUser(userMapper.toServer(user)))).build();

        } catch (StorageException e) {
            if (e.getErrorCode() == AssembladeErrorCode.ASB_0006) {
                return Response.status(Response.Status.NOT_FOUND).build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        }
    }

    @DELETE
    @Path("{userId}")
    public Response deleteUser(@PathParam("userId") String userId) {
        try {
            userManager.deleteUser(userId);
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
