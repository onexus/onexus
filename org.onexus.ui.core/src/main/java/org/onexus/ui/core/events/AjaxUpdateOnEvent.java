package org.onexus.ui.core.events;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.request.cycle.RequestCycle;

public class AjaxUpdateOnEvent extends Behavior {

    private Object[] events;

    public AjaxUpdateOnEvent(Object... event) {
        this.events = event;
    }

    @Override
    public void bind(Component component) {
        component.setOutputMarkupId(true);
    }

    @Override
    public void onEvent(Component component, IEvent<?> event) {

        if (events != null) {
            for (Object e : events) {
                if (e.equals(event.getPayload())) {
                    RequestCycle.get().find(AjaxRequestTarget.class).add(component);
                }
            }
        }

    }
}
