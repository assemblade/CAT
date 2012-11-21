package com.assemblade.rest;

import com.assemblade.client.model.User;
import com.assemblade.opendj.AssembladeErrorCode;
import com.assemblade.opendj.StorageException;
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

    public Users(UserManager userManager) {
        this.userManager = userManager;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsers() {
        List<User> users = new ArrayList<User>();
        try {
            for (com.assemblade.server.model.User user : userManager.getUsers()) {
                users.add(map(user));
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
        return Response.ok(map(userManager.getAuthenticatedUser())).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addUser(User user) {
        try {
            user = map(userManager.addUser(map(user)));
            return Response.ok().entity(user).build();
        } catch (StorageException e) {
            if (e.getErrorCode() == AssembladeErrorCode.ASB_0003) {
                return Response.status(Response.Status.CONFLICT).build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response editUser(User user) {
        try {
            user = map(userManager.updateUser(map(user)));
            return Response.ok(user).build();

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

    private User map(com.assemblade.server.model.User serverUser) {
        User user = new User();
        user.setUserId(serverUser.getUserId());
        user.setFullName(serverUser.getFullName());
        user.setEmailAddress(serverUser.getEmailAddress());
        user.setGlobalAdministrator(serverUser.isGlobalAdministrator());
        user.setGroupAdministrator(serverUser.isGroupAdministrator());
        user.setAuthenticationPolicy(serverUser.getAuthenticationPolicy());
        user.setWritable(serverUser.isWritable());
        user.setDeletable(serverUser.isDeletable());
        return user;
    }

    private com.assemblade.server.model.User map(User user) {
        com.assemblade.server.model.User serverUser = new com.assemblade.server.model.User();
        serverUser.setUserId(user.getUserId());
        serverUser.setFullName(user.getFullName());
        serverUser.setEmailAddress(user.getEmailAddress());
        serverUser.setAuthenticationPolicy(user.getAuthenticationPolicy());
        serverUser.setPassword(user.getPassword());
        return serverUser;
    }
}
