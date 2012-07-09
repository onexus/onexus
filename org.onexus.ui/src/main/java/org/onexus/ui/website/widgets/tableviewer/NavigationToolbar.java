package org.onexus.ui.website.widgets.tableviewer;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.PropertyModel;
import org.onexus.core.IEntityTable;
import org.onexus.core.TaskStatus;
import org.onexus.core.query.Query;

public class NavigationToolbar extends AbstractToolbar {

    public NavigationToolbar(final DataTable<?, ?> table) {
        super(table);




    }

    @Override
    protected void onBeforeRender() {

        WebMarkupContainer span = new WebMarkupContainer("span");
        addOrReplace(span);
        span.add(AttributeModifier.replace("colspan", String.valueOf(getTable().getColumns().size())));

        span.add(new PrevLink("prev"));

        span.add(new NextLink("next"));

        AjaxLink<String> countLink = new IndicatingAjaxLink<String>("count") {
            @Override
            public void onClick(AjaxRequestTarget target) {
              getDataProvider().forceCount();
              target.add(getTable());
            }
        };
        span.add(countLink);
        countLink.add(new Label("from", new PropertyModel<Long>(this, "from")));
        countLink.add(new Label("to", new PropertyModel<Long>(this, "to")));
        countLink.add(new Label("of", new PropertyModel<String>(this, "of")));

        super.onBeforeRender();
    }

    public long getFrom() {
        long itemsPerPage = getTable().getItemsPerPage();
        long currentPage = getTable().getCurrentPage();
        return (currentPage * itemsPerPage) + 1;
    }

    public long getTo() {
        long itemsPerPage = getTable().getItemsPerPage();
        long from = getFrom();
        return from + Math.min(itemsPerPage, getDataProvider().size() - from + 1) - 1;
    }

    public String getOf() {

        long realSize = getDataProvider().getRealSize();

        if (realSize == -1) {
            return "?";
        }

        return Long.toString(realSize);
    }

    private EntitiesRowProvider getDataProvider() {
        return (EntitiesRowProvider) getTable().getDataProvider();
    }

    private class PrevLink extends AjaxLink<String> {

        public PrevLink(String id) {
            super(id);
        }

        @Override
        public void onClick(AjaxRequestTarget target) {
            getTable().setCurrentPage( getTable().getCurrentPage() - 1);
            target.add(getTable());
        }

        @Override
        protected void onBeforeRender() {
            setVisible(getFrom() > 1);
            super.onBeforeRender();
        }

    }

    private class NextLink extends AjaxLink<String> {

        public NextLink(String id) {
            super(id);
        }

        @Override
        public void onClick(AjaxRequestTarget target) {

            long page = getTable().getCurrentPage() + 1;
            getTable().setCurrentPage( page );

            getDataProvider().setKnownSize(((page+1)*getTable().getItemsPerPage()) + 2);

            target.add(getTable());

        }

        @Override
        protected void onBeforeRender() {
            long to = getTo();
            long size = getDataProvider().size();

            setVisible(size > to);
            super.onBeforeRender();
        }
    }

}