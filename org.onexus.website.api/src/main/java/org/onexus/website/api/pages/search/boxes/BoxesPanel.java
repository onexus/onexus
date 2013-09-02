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
package org.onexus.website.api.pages.search.boxes;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.onexus.collection.api.ICollectionManager;
import org.onexus.collection.api.IEntity;
import org.onexus.collection.api.IEntityTable;
import org.onexus.collection.api.query.Contains;
import org.onexus.collection.api.query.In;
import org.onexus.collection.api.query.OrderBy;
import org.onexus.collection.api.query.Query;
import org.onexus.collection.api.utils.QueryUtils;
import org.onexus.resource.api.ORI;
import org.onexus.website.api.pages.search.FigureConfig;
import org.onexus.website.api.pages.search.SearchLink;
import org.onexus.website.api.pages.search.SearchPageStatus;
import org.onexus.website.api.pages.search.SearchType;
import org.onexus.website.api.pages.search.figures.FigureBox;
import org.onexus.website.api.pages.search.figures.LinksBox;
import org.onexus.website.api.widgets.selection.FilterConfig;
import org.ops4j.pax.wicket.api.PaxWicketBean;

import java.util.List;

public class BoxesPanel extends Panel {

    @PaxWicketBean(name = "collectionManager")
    private ICollectionManager collectionManager;

    public BoxesPanel(String id, SearchPageStatus status, ORI baseUri, FilterConfig filterConfig) {
        super(id);
        setMarkupId("boxes");
        add(new AttributeModifier("class", "accordion"));

        RepeatingView boxes = new RepeatingView("boxes");

        SearchType type = status.getType();

        if (status.getSearch() == null) {

            // Nothing selected
            add(new EmptyPanel("disambiguation").setVisible(false));
            List<SearchLink> links = type.getFixLinks();
            if (links != null && !links.isEmpty()) {
                boxes.add(new MainLinksBox(boxes.newChildId(), links));
            }

        } else {
            ORI collectionUri = type.getCollection().toAbsolute(baseUri);
            if (filterConfig == null && status.getSearch().indexOf(',') == -1) {

                // Single entity selection
                IEntityTable table = getEntityTable(type, collectionUri, status.getSearch());

                if (table.next()) {

                    IEntity entity = table.getEntity(collectionUri);

                    boxes.add(new LinksBox(boxes.newChildId(), 0, status, entity));

                    for (FigureConfig figure : type.getFigures()) {
                        boxes.add(new FigureBox(boxes.newChildId(), figure,  baseUri, entity));
                    }

                    if (table.next()) {
                        StringBuilder disambiguation = new StringBuilder();
                        disambiguation.append("<strong>Did you mean...</strong>&nbsp;");

                        disambiguation.append("<a href=''>");
                        disambiguation.append(getEntityLabel(table.getEntity(collectionUri)));
                        disambiguation.append("</a>");
                        while (table.next()) {
                            disambiguation.append(", <a href=''>");
                            disambiguation.append(getEntityLabel(table.getEntity(collectionUri)));
                            disambiguation.append("</a>");
                        }

                        add(new Label("disambiguation", disambiguation.toString()).setEscapeModelStrings(false));
                    } else {
                        add(new EmptyPanel("disambiguation").setVisible(false));
                    }

                } else {
                    add(new EmptyPanel("disambiguation"));
                    boxes.add(new Label(boxes.newChildId(), "No results found").add(new AttributeModifier("class", "alert")));
                }
                table.close();

            } else {

                // Multiple entities selection

                add(new EmptyPanel("disambiguation").setVisible(false));

                if (filterConfig == null) {
                    filterConfig = new FilterConfig(status.getSearch());

                    filterConfig.setCollection(collectionUri);
                    filterConfig.setDefine("fc='" + collectionUri + "'");
                    String mainKey = type.getKeysList().get(0);
                    In where = new In("fc", mainKey);
                    String[] values = status.getSearch().split(",");
                    for (String value : values) {
                        where.addValue(value.trim());
                    }
                    filterConfig.setWhere(where.toString());

                }

                boxes.add(new LinksBox(boxes.newChildId(), 0, status, collectionUri, filterConfig));
            }
        }


        add(boxes);

    }

    private String getEntityLabel(IEntity entity) {
        String labelField = entity.getCollection().getProperty("FIXED_ENTITY_FIELD");
        String label = (labelField == null ? StringUtils.replace(entity.getId(), "\t", "-") : String.valueOf(entity.get(labelField)));
        return label;
    }

    private IEntityTable getEntityTable(SearchType type, ORI collectionUri, String search) {

        Query query = new Query();

        String collectionAlias = QueryUtils.newCollectionAlias(query, collectionUri);
        query.setFrom(collectionAlias);

        List<String> fieldList = type.getFieldsList();
        query.addSelect(collectionAlias, null);

        for (String field : fieldList) {
            QueryUtils.or(query, new Contains(collectionAlias, field, search));
        }

        query.addOrderBy(new OrderBy(collectionAlias, fieldList.get(0)));
        query.setCount(20);

        return collectionManager.load(query);
    }


}
