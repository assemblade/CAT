package com.assemblade.rest;

import com.assemblade.client.model.Group;
import com.assemblade.client.model.GroupUser;
import com.assemblade.client.model.User;
import com.assemblade.opendj.AssembladeErrorCode;
import com.assemblade.opendj.StorageException;
import com.assemblade.server.model.UserNotInGroup;
import com.assemblade.server.users.GroupManager;

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

@Path("/groups")
public class Groups {
    private final GroupManager groupManager;

    public Groups(GroupManager groupManager) {
        this.groupManager = groupManager;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllGroups() {
        List<Group> groups = new ArrayList<Group>();
        try {
            for (com.assemblade.server.model.Group group: groupManager.getGroups()) {
                groups.add(map(group));
            }
            return Response.ok(groups).build();
        } catch (StorageException e) {
            if ((e.getErrorCode() == AssembladeErrorCode.ASB_0006) || (e.getErrorCode() == AssembladeErrorCode.ASB_0010)) {
                return Response.status(Response.Status.NOT_FOUND).build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        }
    }

    @GET
    @Path("{groupId}/members")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsersInGroup(@PathParam("groupId") String groupId) {
        List<GroupUser> users = new ArrayList<GroupUser>();
        try {
            for (com.assemblade.server.model.GroupUser user : groupManager.getListOfUsersInGroup(groupId)) {
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
    @Path("{groupId}/nonmembers")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsersNotInGroup(@PathParam("groupId") String groupId) {
        List<User> users = new ArrayList<User>();
        try {
            for (com.assemblade.server.model.UserNotInGroup user : groupManager.getListOfUsersNotInGroup(groupId)) {
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

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addGroup(Group group) {
        try {
            return Response.ok(map(groupManager.addGroup(group.getName(), group.getDescription()))).build();
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
    public Response updateGroup(Group group) {
        try {
            return Response.ok(map(groupManager.updateGroup(map(group)))).build();
        } catch (StorageException e) {
            if (e.getErrorCode() == AssembladeErrorCode.ASB_0003) {
                return Response.status(Response.Status.CONFLICT).build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        }
    }

    @DELETE
    @Path("{groupId}")
    public Response deleteFolder(@PathParam("groupId") String groupId) {
        try {
            groupManager.deleteGroup(groupId);
        } catch (StorageException e) {
            if (e.getErrorCode() == AssembladeErrorCode.ASB_0006) {
                return Response.status(Response.Status.NOT_FOUND).build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        }
        return Response.noContent().build();
    }

    private Group map(com.assemblade.server.model.Group serverGroup) {
        Group group = new Group();

        group.setId(serverGroup.getId());
        group.setName(serverGroup.getDisplayName());
        group.setDescription(serverGroup.getDescription());
        group.setWritable(serverGroup.isWritable());
        group.setDeletable(serverGroup.isDeletable());
        group.setType(serverGroup.getType());

        return group;
    }

    private GroupUser map(com.assemblade.server.model.GroupUser serverUser) {
        GroupUser user = new GroupUser();
        user.setUserId(serverUser.getUserId());
        user.setFullName(serverUser.getFullName());
        user.setEmailAddress(serverUser.getEmailAddress());
        user.setAdministrator(serverUser.isAdministrator());

        return user;
    }

    private User map(UserNotInGroup serverUser) {
        User user = new User();

        user.setUserId(serverUser.getUserId());
        user.setFullName(serverUser.getFullName());
        user.setEmailAddress(serverUser.getEmailAddress());

        return user;
    }

    private com.assemblade.server.model.Group map(Group group) {
        com.assemblade.server.model.Group serverGroup = new com.assemblade.server.model.Group();
        serverGroup.setId(group.getId());
        serverGroup.setName(group.getName());
        serverGroup.setDescription(group.getDescription());

        return serverGroup;
    }
}
