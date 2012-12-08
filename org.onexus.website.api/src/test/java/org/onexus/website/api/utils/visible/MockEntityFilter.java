package org.onexus.website.api.utils.visible;


import org.h2.util.StringUtils;
import org.onexus.collection.api.query.Filter;
import org.onexus.collection.api.query.Query;
import org.onexus.resource.api.ORI;
import org.onexus.website.api.pages.browser.IFilter;
import org.onexus.website.api.widgets.filters.FilterConfig;

public class MockEntityFilter implements IFilter {

    private ORI filteredCollection;
    private String fieldValue;

    public MockEntityFilter(ORI filteredCollection, String fieldValue) {
        this.fieldValue = fieldValue;
        this.filteredCollection = filteredCollection;
    }

    @Override
    public boolean match(VisibleRule rule) {

        String filterPath = filteredCollection.getPath();
        String rulePath = rule.getFilteredCollection().getPath();

        boolean validCollection = ( filterPath == null || rulePath == null ) ? false : filterPath.endsWith(rulePath);

        if (rule.getField() == null) {
            return validCollection;
        }

        return StringUtils.equals(fieldValue, rule.getValue());

    }


    @Override
    public ORI getFilteredCollection() {
        return filteredCollection;
    }

    @Override
    public FilterConfig getFilterConfig() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isEnable() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setEnable(boolean enabled) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isDeletable() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setDeletable(boolean deletable) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Filter buildFilter(Query query) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getLabel(Query query) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getTitle(Query query) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toUrlParameter() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void loadUrlPrameter(String parameter) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getVisible() {
        throw new UnsupportedOperationException();
    }
}
