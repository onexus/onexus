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
package org.onexus.ui.website.widgets;

import org.apache.wicket.model.IModel;
import org.onexus.ui.IResourceRegister;

public abstract class AbstractWidgetCreator<C extends WidgetConfig, S extends WidgetStatus> implements IWidgetCreator {

    private Class<C> configType;
    private String title;
    private String description;
    
    public AbstractWidgetCreator(Class<C> configType, String title, String description) {
        super();

        this.configType = configType;
        this.title = title;
        this.description = description;
    }

    @Override
    public void register(IResourceRegister resourceRegister) {
        resourceRegister.register(configType);
    }

    @Override
    public boolean canCreate(WidgetConfig config) {
        return configType.equals(config.getClass());
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Widget<?, ?> create(String componentId, IModel<?> statusModel) {
        return build(componentId, (IModel<S>) statusModel);
    }

    protected abstract Widget<?, ?> build(String componentId, IModel<S> statusModel);

}
