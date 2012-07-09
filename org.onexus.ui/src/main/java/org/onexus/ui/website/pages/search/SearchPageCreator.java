package org.onexus.ui.website.pages.search;

import org.apache.wicket.model.IModel;
import org.onexus.ui.IResourceRegister;
import org.onexus.ui.website.pages.AbstractPageCreator;
import org.onexus.ui.website.pages.Page;

public class SearchPageCreator extends AbstractPageCreator<SearchPageConfig, SearchPageStatus> {

    public SearchPageCreator() {
        super(SearchPageConfig.class, "search", "Search page");
    }

    @Override
    protected Page<?, ?> build(String componentId, IModel<SearchPageStatus> statusModel) {
        return new SearchPage(componentId, statusModel);
    }

    @Override
    public void register(IResourceRegister resourceRegister) {
        super.register(resourceRegister);

       resourceRegister.register(SearchPageStatus.class);
       resourceRegister.register(SearchPageStatus.class);
       resourceRegister.register(SearchType.class);
       resourceRegister.register(SearchLink.class);
    }
}
