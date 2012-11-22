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
package com.assemblade.client;

import com.assemblade.client.model.Authentication;
import com.assemblade.client.model.Folder;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Folders extends AbstractClient {
    public Folders(Authentication authentication) {
        super(authentication);
    }

    public List<Folder> getRootFolders() {
        GetMethod get = new GetMethod(baseUrl + "/folders");
        try {
            if (executeMethod(get) == 200) {
                try {
                    return mapper.readValue(get.getResponseBodyAsStream(), new TypeReference<List<Folder>>(){});
                } catch (IOException e) {
                }
            }
        } finally {
            get.releaseConnection();
        }
        return new ArrayList<Folder>();
    }

    public List<Folder> getChildFolders(Folder parent) {
        GetMethod get = new GetMethod(baseUrl + "/folders/" + parent.getId());
        try {
            if (executeMethod(get) == 200) {
                try {
                    return mapper.readValue(get.getResponseBodyAsStream(), new TypeReference<List<Folder>>(){});
                } catch (IOException e) {
                }
            }
        } finally {
            get.releaseConnection();
        }
        return new ArrayList<Folder>();
    }

    public Folder addRootFolder(Folder folder) {
        PostMethod post = new PostMethod(baseUrl + "/folders");
        try {
            try {
                post.setRequestEntity(new StringRequestEntity(mapper.writeValueAsString(folder), "application/json", null));
                if (executeMethod(post) == 200) {
                    return mapper.readValue(post.getResponseBodyAsStream(), Folder.class);
                }
            } catch (IOException e) {
            }
        } finally {
            post.releaseConnection();
        }
        return null;
    }

    public Folder addChildFolder(Folder parent, Folder folder) {
        PostMethod post = new PostMethod(baseUrl + "/folders/" + parent.getId());
        try {
            try {
                post.setRequestEntity(new StringRequestEntity(mapper.writeValueAsString(folder), "application/json", null));
                if (executeMethod(post) == 200) {
                    return mapper.readValue(post.getResponseBodyAsStream(), Folder.class);
                }
            } catch (IOException e) {
            }
        } finally {
            post.releaseConnection();
        }
        return null;
    }

    public Folder updateFolder(Folder folder) {
        PutMethod put = new PutMethod(baseUrl + "/folders/" + folder.getId());
        try {
            try {
                put.setRequestEntity(new StringRequestEntity(mapper.writeValueAsString(folder), "application/json", null));
                if (executeMethod(put) == 200) {
                    return mapper.readValue(put.getResponseBodyAsStream(), Folder.class);
                }
            } catch (IOException e) {
            }
        } finally {
            put.releaseConnection();
        }
        return null;
    }

    public boolean deleteFolder(Folder folder) {
        DeleteMethod delete = new DeleteMethod(baseUrl + "/folders/" + folder.getId());
        try {
            return executeMethod(delete) == 204;
        } finally {
            delete.releaseConnection();
        }
    }
}
