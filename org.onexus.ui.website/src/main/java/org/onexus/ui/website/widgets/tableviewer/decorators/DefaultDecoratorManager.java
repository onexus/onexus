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

import org.onexus.collection.api.Collection;
import org.onexus.collection.api.Field;
import org.onexus.resource.api.ParameterKey;
import org.onexus.ui.website.widgets.tableviewer.decorators.utils.FieldDecorator;
import org.onexus.ui.website.widgets.tableviewer.formaters.DoubleFormater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class DefaultDecoratorManager implements IDecoratorManager {

    private static final Logger log = LoggerFactory.getLogger(DefaultDecoratorManager.class);
    private final static String annotationKey = "BROWSER_DECORATOR";

    private List<IDecoratorCreator> creators;


    @Override
    public IDecorator getDecorator(String decorator, Collection collection, Field field) {

        if (decorator == null && field != null) {
            decorator = field.getProperty(annotationKey);
        }

        IDecoratorCreator creator = null;
        Map<ParameterKey, String> parameters = new HashMap<ParameterKey, String>();

        // Look for a decorator creator
        if (decorator != null) {
            String decoratorId = decorator;


            // Parse parameters
            int parSep = decoratorId.indexOf('(');
            if (parSep > 0) {

                try {
                    decoratorId = decorator.substring(0, parSep);
                    int parEnd = decorator.indexOf(')');
                    String[] parametersNonTrim = decorator.substring(parSep + 1, parEnd).split(",");

                    // Get creator
                    creator = getCreator(decoratorId);

                    if (creator == null) {
                        log.warn("Unknown decorator '" + decorator + "'");
                    } else {
                        for (int i = 0; i < parametersNonTrim.length; i++) {
                            String keyAndValue[] = parametersNonTrim[i].split(Pattern.quote("=\""));

                            ParameterKey key = null;
                            for (ParameterKey k : creator.getParameterKeys()) {
                                if (k.getKey().equalsIgnoreCase(keyAndValue[0])) {
                                    key = k;
                                    break;
                                }
                            }

                            if (key == null) {
                                throw new UnsupportedOperationException("Unknown parameter '" + keyAndValue[0] + "' on decorator '" + decorator + "'");
                            }

                            String value = keyAndValue[1].trim();
                            value = value.substring(0, value.length()-1);
                            parameters.put(key, value);
                        }
                    }
                } catch (UnsupportedOperationException e) {
                    throw e;
                } catch (Exception e) {
                    throw new UnsupportedOperationException("Malformed decorator parameters on '" + decorator + "'");
                }
            } else {
                // Get creator
                creator = getCreator(decoratorId);
            }
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
