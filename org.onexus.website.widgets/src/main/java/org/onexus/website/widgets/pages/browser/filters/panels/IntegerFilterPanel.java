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
package org.onexus.website.widgets.pages.browser.filters.panels;

import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.onexus.website.api.IFilter;
import org.onexus.website.widgets.pages.browser.filters.operations.*;

import java.util.Arrays;
import java.util.List;

public abstract class IntegerFilterPanel extends AbstractFilterPanel<Integer> {

    private static final List<FilterOperation> OPERATIONS = Arrays.asList(new FilterOperation[]{
            EqualOperation.INSTANCE,
            NotEqualOperation.INSTANCE,
            GreaterThanOperation.INSTANCE,
            GreaterThanOrEqualOperation.INSTANCE,
            LessThanOperation.INSTANCE,
            LessThanOrEqualOperation.INSTANCE,
            IsEmptyOperation.INSTANCE,
            IsNotEmptyOperation.INSTANCE
    });

    public IntegerFilterPanel(String id, IFilter fieldHeader) {
        super(id, fieldHeader, new FilterOption<Integer>(EqualOperation.INSTANCE), OPERATIONS);
    }

    @Override
    protected FormComponent<Integer> createValueFormComponent(String componentId) {
        return new TextField<Integer>(componentId);
    }
}
