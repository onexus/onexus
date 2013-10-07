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
package org.onexus.resource.api.utils;

import org.onexus.resource.api.IResourceListener;
import org.onexus.resource.api.Project;
import org.onexus.resource.api.Resource;

/**
 * Convenience {@link IResourceListener} implementation that does nothing.
 */
public abstract class ResourceListener implements IResourceListener {

    @Override
    public void onProjectCreate(Project project) {
    }

    @Override
    public void onProjectChange(Project project) {
    }

    @Override
    public void onProjectDelete(Project project) {
    }

    @Override
    public void onResourceCreate(Resource resource) {
    }

    @Override
    public void onResourceChange(Resource resource) {
    }

    @Override
    public void onResourceDelete(Resource resource) {
    }
}
