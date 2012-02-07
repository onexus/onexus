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
package org.onexus.ui.website.viewers.tableviewer.headers;

import org.apache.wicket.Component;
import org.onexus.core.resources.Collection;
import org.onexus.core.resources.Field;
import org.onexus.ui.website.formaters.StringFormater;
import org.onexus.ui.website.utils.panels.HelpMark;

public class CollectionHeader extends ElementHeader {

    private Collection collection;

    public CollectionHeader(Collection dataType) {
        super((dataType == null ? null : dataType), (dataType == null ? null
                : new StringHeader(null)), new StringFormater(30, false));
        this.collection = dataType;
    }

    @Override
    public Component getHeader(String componentId) {
        if (collection.getProperty("HELP") != null) {
            return new HelpMark(componentId, collection.getName(),
                    getFormatedLabel(), getHelpContent());
        } else {
            return super.getHeader(componentId);
        }

    }

    @Override
    public String getLabel() {
        return (collection == null ? "" : collection.getTitle());
    }

    @Override
    public String getTitle() {
        return collection.getDescription();
    }

    private String getHelpContent() {
        StringBuilder str = new StringBuilder();

        str.append("<p>");
        str.append(collection.getProperty("HELP"));
        str.append("</p>");

        str.append("<ul>");
        for (Field field : collection.getFields()) {
            if (field.getProperty("HELP") != null) {
                str.append("<li>");
                str.append("<strong>").append(field.getShortName())
                        .append(":</strong>&nbsp;");
                str.append(field.getProperty("HELP"));
                if (field.getProperty("HELP-LINK") != null) {
                    str.append("&nbsp;&nbsp;<a href=\"")
                            .append(field.getProperty("HELP-LINK"))
                            .append("\" target=\"_tab\">more...</a>");
                }
                str.append("</li>");
            }
        }
        str.append("</ul>");

        return str.toString();
    }

}
