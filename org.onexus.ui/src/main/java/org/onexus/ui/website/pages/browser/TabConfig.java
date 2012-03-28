package org.onexus.ui.website.pages.browser;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import org.onexus.ui.website.utils.visible.IVisible;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@XStreamAlias("tab")
public class TabConfig implements Serializable, IVisible {

    private String id;
    private String title;
    private String visible;

    @XStreamImplicit(itemFieldName="view")
    private List<ViewConfig> views = new ArrayList<ViewConfig>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVisible() {
        return visible;
    }

    public void setVisible(String visible) {
        this.visible = visible;
    }
    
    public ViewConfig getView(String viewId) {

        if (viewId == null) {
            return null;
        }
        
        for (ViewConfig view : getViews()) {
            if (viewId.equals(view.getTitle())) {
                return  view;
            }
        }

        return null;
    }

    public List<ViewConfig> getViews() {
        return views;
    }

    public void setViews(List<ViewConfig> views) {
        this.views = views;
    }
}
