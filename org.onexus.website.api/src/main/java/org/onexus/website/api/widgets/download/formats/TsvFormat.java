package org.onexus.website.api.widgets.download.formats;

import org.onexus.collection.api.Collection;
import org.onexus.collection.api.Field;
import org.onexus.collection.api.IEntity;
import org.onexus.collection.api.IEntityTable;
import org.onexus.collection.api.utils.QueryUtils;
import org.onexus.resource.api.IResourceManager;
import org.onexus.resource.api.ORI;
import org.onexus.website.api.WebsiteApplication;
import org.ops4j.pax.wicket.api.PaxWicketBean;

import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TsvFormat extends AbstractFormat {

    @PaxWicketBean(name = "resourceManager")
    private IResourceManager resourceManager;

    public TsvFormat() {
        super("tsv", "text/tab-separated-values", "Tabulated text file");
    }

    @Override
    public void write(IEntityTable result, OutputStream out) throws IOException {
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(out));
        writeHeader(pw, result);
        while (result.next()) {
            writeRow(pw, result);
        }
        pw.close();
    }

    private void writeHeader(PrintWriter response, IEntityTable table) {

        Iterator<Map.Entry<String, List<String>>> selectIt = table.getQuery().getSelect().entrySet().iterator();
        while (selectIt.hasNext()) {
            Map.Entry<String, List<String>> select = selectIt.next();

            ORI collectionUri = QueryUtils.getCollectionOri(table.getQuery(), select.getKey());
            Collection collection = getResourceManager().load(Collection.class, collectionUri);

            Iterator<String> fieldId = select.getValue().iterator();
            while (fieldId.hasNext()) {
                Field field = collection.getField(fieldId.next());
                if (field == null) {
                    continue;
                }
                String label = field.getLabel();
                if (label == null) {
                    label = field.getId();
                }
                response.write(label);

                if (fieldId.hasNext() || selectIt.hasNext()) {
                    response.write("\t");
                }
            }

        }


        response.write("\n");
    }

    private static void writeRow(PrintWriter response, IEntityTable table) {

        Iterator<Map.Entry<String, List<String>>> selectIt = table.getQuery().getSelect().entrySet().iterator();
        while (selectIt.hasNext()) {
            Map.Entry<String, List<String>> select = selectIt.next();

            ORI collection = QueryUtils.getCollectionOri(table.getQuery(), select.getKey());
            IEntity entity = table.getEntity(collection);

            Iterator<String> fieldId = select.getValue().iterator();
            while (fieldId.hasNext()) {
                response.write(String.valueOf(entity.get(fieldId.next())));
                if (fieldId.hasNext() || selectIt.hasNext()) {
                    response.write("\t");
                }
            }
        }

        response.write("\n");
    }

    private IResourceManager getResourceManager() {
        if (resourceManager == null) {
            WebsiteApplication.inject(this);
        }

        return resourceManager;
    }
}
