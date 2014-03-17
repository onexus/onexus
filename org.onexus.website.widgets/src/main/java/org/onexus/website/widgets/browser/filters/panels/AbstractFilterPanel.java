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
package org.onexus.website.widgets.browser.filters.panels;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.onexus.collection.api.query.Filter;
import org.onexus.website.api.FilterConfig;
import org.onexus.website.api.IFilter;
import org.onexus.website.widgets.browser.filters.operations.FilterOperation;

import java.util.List;

public abstract class AbstractFilterPanel<T> extends Panel {

    private IModel<FilterOption> option;
    private IFilter header;
    private FilterOption<T> defaultOption;
    private List<FilterOperation> operations;

    public AbstractFilterPanel(String id, IFilter fieldHeader, FilterOption<T> defaultOption, List<FilterOperation> operations) {
        super(id);

        this.header = fieldHeader;
        this.option = new CompoundPropertyModel<FilterOption>(new Model<FilterOption>(defaultOption));
        this.defaultOption = defaultOption;
        this.operations = operations;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        Form<FilterOption> form = new Form("form", option);

        // Feedback panel
        final FeedbackPanel feedbackPanel = new FeedbackPanel("feedback");
        feedbackPanel.setOutputMarkupId(true);
        form.add(feedbackPanel);

        // Value component
        final FormComponent<T> valueComponent = createValueFormComponent("value");
        valueComponent.setOutputMarkupPlaceholderTag(true);
        valueComponent.setVisible(defaultOption.getOperation().isNeedsValue());
        form.add(valueComponent);

        // Operations drop down
        final DropDownChoice<FilterOperation> operationsDropDown = new DropDownChoice<FilterOperation>("operation", operations);
        operationsDropDown.setNullValid(false);
        operationsDropDown.add(new OnChangeAjaxBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                valueComponent.setVisible(option.getObject().getOperation().isNeedsValue());
                target.add(valueComponent);
            }
        });
        form.add(operationsDropDown);

        // Create filter link
        form.add(new AjaxSubmitLink("submit") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {

                FilterOption<T> fo = option.getObject();
                T value = fo.getValue();

                FilterOperation operation = fo.getOperation();
                String title = operation.createTitle(header.getLabel(), value);
                FilterConfig fc = new FilterConfig(title);

                fc.setCollection(header.getCollection().getORI());
                fc.setDefine("fc='" + fc.getCollection() + "'");


                Filter where = operation.createFilter("fc", header.getField().getId(), value);
                fc.setWhere(where.toString());

                addFilter(target, fc);
            }
        });

        add(form);

    }

    protected abstract FormComponent<T> createValueFormComponent(String componentId);

    protected abstract void addFilter(AjaxRequestTarget target, FilterConfig filterConfig);

}
