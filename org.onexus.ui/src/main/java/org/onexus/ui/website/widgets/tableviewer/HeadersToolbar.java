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
package org.onexus.ui.website.widgets.tableviewer;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.IAjaxCallDecorator;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.sort.AjaxFallbackOrderByBorder;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortStateLocator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.Model;
import org.onexus.ui.website.utils.panels.HelpMark;
import org.onexus.ui.website.widgets.tableviewer.columns.MatrixTrack;
import org.onexus.ui.website.widgets.tableviewer.columns.Track;
import org.onexus.ui.website.widgets.tableviewer.headers.FieldHeader;
import org.onexus.ui.website.widgets.tableviewer.headers.IHeader;

import java.util.List;

public class HeadersToolbar extends AbstractToolbar {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor
     *
     * @param table        data table this toolbar will be attached to
     * @param stateLocator locator for the ISortState implementation used by sortable
     *                     headers
     */
    public <T> HeadersToolbar(final DataTable<T> table,
                              final ISortStateLocator stateLocator) {
        super(table);

        table.setOutputMarkupId(true);

        RepeatingView headersGrandParents = new RepeatingView(
                "headersGrandParents");
        RepeatingView headersParents = new RepeatingView("headersParents");
        RepeatingView headers = new RepeatingView("headers");
        add(headersGrandParents);
        add(headersParents);
        add(headers);

        final List<IColumn<T>> columns = table.getColumns();

        WebMarkupContainer secondHeaderContainer = null;
        String lastSecondHeaderTitle = null;
        int lastSecondHeaderColspan = 0;

        WebMarkupContainer thirdHeaderContainer = null;
        String lastThirdHeaderTitle = null;
        int lastThirdHeaderColspan = 0;

        for (final IColumn<T> c : columns) {

            // Process only Track columns
            MatrixTrack column = null;
            if (c instanceof MatrixTrack) {
                column = (MatrixTrack) c;
            } else {

                if (c instanceof Track) {
                    WebMarkupContainer item = new WebMarkupContainer(
                            headers.newChildId());
                    item.setRenderBodyOnly(true);

                    WebMarkupContainer parentItem = new WebMarkupContainer(
                            headersParents.newChildId());
                    parentItem.setRenderBodyOnly(true);

                    WebMarkupContainer grandParentItem = new WebMarkupContainer(
                            headersGrandParents.newChildId());
                    grandParentItem.setRenderBodyOnly(true);

                    headers.add(item);
                    headersParents.add(parentItem);
                    headersGrandParents.add(grandParentItem);

                    WebMarkupContainer firstHeaderContainer = new WebMarkupContainer(
                            "header");

                    firstHeaderContainer.add(c.getHeader("label"));
                    firstHeaderContainer
                            .add(new AttributeModifier(
                                    "style",
                                    new Model<String>(
                                            "font-size:12px; font-family:sans-serif;")));

                    item.add(firstHeaderContainer);

                    secondHeaderContainer = new WebMarkupContainer("header");
                    // secondHeaderContainer.add( new EmptyPanel("label"));
                    // secondHeaderContainer.add(new EmptyPanel("help"));
                    decorateSecondParentHeader(secondHeaderContainer, null,
                            column);

                    secondHeaderContainer.add(new AttributeModifier("class",
                            new Model<String>("empty")));
                    parentItem.add(secondHeaderContainer);
                    secondHeaderContainer = null;

                    thirdHeaderContainer = new WebMarkupContainer("header");
                    thirdHeaderContainer.add(new EmptyPanel("label"));
                    thirdHeaderContainer.add(new AttributeModifier("class",
                            new Model<String>("empty")));
                    grandParentItem.add(thirdHeaderContainer);
                    thirdHeaderContainer = null;
                }

                continue;
            }

            IHeader firstHeader = column.getHeaderDecorator();
            IHeader secondHeader = (firstHeader == null ? null : firstHeader
                    .getParentHeader());
            IHeader thirdHeader = (secondHeader == null ? null : secondHeader
                    .getParentHeader());

            WebMarkupContainer item = new WebMarkupContainer(
                    headers.newChildId());
            headers.add(item);

            WebMarkupContainer firstHeaderContainer = null;
            if (column.isSortable()) {
                firstHeaderContainer = newSortableHeader("header",
                        column.getSortProperty(), stateLocator);
            } else {
                firstHeaderContainer = new WebMarkupContainer("header");
            }

            item.add(firstHeaderContainer);
            item.setRenderBodyOnly(true);
            firstHeaderContainer.add(firstHeader.getHeader("label"));
            if (firstHeader.getLabel() == null) {
                firstHeaderContainer.add(new AttributeModifier("class",
                        new Model<String>("empty")));
            }

            // Add parent and grand parent headers
            String secondTitle = null;
            String thirdTitle = null;

            firstHeaderContainer.add(new AttributeModifier("title",
                    new Model<String>(firstHeader.getTitle())));
            firstHeaderContainer
                    .add(new AttributeModifier("style", new Model<String>(
                            "font-size:12px; font-family:sans-serif;")));

            if (secondHeader != null) {
                secondTitle = secondHeader.getLabel();
            }
            if (thirdHeader != null) {
                thirdTitle = thirdHeader.getLabel();
            }

            if ((secondHeaderContainer != null)
                    && ((lastSecondHeaderTitle == null && secondTitle == null) || (lastSecondHeaderTitle != null
                    && secondTitle != null && lastSecondHeaderTitle
                    .equals(secondTitle)))) {
                lastSecondHeaderColspan += 1;
                secondHeaderContainer.add(new AttributeModifier("colspan",
                        new Model<String>(Integer
                                .toString(lastSecondHeaderColspan))));

            } else {
                // Add parentItem
                WebMarkupContainer parentItem = new WebMarkupContainer(
                        headersParents.newChildId());
                headersParents.add(parentItem);
                secondHeaderContainer = new WebMarkupContainer("header");
                // secondHeaderContainer.add(secondHeader.getHeader("label"));
                // secondHeaderContainer.add(secondHeader.getHelp("help"));
                // //secondHeader.getHeader("label").getDefaultModelObjectAsString()
                decorateSecondParentHeader(secondHeaderContainer, secondHeader,
                        column);

                if (secondTitle == null) {
                    secondHeaderContainer.add(new AttributeModifier("class",
                            new Model<String>("empty")));
                }

                if (secondHeader.getLabel() != null) {
                    secondHeaderContainer.add(new AttributeModifier("title",
                            new Model<String>(secondHeader.getTitle())));
                }

                secondHeaderContainer.add(new AttributeModifier("style",
                        new Model<String>(
                                "font-size:12px; font-family:sans-serif;")));
                parentItem.add(secondHeaderContainer);
                parentItem.setRenderBodyOnly(true);
                lastSecondHeaderTitle = secondTitle;
                lastSecondHeaderColspan = 1;
            }

            // GrandParents
            if ((thirdHeaderContainer != null)
                    && ((lastThirdHeaderTitle == null && thirdTitle == null) || (lastThirdHeaderTitle != null
                    && thirdTitle != null && lastThirdHeaderTitle
                    .equals(thirdTitle)))) {
                lastThirdHeaderColspan += 1;
                thirdHeaderContainer.add(new AttributeModifier("colspan",
                        new Model<String>(Integer
                                .toString(lastThirdHeaderColspan))));
            } else {
                // Add grandParentItem
                WebMarkupContainer grandParentItem = new WebMarkupContainer(
                        headersGrandParents.newChildId());
                headersGrandParents.add(grandParentItem);
                thirdHeaderContainer = new WebMarkupContainer("header");
                thirdHeaderContainer.add(thirdHeader.getHeader("label"));
                if (thirdTitle == null) {
                    thirdHeaderContainer.add(new AttributeModifier("class",
                            new Model<String>("empty")));
                }

                if (thirdHeader.getLabel() != null) {
                    thirdHeaderContainer.add(new AttributeModifier("title",
                            new Model<String>(thirdHeader.getTitle())));
                }

                thirdHeaderContainer.add(new AttributeModifier("style",
                        new Model<String>(
                                "font-size:12px; font-family:sans-serif;")));
                grandParentItem.add(thirdHeaderContainer);
                grandParentItem.setRenderBodyOnly(true);
                lastThirdHeaderTitle = thirdTitle;
                lastThirdHeaderColspan = 1;

            }

        }
    }

    /**
     * @see org.apache.wicket.extensions.markup.html.repeater.data.table.HeadersToolbar#newSortableHeader(java.lang.String,
     *      java.lang.String,
     *      org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortStateLocator)
     */
    protected WebMarkupContainer newSortableHeader(String borderId,
                                                   String property, ISortStateLocator locator) {
        return new AjaxFallbackOrderByBorder(borderId, property, locator,
                getAjaxCallDecorator()) {

            private static final long serialVersionUID = 1L;

            @Override
            protected void onAjaxClick(AjaxRequestTarget target) {
                target.add(getTable());

                //send(getPage(), Broadcast.BREADTH, EventSortUpdate.EVENT);

            }

            @Override
            protected void onSortChanged() {
                super.onSortChanged();
                getTable().setCurrentPage(0);
            }
        };
    }

    /**
     * Returns a decorator that will be used to decorate ajax links used in
     * sortable headers
     *
     * @return decorator or null for none
     */
    protected IAjaxCallDecorator getAjaxCallDecorator() {
        return null;
    }

    /**
     * Add Help if it is necessary
     *
     * @param grandParentItem
     */
    private void decorateSecondParentHeader(
            final WebMarkupContainer headerComponent, IHeader secondHeader,
            MatrixTrack entityTrack) {
        // Ini Label
        if (secondHeader != null) {
            headerComponent.add(secondHeader.getHeader("label"));
        } else {
            headerComponent.add(new EmptyPanel("label"));
        }

        if (entityTrack != null && entityTrack.getHeaderDecorator() != null
                && entityTrack.getHeaderDecorator() instanceof FieldHeader) {

            FieldHeader fieldHeader = (FieldHeader) entityTrack
                    .getHeaderDecorator();
            String helpText = fieldHeader.getDataType().getProperty(
                    "HELP_DESCRIPTION");
            headerComponent.addOrReplace(new HelpMark("help", secondHeader
                    .getTitle(), helpText));

        } else {
            headerComponent.addOrReplace(new EmptyPanel("help"));
        }
    }
}
