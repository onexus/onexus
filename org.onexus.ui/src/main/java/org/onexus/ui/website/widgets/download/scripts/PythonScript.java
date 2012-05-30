package org.onexus.ui.website.widgets.download.scripts;

public class PythonScript implements IQueryScript {
    @Override
    public String getContent(String query, CharSequence url) {
        return
                "import urllib2\n" +
                "\n" +
                "query=\"" + query + "\"\n" +
                "\n" +
                "req = urllib2.Request(\"" + url + "\")\n" +
                "res = urllib2.urlopen(req, query)\n" +
                "print res.read()";
    }

    @Override
    public String getLabel() {
        return "python";
    }

    @Override
    public String getPlugin() {
        return "python";
    }

}
