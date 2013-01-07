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
package org.onexus.website.api.pages.search;

import com.thoughtworks.xstream.annotations.XStreamImplicit;
import org.apache.commons.lang3.StringUtils;
import org.onexus.resource.api.ORI;
import org.onexus.website.api.widgets.filters.FiltersWidgetConfig;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchType implements Serializable {

    private ORI collection;
    private String fields;
    private String keys;
    private String examples;
    private String template;

    private FiltersWidgetConfig filters;

    @XStreamImplicit(itemFieldName = "link")
    private List<SearchLink> links = new ArrayList<SearchLink>();


    public ORI getCollection() {
        return collection;
    }

    public void setCollection(ORI collection) {
        this.collection = collection;
    }

    public String getFields() {
        return fields;
    }

    public void setFields(String fields) {
        this.fields = fields;
    }

    public String getKeys() {
        return keys;
    }

    public void setKeys(String keys) {
        this.keys = keys;
    }

    public String getExamples() {
        return examples;
    }

    public void setExamples(String examples) {
        this.examples = examples;
    }

    public List<SearchLink> getLinks() {
        return links;
    }

    public void setLinks(List<SearchLink> links) {
        this.links = links;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public FiltersWidgetConfig getFilters() {
        return filters;
    }

    public void setFilters(FiltersWidgetConfig filters) {
        this.filters = filters;
    }

    public List<String> getKeysList() {

        if (this.keys == null) {
            return getFieldsList();
        }

        return stringToList(this.keys);
    }

    public List<String> getFieldsList() {
        return stringToList(this.fields);
    }

    private static List<String> stringToList(String input) {

        if (StringUtils.isEmpty(input)) {
            return Collections.emptyList();
        }

        String[] values = input.split(",");
        List<String> valuesList = new ArrayList<String>(values.length);

        for (String value : values) {
            valuesList.add(value.trim());
        }

        return valuesList;
    }
}
