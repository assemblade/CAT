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

    public void addUser(Storable storable) throws StorageException {
        AddOperation result = connection.processAdd(storable.getDN(), storable.getObjectClasses(), storable.getUserAttributes(), storable.getOperationalAttributes());
        if (result.getResultCode() != ResultCode.SUCCESS) {
            log.error("Failed to add entry [" + storable.getDn().toString() + "] because: " + result.getErrorMessage().toString());
            throw new StorageException(AssembladeErrorCode.ASB_0003);
        }
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
        AddOperation result = connection.processAdd(storable.getDN(), storable.getObjectClasses(), storable.getUserAttributes(), storable.getOperationalAttributes());
        if (result.getResultCode() != ResultCode.SUCCESS) {
            log.error("Failed to add entry [" + storable.getDn().toString() + "] because: " + result.getErrorMessage().toString());
            throw new StorageException(AssembladeErrorCode.ASB_0003);
        }

        if (storable.recordChanges()) {
            addChangeLogEntry("add", storable);
        }
	}

    public void add(Configuration configuration) throws StorageException {
        AddOperation result = connection.processAdd(configuration.getDN(), configuration.getObjectClasses(), configuration.getUserAttributes(), new HashMap<AttributeType, List<Attribute>>());
        if (result.getResultCode() != ResultCode.SUCCESS) {
            log.error("Failed to add entry [" + configuration.getDn().toString() + "] because: " + result.getErrorMessage().toString());
            throw new StorageException(AssembladeErrorCode.ASB_0003);
        }
    }

    public <T extends Storable> void update(T storable) throws StorageException {
        Entry currentEntry = getRawEntry(storable);
        if (currentEntry != null) {
            if (storable.requiresRename(currentEntry)) {
                rename(currentEntry.getDN().toString(), storable.getRDN());
                if (storable.recordChanges()) {
                    addChangeLogEntry("rename", storable);
                }
            }
            if (storable.requiresMove(currentEntry)) {
                move(currentEntry.getDN().toString(), currentEntry.getDN().getRDN().toString(), storable.getParentDn());
                if (storable.recordChanges()) {
                    addChangeLogEntry("move", storable);
                }
            }
            if (storable.requiresUpdate(currentEntry)) {
                List<Modification> modifications = storable.getModifications(currentEntry);
                if (modifications.size() > 0) {
                    ModifyOperation result = connection.processModify(storable.getDN(), modifications);
                    if (result.getResultCode() != ResultCode.SUCCESS) {
                        log.error("Failed to update entry [" + storable.getDn().toString() + "] because: " + result.getErrorMessage().toString());
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
                            log.error("Failed to update entry [" + configuration.getDn().toString() + "] because: " + result.getErrorMessage().toString());
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
		ModifyDNOperation result = connection.processModifyDN(dn, newRdn, true);
		if (result.getResultCode() != ResultCode.SUCCESS) {
            log.error("Failed to modify DN of entry [" + dn + "->" + newRdn + "] because: " + result.getErrorMessage().toString());
            throw new StorageException(AssembladeErrorCode.ASB_0008);
        }
	}

    public void move(String dn, String newRdn, String newParent) throws StorageException {
        ModifyDNOperation result = connection.processModifyDN(dn, newRdn, true, newParent);
        if (result.getResultCode() != ResultCode.SUCCESS) {
            log.error("Failed to move entry [" + dn + "] to new parent [" + newParent + "]");
            throw new StorageException(AssembladeErrorCode.ASB_0009);
        }
    }

	public void store(Storable storable) throws StorageException {
		if (exists(storable)) {
			update(storable);
		} else {
			add(storable);
		}
	}
	
	public void delete(Storable storable) throws StorageException {
		delete(storable, false);
	}

	public void delete(Storable storable, boolean subTree) throws StorageException {
        delete(storable.getDn(), subTree);
	}

	public void delete(String dn, boolean deleteSubTree) throws StorageException {
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
	
	public boolean exists(Storable storable) {
		try {
			InternalSearchOperation result = connection.processSearch(storable.getDn(), SearchScope.BASE_OBJECT, DereferencePolicy.NEVER_DEREF_ALIASES, 0, 0, false, "(objectclass=*)", new LinkedHashSet<String>());
			if ((result.getResultCode() == ResultCode.SUCCESS) && (result.getSearchEntries().size() == 1)) {
				return true;
			}
		} catch (DirectoryException e) {
			log.error("Caught an exception checking the existance of entry: " + storable.getDn(), e);
		}
		return false;
	}

	public boolean exists(String dn) {
		try {
			InternalSearchOperation result = connection.processSearch(dn, SearchScope.BASE_OBJECT, DereferencePolicy.NEVER_DEREF_ALIASES, 0, 0, false, "(objectclass=*)", new LinkedHashSet<String>());
			if ((result.getResultCode() == ResultCode.SUCCESS) && (result.getSearchEntries().size() == 1)) {
				return true;
			}
		} catch (DirectoryException e) {
			log.error("Caught an exception checking the existance of entry: " + dn, e);
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public <T extends Storable> T get(final T storable) throws StorageException {
		final SearchResult<T> result = new SearchResult<T>();
		try {
			List<Control> controls = new ArrayList<Control>();
			LinkedHashSet<String> attributeSet = new LinkedHashSet<String>();
			attributeSet.addAll(storable.getAttributeNames());
			if (attributeSet.contains("aclRights")) {
				controls.add(new GetEffectiveRightsRequestControl(false, null, new ArrayList<String>()));
			}
			InternalSearchOperation searchResult = connection.processSearch(storable.getDn(), SearchScope.BASE_OBJECT, DereferencePolicy.NEVER_DEREF_ALIASES, 0, 0, false, "(objectclass=*)", attributeSet, controls, new InternalSearchListener() {
				public void handleInternalSearchEntry(InternalSearchOperation operation, SearchResultEntry searchEntry) throws DirectoryException {
					result.addEntry((T)storable.getDecorator().decorate(searchEntry));
				}

				public void handleInternalSearchReference(InternalSearchOperation operation, SearchResultReference reference) throws DirectoryException {
				}
			});
			if (searchResult.getResultCode() == ResultCode.SUCCESS) {
				if (result.size() > 0) {
					return result.get(0);
				} else {
                    log.error("Got multiple entries for what should have been a single entry [" + storable.getDn() + "]");
                    throw new StorageException(AssembladeErrorCode.ASB_0006);
                }
			} else {
                log.error("Failed to get entry [" + storable.getDn().toString() + "] because: " + searchResult.getErrorMessage().toString());
                throw new StorageException(AssembladeErrorCode.ASB_0006);
            }
		} catch (DirectoryException e) {
			log.error("Exception thrown getting entry [" + storable.getDn() + "]", e);
            throw new StorageException(AssembladeErrorCode.ASB_9999);
        }
	}

    public <T extends Storable> Entry getRawEntry(final T storable) throws StorageException {
        final List<Entry> result = new ArrayList<Entry>();
        try {
            List<Control> controls = new ArrayList<Control>();
            LinkedHashSet<String> attributeSet = new LinkedHashSet<String>();
            attributeSet.addAll(storable.getAttributeNames());
            if (attributeSet.contains("aclRights")) {
                controls.add(new GetEffectiveRightsRequestControl(false, null, new ArrayList<String>()));
            }
            InternalSearchOperation searchResult = connection.processSearch(dnFromId(storable.getId()), SearchScope.BASE_OBJECT, DereferencePolicy.NEVER_DEREF_ALIASES, 0, 0, false, "(objectclass=*)", attributeSet, controls, new InternalSearchListener() {
                public void handleInternalSearchEntry(InternalSearchOperation operation, SearchResultEntry searchEntry) throws DirectoryException {
                    result.add(searchEntry);
                }

                public void handleInternalSearchReference(InternalSearchOperation operation, SearchResultReference reference) throws DirectoryException {
                }
            });
            if (searchResult.getResultCode() == ResultCode.SUCCESS) {
                if (result.size() > 0) {
                    return result.get(0);
                }
            }
        } catch (DirectoryException e) {
            log.warn("Exception thrown getting entry [" + storable.getDn() + "]", e);
        }
        return null;
    }




    @SuppressWarnings("unchecked")
    public <T extends Configuration> T get(final T configuration) throws StorageException {
        final List<T> result = new ArrayList<T>();
        try {
            List<Control> controls = new ArrayList<Control>();
            LinkedHashSet<String> attributeSet = new LinkedHashSet<String>();
            attributeSet.addAll(configuration.getAttributeNames());
            if (attributeSet.contains("aclRights")) {
                controls.add(new GetEffectiveRightsRequestControl(false, null, new ArrayList<String>()));
            }
            InternalSearchOperation searchResult = connection.processSearch(configuration.getDn(), SearchScope.BASE_OBJECT, DereferencePolicy.NEVER_DEREF_ALIASES, 0, 0, false, "(objectclass=*)", attributeSet, controls, new InternalSearchListener() {
                public void handleInternalSearchEntry(InternalSearchOperation operation, SearchResultEntry searchEntry) throws DirectoryException {
                    result.add((T) configuration.getDecorator().decorate(searchEntry));
                }

                public void handleInternalSearchReference(InternalSearchOperation operation, SearchResultReference reference) throws DirectoryException {
                }
            });
            if (searchResult.getResultCode() == ResultCode.SUCCESS) {
                if (result.size() > 0) {
                    return result.get(0);
                }
            }
        } catch (DirectoryException e) {
            log.warn("Exception thrown getting entry [" + configuration.getDn() + "]", e);
        }
        return null;
    }

    public List<Configuration> getConfigurationItems(String filter) throws StorageException {
        final List<Configuration> result = new ArrayList<Configuration>();

        try {
            InternalSearchOperation searchResult = connection.processSearch("cn=config", SearchScope.WHOLE_SUBTREE, filter);

            if (searchResult.getResultCode() == ResultCode.SUCCESS) {
                for (Entry entry : searchResult.getSearchEntries()) {
                    ConfigurationDecorator decorator = AbstractConfiguration.getDecorator(entry);
                    if (decorator != null) {
                        result.add(decorator.decorate(entry));
                    }
                }
            } else {
                //TODO: Handle error
            }
        } catch (DirectoryException e) {
            log.warn("Exception thrown while getting configuration items for filter: " + filter, e);
        }
        return result;
    }




    public <T extends Storable> T getByEntryDn(final T storable, String dn) throws StorageException {
        final SearchResult<T> result = new SearchResult<T>();
        try {
            List<Control> controls = new ArrayList<Control>();
            LinkedHashSet<String> attributeSet = new LinkedHashSet<String>();
            attributeSet.addAll(storable.getAttributeNames());
            if (attributeSet.contains("aclRights")) {
                controls.add(new GetEffectiveRightsRequestControl(false, null, new ArrayList<String>()));
            }
            InternalSearchOperation searchResult = connection.processSearch(dn, SearchScope.BASE_OBJECT, DereferencePolicy.NEVER_DEREF_ALIASES, 0, 0, false, "(objectclass=*)", attributeSet, controls, new InternalSearchListener() {
                public void handleInternalSearchEntry(InternalSearchOperation operation, SearchResultEntry searchEntry) throws DirectoryException {
                    result.addEntry((T)storable.getDecorator().decorate(searchEntry));
                }

                public void handleInternalSearchReference(InternalSearchOperation operation, SearchResultReference reference) throws DirectoryException {
                }
            });
            if (searchResult.getResultCode() == ResultCode.SUCCESS) {
                if (result.size() > 0) {
                    return result.get(0);
                }
            }
        } catch (DirectoryException e) {
            log.warn("Exception thrown getting entry [" + dn + "]", e);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <T extends Storable> T getByEntryDnAndFilter(T storable, String dn, String filter) throws StorageException {
        try {
            LinkedHashSet<String> attributeSet = new LinkedHashSet<String>();
            attributeSet.addAll(storable.getAttributeNames());
            InternalSearchOperation result = connection.processSearch(dn, SearchScope.WHOLE_SUBTREE, DereferencePolicy.NEVER_DEREF_ALIASES, 0, 0, false, filter, attributeSet);
            if (result.getResultCode() == ResultCode.SUCCESS) {
                if (result.getSearchEntries().size() > 0) {
                    return (T)storable.getDecorator().decorate(result.getSearchEntries().getFirst());
                }
            }
        } catch (DirectoryException e) {
            log.warn("Exception thrown getting entry [" + storable.getDn() + "]", e);
        }
        return null;
    }

    public <T extends Storable> T getByEntryId(T storable, String id) throws StorageException {
        T object = null;
        try {
            LinkedHashSet<String> attributeSet = new LinkedHashSet<String>();
            attributeSet.addAll(storable.getAttributeNames());
            InternalSearchOperation result = connection.processSearch("dc=assemblade,dc=com", SearchScope.WHOLE_SUBTREE, DereferencePolicy.NEVER_DEREF_ALIASES, 0, 0, false, "(entryUUID=" + id + ")", attributeSet);
            if (result.getResultCode() == ResultCode.SUCCESS) {
                if (result.getSearchEntries().size() > 0) {
                    object = (T)storable.getDecorator().decorate(result.getSearchEntries().getFirst());
                }
            }
        } catch (DirectoryException e) {
            log.warn("Exception thrown getting entry [" + storable.getDn() + "]", e);
        }
        if (object == null) {
            throw new StorageException(AssembladeErrorCode.ASB_0006);
        }
        return object;
    }

    public String dnFromId(String id) throws StorageException {
        try {
            LinkedHashSet<String> attributeSet = new LinkedHashSet<String>();
            InternalSearchOperation result = connection.processSearch("dc=assemblade,dc=com", SearchScope.WHOLE_SUBTREE, DereferencePolicy.NEVER_DEREF_ALIASES, 0, 0, false, "(entryUUID=" + id + ")", attributeSet);
            if (result.getResultCode() == ResultCode.SUCCESS) {
                if (result.getSearchEntries().size() > 0) {
                    return result.getSearchEntries().getFirst().getDN().toString();
                }
            }
        } catch (DirectoryException e) {
            log.warn("Exception thrown resolving id [" + id + "]", e);
        }
        return null;
    }

    public <T extends Storable> SearchResult<T> search(final T storable, String baseDn, boolean subTree) throws StorageException {
		final SearchResult<T> result = new SearchResult<T>();

		List<Control> controls = new ArrayList<Control>();
		
		LinkedHashSet<String> attributeSet = new LinkedHashSet<String>(storable.getAttributeNames());
		
		try {
			if (attributeSet.contains("aclRights")) {
				controls.add(new GetEffectiveRightsRequestControl(false, null, new ArrayList<String>()));
			}
			InternalSearchOperation operation = connection.processSearch(baseDn, subTree ? SearchScope.WHOLE_SUBTREE : SearchScope.SINGLE_LEVEL, DereferencePolicy.NEVER_DEREF_ALIASES, 0, 0, false, storable.getSearchFilter(), attributeSet, controls, new InternalSearchListener() {
				@SuppressWarnings("unchecked")
				public void handleInternalSearchEntry(InternalSearchOperation operation, SearchResultEntry entry) throws DirectoryException {
					result.addEntry((T)storable.getDecorator().decorate(entry));
				}

				public void handleInternalSearchReference(InternalSearchOperation operation, SearchResultReference reference) throws DirectoryException {
				}
			});
			if (operation.getResultCode() != ResultCode.SUCCESS) {
                log.error("Failed to search under [" + storable.getDn().toString() + "] because: " + operation.getErrorMessage().toString());
                throw new StorageException(AssembladeErrorCode.ASB_0010);
			} else {
                result.completedSearch();
			}
		} catch (DirectoryException e) {
            log.error("Caught a directory exception trying to search under [" + storable.getDn().toString() + "]", e);
            throw new StorageException(AssembladeErrorCode.ASB_9999);
        }
		return result;
	}

    public <T extends Storable> List<T> search(final T storable, String baseDn, String searchFilter) throws StorageException {
        final List<T> result = new ArrayList<T>();

        List<Control> controls = new ArrayList<Control>();

        LinkedHashSet<String> attributeSet = new LinkedHashSet<String>(storable.getAttributeNames());

        try {
            if (attributeSet.contains("aclRights")) {
                controls.add(new GetEffectiveRightsRequestControl(false, null, new ArrayList<String>()));
            }
            InternalSearchOperation operation = connection.processSearch(baseDn, SearchScope.WHOLE_SUBTREE, DereferencePolicy.NEVER_DEREF_ALIASES, 0, 0, false, searchFilter, attributeSet, controls, new InternalSearchListener() {
                @SuppressWarnings("unchecked")
                public void handleInternalSearchEntry(InternalSearchOperation operation, SearchResultEntry entry) throws DirectoryException {
                    result.add((T)storable.getDecorator().decorate(entry));
                }

                public void handleInternalSearchReference(InternalSearchOperation operation, SearchResultReference reference) throws DirectoryException {
                }
            });
            if (operation.getResultCode() != ResultCode.SUCCESS) {

            }
        } catch (DirectoryException e) {
        }
        return result;
    }

    public <T extends Configuration> List<T> search(final T configuration, String baseDn) throws StorageException {
        final List<T> result = new ArrayList<T>();
        List<Control> controls = new ArrayList<Control>();
        LinkedHashSet<String> attributeSet = new LinkedHashSet<String>(configuration.getAttributeNames());
        try {
            InternalSearchOperation operation = connection.processSearch(baseDn, SearchScope.SINGLE_LEVEL, DereferencePolicy.NEVER_DEREF_ALIASES, 0, 0, false, configuration.getSearchFilter(), attributeSet, controls, new InternalSearchListener() {
                @SuppressWarnings("unchecked")
                public void handleInternalSearchEntry(InternalSearchOperation operation, SearchResultEntry entry) throws DirectoryException {
                    result.add((T) configuration.getDecorator().decorate(entry));
                }
                public void handleInternalSearchReference(InternalSearchOperation operation, SearchResultReference reference) throws DirectoryException {
                }
            });
            if (operation.getResultCode() != ResultCode.SUCCESS) {

            }
        } catch (DirectoryException e) {
        }
        return result;
    }

    public <T extends Configuration> List<T> search(final T configuration, String baseDn, String searchFilter) throws StorageException {
        final List<T> result = new ArrayList<T>();
        List<Control> controls = new ArrayList<Control>();
        LinkedHashSet<String> attributeSet = new LinkedHashSet<String>(configuration.getAttributeNames());
        try {
            InternalSearchOperation operation = connection.processSearch(baseDn, SearchScope.WHOLE_SUBTREE, DereferencePolicy.NEVER_DEREF_ALIASES, 0, 0, false, searchFilter, attributeSet, controls, new InternalSearchListener() {
                @SuppressWarnings("unchecked")
                public void handleInternalSearchEntry(InternalSearchOperation operation, SearchResultEntry entry) throws DirectoryException {
                    result.add((T) configuration.getDecorator().decorate(entry));
                }
                public void handleInternalSearchReference(InternalSearchOperation operation, SearchResultReference reference) throws DirectoryException {
                }
            });
            if (operation.getResultCode() != ResultCode.SUCCESS) {

            }
        } catch (DirectoryException e) {
        }
        return result;
    }

    private void addChangeLogEntry(String type, Storable storable) throws StorageException {
        ChangeLogEntry change = new ChangeLogEntry("add", storable.getDn(), GeneralizedTimeSyntax.format(System.currentTimeMillis()), storable, null, false, null);

        AddOperation result = connection.processAdd(change.getDN(), change.getObjectClasses(), change.getUserAttributes(), change.getOperationalAttributes());

        if (result.getResultCode() != ResultCode.SUCCESS) {
            log.error("Failed to add a change entry to [" + storable.getDn() + "] because: " + result.getErrorMessage().toString());
            throw new StorageException(AssembladeErrorCode.ASB_0003);
        }
    }


    public void dumpTree(String dn, boolean subTree, String filter) throws Exception {
		final StringBuffer buffer = new StringBuffer();

		List<Control> controls = new ArrayList<Control>();
		
		LinkedHashSet<String> attributeSet = new LinkedHashSet<String>(Arrays.asList("*", "+"));

		InternalSearchOperation operation = connection.processSearch(dn, subTree ? SearchScope.WHOLE_SUBTREE : SearchScope.SINGLE_LEVEL, DereferencePolicy.NEVER_DEREF_ALIASES, 0, 0, false, filter, attributeSet, controls, new InternalSearchListener() {
			public void handleInternalSearchEntry(InternalSearchOperation operation, SearchResultEntry entry) throws DirectoryException {
				buffer.append(entry.getDN().toString());
				buffer.append('\n');
			}

			public void handleInternalSearchReference(InternalSearchOperation operation, SearchResultReference reference) throws DirectoryException {
			}
		});

		System.out.println(buffer.toString());
	}
}
