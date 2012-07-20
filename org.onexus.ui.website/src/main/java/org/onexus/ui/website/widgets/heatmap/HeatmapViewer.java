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
package org.onexus.ui.website.widgets.heatmap;

import org.apache.wicket.markup.html.link.InlineFrame;
import org.apache.wicket.model.IModel;
import org.onexus.resource.api.IResourceManager;
import org.onexus.ui.website.events.EventAddFilter;
import org.onexus.ui.website.events.EventQueryUpdate;
import org.onexus.ui.website.events.EventRemoveFilter;
import org.onexus.ui.website.pages.browser.BrowserPage;
import org.onexus.ui.website.pages.browser.BrowserPageConfig;
import org.onexus.ui.website.pages.browser.BrowserPageStatus;
import org.onexus.ui.website.widgets.Widget;

import javax.inject.Inject;

public class HeatmapViewer extends Widget<HeatmapViewerConfig, HeatmapViewerStatus> {

    @Inject
    public IResourceManager resourceManager;

    public HeatmapViewer(String componentId, IModel<HeatmapViewerStatus> status) {
        super(componentId, status);

        onEventFireUpdate(EventQueryUpdate.class, EventAddFilter.class, EventRemoveFilter.class);
    }

    @Override
    protected void onBeforeRender() {

        addOrReplace(new InlineFrame("heatmap", new HeatmapPage(getConfig(), getQuery())));

        super.onBeforeRender();

    }


    private String getReleaseURI() {

        BrowserPageStatus browserStatus = getPageStatus();
        return (browserStatus != null ? browserStatus.getBase() : null);
    }

    private BrowserPageStatus getPageStatus() {
        return findParent(BrowserPage.class).getStatus();
    }

    private BrowserPageConfig getPageConfig() {
        return (BrowserPageConfig) getPageStatus().getConfig();
    }
}
