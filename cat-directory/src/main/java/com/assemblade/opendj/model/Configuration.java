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
package com.assemblade.opendj.model;

import com.assemblade.opendj.StorageException;
import org.opends.server.types.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface Configuration {
    public String getRDN();
    public DN getDN() throws StorageException;
    public String getDn();
    public String getRootDn();
    public String getJavaClass();
    public String getSearchFilter();
    public Map<ObjectClass, String> getObjectClasses();
    public Collection<String> getAttributeNames();
    public ConfigurationDecorator getDecorator();
    public Map<AttributeType, List<Attribute>> getUserAttributes();
    public List<Modification> getModifications(Entry currentEntry);
    public boolean requiresRename(Entry currentEntry);
    public boolean requiresUpdate(Entry currentEntry);
}
