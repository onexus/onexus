/**
 *  Copyright 2011 Universitat Pompeu Fabra.
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
package org.onexus.ui.website.viewers;

import org.apache.wicket.model.IModel;
import org.onexus.ui.website.AbstractWebsiteCreator;

public abstract class AbstractCollectionViewerCreator<C extends ViewerConfig, S extends ViewerStatus> extends
	AbstractWebsiteCreator<ViewerConfig, ViewerStatus> implements ICollectionViewerCreator {

    public AbstractCollectionViewerCreator(Class<C> configType, String title, String description) {
	super(configType, title, description);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Viewer<?, ?> create(String componentId, ViewerConfig config, IModel<ViewerStatus> statusModel) {
	return build(componentId, (C) config, (IModel<S>) statusModel);
    }

    protected abstract Viewer<?, ?> build(String componentId, C config, IModel<S> statusModel);

}
