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
package org.onexus.ui.website.widgets.tableviewer.decorators;

import org.onexus.resource.api.resources.Collection;
import org.onexus.resource.api.resources.Field;
import org.onexus.ui.website.widgets.tableviewer.formaters.DoubleFormater;

import java.util.List;

public class DefaultDecoratorManager implements IDecoratorManager {

    private final static String annotationKey = "BROWSER_DECORATOR";

    private List<IDecoratorCreator> creators;


    @Override
    public IDecorator getDecorator(String decorator, Collection collection, Field field) {

        if (decorator == null && field != null) {
            decorator = field.getProperty(annotationKey);
        }

        IDecoratorCreator creator = null;
        String[] parameters = null;

        // Look for a decorator creator
        if (decorator != null) {
            String decoratorId = decorator;

            // Parse parameters
            int parSep = decoratorId.indexOf('(');
            if (parSep > 0) {
                decoratorId = decorator.substring(0, parSep);
                int parEnd = decorator.indexOf(')');
                String[] parametersNonTrim = decorator.substring(parSep + 1, parEnd).split(",");
                parameters = new String[parametersNonTrim.length];
                for (int i = 0; i < parametersNonTrim.length; i++) {
                    parameters[i] = parametersNonTrim[i].trim();
                }
            } else {
                parameters = new String[0];
            }

            // Get creator
            creator = getCreator(decoratorId);

        }

        if (creator == null) {
            return getDefaultDecorator(field);
        }

        return creator.createDecorator(collection, field, parameters);

    }

    private IDecorator getDefaultDecorator(Field field) {

        if (field.getType().equals(Double.class)) {
            return new FieldDecorator(field, new DoubleFormater(3));
        }

        return new FieldDecorator(field);
    }

    private IDecoratorCreator getCreator(String decoratorId) {

        if (creators != null) {
            for (IDecoratorCreator creator : creators) {
                if (creator.getDecoratorId().equalsIgnoreCase(decoratorId)) {
                    return creator;
                }
            }
        }

        return null;
    }


    public List<IDecoratorCreator> getCreators() {
        return creators;
    }

    public void setCreators(List<IDecoratorCreator> creators) {
        this.creators = creators;
    }
}
