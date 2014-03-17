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
package org.onexus.ui.api.pages.resource;

import org.apache.wicket.model.LoadableDetachableModel;
import org.onexus.resource.api.IResourceManager;
import org.onexus.resource.api.ORI;
import org.onexus.resource.api.Resource;
import org.onexus.ui.api.OnexusWebApplication;

import javax.inject.Inject;

public class ResourceModel extends LoadableDetachableModel<Resource> {

    private ORI resourceURI;

    @Inject
    private IResourceManager resourceManager;

    public ResourceModel() {
        super();
    }

    public ResourceModel(String resourceParameter) {
        if (resourceParameter != null) {
            this.resourceURI = new ORI(resourceParameter);
        }
    }

    public ResourceModel(ORI resourceURI) {
        super();
        this.resourceURI = resourceURI;
    }

    public ResourceModel(Resource resource) {
        super(resource);
        this.resourceURI = resource.getORI();
    }

    @Override
    public void setObject(Resource object) {
        super.setObject(object);
        this.resourceURI = object.getORI();
    }

    @Override
    protected Resource load() {

        if (resourceURI != null) {
            return getResourceManager().load(Resource.class, resourceURI);
        }

        return null;
    }

    private IResourceManager getResourceManager() {
        if (resourceManager == null) {
            OnexusWebApplication.inject(this);
        }
        return resourceManager;
    }

    @Override
    protected void onDetach() {
        if (getObject() != null) {
            this.resourceURI = getObject().getORI();
        } else {
            this.resourceURI = null;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ResourceModel) {
            return ((ResourceModel) obj).resourceURI.equals(this.resourceURI);
        }
        return false;
    }


    public int hashCode() {
        return resourceURI.hashCode();
    }


}
