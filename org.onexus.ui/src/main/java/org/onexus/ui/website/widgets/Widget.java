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

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import org.onexus.core.query.Query;
import org.onexus.ui.website.IWebsiteModel;
import org.onexus.ui.website.events.AbstractEvent;
import org.onexus.ui.website.events.EventPanel;
import org.onexus.ui.website.pages.IPageModel;
import org.onexus.ui.website.pages.PageStatus;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public abstract class Widget<C extends WidgetConfig, S extends WidgetStatus> extends EventPanel {
    
    private IWidgetModel statusModel;

    public Widget(String componentId, IWidgetModel statusModel) {
        super(componentId);
        
        this.statusModel = statusModel;
    }

    @SuppressWarnings(value = "unchecked")
    public C getConfig() {
        return (C) statusModel.getConfig();
    }

    @SuppressWarnings(value = "unchecked")
    public S getStatus() {
        return (S) statusModel.getObject();
    }

    protected IPageModel getPageModel() {
        return statusModel.getPageModel();
    }
    
    protected IWebsiteModel getWebsiteModel() {
        IPageModel pageModel = getPageModel();

        if (pageModel != null) {
            return pageModel.getWebsiteModel();
        }

        return null;
    }

    protected void buildQuery(Query query) {
        getPage().visitChildren(new QueryBuilder(query));
    }

    private static final class QueryBuilder implements IVisitor<Component, Void> {

        private final Query query;

        public QueryBuilder(final Query query) {
            super();
            this.query = query;
        }

        @Override
        public void component(Component component, IVisit<Void> visit) {
            if (component instanceof IQueryContributor) {
                ((IQueryContributor) component).onQueryBuild(query);
            }
        }

    }
}
