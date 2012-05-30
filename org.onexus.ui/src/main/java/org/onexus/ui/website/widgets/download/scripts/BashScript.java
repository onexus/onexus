package org.onexus.ui.website.widgets.download.scripts;

public class BashScript implements IQueryScript {
    @Override
    public String getContent(String query, CharSequence url) {
        return
                "#!/bin/bash\n" +
                "\n" +
                "query=\"" + query + "\"\n" +
                "\n" +
                "echo $query | curl -X POST -d @- " + url;
    }

    @Override
    public String getLabel() {
        return "bash";
    }

    @Override
    public String getPlugin() {
        return "shell";
    }

}
