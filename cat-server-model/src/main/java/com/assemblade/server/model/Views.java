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
package com.assemblade.server.model;

import com.assemblade.opendj.model.StorableDecorator;
import com.assemblade.utils.localisation.Localiser;
import org.opends.server.core.DirectoryServer;
import org.opends.server.types.ObjectClass;

import java.util.Map;

public class Views extends AbstractFolder {
    private static final long serialVersionUID = 1L;

    private static final String VIEWS_DN = "cn=views,dc=assemblade,dc=com";

    public Views() {
        this.name = "views";
        this.description = Localiser.getInstance().translate(getName() + ".description");
        this.addable = true;
        this.writable = false;
        this.deletable = false;
    }

    @Override
    public String getRDN() {
        return "cn=views";
    }

    @Override
    protected String getRootDn() {
        return "dc=assemblade,dc=com";
    }

    @Override
    public String getType() {
		return "views";
	}

    @Override
    public boolean getIsFolder() {
        return true;
    }

    @Override
    public String getPermissionsAttributes() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getRootPermissions() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getSearchFilter() {
        return "(objectClass=assemblade-views)";
    }

    @Override
    public Map<ObjectClass, String> getObjectClasses() {
        Map<ObjectClass, String> objectClasses = super.getObjectClasses();
        objectClasses.put(DirectoryServer.getObjectClass("assemblade-views"), "assemblade-views");
        return objectClasses;
    }

    @Override
    public StorableDecorator getDecorator() {
        return new Decorator();
    }

    private class Decorator extends AbstractFolder.Decorator<Views> {
        @Override
        public Views newInstance() {
            return new Views();
        }
    }
}
