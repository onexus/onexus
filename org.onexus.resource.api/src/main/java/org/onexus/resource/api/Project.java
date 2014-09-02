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
package org.onexus.resource.api;


import org.onexus.resource.api.annotations.ResourceAlias;
import org.onexus.resource.api.annotations.ResourceRegister;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.util.List;

@ResourceAlias("project")
@ResourceRegister({Plugin.class, Service.class})
public class Project extends Resource {

    @Pattern(regexp = PATTERN_ID)
    private transient String name;

    private String alias;

    @Valid
    private List<Plugin> plugins;

    @Valid
    private List<Service> services;

    public Project() {
        super();
    }

    public Project(String projectUrl, String name) {
        super(new ORI(projectUrl, null));

        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getURL() {
        return getORI().getProjectUrl();
    }

    public Plugin getPlugin(String pluginId) {

        if (plugins == null || pluginId == null) {
            return null;
        }

        for (Plugin plugin : plugins) {
            if (pluginId.equals(plugin.getId())) {
                return plugin;
            }
        }

        return null;
    }

    public List<Plugin> getPlugins() {
        return plugins;
    }

    public void setPlugins(List<Plugin> plugins) {
        this.plugins = plugins;
    }

    public Service getService(String serviceId) {

        if (services == null || serviceId == null) {
            return null;
        }

        for (Service service : services) {
            if (serviceId.equals(service.getId())) {
                return service;
            }
        }

        return null;
    }

    public List<Service> getServices() {
        return services;
    }

    public void setServices(List<Service> services) {
        this.services = services;
    }

    @Override
    public String toString() {
        return "Project{" +
                "plugins=" + plugins +
                '}';
    }
}
