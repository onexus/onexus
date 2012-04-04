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
package org.onexus.ui.workspace.tree;

import org.apache.wicket.model.AbstractReadOnlyModel;
import org.onexus.core.IResourceManager;
import org.onexus.core.resources.Workspace;

import java.util.ArrayList;
import java.util.List;

public abstract class WorkspaceListModel extends AbstractReadOnlyModel<List<Workspace>> {

    private transient List<Workspace> workspaces;

    @Override
    public List<Workspace> getObject() {

        if (workspaces == null) {
            workspaces = new ArrayList<Workspace>();

            workspaces = getResourceManager().loadChildren(Workspace.class, null);
        }

        return workspaces;
    }

    protected abstract IResourceManager getResourceManager();
}
