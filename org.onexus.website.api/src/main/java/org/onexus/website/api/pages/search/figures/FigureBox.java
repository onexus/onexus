package org.onexus.website.api.pages.search.figures;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.util.encoding.UrlEncoder;
import org.apache.wicket.util.string.Strings;
import org.onexus.collection.api.IEntity;
import org.onexus.resource.api.ORI;
import org.onexus.website.api.Website;
import org.onexus.website.api.WebsiteApplication;
import org.onexus.website.api.pages.browser.IEntitySelection;
import org.onexus.website.api.pages.browser.SingleEntitySelection;
import org.onexus.website.api.pages.search.FigureConfig;
import org.onexus.website.api.pages.search.SearchLink;
import org.onexus.website.api.pages.search.figures.bar.BarFigureConfig;
import org.onexus.website.api.pages.search.figures.bar.BarFigurePanel;
import org.onexus.website.api.pages.search.figures.html.HtmlFigureConfig;
import org.onexus.website.api.pages.search.figures.html.HtmlFigurePanel;
import org.onexus.website.api.pages.search.figures.table.TableFigureConfig;
import org.onexus.website.api.pages.search.figures.table.TableFigurePanel;
import org.onexus.website.api.utils.HtmlDataResourceModel;
import org.onexus.website.api.widgets.selection.MultipleEntitySelection;
import org.onexus.website.api.widgets.selection.FilterConfig;

public class FigureBox extends Panel {

    private ORI parentUri;
    private FigureConfig config;
    private IEntitySelection selection;

    public FigureBox(String id, FigureConfig config, ORI parentUri, IEntitySelection selection) {
        super(id);

        this.config = config;
        this.parentUri = parentUri;
        this.selection = selection;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        WebMarkupContainer toggle = new WebMarkupContainer("accordion-toggle");
        WebMarkupContainer body = new WebMarkupContainer("accordion-body");
        String bodyId = getMarkupId() + "-body";
        body.setMarkupId(bodyId);
        toggle.add(new AttributeModifier("href", "#" + bodyId));

        toggle.add(new Label("label", config.getTitle()));

        Label description = new Label("description", config.getDescription());
        description.setEscapeModelStrings(false);
        description.setVisible( !Strings.isEmpty(config.getDescription()) );
        body.add(description);

        if (config instanceof HtmlFigureConfig) {
            body.add(new HtmlFigurePanel("content", parentUri, (HtmlFigureConfig) config));
        } else if (config instanceof TableFigureConfig) {
            body.add(new TableFigurePanel("content", parentUri, selection, (TableFigureConfig) config));
        } else if (config instanceof BarFigureConfig) {
            body.add(new BarFigurePanel("content", parentUri, selection, (BarFigureConfig) config));
        } else {
            body.add(new Label("content", "Unknown figure type"));
        }

        SearchLink searchLink = config.getLink();
        WebMarkupContainer links = new WebMarkupContainer("links");
        if (searchLink != null) {

            // Create link url
            String prefix = (getPage().getPageParameters().get(Website.PARAMETER_CURRENT_PAGE).isEmpty()) ? WebsiteApplication.get().getWebPath() + "/" : "";
            String url = searchLink.getUrl();
            if (selection!=null) {
                String parameter = (selection instanceof SingleEntitySelection ? "pf=" : "pfc=");
                String varFilter = parameter + UrlEncoder.QUERY_INSTANCE.encode(selection.toUrlParameter(false, null), "UTF-8");
                url = url.replace("$filter", varFilter);
            }

            WebMarkupContainer link = new WebMarkupContainer("link");
            link.add(new AttributeModifier("href", prefix + url));
            link.add(new Label("label", searchLink.getTitle()).setEscapeModelStrings(false));
            links.add(link);

        } else {
            links.setVisible(false);
        }
        body.add(links);

        add(toggle);
        add(body);
    }
}
