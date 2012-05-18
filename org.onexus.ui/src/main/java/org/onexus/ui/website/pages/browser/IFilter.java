package org.onexus.ui.website.pages.browser;

import org.apache.wicket.markup.html.panel.Panel;
import org.onexus.core.query.Filter;
import org.onexus.core.query.Query;
import org.onexus.ui.website.utils.visible.IVisible;
import org.onexus.ui.website.utils.visible.VisibleRule;

import java.io.Serializable;

public interface IFilter extends Serializable, IVisible {

    String getId();

    String getFilteredCollection();

    boolean isEnable();

    void setEnable(boolean enabled );

    boolean isDeletable();

    void setDeletable(boolean deletable );

    Filter buildFilter(Query query);

    String getLabel(Query query);

    String getTitle(Query query);

    Panel getTooltip(String componentId, Query query);

    boolean isVisible(VisibleRule rule);
}
