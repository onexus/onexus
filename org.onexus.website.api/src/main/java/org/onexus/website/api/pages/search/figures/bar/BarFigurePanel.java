package org.onexus.website.api.pages.search.figures.bar;

import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnLoadHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.resource.JQueryPluginResourceReference;
import org.onexus.collection.api.ICollectionManager;
import org.onexus.collection.api.IEntityTable;
import org.onexus.collection.api.query.Filter;
import org.onexus.collection.api.query.Query;
import org.onexus.collection.api.utils.QueryUtils;
import org.onexus.resource.api.ORI;
import org.onexus.website.api.pages.browser.IEntitySelection;
import org.ops4j.pax.wicket.api.PaxWicketBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class BarFigurePanel extends Panel {

    @PaxWicketBean(name = "collectionManager")
    private ICollectionManager collectionManager;

    private ORI parentOri;
    private IEntitySelection selection;
    private BarFigureConfig config;

    private WebMarkupContainer chart;
    private final static HeaderItem JS_HIGHCHARTS = JavaScriptHeaderItem.forReference(new JQueryPluginResourceReference(BarFigurePanel.class, "highcharts.js"));

    public BarFigurePanel(String id, ORI parentOri, IEntitySelection selection, BarFigureConfig config) {
        super(id);

        this.parentOri = parentOri;
        this.selection = selection;
        this.config = config;

        chart = new WebMarkupContainer("chart");
        chart.setOutputMarkupId(true);

        add(chart);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(JS_HIGHCHARTS);
        response.render(OnLoadHeaderItem.forScript(newJavaScript()));
    }

    private Query createQuery() {

        Query query = new Query();
        query.setOn(parentOri);

        CollectionField value = config.getValue();
        CollectionField xAxis = config.getxAxis();
        CollectionField yAxis = config.getyAxis();

        String valueAlias = QueryUtils.newCollectionAlias(query, value.getCollection());
        String xAxisAlias = QueryUtils.newCollectionAlias(query, xAxis.getCollection());
        String yAxisAlias = QueryUtils.newCollectionAlias(query, yAxis.getCollection());

        query.setFrom(valueAlias);

        query.addSelect(valueAlias, Arrays.asList(value.getField()));
        query.addSelect(xAxisAlias, Arrays.asList(xAxis.getField()));
        query.addSelect(yAxisAlias, Arrays.asList(yAxis.getField()));

        Filter filter = selection.buildFilter(query);
        QueryUtils.and(query, filter);

        return query;
    }

    private String newJavaScript() {
        StringBuilder code = new StringBuilder();

        Query query = createQuery();

        CollectionField value = config.getValue();
        CollectionField xAxis = config.getxAxis();
        CollectionField yAxis = config.getyAxis();
        IEntityTable table = collectionManager.load(query);


        List<String> categories = new ArrayList<String>();
        Map<String, Map<String, String>> values = new HashMap<String, Map<String, String>>();
        while(table.next()) {

            String xAxisData = String.valueOf(table.getEntity(xAxis.getCollection()).get(xAxis.getField()));
            String yAxisData = String.valueOf(table.getEntity(yAxis.getCollection()).get(yAxis.getField()));
            String valueData = String.valueOf(table.getEntity(value.getCollection()).get(value.getField()));

            if (!categories.contains(xAxisData)) {
                categories.add(xAxisData);
            }

            if (!values.containsKey(yAxisData)) {
                values.put(yAxisData, new HashMap<String, String>());
            }

            values.get(yAxisData).put(xAxisData, valueData);
        }
        table.close();

        code.append("$(function () { $('#");

        code.append(chart.getMarkupId());

        code.append("').highcharts({\n" +
                "    chart: {\n" +
                "    type: 'bar'\n" +
                "    },\n" +
                "\n" +
                "    title: {\n" +
                "    text: null\n" +
                "    },\n" +
                "\n" +
                "    xAxis: {\n" +
                "    labels: {\n" +
                "                    enabled: " + (categories.size() > 30 ? "false" : "true") + "\n" +
                "                }," +
                "    categories: [");

        Iterator<String> categoriesIterator = categories.iterator();
        while (categoriesIterator.hasNext()) {
            code.append("'").append(categoriesIterator.next()).append("'");
            if (categoriesIterator.hasNext()) {
                code.append(", ");
            }
        }

        code.append("]\n" +
                "    },\n" +
                "    yAxis: {\n" +
                "    min: 0,\n" +
                "    title: {\n" +
                "    text: 'mutated samples'\n" +
                "    }\n" +
                "    },\n" +
                "    tooltip: {\n" +
                "    valueDecimals: 0,\n" +
                "    valueSuffix: ' samples'\n" +
                "    },\n" +
                "    plotOptions: {\n" +
                "    series: {\n" +
                "    stacking: 'normal'\n" +
                "    }\n" +
                "    },\n" +
                "    legend: {\n" +
                "    layout: 'vertical',\n" +
                "    align: 'right',\n" +
                "    verticalAlign: 'top',\n" +
                "    x: 0,\n" +
                "    y: 30,\n" +
                "    floating: false,\n" +
                "    borderWidth: 1,\n" +
                "    backgroundColor: '#FFFFFF',\n" +
                "    shadow: true,\n" +
                "    labelFormatter: function() {\n" +
                "                return this.name.length>20 ? this.name.substr(0,19)+'...' : this.name;\n" +
                "            }" +
                "    },\n" +
                "    credits: {\n" +
                "    enabled: false\n" +
                "    },\n" +
                "    series: [");

        Iterator<String> seriesIterator = values.keySet().iterator();
        while (seriesIterator.hasNext()) {
            String serieName = seriesIterator.next();
            code.append("{ name: '").append(serieName).append("'\n, data: [");

            categoriesIterator = categories.iterator();
            while (categoriesIterator.hasNext()) {
                code.append(values.get(serieName).get(categoriesIterator.next()));
                if (categoriesIterator.hasNext()) {
                    code.append(", ");
                }
            }

            if (seriesIterator.hasNext()) {
                code.append("] }, \n");
            } else {
                code.append("] }]\n");
            }

        }

        code.append("});});");

        return code.toString();
    }

}
