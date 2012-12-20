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
package com.assemblade.opendj;

import com.assemblade.opendj.model.AbstractConfiguration;
import com.assemblade.opendj.model.ChangeLogEntry;
import com.assemblade.opendj.model.Configuration;
import com.assemblade.opendj.model.ConfigurationDecorator;
import com.assemblade.opendj.model.Storable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opends.server.controls.GetEffectiveRightsRequestControl;
import org.opends.server.controls.SubtreeDeleteControl;
import org.opends.server.core.AddOperation;
import org.opends.server.core.DeleteOperation;
import org.opends.server.core.DirectoryServer;
import org.opends.server.core.ExtendedOperation;
import org.opends.server.core.ModifyDNOperation;
import org.opends.server.core.ModifyOperation;
import org.opends.server.protocols.asn1.ASN1;
import org.opends.server.protocols.asn1.ASN1Writer;
import org.opends.server.protocols.internal.InternalClientConnection;
import org.opends.server.protocols.internal.InternalSearchListener;
import org.opends.server.protocols.internal.InternalSearchOperation;
import org.opends.server.schema.GeneralizedTimeSyntax;
import org.opends.server.types.Attribute;
import org.opends.server.types.AttributeType;
import org.opends.server.types.ByteStringBuilder;
import org.opends.server.types.Control;
import org.opends.server.types.DereferencePolicy;
import org.opends.server.types.DirectoryException;
import org.opends.server.types.Entry;
import org.opends.server.types.Modification;
import org.opends.server.types.ResultCode;
import org.opends.server.types.SearchResultEntry;
import org.opends.server.types.SearchResultReference;
import org.opends.server.types.SearchScope;
import org.opends.server.util.ServerConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;

import static org.opends.server.extensions.ExtensionsConstants.TYPE_PASSWORD_MODIFY_NEW_PASSWORD;
import static org.opends.server.extensions.ExtensionsConstants.TYPE_PASSWORD_MODIFY_OLD_PASSWORD;
import static org.opends.server.extensions.ExtensionsConstants.TYPE_PASSWORD_MODIFY_USER_ID;

public class Session {
	private Log log = LogFactory.getLog(Session.class);

	private InternalClientConnection connection;
	
	public Session(InternalClientConnection connection) {
		this.connection = connection;
	}

    public void changePassword(String userDn, String currentPassword, String newPassword) throws StorageException {
        ByteStringBuilder builder = new ByteStringBuilder();
        ASN1Writer asn1Writer = ASN1.getWriter(builder);

        try {
            asn1Writer.writeStartSequence();
            asn1Writer.writeOctetString(TYPE_PASSWORD_MODIFY_USER_ID, "dn:" + userDn);
            asn1Writer.writeOctetString(TYPE_PASSWORD_MODIFY_OLD_PASSWORD, currentPassword);
            asn1Writer.writeOctetString(TYPE_PASSWORD_MODIFY_NEW_PASSWORD, newPassword);
            asn1Writer.writeEndSequence();

            ExtendedOperation result = connection.processExtendedOperation(ServerConstants.OID_PASSWORD_MODIFY_REQUEST, builder.toByteString());

            if (result.getResultCode() != ResultCode.SUCCESS) {
                log.error("Failed to change user [" + userDn + "] password because: " + result.getErrorMessage().toString());
                throw new StorageException(AssembladeErrorCode.ASB_0011);
            }
        } catch (Exception e) {
            log.error("Caught an exception trying to change user [" + userDn + "] password", e);
            throw new StorageException(AssembladeErrorCode.ASB_0011);
        }
    }

	public void add(Storable storable) throws StorageException {
        log.debug("Adding " + storable.getDn());
        AddOperation result = connection.processAdd(storable.getDN(), storable.getObjectClasses(), storable.getUserAttributes(), storable.getOperationalAttributes());
        if (result.getResultCode() != ResultCode.SUCCESS) {
            log.error("Failed to add entry [" + storable.getDn() + "] because: " + result.getErrorMessage().toString());
            throw new StorageException(AssembladeErrorCode.ASB_0003);
        }

        if (storable.recordChanges()) {
            addChangeLogEntry("add", storable);
        }
	}

    public void add(Configuration configuration) throws StorageException {
        log.debug("Adding " + configuration.getDn());
        AddOperation result = connection.processAdd(configuration.getDN(), configuration.getObjectClasses(), configuration.getUserAttributes(), new HashMap<AttributeType, List<Attribute>>());
        if (result.getResultCode() != ResultCode.SUCCESS) {
            log.error("Failed to add entry [" + configuration.getDn() + "] because: " + result.getErrorMessage().toString());
            throw new StorageException(AssembladeErrorCode.ASB_0003);
        }
    }

    public <T extends Storable> void update(T storable) throws StorageException {
        log.debug("Updating " + storable.getDn());
        Entry currentEntry = internalGet(dnFromId(storable.getId()), SearchScope.BASE_OBJECT, "(objectclass=*)", new LinkedHashSet<String>(storable.getAttributeNames()));
        if (currentEntry != null) {
            if (storable.requiresRename(this, currentEntry)) {
                rename(currentEntry.getDN().toString(), storable.getRDN());
                if (storable.recordChanges()) {
                    addChangeLogEntry("rename", storable);
                }
            }
            if (storable.requiresMove(this, currentEntry)) {
                move(currentEntry.getDN().toString(), currentEntry.getDN().getRDN().toString(), storable.getParentDn());
                if (storable.recordChanges()) {
                    addChangeLogEntry("move", storable);
                }
            }
            if (storable.requiresUpdate(this, currentEntry)) {
                List<Modification> modifications = storable.getModifications(this, currentEntry);
                if (modifications.size() > 0) {
                    ModifyOperation result = connection.processModify(storable.getDN(), modifications);
                    if (result.getResultCode() != ResultCode.SUCCESS) {
                        log.error("Failed to update entry [" + storable.getDn() + "] because: " + result.getErrorMessage().toString());
                        throw new StorageException(AssembladeErrorCode.ASB_0005);
                    }
                    if (storable.recordChanges()) {
                        addChangeLogEntry("modify", storable);
                    }
                }
            }
        }
	}

    public void update(Configuration configuration) throws StorageException {
        log.debug("Updating " + configuration.getDn());
        try {
            InternalSearchOperation getResult = connection.processSearch(configuration.getDn(), SearchScope.BASE_OBJECT, DereferencePolicy.NEVER_DEREF_ALIASES, 0, 0, false, "(objectclass=*)", new LinkedHashSet<String>());
            if (getResult.getResultCode() == ResultCode.SUCCESS) {
                Entry currentEntry = getResult.getSearchEntries().getFirst();
                if (configuration.requiresRename(currentEntry)) {
                    rename(currentEntry.getDN().toString(), configuration.getRDN());
                }
                if (configuration.requiresUpdate(currentEntry)) {
                    List<Modification> modifications = configuration.getModifications(currentEntry);

                    if (modifications.size() > 0) {
                        ModifyOperation result = connection.processModify(configuration.getDN(), modifications);
                        if (result.getResultCode() != ResultCode.SUCCESS) {
                            log.error("Failed to update entry [" + configuration.getDn() + "] because: " + result.getErrorMessage().toString());
                            throw new StorageException(AssembladeErrorCode.ASB_0005);
                        }
                    }
                }
            }
        } catch (DirectoryException e) {
            log.error("Caught an exception modifying entry [" + configuration.getDn() + "]", e);
            throw new StorageException(AssembladeErrorCode.ASB_0005);
        }
    }

    public void rename(String dn, String newRdn) throws StorageException {
        log.debug("Renaming " + dn + " to " + newRdn);
        ModifyDNOperation result = connection.processModifyDN(dn, newRdn, true);
		if (result.getResultCode() != ResultCode.SUCCESS) {
            log.error("Failed to modify DN of entry [" + dn + "->" + newRdn + "] because: " + result.getErrorMessage().toString());
            throw new StorageException(AssembladeErrorCode.ASB_0008);
        }
	}

    public void move(String dn, String newRdn, String newParent) throws StorageException {
        log.debug("Moving " + dn + " to " + newParent);
        ModifyDNOperation result = connection.processModifyDN(dn, newRdn, true, newParent);
        if (result.getResultCode() != ResultCode.SUCCESS) {
            log.error("Failed to move entry [" + dn + "] to new parent [" + newParent + "]");
            throw new StorageException(AssembladeErrorCode.ASB_0009);
        }
    }

	public void delete(Storable storable) throws StorageException {
		delete(storable, false);
	}

	public void delete(Storable storable, boolean subTree) throws StorageException {
        delete(storable.getDn(), subTree);
	}

	public void delete(String dn, boolean deleteSubTree) throws StorageException {
        log.debug("Deleting " + dn + (deleteSubTree ? " and whole sub-tree" : ""));
		List<Control> controls = new ArrayList<Control>();
		if (deleteSubTree) {
			controls.add(new SubtreeDeleteControl(true));
		}
		DeleteOperation result = connection.processDelete(dn, controls);
		if (result.getResultCode() != ResultCode.SUCCESS) {
            log.error("Failed to delete [" + dn + "] because: " + result.getErrorMessage().toString());
            throw new StorageException(AssembladeErrorCode.ASB_0007);
        }
	}

	@SuppressWarnings("unchecked")
	public <T extends Storable> T get(final T storable) throws StorageException {
        log.debug("Getting " + storable.getDn());
        return (T)storable.getDecorator().decorate(this, internalGet(storable.getDn(), SearchScope.BASE_OBJECT, "(objectclass=*)", new LinkedHashSet<String>(storable.getAttributeNames())));
	}

    @SuppressWarnings("unchecked")
    public <T extends Configuration> T get(final T configuration) throws StorageException {
        log.debug("Getting " + configuration.getDn());
        return (T)configuration.getDecorator().decorate(internalGet(configuration.getDn(), SearchScope.BASE_OBJECT, "(objectclass=*)", new LinkedHashSet<String>(configuration.getAttributeNames())));
    }

    public Configuration getConfigurationByDn(String dn) throws StorageException {
        log.debug("Getting configuration by dn " + dn);
        Entry entry = internalGet(dn, SearchScope.BASE_OBJECT, "(objectclass=*)", new LinkedHashSet<String>(Arrays.asList("+", "*")));
        ConfigurationDecorator decorator = AbstractConfiguration.getDecorator(entry);
        if (decorator == null) {
            log.error("Failed to get decorator for " + dn);
            throw new StorageException(AssembladeErrorCode.ASB_0006);
        } else {
            return decorator.decorate(entry);
        }
    }

    public <T extends Storable> T getByEntryDn(final T storable, String dn) throws StorageException {
        log.debug("Getting entry by dn " + dn);
        return (T)storable.getDecorator().decorate(this, internalGet(dn, SearchScope.BASE_OBJECT, "(objectclass=*)", new LinkedHashSet<String>(storable.getAttributeNames())));
    }

    public <T extends Storable> T getByEntryDnAndFilter(T storable, String dn, String filter) throws StorageException {
        log.debug("Getting entry by dn " + dn + " with filter " + filter);
        return (T)storable.getDecorator().decorate(this, internalGet(dn, SearchScope.WHOLE_SUBTREE, filter, new LinkedHashSet<String>(storable.getAttributeNames())));
    }

    public <T extends Storable> T getByEntryId(T storable, String id) throws StorageException {
        log.debug("Getting entry by id " + id);
        return (T)storable.getDecorator().decorate(this, internalGet("dc=assemblade,dc=com", SearchScope.WHOLE_SUBTREE, "(entryUUID=" + id + ")", new LinkedHashSet<String>(storable.getAttributeNames())));
    }

    public String dnFromId(String id) throws StorageException {
        return  internalGet("dc=assemblade,dc=com", SearchScope.WHOLE_SUBTREE, "(entryUUID=" + id + ")", new LinkedHashSet<String>()).getDN().toString();
    }

    public String idfromdn(String dn) throws StorageException {
        return LdapUtils.getSingleAttributeStringValue(internalGet(dn, SearchScope.BASE_OBJECT, "(objectclass=*)", new LinkedHashSet<String>(Arrays.asList("entryUUID"))).getAttribute("entryuuid"));
    }

    public List<Configuration> getConfigurationItems(String filter) throws StorageException {
        List<Configuration> result = new ArrayList<Configuration>();

        for (Entry entry : internalSearch("cn=config", true, filter, new LinkedHashSet<String>(Arrays.asList("+", "*")))) {
            ConfigurationDecorator decorator = AbstractConfiguration.getDecorator(entry);
            if (decorator != null) {
                result.add(decorator.decorate(entry));
            }
        }
        return result;
    }

    public <T extends Storable> List<T> search(final T storable, String baseDn, boolean subTree) throws StorageException {
        List<T> result = new ArrayList<T>();

        for (Entry entry : internalSearch(baseDn, subTree, storable.getSearchFilter(), new LinkedHashSet<String>(storable.getAttributeNames()))) {
            result.add((T)storable.getDecorator().decorate(this, entry));
        }
        return result;
	}

    public <T extends Storable> List<T> search(final T storable, String baseDn, String searchFilter) throws StorageException {
        List<T> result = new ArrayList<T>();

        for (Entry entry : internalSearch(baseDn, true, searchFilter, new LinkedHashSet<String>(storable.getAttributeNames()))) {
            result.add((T)storable.getDecorator().decorate(this, entry));
        }
        return result;
    }

    public <T extends Configuration> List<T> search(final T configuration, String baseDn) throws StorageException {
        List<T> result = new ArrayList<T>();

        for (Entry entry : internalSearch(baseDn, false, configuration.getSearchFilter(), new LinkedHashSet<String>(configuration.getAttributeNames()))) {
            result.add((T) configuration.getDecorator().decorate(entry));
        }
        return result;
    }

    public <T extends Configuration> List<T> search(final T configuration, String baseDn, String searchFilter) throws StorageException {
        List<T> result = new ArrayList<T>();

        for (Entry entry : internalSearch(baseDn, true, searchFilter, new LinkedHashSet<String>(configuration.getAttributeNames()))) {
            result.add((T) configuration.getDecorator().decorate(entry));
        }
        return result;
    }

    private Entry internalGet(String dn, SearchScope scope, String filter, LinkedHashSet<String> attributes) throws StorageException {
        final List<Entry> entries = new ArrayList<Entry>();
        try {
            List<Control> controls = new ArrayList<Control>();
            if (attributes.contains("aclRights") || attributes.contains("*")) {
                controls.add(new GetEffectiveRightsRequestControl(false, null, new ArrayList<String>()));
            }
            InternalSearchOperation searchResult = connection.processSearch(dn, scope, DereferencePolicy.NEVER_DEREF_ALIASES, 0, 0, false, filter, attributes, controls, new InternalSearchListener() {
                public void handleInternalSearchEntry(InternalSearchOperation operation, SearchResultEntry searchEntry) throws DirectoryException {
                    entries.add(searchEntry);
                }

                public void handleInternalSearchReference(InternalSearchOperation operation, SearchResultReference reference) throws DirectoryException {
                }
            });
            if (searchResult.getResultCode() == ResultCode.SUCCESS) {
                if (entries.size() == 1) {
                    return entries.get(0);
                } else {
                    log.debug("Got " + entries.size() + " entries for what should have been a single entry [" + dn + "]");
                    throw new StorageException(AssembladeErrorCode.ASB_0006);
                }
            } else {
                log.error("Failed to get entry [" + dn + "] because: " + searchResult.getErrorMessage().toString());
                dumpTree("dc=assemblade,dc=com", true, "(objectclass=*)");
                throw new StorageException(AssembladeErrorCode.ASB_0006);
            }
        } catch (DirectoryException e) {
            log.error("Exception thrown getting entry [" + dn + "]", e);
            throw new StorageException(AssembladeErrorCode.ASB_9999);
        }
    }

    private List<Entry> internalSearch(String dn, boolean subTree, String filter, LinkedHashSet<String> attributes) throws StorageException {
        final List<Entry> result = new ArrayList<Entry>();
        List<Control> controls = new ArrayList<Control>();

        try {
            if (attributes.contains("aclRights")) {
                controls.add(new GetEffectiveRightsRequestControl(false, null, new ArrayList<String>()));
            }
            InternalSearchOperation operation = connection.processSearch(dn, subTree ? SearchScope.WHOLE_SUBTREE : SearchScope.SINGLE_LEVEL, DereferencePolicy.NEVER_DEREF_ALIASES, 0, 0, false, filter, attributes, controls, new InternalSearchListener() {
                @SuppressWarnings("unchecked")
                public void handleInternalSearchEntry(InternalSearchOperation operation, SearchResultEntry entry) throws DirectoryException {
                    result.add(entry);
                }

                public void handleInternalSearchReference(InternalSearchOperation operation, SearchResultReference reference) throws DirectoryException {
                }
            });
            if (operation.getResultCode() != ResultCode.SUCCESS) {
                log.error("Failed to search under [" + dn + "] because: " + operation.getErrorMessage().toString());
                throw new StorageException(AssembladeErrorCode.ASB_0010);
            }
        } catch (DirectoryException e) {
            log.error("Caught a directory exception trying to search under [" + dn + "]", e);
            throw new StorageException(AssembladeErrorCode.ASB_9999);
        }

        return result;
    }


    private void addChangeLogEntry(String type, Storable storable) throws StorageException {
        ChangeLogEntry change = new ChangeLogEntry(type, storable.getDn(), GeneralizedTimeSyntax.format(System.currentTimeMillis()), storable, null, false, null);

        AddOperation result = connection.processAdd(change.getDN(), change.getObjectClasses(), change.getUserAttributes(), change.getOperationalAttributes());

        if (result.getResultCode() != ResultCode.SUCCESS) {
            log.error("Failed to add a change entry to [" + storable.getDn() + "] because: " + result.getErrorMessage().toString());
            throw new StorageException(AssembladeErrorCode.ASB_0003);
        }
    }

    private void dumpTree(String dn, boolean subTree, String filter) {
        if (log.isDebugEnabled()) {
            List<Control> controls = new ArrayList<Control>();

            LinkedHashSet<String> attributeSet = new LinkedHashSet<String>(Arrays.asList("*", "+"));

            try {
                connection.processSearch(dn, subTree ? SearchScope.WHOLE_SUBTREE : SearchScope.SINGLE_LEVEL, DereferencePolicy.NEVER_DEREF_ALIASES, 0, 0, false, filter, attributeSet, controls, new InternalSearchListener() {
                    public void handleInternalSearchEntry(InternalSearchOperation operation, SearchResultEntry entry) throws DirectoryException {
                        log.debug(entry.getDN().toString());
                    }

                    public void handleInternalSearchReference(InternalSearchOperation operation, SearchResultReference reference) throws DirectoryException {
                    }
                });
            } catch (DirectoryException e) {
                log.error("Exception during dumptree", e);
            }
        }
	}
}
