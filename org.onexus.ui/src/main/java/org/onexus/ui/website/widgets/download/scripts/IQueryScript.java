package org.onexus.ui.website.widgets.download.scripts;

import java.io.Serializable;

public interface IQueryScript extends Serializable {

    public String toString();

    String getContent(String query, CharSequence url);

    String getLabel();

    String getPlugin();
}
