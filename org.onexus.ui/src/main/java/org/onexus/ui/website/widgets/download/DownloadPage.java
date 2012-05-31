package org.onexus.ui.website.widgets.download;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.link.ResourceLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.protocol.http.RequestUtils;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.onexus.ui.OnexusWebApplication;
import org.onexus.ui.website.widgets.download.scripts.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

public class DownloadPage extends WebPage {

    public final static List<IQueryScript> scripts = Arrays.asList(new IQueryScript[]{
            new BashScript(),
            new PythonScript(),
            new PerlScript(),
            new RScript()
    });

    public final static ResourceReference CSS = new CssResourceReference(DownloadPage.class, "codemirror.css");
    public final static ResourceReference JS = new JavaScriptResourceReference(DownloadPage.class, "codemirror.js");
    public final static ResourceReference JS_SHELL = new JavaScriptResourceReference(DownloadPage.class, "scripts/shell.js");
    public final static ResourceReference JS_PYTHON = new JavaScriptResourceReference(DownloadPage.class, "scripts/python.js");
    public final static ResourceReference JS_PERL = new JavaScriptResourceReference(DownloadPage.class, "scripts/perl.js");
    public final static ResourceReference JS_R = new JavaScriptResourceReference(DownloadPage.class, "scripts/r.js");

    private transient IQueryScript selectedScript;



    public DownloadPage(PageParameters parameters) {
        super(parameters);

        ResourceReference webservice = OnexusWebApplication.get().getWebService();

        // Update content textarea
        CharSequence wsPath = urlFor(webservice, null);

        // Get server url
        HttpServletRequest request = (HttpServletRequest) RequestCycle.get().getRequest().getContainerRequest();
        String rootUrl = request.getRequestURL().toString();


        final String query = parameters.get("query").toString("");
        String fileName = "file-" + Integer.toHexString(query.hashCode()) + ".tsv";
        final String url = RequestUtils.toAbsolutePath( rootUrl, wsPath.toString() );

        // Add download file link
        PageParameters params = new PageParameters();
        params.add("query", query);
        params.add("filename", fileName);
        Link<String> link = new ResourceLink<String>("tsvLink", webservice, params);
        link.add(new Label("filename", fileName));
        add(link);

        // Add Scripts tabs
        add(new ListView<IQueryScript>("scripts", scripts) {
            @Override
            protected void populateItem(ListItem<IQueryScript> item) {

                IQueryScript queryScript = item.getModelObject();
                if (getSelectedScript().equals(queryScript)) {
                    item.add(new AttributeModifier("class", "selected"));
                }

                AjaxLink<IQueryScript> scriptLink = new AjaxLink<IQueryScript>("link", item.getModel()) {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        DownloadPage.this.selectedScript = getModelObject();
                        String scriptContent = getSelectedScript().getContent(query, url);
                        DownloadPage.this.addOrReplace(new Label("query", scriptContent).setEscapeModelStrings(true));
                        target.add(DownloadPage.this);
                    }
                };
                scriptLink.add(new Label("label", queryScript.getLabel()));
                item.add(scriptLink);
            }
        });

        String scriptContent = getSelectedScript().getContent(query, url);
        add(new Label("query", scriptContent).setEscapeModelStrings(true));

    }

    private String getCodeMirror() {
        String plugin = getSelectedScript().getPlugin();
        return          " window.onload = function() {\n" +
                        "              var editor = CodeMirror.fromTextArea(document.getElementById('code'), {\n" +
                        "                mode: '" + plugin + "',\n" +
                        "                lineNumbers: true,\n" +
                        "                readOnly: true,\n" +
                        "                matchBrackets: true\n" +
                        "                });\n" +
                        " };";
    }

    private IQueryScript getSelectedScript() {
        if (selectedScript==null) {
            selectedScript = scripts.get(0);
        }

        return selectedScript;
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(CssHeaderItem.forReference(CSS));
        response.render(JavaScriptHeaderItem.forReference(JS));
        response.render(JavaScriptHeaderItem.forReference(JS_SHELL));
        response.render(JavaScriptHeaderItem.forReference(JS_PYTHON));
        response.render(JavaScriptHeaderItem.forReference(JS_PERL));
        response.render(JavaScriptHeaderItem.forReference(JS_R));
        response.render(JavaScriptHeaderItem.forScript(getCodeMirror(), "codemirror"));
    }
}
