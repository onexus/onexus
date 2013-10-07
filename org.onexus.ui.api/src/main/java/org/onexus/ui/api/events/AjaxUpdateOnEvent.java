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
package org.onexus.ui.api.events;

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
