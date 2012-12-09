package org.onexus.website.api.utils.panels.ondomready;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.CssReferenceHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.PackageResourceReference;

public abstract class OnDomReadyPanel extends Panel {

    private static CssReferenceHeaderItem CSS = CssHeaderItem.forReference(new PackageResourceReference(OnDomReadyPanel.class, "OnDomReadyPanel.css"));

    public OnDomReadyPanel(String id) {
        this(id, null);
    }

    public OnDomReadyPanel(String id, IModel<?> model) {
        super(id, model);
        setOutputMarkupId(true);

        add(new AjaxEventBehavior("domready") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {

                MarkupContainer parent = OnDomReadyPanel.this.getParent();

                Panel newPanel = onDomReadyPanel(OnDomReadyPanel.this.getId());
                newPanel.setOutputMarkupId(true);

                parent.addOrReplace( newPanel );
                target.add( newPanel );
            }
        });

        add(new AttributeAppender("class", " ondomreadypanel"));

    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(CSS);
    }

    protected abstract Panel onDomReadyPanel(String componentId);


}
