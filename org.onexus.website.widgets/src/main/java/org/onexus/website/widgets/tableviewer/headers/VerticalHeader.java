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
package org.onexus.website.widgets.tableviewer.headers;

import org.apache.wicket.Component;
import org.onexus.website.widgets.tableviewer.formaters.ITextFormater;

public class VerticalHeader extends StringHeader {

    public VerticalHeader(String label, IHeader parentHeader) {
        super(label, parentHeader, null);
    }

    public VerticalHeader(String label, IHeader parentHeader,
                          ITextFormater formater) {
        super(label, parentHeader, formater);
    }

    public VerticalHeader(String label, ITextFormater formater) {
        super(label, formater);
    }

    public VerticalHeader(String label) {
        super(label);
    }

    @Override
    public Component getHeader(String componentId) {
        return new VerticalTextPanel(componentId, getFormatedLabel());
    }

}
