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
import org.codehaus.jackson.type.TypeReference;

import java.util.List;

public class Folders extends AbstractClient {
    public Folders(Authentication authentication) {
        super(authentication);
    }

    public List<Folder> getRootFolders() throws ClientException {
        return get("/folders", new TypeReference<List<Folder>>() {});
    }

    public List<Folder> getChildFolders(Folder parent) throws ClientException {
        return get("/folders/" + parent.getId(), new TypeReference<List<Folder>>() {});
    }

    public Folder addRootFolder(Folder folder) throws ClientException {
        return add("/folders", folder, new TypeReference<Folder>() {});
    }

    public Folder addChildFolder(Folder parent, Folder folder) throws ClientException {
        return add("/folders/" + parent.getId(), folder, new TypeReference<Folder>() {});
    }

    public Folder updateFolder(Folder folder) throws ClientException {
        return update("/folders/" + folder.getId(), folder, new TypeReference<Folder>() {});
    }

    public void deleteFolder(Folder folder) throws ClientException {
        delete("/folders/" + folder.getId());
    }
}
