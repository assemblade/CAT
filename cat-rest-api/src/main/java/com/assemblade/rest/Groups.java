package com.assemblade.rest;

import com.assemblade.client.model.Group;
import com.assemblade.client.model.GroupMember;
import com.assemblade.client.model.User;
import com.assemblade.opendj.AssembladeErrorCode;
import com.assemblade.opendj.StorageException;
import com.assemblade.rest.mappers.GroupMapper;
import com.assemblade.rest.mappers.GroupMemberMapper;
import com.assemblade.server.model.UserNotInGroup;
import com.assemblade.server.security.AuthenticationHolder;
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
    private final GroupMemberMapper groupMemberMapper;

    public Groups(GroupManager groupManager, GroupMapper groupMapper, GroupMemberMapper groupMemberMapper) {
        this.groupManager = groupManager;
        this.groupMapper = groupMapper;
        this.groupMemberMapper = groupMemberMapper;
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
    public Response getGroupMembers(@PathParam("groupId") String groupId) {
        List<GroupMember> members = new ArrayList<GroupMember>();
        try {
            for (com.assemblade.server.model.GroupMember member : groupManager.getListOfUsersInGroup(groupId)) {
                members.add(groupMemberMapper.toClient(member));
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

    @POST
    @Path("{groupId}/members")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addMemberToGroup(@PathParam("groupId") String groupId, GroupMember groupMember) {
        try {
            return Response.ok(groupMemberMapper.toClient(groupManager.addMemberToGroup(groupMemberMapper.toServer(groupMember)))).build();
        } catch (StorageException e) {
            if ((e.getErrorCode() == AssembladeErrorCode.ASB_0006) || (e.getErrorCode() == AssembladeErrorCode.ASB_0010)) {
                return Response.status(Response.Status.NOT_FOUND).build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        }
    }

    @PUT
    @Path("{groupId}/members/{memberId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response editGroupMember(@PathParam("groupId") String groupId, @PathParam("memberId") String memberId, GroupMember groupMember) {
        try {
            return Response.ok(groupMemberMapper.toClient(groupManager.setGroupMemberAdministrativeRights(groupMemberMapper.toServer(groupMember)))).build();
        } catch (StorageException e) {
            if ((e.getErrorCode() == AssembladeErrorCode.ASB_0006) || (e.getErrorCode() == AssembladeErrorCode.ASB_0010)) {
                return Response.status(Response.Status.NOT_FOUND).build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        }
    }

    @DELETE
    @Path("{groupId}/members/{memberId}")
    public Response removeMemberFromGroup(@PathParam("groupId") String groupId, @PathParam("memberId") String memberId) {
        try {
            groupManager.removeMemberFromGroup(groupId, memberId);
        } catch (StorageException e) {
            if (e.getErrorCode() == AssembladeErrorCode.ASB_0006) {
                return Response.status(Response.Status.NOT_FOUND).build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        }
        return Response.noContent().build();
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
            return Response.ok(groupMapper.toClient(groupManager.addGroup(groupMapper.toServer(group)))).build();
        } catch (StorageException e) {
            if (e.getErrorCode() == AssembladeErrorCode.ASB_0003) {
                return Response.status(Response.Status.CONFLICT).build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        }
    }

    @PUT
    @Path("{groupId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateGroup(@PathParam("groupId") String groupId, Group group) {
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
    public Response deleteGroup(@PathParam("groupId") String groupId) {
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

    private User map(UserNotInGroup serverUser) {
        User user = new User();

        user.setUserId(serverUser.getUserId());
        user.setFullName(serverUser.getFullName());
        user.setEmailAddress(serverUser.getEmailAddress());

        return user;
    }
}
