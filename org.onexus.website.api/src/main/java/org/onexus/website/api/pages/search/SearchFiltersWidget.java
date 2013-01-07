package org.onexus.website.api.pages.search;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.onexus.website.api.pages.browser.IFilter;
import org.onexus.website.api.widgets.filters.FilterConfig;
import org.onexus.website.api.widgets.filters.FiltersWidget;
import org.onexus.website.api.widgets.filters.FiltersWidgetStatus;

import java.util.ArrayList;
import java.util.List;

public abstract class SearchFiltersWidget extends FiltersWidget {

    private transient List<IFilter> filters;

    public SearchFiltersWidget(String componentId, IModel<FiltersWidgetStatus> statusModel) {
        super(componentId, statusModel);
    }

    @Override
    protected boolean isFilterApplyed(FilterConfig filterConfig) {
        return false;
    }

    @Override
    protected void unapplyFilter(FilterConfig filterConfig, AjaxRequestTarget target) {
        // Nothing to do
    }

    @Override
    protected abstract void applyFilter(FilterConfig filterConfig, AjaxRequestTarget target);

    @Override
    protected List<IFilter> getFilters() {
        if (filters == null) {
            filters = new ArrayList<IFilter>();
        }

        return filters;
    }
}
