package com.assemblade.rest;

import com.assemblade.client.model.Group;
import com.assemblade.client.model.GroupMember;
import com.assemblade.client.model.User;
import com.assemblade.opendj.AssembladeErrorCode;
import com.assemblade.opendj.StorageException;
import com.assemblade.rest.mappers.GroupMapper;
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
    private final GroupMapper groupMapper;

    public Groups(GroupManager groupManager, GroupMapper groupMapper) {
        this.groupManager = groupManager;
        this.groupMapper = groupMapper;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllGroups() {
        List<Group> groups = new ArrayList<Group>();
        try {
            for (com.assemblade.server.model.Group group: groupManager.getGroups()) {
                groups.add(groupMapper.toClient(group));
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
    @Path("/administrator")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAdministratorGroup() {
        try {
            return Response.ok(groupMapper.toClient(groupManager.getAdministratorGroup())).build();
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
        List<GroupMember> members = new ArrayList<GroupMember>();
        try {
            for (com.assemblade.server.model.GroupUser user : groupManager.getListOfUsersInGroup(groupId)) {
                members.add(map(user));
            }
            return Response.ok(members).build();
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
            return Response.ok(groupMapper.toClient(groupManager.addGroup(group.getName(), group.getDescription()))).build();
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
            return Response.ok(groupMapper.toClient(groupManager.updateGroup(groupMapper.toServer(group)))).build();
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

    private GroupMember map(com.assemblade.server.model.GroupUser serverUser) {
        GroupMember member = new GroupMember();
        member.setUserId(serverUser.getUserId());
        member.setFullName(serverUser.getFullName());
        member.setEmailAddress(serverUser.getEmailAddress());
        member.setAdministrator(serverUser.isAdministrator());

        return member;
    }

    private User map(UserNotInGroup serverUser) {
        User user = new User();

        user.setUserId(serverUser.getUserId());
        user.setFullName(serverUser.getFullName());
        user.setEmailAddress(serverUser.getEmailAddress());

        return user;
    }
}
