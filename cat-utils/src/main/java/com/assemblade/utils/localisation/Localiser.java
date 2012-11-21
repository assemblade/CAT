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
package com.assemblade.utils.localisation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;

public class Localiser {
	private Log log = LogFactory.getLog(Localiser.class);
	private static Localiser instance;

	private Properties localisedStrings;
		
	public static Localiser getInstance() {
		if (instance == null) {
			instance = new Localiser();
		}
		return instance;
	}
	
	private Localiser() {
		localisedStrings = new Properties();

		Locale current = Locale.getDefault();
		
		String language = current.getLanguage();
		
		String propertyFile = language + ".properties";

		InputStream is = Localiser.class.getClassLoader().getResourceAsStream(propertyFile);
		
		if (is != null) {
			propertyFile = "en.properties";
			is = Localiser.class.getClassLoader().getResourceAsStream(propertyFile);
		}
		if (is != null) {
			try {
				localisedStrings.load(is);
			} catch (IOException e) {
				log.error("Failed to load localised properties from resource: " + propertyFile, e);
			}
		} else {
			log.error("Could not find default localisation property file: " + propertyFile);
		}
	}
	
	public String translate(String string) {
		return localisedStrings.getProperty(string, string);
	}

}
