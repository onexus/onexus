/**
 *  Copyright 2011 Universitat Pompeu Fabra.
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
package org.onexus.ui.website;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.IWrapModel;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import org.onexus.core.query.Query;
import org.onexus.ui.website.events.AbstractEvent;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class AbstractWebsitePanel<C extends IWebsiteConfig, S extends IWebsiteStatus> extends Panel {

    private C config;
    private Set<Class<?>> registeredEvents = new HashSet<Class<?>>();

    public AbstractWebsitePanel(String componentId, C config, IModel<S> statusModel) {
        super(componentId, statusModel);
        setOutputMarkupId(true);
        this.config = config;
    }

    public C getConfig() {
        return config;
    }

    @SuppressWarnings("unchecked")
    public S getStatus() {
        S status = getModelStatus().getObject();

        if (status == null) {
            status = (S) config.getDefaultStatus();
            getModelStatus().setObject(status);
        }

        if (status == null) {
            status = (S) config.createEmptyStatus();
            getModelStatus().setObject(status);
        }
        return status;

    }

    public void setStatus(S status) {
        getModelStatus().setObject(status);
    }

    @SuppressWarnings("unchecked")
    public IModel<S> getModelStatus() {
        return (IModel<S>) getDefaultModel();
    }

    protected WebsiteStatus getWebsiteStatus() {
        IModel<WebsiteStatus> statusModel = findInnerModel(WebsiteStatus.class, getDefaultModel());
        return (statusModel == null ? null : statusModel.getObject());
    }

    protected WebsiteConfig getWebsiteConfig() {
        return findInnerConfig(WebsiteConfig.class, getDefaultModel());
    }

    @SuppressWarnings("unchecked")
    public static <C extends IWebsiteConfig> C findInnerConfig(Class<C> configClass, IModel<?> model) {
        if (model != null && (model instanceof IWebsiteModel)) {
            IWebsiteConfig config = ((IWebsiteModel) model).getConfig();
            if (config != null && config.getClass().equals(configClass)) {
                return (C) config;
            }
        }

        if (model instanceof IWrapModel) {
            IModel<?> wrapModel = ((IWrapModel<?>) model).getWrappedModel();

            return findInnerConfig(configClass, wrapModel);
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public static <C> IModel<C> findInnerModel(Class<C> objectClass, IModel<?> model) {
        if (model != null && model.getObject() != null && objectClass.isAssignableFrom(model.getObject().getClass())) {
            return (IModel<C>) model;
        }

        if (model instanceof IWrapModel) {
            IModel<?> wrapModel = ((IWrapModel<?>) model).getWrappedModel();

            if (wrapModel != null) {
                return findInnerModel(objectClass, wrapModel);
            }
        }

        return null;
    }

    protected void sendEvent(AbstractEvent event) {
        send(getPage(), Broadcast.BREADTH, event);
    }

    /**
     * @param eventClass Update this panel on 'eventClass' events.
     */
    protected void onEventFireUpdate(Class<?>... eventClass) {
        registeredEvents.addAll(Arrays.asList(eventClass));
    }

    @Override
    public void onEvent(IEvent<?> event) {

        // Add this panel to AJAX target if it's a registered Event.
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

    protected void buildQuery(Query query) {
        getPage().visitChildren(new QueryBuilder(query));
    }

    private static final class QueryBuilder implements IVisitor<Component, Void> {

        private final Query query;

        public QueryBuilder(final Query query) {
            super();
            this.query = query;
        }

        @Override
        public void component(Component component, IVisit<Void> visit) {
            if (component instanceof IQueryContributor) {
                ((IQueryContributor) component).onQueryBuild(query);
            }
        }

    }

}
