/**
 *  Copyright 2012 Universitat Pompeu Fabra.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */
package org.onexus.resource.profile.internal;

import org.apache.commons.lang3.StringUtils;
import org.onexus.resource.api.IProfileManager;
import org.onexus.resource.api.session.LoginContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Properties;

public class ProfileManager implements IProfileManager {

    private static final Logger log = LoggerFactory.getLogger(ProfileManager.class);
    public final static String ONEXUS_FOLDER = System.getProperty("user.home") + File.separator + ".onexus";
    public final static String ONEXUS_PROFILES_FOLDER = ONEXUS_FOLDER + File.separator + "profiles";


    @Override
    public Collection<String> getKeys() {
        Properties properties = null;
        try {
            properties = loadProperties(getUserName());
        } catch (IOException e) {
            log.error("Loading '"+getUserName()+"' profile properties file.", e);
            return Collections.EMPTY_LIST;
        }
        return properties.stringPropertyNames();
    }

    @Override
    public String getValue(String key) {
        Properties properties = null;
        try {
            properties = loadProperties(getUserName());
        } catch (IOException e) {
            log.error("Loading '" + getUserName() + "' profile properties file.", e);
            return null;
        }
        return properties.getProperty(key);
    }

    @Override
    public String[] getValueArray(String key) {
        return splitValue(getValue(key));
    }

    @Override
    public void putValue(String key, String value) {
        String userName = getUserName();
        Properties properties = null;

        try {
            properties = loadProperties(userName);
        } catch (IOException e) {
            log.error("Loading '"+getUserName()+"' profile properties file.", e);
            return;
        }

        properties.put(key, value);
        try {
            storeProperties(userName, properties);
        } catch (IOException e) {
            log.error("Storing '" + getUserName() + "' profile properties file.", e);
        }
    }

    @Override
    public void putValueArray(String key, String[] values) {
        putValue(key, joinValues(values));
    }

    public void load() {

    }

    private String getUserName() {
        LoginContext ctx = LoginContext.get();
        return ctx.getUserName();
    }

    private Properties loadProperties(String userName) throws IOException {

        Properties properties = new Properties();

        File file = new File(ONEXUS_PROFILES_FOLDER + File.separator + userName);

        if (!file.exists()) {
            //TODO
        }

        properties.load(new FileReader(file));

        return properties;
    }

    private void storeProperties(String userName, Properties properties) throws IOException {
        properties.store(new FileWriter(ONEXUS_PROFILES_FOLDER + File.separator + userName), "");
    }

    private String[] splitValue(String value) {
        return StringUtils.split(value, '\t');
    }

    private String joinValues(String[] values) {
        return StringUtils.join(values, '\t');
    }

}
