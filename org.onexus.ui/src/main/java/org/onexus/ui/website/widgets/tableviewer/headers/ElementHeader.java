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
package org.onexus.ui.website.widgets.tableviewer.headers;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.onexus.core.resources.IMetadata;
import org.onexus.ui.website.widgets.tableviewer.formaters.ITextFormater;

public class ElementHeader implements IHeader {

    private IMetadata element;
    private IHeader parentHeader;
    private ITextFormater textFormater;
    private String sortProperty = null;

    public ElementHeader(IMetadata element, ITextFormater formater) {
        this(element, null, formater);
    }

    public ElementHeader(IMetadata element, IHeader parentHeader,
                         ITextFormater formater) {
        super();
        this.element = element;
        this.parentHeader = parentHeader;
        this.textFormater = formater;
    }

    @Override
    public Component getHeader(String componentId) {
        return new Label(componentId, getFormatedLabel());
    }

    @Override
    public String getLabel() {
        String label = null;

        if (element != null) {

            // FIXME Use a renderer

            label = element.getName();
        }

        return label;
    }

    @Override
    public String getTitle() {
        return getLabel();
    }

    public String getFormatedLabel() {
        return textFormater.format(getLabel());
    }

    @Override
    public IHeader getParentHeader() {
        return parentHeader;
    }

    @Override
    public String getSortProperty() {
        return sortProperty;
    }

    public void setSortProperty(String sortProperty) {
        this.sortProperty = sortProperty;
    }

    @Override
    public boolean isSortable() {
        return (sortProperty != null);
    }

    @Override
    public Component getHelp(String componentId) {
        return new EmptyPanel(componentId);
    }

}
