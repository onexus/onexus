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
package org.onexus.website.widgets.download.formats;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.onexus.collection.api.Collection;
import org.onexus.collection.api.Field;
import org.onexus.collection.api.IEntity;
import org.onexus.collection.api.IEntityTable;
import org.onexus.collection.api.utils.QueryUtils;
import org.onexus.resource.api.IResourceManager;
import org.onexus.resource.api.ORI;
import org.onexus.website.api.WebsiteApplication;

import javax.inject.Inject;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ExcelFormat extends AbstractFormat {

    @Inject
    private IResourceManager resourceManager;

    public ExcelFormat() {
        super("xls", "application/vnd.ms-excel", "Microsoft Excel file (max. 50000 rows)");
    }

    @Override
    public Long getMaxRowsLimit() {
        return Long.valueOf(50000);
    }

    @Override
    public void write(IEntityTable result, OutputStream out) throws IOException {

        Workbook wb = new HSSFWorkbook();
        CreationHelper createHelper = wb.getCreationHelper();
        Sheet sheet = wb.createSheet("new sheet");

        // Header row
        int rowIndex = 0;
        Row row = sheet.createRow(rowIndex);
        writeHeader(row, createHelper, result);

        while (result.next()) {
            rowIndex++;
            row = sheet.createRow(rowIndex);
            writeRow(row, result);
        }

        // Write the output to a file
        wb.write(out);

    }

    private void writeHeader(Row header, CreationHelper helper, IEntityTable table) {

        Iterator<Map.Entry<String, List<String>>> selectIt = table.getQuery().getSelect().entrySet().iterator();
        int cell = 0;
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
                header.createCell(cell).setCellValue(helper.createRichTextString(label));
                cell++;
            }

        }
    }

    private void writeRow(Row row, IEntityTable table) {

        Iterator<Map.Entry<String, List<String>>> selectIt = table.getQuery().getSelect().entrySet().iterator();

        int cell = 0;
        while (selectIt.hasNext()) {
            Map.Entry<String, List<String>> select = selectIt.next();

            ORI collectionUri = QueryUtils.getCollectionOri(table.getQuery(), select.getKey());
            IEntity entity = table.getEntity(collectionUri);

            Iterator<String> fieldId = select.getValue().iterator();
            while (fieldId.hasNext()) {

                String field = fieldId.next();
                Object value = entity.get(field);

                if (value != null) {
                    if (value instanceof Number) {
                        Number number = (Number) value;
                        row.createCell(cell).setCellValue(number.doubleValue());
                    } else {
                        row.createCell(cell).setCellValue(String.valueOf(value));
                    }
                }

                cell++;
            }
        }

    }

    private IResourceManager getResourceManager() {
        if (resourceManager == null) {
            WebsiteApplication.inject(this);
        }

        return resourceManager;
    }
}
