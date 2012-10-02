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
package ${package};

import org.apache.wicket.model.IModel;
import org.onexus.ui.website.WebsiteConfig;
import org.onexus.ui.website.widgets.AbstractWidgetCreator;
import org.onexus.ui.website.widgets.Widget;

public class HelloWidgetCreator extends AbstractWidgetCreator<HelloWidgetConfig, HelloWidgetStatus> {

    public HelloWidgetCreator() {
        super(HelloWidgetConfig.class, "widget-hello", "Hello world widget");
    }

    @Override
    protected Widget<?, ?> build(String componentId, IModel<HelloWidgetStatus> statusModel) {
        return new HelloWidget(componentId, statusModel);
    }

}
