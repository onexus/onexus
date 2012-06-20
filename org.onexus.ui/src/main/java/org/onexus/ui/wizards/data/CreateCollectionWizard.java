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
package org.onexus.ui.wizards.data;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.extensions.wizard.WizardModel;
import org.apache.wicket.extensions.wizard.WizardStep;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.onexus.core.IResourceManager;
import org.onexus.core.IDataManager;
import org.onexus.core.resources.Collection;
import org.onexus.core.resources.*;
import org.onexus.core.types.Text;
import org.onexus.core.utils.ResourceUtils;
import org.onexus.ui.wizards.AbstractWizard;
import org.onexus.ui.workspace.pages.resource.ResourcesPage;

import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

public class CreateCollectionWizard extends AbstractWizard {

    @Inject
    public IDataManager dataManager;

    @Inject
    public IResourceManager resourceManager;

    // Formats
    private static String CSV = "Comma separated values";
    private static String TSV = "Tab separated values";
    private static final List<String> FORMATS = Arrays.asList(new String[]{TSV, CSV});

    // Maximum lines to load to deduce the datatype 
    private final static int MAXIMUM_LINES = 100;

    private String selected = TSV;
    private String sourceURI;

    public CreateCollectionWizard(String id, IModel<? extends Resource> model) {
        super(id);

        sourceURI = model.getObject().getURI();

        WizardModel wizardModel = new WizardModel();
        wizardModel.add(new ChooseFormat());
        init(wizardModel);
    }

    @Override
    public void onFinish() {
        super.onFinish();

        String separator = " ";
        if (selected.equals(CSV)) {
            separator = ",";
        }

        if (selected.equals(TSV)) {
            separator = "\t";
        }

        List<URL> urls = dataManager.retrieve(sourceURI);

        if (urls == null && urls.isEmpty()) {
            return;
        }

        URL url = urls.get(0);

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(urls.get(0).openStream()));

            // Get headers
            String headers[] = in.readLine().split(separator);

            // Build values map
            Map<String, Set<String>> data = new HashMap<String, Set<String>>();
            for (String header : headers) {
                data.put(header, new HashSet<String>());
            }
            String line = in.readLine();

            int nullEmpty = 0;
            int nullDash = 0;
            int nullString = 0;
            for (int i = 0; i < MAXIMUM_LINES && line != null; i++) {
                String values[] = line.split(separator);
                for (int h = 0; h < headers.length && h < values.length; h++) {

                    if (values[h] == null || values[h].isEmpty()) {
                        nullEmpty++;
                    } else if (values[h].equals("-")) {
                        nullDash++;
                    } else if (values[h].equals("NULL")) {
                        nullString++;
                    }

                    data.get(headers[h]).add(values[h]);
                }
                line = in.readLine();
            }
            in.close();

            // Create collection
            Collection collection = newCollection();

            // Collect fields from other collections in the same folder
            Map<String, Field> otherFields = collectFields();

            List<Field> fields = new ArrayList<Field>();
            for (String header : headers) {
                String shortName, title;
                if (otherFields.containsKey(header)) {
                    Field field = otherFields.get(header);
                    shortName = field.getLabel();
                    title = field.getTitle();
                } else {
                    String lower = StringUtils.lowerCase(header);
                    shortName = StringUtils.abbreviate(lower, 10);
                    title = StringUtils.capitalize(lower);
                }

                fields.add(new Field(header, shortName, title, deduceClass(data.get(header))));
            }
            collection.setFields(fields);

            // Deduce links from other collections in the same folder
            Map<String, Link> otherLinks = collectLinks();
            List<Link> links = new ArrayList<Link>();

            List<Collection> allProjectCollections = new ArrayList<Collection>();
            addAllCollections(allProjectCollections, ResourceUtils.getProjectURI(sourceURI) );

            for (String header : headers) {
                if (otherLinks.containsKey(header)) {
                    Link otherLink = otherLinks.get(header);
                    Link link = new Link();
                    link.setCollection(otherLink.getCollection());
                    link.getFields().add(otherLink.getFields().get(0));
                    links.add(link);
                } else {

                    for (Collection col : allProjectCollections) {
                        Field field = col.getField(header);

                        if (field != null && (header.toLowerCase().endsWith("id") || header.toLowerCase().endsWith("key"))) {

                            // Only link to collections without any link
                            if (col.getLinks() == null || col.getLinks().isEmpty()) {
                                Link link = new Link();
                                link.setCollection( Resource.SEPARATOR + ResourceUtils.getResourcePath(col.getURI()));
                                link.getFields().add(header);
                                links.add(link);
                            }
                        }
                    }


                }
            }
            collection.setLinks(links);


            Loader loader = new Loader();
            loader.setPlugin("tsv-loader");
            List<Parameter> parameters = new ArrayList<Parameter>();
            parameters.add(new Parameter("SOURCE_URI", ResourceUtils.getResourceName(sourceURI)));

            if (nullEmpty > nullDash && nullEmpty > nullString) {
                parameters.add(new Parameter("NULL_VALUE", ""));
            }

            if (nullString > nullDash && nullString > nullEmpty) {
                parameters.add(new Parameter("NULL_VALUE", "NULL"));
            }

            loader.setParameters(parameters);
            collection.setLoader(loader);

            resourceManager.save(collection);

            PageParameters params = new PageParameters().add(ResourcesPage.PARAMETER_RESOURCE, collection.getURI());
            setResponsePage(ResourcesPage.class, params);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    private Map<String, Link> collectLinks() {
        Map<String, Link> links = new HashMap<String, Link>();

        List<Collection> collections = new ArrayList<Collection>();
        addAllCollections(collections, ResourceUtils.getProjectURI(sourceURI) );

        for (Collection collection : collections) {
            for (Link link : collection.getLinks()) {

                // Only simple links (not composed)
                if (link.getFields().size() == 1) {
                    String field = Link.getFromFieldName(link.getFields().get(0));
                    links.put(field, link);
                }
            }
        }

        return links;
    }

    private Map<String, Field> collectFields() {
        Map<String, Field> fields = new HashMap<String, Field>();

        List<Collection> collections = new ArrayList<Collection>();
        addAllCollections(collections, ResourceUtils.getProjectURI(sourceURI) );

        for (Collection collection : collections) {
            for (Field field : collection.getFields()) {
                fields.put(field.getId(), field);
            }
        }

        return fields;
    }

    private Collection newCollection() {

        String sourceName = ResourceUtils.getResourceName(sourceURI);
        String collectionName;

        int punt;
        if ((punt = sourceName.lastIndexOf(".")) != -1) {
            collectionName = sourceName.substring(0, punt);
        } else {
            collectionName = sourceName + ".col";
        }

        String collectionURI = ResourceUtils.concatURIs(
                ResourceUtils.getParentURI(sourceURI),
                collectionName
        );

        Collection collection = new Collection();
        collection.setURI(collectionURI);
        collection.setName(collectionName);
        collection.setTitle(collectionName);

        return collection;
    }

    private void addAllCollections(List<Collection> collections, String parentUri) {
        collections.addAll(resourceManager.loadChildren(Collection.class, parentUri));
        List<Folder> folders = resourceManager.loadChildren(Folder.class, parentUri);
        for (Folder folder : folders) {
            addAllCollections(collections, folder.getURI());
        }
    }

    public String getSelected() {
        return selected;
    }

    public void setSelected(String selected) {
        this.selected = selected;
    }

    private static Class<?> deduceClass(Set<String> values) {

        boolean longString = false;
        boolean integerType = true;
        boolean doubleType = false;

        for (String value : values) {

            // Skip null and empty values
            if (value == null || value.isEmpty() || value.equals("-")) {
                continue;
            }

            if (integerType) {
                try {
                    Integer.valueOf(value);
                } catch (NumberFormatException e) {
                    integerType = false;
                    doubleType = true;
                }
            }

            if (doubleType) {
                try {
                    Double.valueOf(value);
                } catch (NumberFormatException e) {
                    doubleType = false;
                }
            }

            if (value.length() > 127) {
                longString = true;
            }
        }

        if (integerType) {
            return Integer.class;
        }

        if (doubleType) {
            return Double.class;
        }

        if (longString) {
            return Text.class;
        } else {
            return String.class;
        }
    }


    private final class ChooseFormat extends WizardStep {

        public ChooseFormat() {
            super("Format", "Choose one file format");

            RadioChoice<String> commandOptions = new RadioChoice<String>("formats", new PropertyModel<String>(CreateCollectionWizard.this, "selected"), FORMATS);
            add(commandOptions);

        }
    }


}
