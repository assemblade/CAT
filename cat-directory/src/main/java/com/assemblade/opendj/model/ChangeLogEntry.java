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

import com.assemblade.opendj.LdapUtils;
import com.assemblade.opendj.SequenceNumberGenerator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opends.server.core.DirectoryServer;
import org.opends.server.schema.GeneralizedTimeSyntax;
import org.opends.server.types.Attribute;
import org.opends.server.types.AttributeType;
import org.opends.server.types.ByteString;
import org.opends.server.types.DirectoryException;
import org.opends.server.types.Entry;
import org.opends.server.types.ObjectClass;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ChangeLogEntry extends AbstractStorable {
	private static final long serialVersionUID = 1L;

	private static Log log = LogFactory.getLog(ChangeLogEntry.class);
	
	private String sequence;
	private String type;
	private String targetDn;
	private String timeStamp;
	private Storable changeObject;
	private String newRDN;
	private boolean deleteOldRDN;
	private String newSuperior;
	private String user;

    public ChangeLogEntry() {
	}

	public ChangeLogEntry(String type, String targetDn, String timeStamp, Storable changeObject, String newRDN, boolean deleteOldRDN, String newSuperior) {
		this.sequence = SequenceNumberGenerator.getNextSequenceNumber();
		this.type = type;
		this.targetDn = targetDn;
		this.timeStamp = timeStamp;
		this.changeObject = changeObject;
		this.newRDN = newRDN;
		this.deleteOldRDN = deleteOldRDN;
		this.newSuperior = newSuperior;
	}

    @Override
    public Map<ObjectClass, String> getObjectClasses() {
        Map<ObjectClass, String> objectClasses = super.getObjectClasses();
        objectClasses.put(DirectoryServer.getObjectClass("changelogentry"), "changeLogEntry");
        return objectClasses;
	}

    @Override
    public Map<AttributeType, List<Attribute>> getUserAttributes() {
		Map<AttributeType, List<Attribute>> attributeMap = super.getUserAttributes();

        LdapUtils.addSingleValueAttributeToMap(attributeMap, "changeNumber", sequence);
        LdapUtils.addSingleValueAttributeToMap(attributeMap, "changeType", type);
        LdapUtils.addSingleValueAttributeToMap(attributeMap, "targetDN", targetDn);
        LdapUtils.addSingleValueAttributeToMap(attributeMap, "changeTime", timeStamp);
        LdapUtils.addSingleValueAttributeToMap(attributeMap, "newRDN", newRDN);
        LdapUtils.addSingleValueAttributeToMap(attributeMap, "deleteOldRDN", deleteOldRDN);
        LdapUtils.addSingleValueAttributeToMap(attributeMap, "newSuperior", newSuperior);
		if (changeObject != null) {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			try {
				new ObjectOutputStream(os).writeObject(changeObject);
			} catch (IOException e) {
				log.error("Failed to serialize [" + changeObject.toString() + "]", e);
			}
            LdapUtils.addSingleValueAttributeToMap(attributeMap, "changes", os.toByteArray());
		}
		
		return attributeMap;
	}

    @Override
    public Collection<String> getAttributeNames() {
        Collection<String> attributeNames = super.getAttributeNames();
        attributeNames.addAll(new ArrayList<String>(Arrays.asList("changeNumber", "targetDN", "changeType", "changeTime", "changes", "newRDN", "deleteOldRDN", "newSuperior", "creatorsName")));
        return attributeNames;
	}

    @Override
    public String getRDN() {
        return "changeNumber=" + sequence;
    }

    @Override
    protected String getRootDn() {
        return targetDn;
    }

    @Override
    public String getParentDn() {
		return targetDn;
	}

    @Override
    public String getSearchFilter() {
		return "(objectClass=changeLogEntry)";
	}

    @Override
    public StorableDecorator<ChangeLogEntry> getDecorator() {
        return new Decorator();
	}

	public String getType() {
		return type;
	}

	public String getTargetDn() {
		return targetDn;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

    public Date getDateTime() {
        try {
            return new Date(GeneralizedTimeSyntax.decodeGeneralizedTimeValue(ByteString.valueOf(timeStamp)));
        } catch (DirectoryException e) {
            return new Date();
        }
    }

	public Storable getChanges() {
		return changeObject;
	}

	public String getNewRDN() {
		return newRDN;
	}

	public boolean isDeleteOldRDN() {
		return deleteOldRDN;
	}

	public String getNewSuperior() {
		return newSuperior;
	}

	public String getUser() {
        String userName = user.substring(0, user.indexOf(","));
        return userName.substring(userName.indexOf("=") + 1);
	}

    public String getAction() {
        return type + " : " + (changeObject == null ? "" : changeObject.getChangeLogDescription());
    }

    private class Decorator extends AbstractStorable.Decorator<ChangeLogEntry> {
        @Override
        public ChangeLogEntry newInstance() {
            return new ChangeLogEntry();
        }

        @Override
        public ChangeLogEntry decorate(Entry entry) {
            ChangeLogEntry changeLogEntry = super.decorate(entry);

            changeLogEntry.sequence = LdapUtils.getSingleAttributeStringValue(entry.getAttribute("changeNumber"));
            changeLogEntry.type = LdapUtils.getSingleAttributeStringValue(entry.getAttribute("changetype"));
            changeLogEntry.targetDn = LdapUtils.getSingleAttributeStringValue(entry.getAttribute("targetdn"));
            changeLogEntry.timeStamp = LdapUtils.getSingleAttributeStringValue(entry.getAttribute("changetime"));
            changeLogEntry.user = LdapUtils.getSingleAttributeStringValue(entry.getAttribute("creatorsname"));

            byte[] changes = LdapUtils.getSingleAttributeByteValue(entry.getAttribute("changes"));
            if (changes != null) {
                try {
                    changeLogEntry.changeObject =  (Storable)new ObjectInputStream(new ByteArrayInputStream(changes)).readObject();
                } catch (Exception e) {
                    log.error("Failed to deserialize a change object", e);
                }
            }
            changeLogEntry.newRDN = LdapUtils.getSingleAttributeStringValue(entry.getAttribute("newrdn"));
            changeLogEntry.deleteOldRDN = LdapUtils.getSingleAttributeBooleanValue(entry.getAttribute("deleteoldrdn"));
            changeLogEntry.newSuperior = LdapUtils.getSingleAttributeStringValue(entry.getAttribute("newsuperior"));

            return changeLogEntry;
        }
    }

}
