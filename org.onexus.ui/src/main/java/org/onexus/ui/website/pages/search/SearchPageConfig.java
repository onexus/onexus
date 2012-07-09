package org.onexus.ui.website.pages.search;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import org.onexus.ui.website.pages.PageConfig;
import org.onexus.ui.website.pages.PageStatus;
import org.onexus.ui.website.widgets.WidgetConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@XStreamAlias("search")
public class SearchPageConfig extends PageConfig {

    private SearchPageStatus defaultStatus;

    @XStreamImplicit( itemFieldName = "type" )
    private List<SearchType> types = new ArrayList<SearchType>();


    public SearchPageConfig() {
        super();
    }

    public List<SearchType> getTypes() {
        return types;
    }

    public void setTypes(List<SearchType> types) {
        this.types = types;
    }

    @Override
    public List<WidgetConfig> getWidgets() {
        return Collections.emptyList();
    }

    @Override
    public PageStatus createEmptyStatus() {
        return new SearchPageStatus();
    }

    @Override
    public PageStatus getDefaultStatus() {
        return defaultStatus;
    }


}
