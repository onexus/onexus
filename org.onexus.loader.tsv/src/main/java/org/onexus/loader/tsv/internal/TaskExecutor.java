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
package org.onexus.loader.tsv.internal;

import org.onexus.data.api.IDataManager;
import org.onexus.collection.api.ILoader;
import org.onexus.collection.api.ITask;
import org.onexus.collection.api.Collection;
import org.onexus.collection.api.Field;
import org.onexus.collection.api.Loader;
import org.onexus.resource.api.Project;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class TaskExecutor implements ILoader {

    public static final String FIELDS_AUTOADD_DATATYPE = "FIELDS_AUTOADD_DATATYPE";
    public static final String FIELDS_AUTOADD_REGEXP = "FIELDS_AUTOADD_REGEXP";

    private IDataManager dataManager;

    public TaskExecutor() {
        super();
    }

    @Override
    public ITask createCallable(Project project, Collection collection) {
        return new TsvTaskCallable(dataManager, collection);
    }

    @Override
    public boolean preprocessCollection(Project project, Collection collection) {

        Loader task = collection.getLoader();
        String autoupdateRegExp = task.getParameter(FIELDS_AUTOADD_REGEXP);

        if (autoupdateRegExp == null) {
            return false;
        }

        String dataType = task.getParameter(FIELDS_AUTOADD_DATATYPE);

        Class<?> dataTypeClass = String.class;
        if (dataType != null) {
            try {
                dataTypeClass = Class.forName(dataType);
            } catch (ClassNotFoundException e) {
                dataTypeClass = String.class;
            }
        }

        FileEntitySet fileEntitySet = new FileEntitySet(dataManager, collection);

        Pattern regExp = Pattern.compile(autoupdateRegExp);
        boolean addedField = false;

        // Remove fields that match the pattern and there are no columns
        List<Field> removeMe = new ArrayList<Field>();
        for (Field field : collection.getFields()) {
            String fieldName = field.getId();

            if (regExp.matcher(fieldName).matches()) {
                if (!fileEntitySet.getHeaders().containsKey(fieldName)) {
                    removeMe.add(field);
                }
            }
        }
        collection.getFields().removeAll(removeMe);

        for (String columnName : fileEntitySet.getHeaders().keySet()) {
            if (regExp.matcher(columnName).matches()) {

                // Check if its already present
                if (collection.getField(columnName) == null) {
                    Field field = new Field();
                    field.setId(columnName);
                    field.setTitle(columnName);
                    field.setType(dataTypeClass);
                    collection.getFields().add(field);
                    addedField = true;
                }
            }
        }

        return addedField;
    }

    public IDataManager getDataManager() {
        return dataManager;
    }

    public void setDataManager(IDataManager dataManager) {
        this.dataManager = dataManager;
    }

}
