package org.onexus.ui.website.widgets.download.scripts;

public class RScript implements IQueryScript {
    @Override
    public String getContent(String query, CharSequence url) {
        return
                "require(RCurl)\n" +
                "\n" +
                "url <- \""+ url +"\"\n" +
                "oql <- \""+ query + "\" \n" +
                "\n" +
                "dataTsv <- httpPUT(url, oql)\n" +
                "data <- read.csv(textConnection(dataTsv), header = TRUE, sep=\"\\t\")" ;
    }

    @Override
    public String getLabel() {
        return "R";
    }

    @Override
    public String getPlugin() {
        return "r";
    }

}
