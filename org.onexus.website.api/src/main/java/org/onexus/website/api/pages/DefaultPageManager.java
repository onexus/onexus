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
package org.onexus.website.api.pages;


import org.apache.wicket.model.IModel;
import org.onexus.resource.api.IResourceRegister;
import org.osgi.framework.ServiceReference;

import java.util.List;

public class DefaultPageManager implements IPageManager {

    private IResourceRegister resourceRegister;

    private List<IPageCreator> creators;

    public Page<?, ?> create(String componentId, IModel<? extends PageStatus> statusModel) {

        PageStatus status = statusModel.getObject();

        if (status != null) {
            for (IPageCreator creator : creators) {
                if (creator.canCreate(status.getConfig())) {
                    return creator.create(componentId, statusModel);
                }
            }
        }

        return null;
    }

    public List<IPageCreator> getCreators() {
        return creators;
    }

    public void setCreators(List<IPageCreator> creators) {
        this.creators = creators;
    }

    public IResourceRegister getResourceRegister() {
        return resourceRegister;
    }

    public void setResourceRegister(IResourceRegister resourceRegister) {
        this.resourceRegister = resourceRegister;
    }

    public void bindResourceRegister(IResourceRegister resourceRegister) {
        bindCreators(null);
    }

    public void bindCreators(ServiceReference serviceRef) {

        if (resourceRegister != null && creators != null) {
            for (IPageCreator bc : creators) {
                bc.register(resourceRegister);
            }
        }
    }

    public void unbindCreators(ServiceReference serviceRef) {
        // Nothing to do
    }

}
