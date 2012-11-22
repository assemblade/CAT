package com.assemblade.client;

import com.assemblade.client.model.Authentication;
import com.assemblade.client.model.Group;
import com.assemblade.client.model.GroupUser;
import com.assemblade.client.model.User;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Groups extends AbstractClient {
    public Groups(Authentication authentication) {
        super(authentication);
    }

    public List<Group> getAllGroups() {
        GetMethod get = new GetMethod(baseUrl + "/groups");
        if (executeMethod(get) == 200) {
            try {
                return mapper.readValue(get.getResponseBodyAsStream(), new TypeReference<List<Group>>(){});
            } catch (IOException e) {
            }
        }
        return new ArrayList<Group>();
    }

    public List<GroupUser> getUsersInGroup(String groupId) {
        GetMethod get = new GetMethod(baseUrl + "/groups/" + groupId + "/members");
        if (executeMethod(get) == 200) {
            try {
                return mapper.readValue(get.getResponseBodyAsStream(), new TypeReference<List<GroupUser>>(){});
            } catch (IOException e) {
            }
        }
        return new ArrayList<GroupUser>();
    }

    public List<User> getUsersNotInGroup(String groupId) {
        GetMethod get = new GetMethod(baseUrl + "/groups/" + groupId + "/nonmembers");
        if (executeMethod(get) == 200) {
            try {
                return mapper.readValue(get.getResponseBodyAsStream(), new TypeReference<List<User>>(){});
            } catch (IOException e) {
            }
        }
        return new ArrayList<User>();
    }

    public Group addGroup(Group group) {
        PostMethod post = new PostMethod(baseUrl + "/groups");
        try {
            post.setRequestEntity(new StringRequestEntity(mapper.writeValueAsString(group), "application/json", null));
            if (executeMethod(post) == 200) {
                return mapper.readValue(post.getResponseBodyAsStream(), Group.class);
            }
        } catch (IOException e) {
        }
        return null;
    }

    public Group updateGroup(Group group) {
        PutMethod put = new PutMethod(baseUrl + "/groups");
        try {
            put.setRequestEntity(new StringRequestEntity(mapper.writeValueAsString(group), "application/json", null));
            if (executeMethod(put) == 200) {
                return mapper.readValue(put.getResponseBodyAsStream(), Group.class);
            }
        } catch (IOException e) {
        }
        return null;
    }

    public boolean deleteGroup(Group group) {
        DeleteMethod delete = new DeleteMethod(baseUrl + "/groups/" + group.getId());
        return executeMethod(delete) == 204;
    }

}
