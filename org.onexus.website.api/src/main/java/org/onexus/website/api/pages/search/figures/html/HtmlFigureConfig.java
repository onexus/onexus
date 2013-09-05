package org.onexus.website.api.pages.search.figures.html;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.onexus.website.api.pages.search.FigureConfig;

@XStreamAlias("figure-html")
public class HtmlFigureConfig extends FigureConfig {

    private String text;

    public HtmlFigureConfig() {
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
