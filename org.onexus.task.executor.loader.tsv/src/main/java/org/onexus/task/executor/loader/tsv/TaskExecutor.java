/**
 *  Copyright 2011 Universitat Pompeu Fabra.
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
package org.onexus.task.executor.loader.tsv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.onexus.core.ISourceManager;
import org.onexus.core.ITaskCallable;
import org.onexus.core.ITaskExecutor;
import org.onexus.core.resources.Collection;
import org.onexus.core.resources.Field;
import org.onexus.core.resources.Task;
import org.onexus.core.utils.ResourceTools;

public class TaskExecutor implements ITaskExecutor {

    public static final String FIELDS_AUTOADD_DATATYPE = "FIELDS_AUTOADD_DATATYPE";
    public static final String FIELDS_AUTOADD_REGEXP = "FIELDS_AUTOADD_REGEXP";

    private ISourceManager sourceManager;

    public TaskExecutor() {
        super();
    }

    @Override
    public boolean isCallable(String toolURI) {
        return "http://www.onexus.org/tools/loader-tsv/1.0.0".equals(toolURI);
    }

    @Override
    public ITaskCallable createCallable(Collection collection) {
        return new TsvTaskCallable(sourceManager, getProperties(collection), collection);
    }

    private Map<String, String> getProperties(Collection collection) {

        Map<String, String> properties = new HashMap<String, String>();

        String releaseURI = ResourceTools.getParentURI(collection.getURI());
        String releaseName = ResourceTools.getResourceName(releaseURI);

        String projectURI = ResourceTools.getParentURI(releaseURI);
        String projectName = ResourceTools.getResourceName(projectURI);

        properties.put("release.uri", releaseURI);
        properties.put("release.name", releaseName);
        properties.put("project.uri", projectURI);
        properties.put("project.name", projectName);

        return properties;
    }

    @Override
    public boolean preprocessCollection(Collection collection) {

        Task task = collection.getTask();
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

        FileEntitySet fileEntitySet = new FileEntitySet(sourceManager, getProperties(collection), collection);

        Pattern regExp = Pattern.compile(autoupdateRegExp);
        boolean addedField = false;

        // Remove fields that match the pattern and there are no columns
        List<Field> removeMe = new ArrayList<Field>();
        for (Field field : collection.getFields()) {
            String fieldName = field.getName();

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
                    field.setName(columnName);
                    field.setTitle(columnName);
                    field.setDataType(dataTypeClass);
                    collection.getFields().add(field);
                    addedField = true;
                }
            }
        }

        return addedField;
    }

    public ISourceManager getSourceManager() {
        return sourceManager;
    }

    public void setSourceManager(ISourceManager sourceManager) {
        this.sourceManager = sourceManager;
    }

}
