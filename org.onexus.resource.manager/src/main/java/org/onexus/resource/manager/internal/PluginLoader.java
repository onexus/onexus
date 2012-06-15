package org.onexus.resource.manager.internal;

import org.onexus.core.resources.Plugin;
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
            try {
                bundle.start();
                log.debug("Plugin " + plugin.getId() + " installed and started. Bundle ID: " + bundle.getBundleId());
            } catch (BundleException e) {
                String msg = "Plugin " + plugin.getId() + " installed but NOT started. Bundle ID: " + bundle.getBundleId();
                log.error(msg, e);
                throw new InvalidParameterException(msg);
            }
        }
    }
}
