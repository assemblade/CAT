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
import com.assemblade.client.model.Property;
import com.assemblade.client.model.View;
import org.codehaus.jackson.type.TypeReference;

import java.util.List;

public class Views extends AbstractClient {
    public Views(Authentication authentication) {
        super(authentication);
    }

    public List<View> getViews() throws ClientException {
        return get("/views", new TypeReference<List<View>>(){});
    }

    public List<Property> getProperties(View view) throws ClientException {
        return get("/views/" + view.getId() + "/properties", new TypeReference<List<Property>>() {});
    }

    public View addView(View view) throws ClientException {
        return add("/views", view, new TypeReference<View>() {});
    }

    public View updateView(View view) throws ClientException {
        return update("/views/" + view.getId(), view, new TypeReference<View>() {});
    }

    public void deleteView(View view) throws ClientException {
        delete("/views/" + view.getId());
    }

}
