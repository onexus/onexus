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
package org.onexus.website.api.pages.browser;

import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.model.IModel;
import org.onexus.website.api.events.AbstractEvent;

public abstract class BrowserPageLink<T> extends AjaxLink<T> {

    public BrowserPageLink(String id) {
        super(id);

    }

    public BrowserPageLink(String id, IModel<T> model) {
        super(id, model);
    }

    protected BrowserPageStatus getBrowserPageStatus() {
        BrowserPage browser = findParent(BrowserPage.class);
        return browser.getModel().getObject();
    }

    protected void sendEvent(AbstractEvent event) {
        send(getPage(), Broadcast.BREADTH, event);
    }


}
