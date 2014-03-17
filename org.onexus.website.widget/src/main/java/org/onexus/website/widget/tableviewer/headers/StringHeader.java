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
package org.onexus.website.widget.tableviewer.headers;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.onexus.website.widget.tableviewer.formaters.ITextFormater;

public class StringHeader implements IHeader {

    private String label;
    private String title;
    private IHeader parentHeader;
    private ITextFormater textFormater;

    public StringHeader(String label) {
        this(label, null, null, null);
    }

    public StringHeader(String label, String title, ITextFormater formater) {
        this(label, title, null, formater);
    }

    public StringHeader(String label, ITextFormater formater) {
        this(label, null, null, formater);
    }

    public StringHeader(String label, IHeader parentHeader,
                        ITextFormater formater) {
        this(label, null, parentHeader, formater);
    }

    public StringHeader(String label, String title, IHeader parentHeader,
                        ITextFormater formater) {
        super();
        this.label = label;
        this.title = title;
        this.parentHeader = parentHeader;
        this.textFormater = formater;
    }

    @Override
    public Component getHeader(String componentId) {
        return new Label(componentId, getFormatedLabel());
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public String getTitle() {
        return title == null ? getLabel() : title;
    }

    public String getFormatedLabel() {
        return textFormater == null ? getLabel() : textFormater.format(getLabel());
    }

    @Override
    public IHeader getParentHeader() {
        return parentHeader == null ? new EmptyHeader() : parentHeader;
    }

    @Override
    public String getSortProperty() {
        return null;
    }

    @Override
    public boolean isSortable() {
        return false;
    }

    @Override
    public boolean isFilterable() {
        return false;
    }

    @Override
    public Component getHelp(String componentId) {
        return new EmptyPanel(componentId);
    }

}
