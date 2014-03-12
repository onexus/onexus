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
package org.onexus.ui.workspace.internal.viewers.tree;

import org.apache.commons.collections.CollectionUtils;
import org.apache.wicket.extensions.markup.html.repeater.tree.ITreeProvider;
import org.apache.wicket.model.IModel;
import org.onexus.resource.api.Folder;
import org.onexus.resource.api.IResourceManager;
import org.onexus.resource.api.Project;
import org.onexus.resource.api.Resource;
import org.onexus.ui.api.OnexusWebApplication;
import org.onexus.ui.api.pages.resource.ResourceModel;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class ResourceTreeProvider implements ITreeProvider<Resource> {

    private IModel<? extends Resource> currentResource;

    @Inject
    private IResourceManager resourceManager;

    private static final Iterator<Resource> EMPTY_ITERATOR = CollectionUtils.EMPTY_COLLECTION.iterator();

    public ResourceTreeProvider(IModel<? extends Resource> resource) {
        super();

        OnexusWebApplication.inject(this);

        this.currentResource = resource;

    }

    @Override
    public Iterator<? extends Resource> getRoots() {

        Resource resource = currentResource.getObject();

        if (resource == null) {
            return EMPTY_ITERATOR;
        }

        Project project = resourceManager.getProject(resource.getORI().getProjectUrl());

        if (project == null) {
            return EMPTY_ITERATOR;
        }

        return Arrays.asList(project).iterator();
    }

    @Override
    public boolean hasChildren(Resource node) {

        if (node instanceof Project) {
            return true;
        }

        if (Folder.class.isAssignableFrom(node.getClass())) {
            return true;
        }

        return false;
    }

    @Override
    public Iterator<? extends Resource> getChildren(Resource node) {

        if (node == null) {
            return EMPTY_ITERATOR;
        }

        List<Resource> children = resourceManager.loadChildren(Resource.class, node.getORI());

        Collections.sort(children, RESOURCE_COMPARATOR);

        if (children == null) {
            return EMPTY_ITERATOR;
        }

        return children.iterator();
    }


    @Override
    public IModel<Resource> model(Resource object) {
        return new ResourceModel(object);
    }

    @Override
    public void detach() {
    }

    private static final ResourceComparator RESOURCE_COMPARATOR = new ResourceComparator();

    private static class ResourceComparator implements Comparator<Resource> {

        @Override
        public int compare(Resource o1, Resource o2) {

            if (o1 == null) {
                return 1;
            }

            if (o2 == null) {
                return -1;
            }

            // First folders
            boolean f1 = o1 instanceof Folder;
            boolean f2 = o2 instanceof Folder;

            if (f1 && !f2) {
                return -1;
            }

            if (f2 && !f1) {
                return 1;
            }

            return o1.getORI().toString().compareTo(o2.getORI().toString());

        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof ResourceComparator;
        }
    }
}
