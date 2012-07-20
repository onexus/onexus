package org.onexus.ui.website.widgets.filters;

import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.onexus.resource.api.IQueryParser;
import org.onexus.resource.api.query.Filter;
import org.onexus.resource.api.query.Query;
import org.onexus.resource.api.utils.QueryUtils;
import org.onexus.ui.core.OnexusWebApplication;
import org.onexus.ui.website.pages.browser.IFilter;
import org.onexus.ui.website.utils.visible.VisibleRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Map;

public class BrowserFilter implements IFilter {

    private static final Logger log = LoggerFactory.getLogger(BrowserFilter.class);
    private FilterConfig config;
    private boolean enable;
    private boolean deletable;

    @Inject
    private IQueryParser queryParser;

    public BrowserFilter(FilterConfig config) {
        this.config = config;
        this.enable = true;
        this.deletable = true;
    }

    @Override
    public FilterConfig getFilterConfig() {
        return config;
    }

    @Override
    public String getFilteredCollection() {
        return config.getCollection();
    }

    @Override
    public boolean isEnable() {
        return enable;
    }

    @Override
    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    @Override
    public boolean isDeletable() {
        return deletable;
    }

    @Override
    public void setDeletable(boolean deletable) {
        this.deletable = deletable;
    }

    @Override
    public Filter buildFilter(Query query) {

        String oqlDefine = config.getDefine();
        String oqlWhere = config.getWhere();

        if (oqlDefine != null && oqlWhere != null) {
            Map<String, String> define = getQueryParser().parseDefine(oqlDefine);


            if (define == null) {
                log.error("Malformed filter definition\n DEFINE: " + config.getDefine() + "\n");
            } else {

                for (Map.Entry<String, String> entry : define.entrySet()) {
                    String collectionAlias = QueryUtils.newCollectionAlias(query, entry.getValue());
                    oqlWhere = oqlWhere.replaceAll(entry.getKey() + ".", collectionAlias + ".");
                }

                Filter where = getQueryParser().parseWhere(oqlWhere);

                if (where == null) {
                    log.error("Malformed filter WHERE: " + oqlWhere + "\n");
                } else {
                    return where;
                }
            }
        }

        return null;
    }

    @Override
    public String getLabel(Query query) {
        return config.getDefine();
    }

    @Override
    public String getTitle(Query query) {
        return config.getName();
    }

    @Override
    public Panel getTooltip(String componentId, Query query) {
        return new EmptyPanel(componentId);
    }

    @Override
    public boolean isVisible(VisibleRule rule) {
        return true;
    }

    @Override
    public String toUrlParameter() {
        //TODO
        return "";
    }

    @Override
    public void loadUrlPrameter(String parameter) {
        //TODO
    }

    @Override
    public String getVisible() {
        return config.getVisible();
    }

    private IQueryParser getQueryParser() {

        if (queryParser == null) {
            OnexusWebApplication app = OnexusWebApplication.get();
            if (app != null) {
                app.getInjector().inject(this);
            }
        }

        return queryParser;
    }

}
