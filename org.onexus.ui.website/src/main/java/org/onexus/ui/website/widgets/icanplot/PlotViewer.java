package org.onexus.ui.website.widgets.icanplot;

import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.model.IModel;
import org.apache.wicket.protocol.http.RequestUtils;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.resource.JQueryPluginResourceReference;
import org.onexus.ui.website.widgets.Widget;

public class PlotViewer extends Widget<PlotViewerConfig, PlotViewerStatus> {

    public PlotViewer(String componentId, IModel<PlotViewerStatus> statusModel) {
        super(componentId, statusModel);
    }


    private final static HeaderItem JS_ICANPLOT = JavaScriptHeaderItem.forReference(new JQueryPluginResourceReference(PlotViewer.class, "icanplot.js"));
    private final static HeaderItem JS_ICANPLOT_ONEXUS = JavaScriptHeaderItem.forReference(new JQueryPluginResourceReference(PlotViewer.class, "icanplot-onexus.js"));
    private final static HeaderItem JS_ICANPLOT_DATA = JavaScriptHeaderItem.forReference(new JQueryPluginResourceReference(PlotViewer.class, "data.js"));

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(JS_ICANPLOT);
        response.render(JS_ICANPLOT_DATA);
        response.render(JS_ICANPLOT_ONEXUS);
        response.render(OnDomReadyHeaderItem.forScript(newJavaScriptPlot()));
    }

    private final static ResourceReference DATA_FILE = new PackageResourceReference(PlotViewer.class, "data-file.txt");

    private String newJavaScriptPlot() {
        String requestUrl = RequestCycle.get().getRequest().getContextPath();
        String resourceUrl = getPage().urlFor(DATA_FILE, null).toString();
        String url = RequestUtils.toAbsolutePath(requestUrl, resourceUrl);
        return "icanplot_init('" + url + " ', [0, 1, -1, -1]);";
    }

}
