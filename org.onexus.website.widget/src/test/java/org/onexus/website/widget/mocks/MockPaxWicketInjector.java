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
package org.onexus.website.widget.mocks;

import org.apache.wicket.Component;
import org.apache.wicket.application.IComponentInstantiationListener;
import org.ops4j.pax.wicket.internal.injection.AbstractPaxWicketInjector;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class MockPaxWicketInjector extends AbstractPaxWicketInjector implements IComponentInstantiationListener {

    private Map<Class<?>, Object> beans = new HashMap<Class<?>, Object>();

    public MockPaxWicketInjector() {
    }

    public void addBean(Class<?> type, Object bean) {
        beans.put(type, bean);
    }

    @Override
    public void inject(Object toInject, Class<?> toHandle) {

        for (Field field : getFields(toHandle)) {

            Class<?> type = getBeanType(field);
            if (beans.containsKey(type)) {
                setField(toInject, field, beans.get(type));
            }
        }
    }

    @Override
    public void onInstantiation(Component component) {
        inject(component, component.getClass());
    }
}