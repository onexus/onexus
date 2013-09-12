package org.onexus.website.api.pages.search.figures.table;

import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.NoRecordsToolbar;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.string.Strings;
import org.onexus.collection.api.IEntityTable;
import org.onexus.collection.api.query.Filter;
import org.onexus.collection.api.query.IQueryParser;
import org.onexus.collection.api.query.OrderBy;
import org.onexus.collection.api.query.Query;
import org.onexus.collection.api.utils.QueryUtils;
import org.onexus.resource.api.ORI;
import org.onexus.website.api.pages.browser.IEntitySelection;
import org.onexus.website.api.widgets.tableviewer.EntitiesRowProvider;
import org.onexus.website.api.widgets.tableviewer.HeadersToolbar;
import org.onexus.website.api.widgets.tableviewer.TableViewerStatus;
import org.onexus.website.api.widgets.tableviewer.columns.IColumnConfig;
import org.ops4j.pax.wicket.api.PaxWicketBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TableFigurePanel extends Panel {

    private static final Logger log = LoggerFactory.getLogger(TableFigurePanel.class);

    private transient EntitiesRowProvider dataProvider;

    private DataTable<IEntityTable, String> resultTable;

    private TableFigureConfig config;

    private final static IModel<TableViewerStatus> STATUS = new Model<TableViewerStatus>(new TableViewerStatus());

    @PaxWicketBean(name = "queryParser")
    private IQueryParser queryParser;

    public TableFigurePanel(String id, ORI parentOri, IEntitySelection selection, TableFigureConfig config) {
        super(id);

        this.config = config;

        int limit = (config.getLimit() == null ? Integer.MAX_VALUE : config.getLimit());

        // Create data provider
        dataProvider = new EntitiesRowProvider(
                createQuery(parentOri, config, selection),
                STATUS,
                limit
        );

        // Create columns
        List<IColumn<IEntityTable, String>> columns = new ArrayList<IColumn<IEntityTable, String>>();
        for (IColumnConfig columnConfig : config.getColumns()) {
            if (!Strings.isEqual("false", columnConfig.getVisible())) {
                columnConfig.addColumns(columns, parentOri, false);
            }
        }

        // Create table
        resultTable = new DataTable<IEntityTable, String>("datatable", columns, dataProvider, limit) {
            @Override
            public boolean isVisible() {
                //return (dataProvider == null ? false : dataProvider.getKnownSize() != 0);
                return getItemCount() != 0;
            }
        };
        resultTable.setOutputMarkupId(true);
        resultTable.setVersioned(false);
        resultTable.addTopToolbar(new HeadersToolbar(resultTable, dataProvider));
        resultTable.addBottomToolbar(new NoRecordsToolbar(resultTable));
        add(resultTable);

        add(new Label("empty", config.getEmpty()) {
            @Override
            public boolean isVisible() {
                //return (dataProvider == null ? false : dataProvider.getKnownSize() == 0);
                return resultTable.getItemCount() == 0;
            }
        });

    }

    private Query createQuery(ORI parentOri, TableFigureConfig config, IEntitySelection selection) {
        Query query = new Query();
        query.setOn(parentOri);

        if (config.getLimit() != null) {
            query.setCount(config.getLimit());
        }

        String collectionAlias = QueryUtils.newCollectionAlias(query, config.getCollection());
        query.setFrom(collectionAlias);

        for (IColumnConfig column : config.getColumns()) {
            column.buildQuery(query);
        }

        String oqlDefine = config.getDefine();
        String oqlWhere = config.getWhere();

        if (oqlDefine != null && oqlWhere != null) {
            Map<String, ORI> define = queryParser.parseDefine(oqlDefine);

            if (define == null) {
                log.error("Malformed filter definition\n DEFINE: " + config.getDefine() + "\n");
            } else {

                for (Map.Entry<String, ORI> entry : define.entrySet()) {
                    String whereAlias = QueryUtils.newCollectionAlias(query, entry.getValue());
                    oqlWhere = oqlWhere.replaceAll(entry.getKey() + ".", whereAlias + ".");
                }

                Filter where = queryParser.parseWhere(oqlWhere);
                QueryUtils.and(query, where);

                if (where == null) {
                    log.error("Malformed figure-table WHERE: " + oqlWhere + "\n");
                }
            }
        }

        if (selection!=null) {
            Filter filter = selection.buildFilter(query);
            QueryUtils.and(query, filter);
        }

        OrderBy orderWithCollection = config.getOrder();

        if (orderWithCollection != null) {
            ORI collectionUri = new ORI(orderWithCollection.getCollection()).toAbsolute(query.getOn());
            collectionAlias = QueryUtils.newCollectionAlias(query, collectionUri);
            OrderBy orderWithAlias = new OrderBy(collectionAlias, orderWithCollection.getField(), orderWithCollection.isAscendent());
            query.addOrderBy(orderWithAlias);
        }

        return query;
    }

    @Override
    protected void onAfterRender() {
        super.onAfterRender();
        if (dataProvider != null) {
            dataProvider.close();
        }
    }
}
