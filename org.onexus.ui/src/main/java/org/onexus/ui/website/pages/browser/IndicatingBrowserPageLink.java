package org.onexus.ui.website.pages.browser;

import org.apache.wicket.ajax.IAjaxIndicatorAware;
import org.apache.wicket.extensions.ajax.markup.html.AjaxIndicatorAppender;
import org.apache.wicket.model.IModel;

public abstract class IndicatingBrowserPageLink<T> extends BrowserPageLink<T>  implements IAjaxIndicatorAware {

    private final AjaxIndicatorAppender indicatorAppender = new AjaxIndicatorAppender();

    public IndicatingBrowserPageLink(String id) {
        super(id);
        add(indicatorAppender);
    }

    public IndicatingBrowserPageLink(String id, IModel<T> tiModel) {
        super(id, tiModel);
        add(indicatorAppender);
    }

    /**
     * @see org.apache.wicket.ajax.IAjaxIndicatorAware#getAjaxIndicatorMarkupId()
     */
    public String getAjaxIndicatorMarkupId()
    {
        return indicatorAppender.getMarkupId();
    }
}
