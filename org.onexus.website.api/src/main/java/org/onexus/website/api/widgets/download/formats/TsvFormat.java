/**
 *  Copyright 2012 Universitat Pompeu Fabra.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */
package org.onexus.website.api.widgets.download.formats;

import org.onexus.collection.api.Collection;
import org.onexus.collection.api.Field;
import org.onexus.collection.api.IEntity;
import org.onexus.collection.api.IEntityTable;
import org.onexus.collection.api.utils.QueryUtils;
import org.onexus.resource.api.IResourceManager;
import org.onexus.resource.api.ORI;
import org.onexus.website.api.WebsiteApplication;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TsvFormat extends AbstractFormat {

    @Inject
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
