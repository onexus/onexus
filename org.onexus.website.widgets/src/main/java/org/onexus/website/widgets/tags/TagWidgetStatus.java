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
package org.onexus.website.widgets.tags;

import org.onexus.collection.api.query.EqualId;
import org.onexus.collection.api.query.Filter;
import org.onexus.collection.api.query.Query;
import org.onexus.collection.api.utils.QueryUtils;
import org.onexus.website.api.widgets.WidgetStatus;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TagWidgetStatus extends WidgetStatus<TagWidgetConfig> {

    private String selection;

    private List<String> selectedTags;

    private boolean filter = false;

    public TagWidgetStatus() {
        super();
    }

    public TagWidgetStatus(String widgetId) {
        super(widgetId);
    }

    public String getSelection() {
        return selection;
    }

    public void setSelection(String selection) {
        this.selection = selection;
    }

    public List<String> getSelectedTags() {
        return selectedTags;
    }

    public void setSelectedTags(List<String> selectedTags) {
        this.selectedTags = selectedTags;
    }

    public boolean isFilter() {
        return filter;
    }

    public void setFilter(boolean filter) {
        this.filter = filter;
    }

    @Override
    public void onQueryBuild(Query query) {

        if (filter) {
            Set<String> selectedValues = new HashSet<String>();

            //TODO TagStore tagStore = getTagStore();
            //for (String tagKey : getSelectedTags()) {
            //    selectedValues.addAll(tagStore.getTagValues(tagKey));
            //}

            if (!selectedValues.isEmpty()) {

                List<Filter> rules = new ArrayList<Filter>(selectedValues.size());
                for (String value : selectedValues) {
                    rules.add(new EqualId(query.getFrom(), value));
                }

                QueryUtils.and(query, QueryUtils.joinOr(rules));
            }

        }

    }

}
