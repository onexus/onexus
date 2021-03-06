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
package org.onexus.ui.workspace.internal.viewers.utils;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.*;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.resource.JQueryPluginResourceReference;


public class PrettifyBehavior extends Behavior {

    private static final HeaderItem CSS = CssHeaderItem.forReference(new CssResourceReference(PrettifyBehavior.class, "prettify.css"));
    private static final HeaderItem JS = JavaScriptHeaderItem.forReference(new JQueryPluginResourceReference(PrettifyBehavior.class, "prettify.js"));

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        response.render(CSS);
        response.render(JS);
        response.render(OnLoadHeaderItem.forScript("prettyPrint()"));
    }
}
