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

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.opends.server.core.SynchronizationProviderConfigManager;


public class SynchronizationProviderService {
	private Log log = LogFactory.getLog(SynchronizationProviderService.class);
	
	public void start() {
		try {
			SynchronizationProviderConfigManager spcm = new SynchronizationProviderConfigManager();
			spcm.initializeSynchronizationProviders();
			log.info("Successfully started SynchronizationProviders");
		} catch (Exception e) {
			log.error("Failed to initialise SynchronizationProviders! " + e.getMessage());
		}
	}
}
