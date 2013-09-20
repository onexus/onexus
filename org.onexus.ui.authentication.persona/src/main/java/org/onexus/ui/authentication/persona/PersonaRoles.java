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
package org.onexus.ui.authentication.persona;


import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Deprecated
public final class PersonaRoles {

    public static final String PERSONA_FILE = System.getProperty("user.home") + File.separator + ".onexus" + File.separator + "usersPersona.properties";
    private static final Logger LOGGER = LoggerFactory.getLogger(PersonaRoles.class);

    private static Properties personaRoles;
    private static FileAlterationMonitor monitor;

    static {

        try {
            personaRoles = loadProperties();
        } catch (IOException e) {
            LOGGER.error("Error loading persona roles config file", e);
        }

        // Watch file changes and fire a reload
        monitor = new FileAlterationMonitor(2000);
        File file = new File(PERSONA_FILE);
        FileAlterationObserver observer = new FileAlterationObserver(file.getParent(), FileFilterUtils.nameFileFilter(file.getName()));
        observer.addListener(new FileAlterationListenerAdaptor() {
            @Override
            public void onFileChange(File file) {
                try {
                    personaRoles = loadProperties();
                } catch (IOException e) {
                    LOGGER.error("Error loading persona roles config file", e);
                }
            }
        });
        monitor.addObserver(observer);
        try {
            monitor.start();
        } catch (Exception e) {
            LOGGER.error("On start persona roles config file monitor", e);
        }


    }

    private PersonaRoles() {
    }

    public static synchronized Properties loadProperties() throws IOException {

        LOGGER.info("Loading persona roles config file");

        Properties properties = new Properties();

        File personaFile = new File(PERSONA_FILE);
        if (!personaFile.exists()) {
            personaFile.createNewFile();
        }

        properties.load(new FileReader(personaFile));

        return properties;

    }


    public static synchronized List<String> getPersonaRoles(String userName) {

        if (!personaRoles.containsKey(userName)) {

            // Register new user
            personaRoles.setProperty(userName, "registered");
            try {
                personaRoles.store(new FileWriter(PERSONA_FILE), "Users registered using Persona");
            } catch (IOException e) {
                LOGGER.error("Storing persona file", e);
            }

        }

        List<String> roles = new ArrayList<String>();
        for (String role : personaRoles.getProperty(userName, "registered").split(",")) {
            roles.add(role.trim());
        }

        return roles;
    }
}
