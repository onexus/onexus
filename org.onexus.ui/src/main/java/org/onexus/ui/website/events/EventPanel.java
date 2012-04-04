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
package org.onexus.ui.website.events;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class EventPanel extends Panel {

    private Set<Class<?>> registeredEvents = new HashSet<Class<?>>();

    public EventPanel(String id) {
        super(id);
        setOutputMarkupId(true);
    }

    public EventPanel(String id, IModel<?> model) {
        super(id, model);
        setOutputMarkupId(true);
    }

    protected void sendEvent(AbstractEvent event) {
        send(getPage(), Broadcast.BREADTH, event);
    }

    protected void onEventFireUpdate(Class<?>... eventClass) {
        registeredEvents.addAll(Arrays.asList(eventClass));
    }

    @Override
    public void onEvent(IEvent<?> event) {

        if (event.getPayload() instanceof AbstractEvent) {
            AjaxRequestTarget target = AjaxRequestTarget.get();

            if (target != null) {
                Class<?> currentEventClass = event.getPayload().getClass();
                for (Class<?> eventClass : registeredEvents) {
                    if (eventClass.isAssignableFrom(currentEventClass)) {
                        target.add(this);
                        event.dontBroadcastDeeper();
                        return;
                    }
                }
            }
        }
    }
}
