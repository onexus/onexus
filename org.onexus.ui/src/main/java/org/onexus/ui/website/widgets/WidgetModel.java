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

import org.onexus.ui.website.pages.IPageModel;
import org.onexus.ui.website.pages.PageStatus;

public class WidgetModel<S extends WidgetStatus> implements IWidgetModel<S> {

    private WidgetConfig config;
    private IPageModel<? extends PageStatus> pageModel;
    private S status;

    public WidgetModel(WidgetConfig config) {
        this(config, null);
    }

    public WidgetModel(WidgetConfig config, IPageModel<? extends PageStatus> pageModel) {
        super();
        this.config = config;
        this.pageModel = pageModel;
    }

    @Override
    public WidgetConfig getConfig() {
        return config;
    }

    @Override
    public IPageModel getPageModel() {
        return pageModel;
    }

    @Override
    public S getObject() {

        if (status != null ) {
            return status;
        }

        if (pageModel!=null) {
            status = (S) pageModel.getObject().getWidgetStatus(config.getId());
        }
        
        if (status == null) {
            status = (S) config.getDefaultStatus();

            if (status == null) {
                status = (S) config.createEmptyStatus();
            }
            
            if (status != null) {
                setObject(status);
            }
        }
        
        return status;
    }

    @Override
    public void setObject(S object) {
        status = object;

        if (pageModel != null) {
            pageModel.getObject().setWidgetStatus(object);
        }
    }

    @Override
    public void detach() {
    }
}
