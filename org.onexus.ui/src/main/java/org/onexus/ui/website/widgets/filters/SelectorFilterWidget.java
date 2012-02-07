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
package org.onexus.ui.website.widgets.filters;

import java.util.Collections;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.onexus.core.query.Filter;
import org.onexus.core.query.Query;
import org.onexus.ui.website.IQueryContributor;
import org.onexus.ui.website.events.EventFiltersUpdate;
import org.onexus.ui.website.events.EventQueryUpdate;
import org.onexus.ui.website.widgets.Widget;

/**
 * SelectorFilterWidget provides the possibility to select one filter from a
 * dropdownchoice.
 * 
 * This component ignores active value (it doesn't make sense have a nonactive
 * filters that we could select them)..
 * 
 * @author armand
 */
public class SelectorFilterWidget extends Widget<SelectorFilterWidgetConfig, SelectorFilterWidgetStatus> implements
	IQueryContributor {

    public SelectorFilterWidget(String componentId, SelectorFilterWidgetConfig config,
	    IModel<SelectorFilterWidgetStatus> statusModel) {
	super(componentId, config, statusModel);

	onEventFireUpdate(EventQueryUpdate.class);

	String title = config.getTitle();
	add(new Label("title", (title != null ? title : "Filters")));

	Form<String> form = new Form<String>("form");
	add(form);

	SelectorFilterWidgetStatus status = getStatus();
	if (status == null) {
	    status = new SelectorFilterWidgetStatus(config.getId(), config.getDefaultFilter());
	    setStatus(status);
	}

	IModel<FilterConfig> selectItemModel = new Model<FilterConfig>();
	List<FilterConfig> filters = config.getFilters();
	
	if (filters == null) {
	    filters = Collections.emptyList();
	}
	if (status.getActiveFilter() != null) {
	    for (FilterConfig fc : filters) {
		if (fc.getId().equals(status.getActiveFilter())) {
		    selectItemModel.setObject(fc);
		}
	    }
	} 

	form.add(new AjaxFilterSelector("filters", selectItemModel, filters));

    }

    @Override
    public void onQueryBuild(Query query) {

	String activeFilter = getStatus().getActiveFilter();

	if (activeFilter != null) {
	    for (FilterConfig filter : getConfig().getFilters()) {
		if (activeFilter.equals(filter.getId())) {
		    for (Filter rule : filter.getRules()) {
			query.putFilter(filter.getId(), rule);
		    }
		}

	    }
	}
    }

    /* Component that makes possible to select a filter */
    private class AjaxFilterSelector extends DropDownChoice<FilterConfig> {

	private AjaxFilterSelector(String id, final IModel<FilterConfig> selectItemModel, List<FilterConfig> listFilters) {
	    super(id, selectItemModel, listFilters, new IChoiceRenderer<FilterConfig>() {

		@Override
		public Object getDisplayValue(FilterConfig filter) {
		    return filter != null ? filter.getName() : null;
		}

		@Override
		public String getIdValue(FilterConfig filter, int index) {
		    return filter != null ? filter.getId() : null;
		}
	    });

	    setNullValid(true);
	    add(new AjaxFormComponentUpdatingBehavior("onchange") {
		@Override
		protected void onUpdate(AjaxRequestTarget target) {
		    FilterConfig filter = (FilterConfig) getDefaultModelObject();
		    SelectorFilterWidget.this.getStatus().setActiveFilter((filter == null ? null : filter.getId()));
		    sendEvent(EventFiltersUpdate.EVENT);
		}
	    });
	}
    }
}
