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
package org.onexus.ui.website.widgets.tags;

import org.apache.wicket.model.IModel;
import org.onexus.ui.IResourceRegister;
import org.onexus.ui.website.widgets.AbstractWidgetCreator;
import org.onexus.ui.website.widgets.Widget;

public class TagWidgetCreator extends AbstractWidgetCreator<TagWidgetConfig, TagWidgetStatus> {
    
    public TagWidgetCreator() {
	super(TagWidgetConfig.class, "tag-widget", "Create and manage labels to the table rows.");
    }

    @Override
    protected Widget<?,?> build(String componentId, TagWidgetConfig config, IModel<TagWidgetStatus> statusModel) {
	return new TagWidget(componentId, config, statusModel);
    }

    @Override
    public void register(IResourceRegister resourceRegister) {
	resourceRegister.register(TagWidgetConfig.class);
	resourceRegister.register(TagColumnConfig.class);	
    }
    
    

}
