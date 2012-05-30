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
package org.onexus.ui.website.widgets.download;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.InlineFrame;
import org.apache.wicket.markup.html.link.ResourceLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.onexus.core.query.Query;
import org.onexus.ui.OnexusWebApplication;
import org.onexus.ui.website.events.EventQueryUpdate;
import org.onexus.ui.website.widgets.Widget;
import org.onexus.ui.website.widgets.download.scripts.BashScript;
import org.onexus.ui.website.widgets.download.scripts.IQueryScript;

import java.util.Arrays;
import java.util.List;

public class DownloadWidget extends Widget<DownloadWidgetConfig, DownloadWidgetStatus> {

    public DownloadWidget(String componentId, IModel<DownloadWidgetStatus> statusModel) {
        super(componentId, statusModel);
        onEventFireUpdate(EventQueryUpdate.class);
    }

    @Override
    protected void onBeforeRender() {

        StringBuilder oql = new StringBuilder();

        Query query = getQuery();
        query.toString(oql, false);

        PageParameters parameters = new PageParameters();
        parameters.add("query", oql.toString());

        addOrReplace(new InlineFrame("iframe", DownloadPage.class, parameters));

        super.onBeforeRender();
    }

}
