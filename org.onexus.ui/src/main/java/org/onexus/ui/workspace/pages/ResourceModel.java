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
package org.onexus.ui.workspace.pages;

import org.apache.wicket.model.LoadableDetachableModel;
import org.onexus.core.IResourceManager;
import org.onexus.core.resources.Resource;
import org.onexus.ui.OnexusWebApplication;
import org.onexus.ui.OnexusWebSession;

import javax.inject.Inject;

public class ResourceModel extends LoadableDetachableModel<Resource> {

    private String resourceURI;

    @Inject
    private IResourceManager resourceManager;

    public ResourceModel() {
        super();
        OnexusWebApplication.inject(this);
    }

    public ResourceModel(String resourceURI) {
        super();
        OnexusWebApplication.inject(this);
        this.resourceURI = resourceURI;
    }

    public ResourceModel(Resource resource) {
        super(resource);
        OnexusWebApplication.inject(this);
        this.resourceURI = resource.getURI();
    }

    @Override
    public void setObject(Resource object) {
        super.setObject(object);
        this.resourceURI = object.getURI();
    }

    @Override
    protected Resource load() {

        if (resourceURI != null) {
            return resourceManager.load(Resource.class, resourceURI);
        }

        return null;
    }

    @Override
    protected void onDetach() {
        if (getObject() != null) {
            this.resourceURI = getObject().getURI();
        } else {
            this.resourceURI = null;
        }
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof ResourceModel)
        {
            return ((ResourceModel)obj).resourceURI.equals(this.resourceURI);
        }
        return false;
    }


    public int hashCode()
    {
        return resourceURI.hashCode();
    }


}
