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
package org.onexus.resource.manager.internal;

import org.onexus.resource.api.Plugin;
import org.onexus.resource.api.Project;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.security.InvalidParameterException;

public class PluginLoader implements Serializable {

    private static final Logger log = LoggerFactory.getLogger(PluginLoader.class);
    private BundleContext context;

    public PluginLoader(BundleContext context) {
        super();
        this.context = context;
    }

    public void load(Project project) {

        if (project.getPlugins() != null) {
            for (Plugin plugin : project.getPlugins()) {
                try {
                    load(plugin);
                } catch (InvalidParameterException e) {
                    log.error(e.getMessage());
                }
            }
        }
    }

    public void load(Plugin plugin) throws InvalidParameterException {

        String location = plugin.getLocation();

        if (location == null) {
            throw new InvalidParameterException("Plugin \" + plugin.getId() + \" without location.");
        }

        location = location.trim();

        if (location.isEmpty()) {
            throw new InvalidParameterException("Plugin " + plugin.getId() + " without location.");
        }

        Bundle bundle = null;
        try {
            bundle = context.installBundle(location, null);
        } catch (IllegalStateException ex) {
            log.error(ex.toString());
        } catch (BundleException ex) {
            if (ex.getNestedException() != null) {
                log.error(ex.getNestedException().toString());
            } else {
                log.error(ex.toString());
            }
        }

        if (bundle != null) {

            if (bundle.getState() == Bundle.ACTIVE) {
                // It was already installed
                return;
            }

            try {

                int previousState = bundle.getState();

                bundle.start();

                log.info("Plugin " + plugin.getId() + " installed and started. Bundle ID: " + bundle.getBundleId());

                Thread.sleep(1000);

            } catch (BundleException e) {
                String msg = "Plugin " + plugin.getId() + " installed but NOT started. Bundle ID: " + bundle.getBundleId();
                log.error(msg, e);
            } catch (InterruptedException e) {
            }
        }
    }
}
