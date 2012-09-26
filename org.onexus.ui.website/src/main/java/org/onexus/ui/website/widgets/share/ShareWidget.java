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
package org.onexus.ui.website.widgets.share;

import org.apache.wicket.Application;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.protocol.http.RequestUtils;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.onexus.ui.api.OnexusWebApplication;
import org.onexus.ui.website.Website;
import org.onexus.ui.website.WebsiteStatus;
import org.onexus.ui.website.events.EventQueryUpdate;
import org.onexus.ui.website.widgets.Widget;

import javax.servlet.http.HttpServletRequest;

public class ShareWidget extends Widget<ShareWidgetConfig, ShareWidgetStatus> {

    private String width = "890";
    private String height = "790";

    private String linkURL;
    private String embedURL;

    public ShareWidget(String componentId, IModel<ShareWidgetStatus> statusModel) {
        super(componentId, statusModel);

        onEventFireUpdate(EventQueryUpdate.class);

        Form form = new Form<ShareWidget>("form");

        TextField<String> width = new TextField<String>("width", new PropertyModel<String>(this, "width"));
        width.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add( ShareWidget.this.get("embed"));
            }
        });
        form.add(width);

        TextField<String> height = new TextField<String>("height", new PropertyModel<String>(this, "height"));
        height.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add( ShareWidget.this.get("embed") );
            }
        });
        form.add(height);

        add(form);
    }


    @Override
    protected void onBeforeRender() {

        Website website = findParent(Website.class);
        PageParameters params = new PageParameters();

        if (website != null) {
            WebsiteStatus status = website.getStatus();
            status.encodeParameters(params);

            // If the website URI is not defined at Application level then add it as a parameter.
            if (Application.get().getMetaData(Website.WEBSITE_CONFIG) == null) {
                String uri = getPage().getPageParameters().get("uri").toString();
                params.add(Website.PARAMETER_WEBSITE, uri);
            }

        }

        linkURL = toAbsolutePath(urlFor(getPage().getClass(), params));
        WebMarkupContainer link = new WebMarkupContainer("link");
        link.add(new AttributeModifier("value", linkURL));
        addOrReplace(link);

        PageParameters embedParams = new PageParameters(params);
        embedParams.set("embed", true);

        embedURL = toAbsolutePath(urlFor(getPage().getClass(), embedParams));
        WebMarkupContainer embed = new WebMarkupContainer("embed");
        embed.setOutputMarkupId(true);

        IModel<String> embedModel = new PropertyModel<String>(this, "embedCode");
        embed.add(new AttributeModifier("value", embedModel));
        addOrReplace(embed);

        super.onBeforeRender();

    }

    public String getEmbedCode() {
        return getEmbedHTML(embedURL, linkURL, getWidth(), getHeight());
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public final static String toAbsolutePath(final CharSequence relativePagePath) {
        String serverUrl = OnexusWebApplication.get().getRequestUrl();
        return RequestUtils.toAbsolutePath(serverUrl, relativePagePath.toString());
    }

    private final static String getEmbedHTML(String embedURL, String browseURL, String width, String height) {

        StringBuilder code = new StringBuilder();

        code.append("<div style=\"width:").append(width).append("px; height: ").append(height).append("px; border: 1px solid #ccc; margin: 5px auto; padding: 5px;\">");
        code.append("<div style=\"position: absolute; width:").append(width).append("px; height: ").append(height).append("px; background: transparent; z-index: 100;\" onclick=\"document.getElementById('43DFX').click();\"></div>");
        code.append("<div style=\"float: right; width: 65px;\">");
        code.append("<a id=\"43DFX\" target=\"_tab\" style=\"position: absolute;\"");
        code.append("href=\"").append(browseURL).append("\">browse</a>");
        code.append("</div>");
        code.append("<iframe align=\"middle\" style=\"width:").append(width).append("px; border: 0px; height: ").append(height).append("px; overflow: hidden;\" scrolling=\"no\"");
        code.append("src=\"").append(embedURL).append("\"></iframe>");
        code.append("</div>");

        return code.toString();

    }

}
