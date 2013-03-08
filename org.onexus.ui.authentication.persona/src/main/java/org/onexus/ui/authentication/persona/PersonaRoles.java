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


import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Deprecated
public class PersonaRoles {

    public final static String PERSONA_FILE = System.getProperty("user.home") + File.separator + ".onexus" + File.separator + "usersPersona.properties";
    private final static Properties personaRoles = new Properties();

    static {

        try {
            File personaFile = new File(PERSONA_FILE);
            if (!personaFile.exists()) {
                personaFile.createNewFile();
            }

            personaRoles.load(new FileReader(personaFile));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public static synchronized List<String> getPersonaRoles(String userName) {

        if (!personaRoles.containsKey(userName)) {

            // Register new user
            personaRoles.setProperty(userName, "registered");
            try {
                personaRoles.store(new FileWriter(PERSONA_FILE), "Users registered using Persona");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        List<String> roles = new ArrayList<String>();
        for (String role : personaRoles.getProperty(userName, "registered").split(",")) {
            roles.add(role.trim());
        }

        return roles;
    }
}
