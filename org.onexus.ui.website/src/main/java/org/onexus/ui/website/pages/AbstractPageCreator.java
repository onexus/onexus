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
package org.onexus.ui.website.pages;

import org.apache.wicket.model.IModel;
import org.onexus.ui.api.IResourceRegister;

public abstract class AbstractPageCreator<C extends PageConfig, S extends PageStatus> implements IPageCreator {
    
    private Class<C> configType;
    private String title;
    private String description;

    public AbstractPageCreator(Class<C> configType, String title, String description) {
        super();

        this.configType = configType;
        this.title = title;
        this.description = description;

    }


    @Override
    public Page<?, ?> create(String componentId, IModel<?> statusModel) {
        return build(componentId, (IModel<S>) statusModel);
    }

    @Override
    public void register(IResourceRegister resourceRegister) {
        resourceRegister.register(configType);
    }

    @Override
    public boolean canCreate(PageConfig config) {
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

    protected abstract Page<?, ?> build(String componentId, IModel<S> statusModel);

}
