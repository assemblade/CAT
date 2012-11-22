package com.assemblade.client;

import com.assemblade.client.model.Authentication;
import com.assemblade.client.model.User;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.util.URIUtil;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Users extends AbstractClient {
    public Users(Authentication authentication) {
        super(authentication);
    }

    public List<User> getAllUsers() {
        GetMethod get = new GetMethod(baseUrl + "/users");
        if (executeMethod(get) == 200) {
            try {
                return mapper.readValue(get.getResponseBodyAsStream(), new TypeReference<List<User>>(){});
            } catch (IOException e) {
            }
        }
        return new ArrayList<User>();
    }

    public User getAuthenticatedUser() {
        GetMethod get = new GetMethod(baseUrl + "/users/current");
        if (executeMethod(get) == 200) {
            try {
                return mapper.readValue(get.getResponseBodyAsStream(), User.class);
            } catch (IOException e) {
            }
        }
        return null;
    }

    public User addUser(User user) {
        PostMethod post = new PostMethod(baseUrl + "/users");
        try {
            post.setRequestEntity(new StringRequestEntity(mapper.writeValueAsString(user), "application/json", null));
            if (executeMethod(post) == 200) {
                return mapper.readValue(post.getResponseBodyAsStream(), User.class);
            }
        } catch (IOException e) {
        }
        return null;
    }

    public User updateUser(User user) {
        PutMethod put = new PutMethod(baseUrl + "/users");
        try {
            put.setRequestEntity(new StringRequestEntity(mapper.writeValueAsString(user), "application/json", null));
            if (executeMethod(put) == 200) {
                return mapper.readValue(put.getResponseBodyAsStream(), User.class);
            }
        } catch (IOException e) {
        }
        return null;
    }

    public boolean deleteUser(String userId) {
        try {
            DeleteMethod delete = new DeleteMethod(baseUrl + "/users/" + URIUtil.encode(userId, URI.allowed_fragment));
            return executeMethod(delete) == 204;
        } catch (URIException e) {
        }
        return false;
    }
}
