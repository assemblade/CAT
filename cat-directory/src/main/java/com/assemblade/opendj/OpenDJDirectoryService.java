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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opends.server.config.ConfigException;
import org.opends.server.controls.PasswordPolicyErrorType;
import org.opends.server.controls.PasswordPolicyRequestControl;
import org.opends.server.controls.PasswordPolicyResponseControl;
import org.opends.server.core.BindOperation;
import org.opends.server.protocols.internal.InternalClientConnection;
import org.opends.server.types.Control;
import org.opends.server.types.DN;
import org.opends.server.types.DirectoryEnvironmentConfig;
import org.opends.server.types.DirectoryException;
import org.opends.server.types.InitializationException;
import org.opends.server.types.ResultCode;
import org.opends.server.util.EmbeddedUtils;

import java.util.ArrayList;
import java.util.List;

public class OpenDJDirectoryService implements DirectoryService {
	private Log log = LogFactory.getLog(OpenDJDirectoryService.class);
	
	private final DirectoryEnvironmentConfig config;
	
	public OpenDJDirectoryService(String rootDirectory) throws Exception {
        log.info("Creating new directory service in " + rootDirectory);
		config = OpenDJUtils.buildEnvironmentConfig(rootDirectory);
        try {
            EmbeddedUtils.startServer(config);
            OpenDJUtils.performFirstTimeInitialization();
        } catch (ConfigException e) {
            throw new StorageException(AssembladeErrorCode.ASB_0001, e);
        } catch (InitializationException e) {
            throw new StorageException(AssembladeErrorCode.ASB_0002, e);
        }
    }

	public void stop() {
		EmbeddedUtils.stopServer(null, null);
	}

	public void restart() {
		EmbeddedUtils.restartServer(null, null, config);
	}

	public Session getSession(String userDn) throws StorageException {
		try {
			return new Session(new InternalClientConnection(DN.decode(userDn)));
		} catch (DirectoryException e) {
			log.error("Failed to get a connection for user " + userDn);
			throw new StorageException(AssembladeErrorCode.ASB_0004, e);
		}
	}

	public Session getAdminSession() {
		return new Session(InternalClientConnection.getRootConnection());
	}

	public BindStatus bind(String userDn, String password) {
        PasswordPolicyRequestControl requestControl = new PasswordPolicyRequestControl(true);
        List<Control> controls = new ArrayList<Control>();
        controls.add(requestControl);
        BindOperation result = InternalClientConnection.getRootConnection().processSimpleBind(userDn, password, controls);

        if (result.getResultCode() == ResultCode.SUCCESS) {
            for (Control control : result.getResponseControls()) {
                if (control instanceof PasswordPolicyResponseControl) {
                    PasswordPolicyResponseControl responseControl = (PasswordPolicyResponseControl)control;
                    if (responseControl.getErrorType() != null) {
                        if (responseControl.getErrorType() == PasswordPolicyErrorType.CHANGE_AFTER_RESET) {
                            return BindStatus.PasswordChangeNeeded;
                        }
                        return BindStatus.BadCredentials;
                    }
                }
            }
            return BindStatus.Success;
        } else {
            return BindStatus.BadCredentials;
        }
	}
}
