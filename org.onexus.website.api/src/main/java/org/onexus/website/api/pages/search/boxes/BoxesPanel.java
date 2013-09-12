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

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.util.string.Strings;
import org.onexus.collection.api.ICollectionManager;
import org.onexus.collection.api.IEntity;
import org.onexus.collection.api.IEntityTable;
import org.onexus.collection.api.query.Contains;
import org.onexus.collection.api.query.Equal;
import org.onexus.collection.api.query.Filter;
import org.onexus.collection.api.query.In;
import org.onexus.collection.api.query.OrderBy;
import org.onexus.collection.api.query.Query;
import org.onexus.collection.api.utils.EntityIterator;
import org.onexus.collection.api.utils.QueryUtils;
import org.onexus.resource.api.ORI;
import org.onexus.website.api.pages.browser.IEntitySelection;
import org.onexus.website.api.pages.browser.SingleEntitySelection;
import org.onexus.website.api.pages.search.FigureConfig;
import org.onexus.website.api.pages.search.SearchLink;
import org.onexus.website.api.pages.search.SearchPageStatus;
import org.onexus.website.api.pages.search.SearchType;
import org.onexus.website.api.pages.search.figures.FigureBox;
import org.onexus.website.api.pages.search.figures.LinksBox;
import org.onexus.website.api.widgets.selection.MultipleEntitySelection;
import org.onexus.website.api.widgets.selection.FilterConfig;
import org.ops4j.pax.wicket.api.PaxWicketBean;

import java.util.List;
import java.util.Set;

public class BoxesPanel extends Panel {

    @PaxWicketBean(name = "collectionManager")
    private ICollectionManager collectionManager;

    private boolean quickList;

    public BoxesPanel(String id, SearchPageStatus status, ORI baseUri, FilterConfig filterConfig) {
        super(id);
        setMarkupId("boxes");
        setOutputMarkupId(true);

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

            for (FigureConfig figure : type.getFigures()) {
                if (!Strings.isEmpty(figure.getVisible()) && "NONE".equalsIgnoreCase(figure.getVisible())) {
                    boxes.add(new FigureBox(boxes.newChildId(), figure,  baseUri, null));
                }
            }

        } else {
            ORI collectionUri = type.getCollection().toAbsolute(baseUri);
            if (filterConfig == null && status.getSearch().indexOf(',') == -1) {

                // Single entity selection
                IEntityTable table = getSingleEntityTable(collectionManager, type, collectionUri, status.getSearch(), true);

                boolean found;
                if (table.next()) {
                    found = true;
                } else {

                    // If we don't found an exact match, look for a similar one
                    table.close();
                    table = getSingleEntityTable(collectionManager, type, collectionUri, status.getSearch(), false);
                    found = table.next();
                }

                if (found) {

                    IEntity entity = table.getEntity(collectionUri);

                    boxes.add(new LinksBox(boxes.newChildId(), status, entity));

                    for (FigureConfig figure : type.getFigures()) {
                        if (Strings.isEmpty(figure.getVisible()) || "SINGLE".equalsIgnoreCase(figure.getVisible())) {
                            boxes.add(new FigureBox(boxes.newChildId(), figure,  baseUri, new SingleEntitySelection(entity)));
                        }
                    }

                    if (table.next()) {
                        add(new DisambiguationPanel("disambiguation", table, collectionUri) {

                            @Override
                            protected void onSelection(AjaxRequestTarget target, String newSearch) {
                                 onDisambiguation(target, newSearch);
                            }
                        });
                    } else {
                        add(new EmptyPanel("disambiguation").setVisible(false));
                    }

                } else {
                    add(new EmptyPanel("disambiguation").setVisible(false));
                    boxes.add(new Label(boxes.newChildId(), "No results found").add(new AttributeModifier("class", "alert")));
                }
                table.close();

            } else {

                // Multiple entities selection

                add(new EmptyPanel("disambiguation").setVisible(false));

                quickList = false;
                if (filterConfig == null) {
                    quickList = true;
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

                IEntityTable table = getMultipleEntityTable(collectionManager, type, collectionUri, filterConfig);
                boxes.add(new LinksBox(boxes.newChildId(),status, collectionUri, filterConfig, new EntityIterator(table, collectionUri)) {
                    @Override
                    protected void onNotFound(Set<String> valuesNotFound) {
                        if (valuesNotFound.isEmpty() || !quickList) {
                            BoxesPanel.this.addOrReplace(new EmptyPanel("disambiguation").setVisible(false));
                        } else {
                            BoxesPanel.this.addOrReplace(new DisambiguationPanel("disambiguation", valuesNotFound) {
                                @Override
                                protected void onSelection(AjaxRequestTarget target, String newSearch) {
                                    onDisambiguation(target, newSearch);
                                }
                            });
                        }
                    }
                });

                for (FigureConfig figure : type.getFigures()) {
                    if (Strings.isEmpty(figure.getVisible()) || "LIST".equalsIgnoreCase(figure.getVisible())) {
                        boxes.add(new FigureBox(boxes.newChildId(), figure,  baseUri, new MultipleEntitySelection(filterConfig)));
                    }
                }
            }
        }


        add(boxes);

    }

    protected void onDisambiguation(AjaxRequestTarget target, String query) {
    }

    private static IEntityTable getMultipleEntityTable(ICollectionManager collectionManager, SearchType type, ORI collectionUri, FilterConfig filter) {

        Query query = new Query();

        String collectionAlias = QueryUtils.newCollectionAlias(query, collectionUri);
        query.setFrom(collectionAlias);

        query.addSelect(collectionAlias, null);

        IEntitySelection selection = new MultipleEntitySelection(filter);
        query.setWhere( selection.buildFilter(query) );

        List<String> fieldList = type.getFieldsList();
        query.addOrderBy(new OrderBy(collectionAlias, fieldList.get(0)));

        return collectionManager.load(query);
    }




    public static IEntityTable getSingleEntityTable(ICollectionManager collectionManager, SearchType type, ORI collectionUri, String search, boolean equal) {

        Query query = new Query();

        String collectionAlias = QueryUtils.newCollectionAlias(query, collectionUri);
        query.setFrom(collectionAlias);

        List<String> fieldList = type.getFieldsList();
        query.addSelect(collectionAlias, null);

        for (String field : fieldList) {
            if (equal) {
                QueryUtils.or(query, new Equal(collectionAlias, field, search));
            } else {
                QueryUtils.or(query, new Contains(collectionAlias, field, search));
            }
        }

        query.addOrderBy(new OrderBy(collectionAlias, fieldList.get(0)));
        query.setCount(20);

        return collectionManager.load(query);
    }


}
