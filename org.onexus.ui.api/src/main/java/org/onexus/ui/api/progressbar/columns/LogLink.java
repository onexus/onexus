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
package org.onexus.ui.api.progressbar.columns;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

public abstract class LogLink extends Panel {

    public LogLink(String id) {
        super(id);

        AjaxLink<String> link = new AjaxLink<String>("link") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                LogLink.this.onClick(target);
            }
        };

        link.add(new Label("label", "<i class=\"icon-eye-open\"></i>").setEscapeModelStrings(false));
        add(link);
    }

    protected abstract void onClick(AjaxRequestTarget target);
}
