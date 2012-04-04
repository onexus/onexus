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
package org.onexus.ui.website.widgets.text;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.model.IModel;
import org.onexus.ui.website.utils.panels.HelpContentPanel;
import org.onexus.ui.website.utils.panels.HelpMark;
import org.onexus.ui.website.widgets.IWidgetModel;
import org.onexus.ui.website.widgets.Widget;

public class TextWidget extends Widget<TextWidgetConfig, TextWidgetStatus> {

    public TextWidget(String componentId, IWidgetModel<TextWidgetStatus> statusModel) {
        super(componentId, statusModel);

        add(new Label("title", getConfig().getTitle()));
        add(new Label("text", getConfig().getText()).setEscapeModelStrings(false));
        
        if (getConfig().getDetails() != null) {
            add(new HelpMark("details", getConfig().getText(), getConfig().getDetails()));
        } else {
            add(new EmptyPanel("details"));
        }

    }

}
