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
package org.onexus.ui.website.pages.browser.layouts.leftmain;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.onexus.ui.website.pages.IPageModel;
import org.onexus.ui.website.pages.browser.BrowserPageStatus;
import org.onexus.ui.website.pages.browser.layouts.topleft.HorizontalWidgetBar;
import org.onexus.ui.website.pages.browser.layouts.topleft.VerticalWidgetBar;
import org.onexus.ui.website.widgets.WidgetConfig;

import java.util.Collection;
import java.util.List;

public class LeftMainLayout extends Panel {

    public static final String LAYOUT = "leftmain";

    public static final String REGION_LEFT = "left";
    public static final String REGION_MAIN = "main";

    public static final CssResourceReference CSS = new CssResourceReference(LeftMainLayout.class, "LeftMainLayout.css");

    public static final RegionPredicate PREDICATE_LEFT = new RegionPredicate(REGION_LEFT);
    public static final RegionPredicate PREDICATE_MAIN = new RegionPredicate(REGION_MAIN);

    public LeftMainLayout(String panelId, List<WidgetConfig> widgets, IPageModel<BrowserPageStatus> statusModel) {
        super(panelId);

        // Add left widgets
        add(new VerticalWidgetBar("leftwidgets", filterWidgets(widgets, PREDICATE_LEFT), statusModel));

        // Add main widgets
        add(new HorizontalWidgetBar("main", filterWidgets(widgets, PREDICATE_MAIN), statusModel));

    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(CSS);
    }

    private static Collection<WidgetConfig> filterWidgets(Collection<WidgetConfig> allWidgets, Predicate predicate) {
        return CollectionUtils.select(allWidgets, predicate);
    }

    private static class RegionPredicate implements Predicate {

        private String region;

        private RegionPredicate(String region) {
            assert region != null;
            this.region = region;
        }

        @Override
        public boolean evaluate(Object object) {

            if (object instanceof WidgetConfig) {
                return (region.equals(((WidgetConfig) object).getRegion()));
            }

            return false;
        }
    }

}
