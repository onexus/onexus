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
package org.onexus.ui.workspace.progressbar;

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.util.time.Duration;

public class TaskStatusProgressBehavior extends AbstractDefaultAjaxBehavior {

    private static final long serialVersionUID = 1L;

    /**
     * The update interval
     */
    private Duration updateInterval;

    private boolean stopped = false;

    private boolean headRendered = false;

    /**
     * Construct.
     *
     * @param updateInterval Duration between AJAX callbacks
     */
    public TaskStatusProgressBehavior(final Duration updateInterval) {
        if (updateInterval == null || updateInterval.getMilliseconds() <= 0) {
            throw new IllegalArgumentException("Invalid update interval");
        }
        this.updateInterval = updateInterval;
    }

    /**
     * Stops the timer
     */
    public final void stop() {
        stopped = true;
    }

    /**
     * Starts the timer
     */
    public final void start() {
        stopped = false;
    }

    /**
     * Sets the update interval duration. This method should only be called
     * within the {@link #onTimer(AjaxRequestTarget)} method.
     *
     * @param updateInterval
     */
    protected final void setUpdateInterval(Duration updateInterval) {
        if (updateInterval == null || updateInterval.getMilliseconds() <= 0) {
            throw new IllegalArgumentException("Invalid update interval");
        }
        this.updateInterval = updateInterval;
    }

    /**
     * Returns the update interval
     *
     * @return The update interval
     */
    public final Duration getUpdateInterval() {
        return updateInterval;
    }

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        super.renderHead(component, response);

        WebRequest request = (WebRequest) RequestCycle.get().getRequest();

        if (!stopped && (!headRendered || !request.isAjax())) {
            headRendered = true;
            response.renderOnLoadJavaScript(getJsTimeoutCall(updateInterval));
        }
    }

    /**
     * @param updateInterval Duration between AJAX callbacks
     * @return JS script
     */
    protected final String getJsTimeoutCall(final Duration updateInterval) {
        // this might look strange, but it is necessary for IE not to leak :(
        return "setTimeout(\"" + getCallbackScript() + "\", "
                + updateInterval.getMilliseconds() + ");";
    }

    @Override
    protected CharSequence getCallbackScript() {
        return generateCallbackScript("wicketAjaxGet('" + getCallbackUrl()
                + "'");
    }

    /**
     * @see org.apache.wicket.ajax.AbstractDefaultAjaxBehavior#getPreconditionScript()
     */
    @Override
    protected CharSequence getPreconditionScript() {
        String precondition = null;
        if (!(getComponent() instanceof Page)) {
            String componentId = getComponent().getMarkupId();
            precondition = "var c = Wicket.$('" + componentId
                    + "'); return typeof(c) != 'undefined' && c != null";
        }
        return precondition;
    }

    /**
     * @see org.apache.wicket.ajax.AbstractDefaultAjaxBehavior#respond(org.apache.wicket.ajax.AjaxRequestTarget)
     */
    @Override
    protected final void respond(final AjaxRequestTarget target) {
        onTimer(target);

        if (!stopped && isEnabled(getComponent())) {
            target.getHeaderResponse().renderOnLoadJavaScript(
                    getJsTimeoutCall(updateInterval));
        }
    }

    /**
     * Listener method for the AJAX timer event.
     *
     * @param target The request target
     */
    protected final void onTimer(final AjaxRequestTarget target) {
        target.add(getComponent());
        onPostProcessTarget(target);

    }


    /**
     * Give the subclass a chance to add something to the target, like a
     * javascript effect call. Called after the hosting component has been added
     * to the target.
     *
     * @param target The AJAX target
     */
    protected void onPostProcessTarget(final AjaxRequestTarget target) {
    }

    /**
     * @return {@code true} if has been stopped via {@link #stop()}
     */
    public final boolean isStopped() {
        return stopped;
    }

}
