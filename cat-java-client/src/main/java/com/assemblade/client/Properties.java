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
import com.assemblade.client.model.Property;
import org.codehaus.jackson.type.TypeReference;

import java.util.List;

public class Properties extends AbstractClient {
    public Properties(Authentication authentication) {
        super(authentication);
    }

    public List<Property> getProperties(Folder folder) throws ClientException {
        return get("/folders/" + folder.getId() + "/properties", new TypeReference<List<Property>>() {});
    }

    public Property addProperty(Property property) throws ClientException {
        return add("/folders/" + property.getFolder().getId() + "/properties", property, new TypeReference<Property>() {});
    }

    public Property updateProperty(Property property) throws ClientException {
        return update("/folders/" + property.getFolder().getId() + "/properties/" + property.getId(), property, new TypeReference<Property>() {});
    }

    public void deleteProperty(Property property) throws ClientException {
        delete("/folders/" + property.getFolder().getId() + "/properties/" + property.getId());
    }
}
