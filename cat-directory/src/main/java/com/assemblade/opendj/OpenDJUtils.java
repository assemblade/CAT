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

import org.apache.commons.io.FileUtils;
import org.opends.server.controls.SubtreeDeleteControl;
import org.opends.server.core.DeleteOperation;
import org.opends.server.core.DirectoryServer;
import org.opends.server.protocols.internal.InternalClientConnection;
import org.opends.server.types.Control;
import org.opends.server.types.DN;
import org.opends.server.types.DirectoryEnvironmentConfig;
import org.opends.server.types.DirectoryException;
import org.opends.server.types.InitializationException;
import org.opends.server.types.LDIFImportConfig;
import org.opends.server.types.ResultCode;
import org.opends.server.util.AddChangeRecordEntry;
import org.opends.server.util.ChangeOperationType;
import org.opends.server.util.ChangeRecordEntry;
import org.opends.server.util.LDIFException;
import org.opends.server.util.LDIFReader;
import org.opends.server.util.ModifyChangeRecordEntry;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OpenDJUtils {
	public static void initializeDatastore(String rootDirectory) throws IOException {
		File serverRoot = new File(rootDirectory);

		FileUtils.forceMkdir(serverRoot);
		FileUtils.forceMkdir(new File(rootDirectory + "/config"));
		FileUtils.forceMkdir(new File(rootDirectory + "/config/schema"));
		FileUtils.forceMkdir(new File(rootDirectory + "/config/upgrade"));
		FileUtils.forceMkdir(new File(rootDirectory + "/locks"));
		FileUtils.forceMkdir(new File(rootDirectory + "/logs"));
		FileUtils.forceMkdir(new File(rootDirectory + "/db"));
		
		extractFileIfNeeded("cat-config.ldif", rootDirectory + "/config/config.ldif");
        extractFileIfNeeded("wordlist.txt", rootDirectory + "/config/wordlist.txt");
		extractFileIfNeeded("admin-backend.ldif", rootDirectory + "/config/admin-backend.ldif");
		extractFileIfNeeded("config.ldif.8087", rootDirectory + "/config/upgrade/config.ldif.8087");
		extractFileIfNeeded("schema.ldif.8087", rootDirectory + "/config/upgrade/schema.ldif.8087");
		extractFileIfNeeded("00-core.ldif", rootDirectory + "/config/schema/00-core.ldif");
		extractFileIfNeeded("01-pwpolicy.ldif", rootDirectory + "/config/schema/01-pwpolicy.ldif");
		extractFileIfNeeded("02-config.ldif", rootDirectory + "/config/schema/02-config.ldif");
		extractFileIfNeeded("03-changelog.ldif", rootDirectory + "/config/schema/03-changelog.ldif");
		extractFileIfNeeded("03-rfc2713.ldif", rootDirectory + "/config/schema/03-rfc2713.ldif");
        extractFileIfNeeded("04-rfc2307bis.ldif", rootDirectory + "/config/schema/04-rfc2307bis.ldif");
        extractFileIfNeeded("07-assemblade.ldif", rootDirectory + "/config/schema/07-assemblade.ldif");
	}
	
	public static DirectoryEnvironmentConfig buildEnvironmentConfig(String rootDirectory) throws IOException, InitializationException {
		DirectoryEnvironmentConfig config = new DirectoryEnvironmentConfig();
		
		File serverRoot = new File(rootDirectory);

		if (!serverRoot.exists()) {
			initializeDatastore(rootDirectory);
		} 
		config.setServerRoot(serverRoot);
		config.setMaintainConfigArchive(false);
		config.setDisableConnectionHandlers(true);
		config.setForceDaemonThreads(true);

		if (!config.getLockDirectory().exists()) {
			initializeDatastore(rootDirectory);
		}
		
		FileUtils.cleanDirectory(config.getLockDirectory());

		return config;
	}

    public static void refreshRootEntry() {
        InternalClientConnection connection = InternalClientConnection.getRootConnection();
        List<Control> controls = new ArrayList<Control>();
        controls.add(new SubtreeDeleteControl(true));
        DeleteOperation result = connection.processDelete("dc=assemblade,dc=com", controls);
        if (result.getResultCode() == ResultCode.SUCCESS) {
            performFirstTimeInitialization();
        }
	}
	
	public static void performFirstTimeInitialization() {
        try {
            if (!DirectoryServer.entryExists(DN.decode("dc=assemblade,dc=com"))) {
                importLdifFile("cat-layout.ldif");
            }
        } catch (DirectoryException e) {
            e.printStackTrace();
        }
    }
	
	public static void importLdifFile(String fileName) {
        InternalClientConnection connection = InternalClientConnection.getRootConnection();
		try {
            LDIFImportConfig config = new LDIFImportConfig(OpenDJUtils.class.getClassLoader().getResourceAsStream(fileName));
            LDIFReader ldifReader = new LDIFReader(config);
			ChangeRecordEntry entry = ldifReader.readChangeRecord(true);
			
			while (entry != null) {
                if (entry.getChangeOperationType() == ChangeOperationType.ADD) {
                    connection.processAdd((AddChangeRecordEntry)entry);
                } else if (entry.getChangeOperationType() == ChangeOperationType.MODIFY) {
                    connection.processModify((ModifyChangeRecordEntry)entry);
                }
				entry = ldifReader.readChangeRecord(true);
			}
		} catch (LDIFException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

	private static void extractFileIfNeeded(String file, String destination) throws IOException {
		File destinationFile = new File(destination);
		if (!destinationFile.exists()) {
			FileUtils.copyURLToFile(OpenDJUtils.class.getClassLoader().getResource(file), destinationFile);
		}
	}

}
