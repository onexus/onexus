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
package org.onexus.website.api.widgets.selection;

import org.onexus.collection.api.query.Filter;
import org.onexus.collection.api.query.IQueryParser;
import org.onexus.collection.api.query.Query;
import org.onexus.collection.api.utils.QueryUtils;
import org.onexus.resource.api.IResourceSerializer;
import org.onexus.resource.api.ORI;
import org.onexus.website.api.WebsiteApplication;
import org.onexus.website.api.pages.browser.IEntitySelection;
import org.onexus.website.api.utils.visible.VisibleRule;
import org.ops4j.pax.wicket.api.PaxWicketBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.regex.Pattern;

public class MultipleEntitySelection implements IEntitySelection {

    private static final Logger log = LoggerFactory.getLogger(MultipleEntitySelection.class);
    private FilterConfig config;
    private boolean enable;
    private boolean deletable;

    @PaxWicketBean(name = "queryParser")
    private IQueryParser queryParser;

    @PaxWicketBean(name = "resourceSerializer")
    private IResourceSerializer resourceSerializer;

    public MultipleEntitySelection() {
    }

    public MultipleEntitySelection(FilterConfig config) {
        this.config = config;
        this.enable = true;
        this.deletable = true;
    }

    @Override
    public FilterConfig getFilterConfig() {
        return config;
    }

    public void setFilterConfig(FilterConfig config) {
        this.config = config;
    }

    @Override
    public ORI getSelectionCollection() {
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
         return compileFilter(this.config, query, getQueryParser());
    }

    private static Filter compileFilter(FilterConfig config, Query query, IQueryParser queryParser) {
        String oqlDefine = config.getDefine();
        String oqlWhere = config.getWhere();

        if (oqlDefine != null && oqlWhere != null) {
            Map<String, ORI> define = queryParser.parseDefine(oqlDefine);


            if (define == null) {
                log.error("Malformed filter definition\n DEFINE: " + config.getDefine() + "\n");
            } else {

                for (Map.Entry<String, ORI> entry : define.entrySet()) {
                    String collectionAlias = QueryUtils.newCollectionAlias(query, entry.getValue());
                    oqlWhere = oqlWhere.replaceAll(entry.getKey() + ".", collectionAlias + ".");
                }

                Filter where = queryParser.parseWhere(oqlWhere);

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
    public String getTitle(Query query) {
        return config.getName();
    }

    @Override
    public boolean match(VisibleRule rule) {

        if (rule.getType() == VisibleRule.SelectionType.SINGLE) {
            return false;
        }

        ORI visibleCollection = config.getCollection();

        //TODO
        boolean validCollection = visibleCollection.getPath().endsWith(rule.getFilteredCollection().getPath());

        if (rule.getField() == null) {
            return validCollection;
        } else {
            return false;
        }
    }

    private static String SEPARATOR = "::";
    private static Pattern DOUBLE_COLON = Pattern.compile(SEPARATOR);

    @Override
    public String toUrlParameter(boolean global, ORI parentOri) {

        StringBuilder str = new StringBuilder();
        str.append(config.getName()).append(SEPARATOR);

        ORI ori;
        if (global) {
            ori = config.getCollection().toAbsolute(parentOri);
        } else {
            ori = config.getCollection();
        }

        str.append(ori).append(SEPARATOR);
        str.append(config.getDefine()).append(SEPARATOR);
        str.append(config.getWhere()).append(SEPARATOR);
        str.append((config.isDeletable() ? "d" : ""));
        return str.toString();

    }

    @Override
    public void loadUrlPrameter(String parameter) {

        String[] values = DOUBLE_COLON.split(parameter);

        config = new FilterConfig();

        config.setName(values[0]);
        config.setCollection(new ORI(values[1]));
        config.setDefine(values[2]);
        config.setWhere(values[3]);

        if (values.length > 4) {
            config.setDeletable(values[4].contains("d"));
        } else {
            config.setDeletable(false);
        }

        deletable = config.isDeletable();
        enable = true;
    }

    private IQueryParser getQueryParser() {

        if (queryParser == null) {
            WebsiteApplication.inject(this);
        }

        return queryParser;
    }


}
