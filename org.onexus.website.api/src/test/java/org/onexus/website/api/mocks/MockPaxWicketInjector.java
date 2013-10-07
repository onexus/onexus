package org.onexus.website.api.mocks;

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