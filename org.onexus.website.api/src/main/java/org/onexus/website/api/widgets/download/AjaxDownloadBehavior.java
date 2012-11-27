package org.onexus.website.api.widgets.download;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.apache.wicket.request.handler.resource.ResourceRequestHandler;
import org.apache.wicket.request.resource.IResource;

public abstract class AjaxDownloadBehavior extends AbstractAjaxBehavior {

	private boolean addAntiCache;

	public AjaxDownloadBehavior() {
		this(true);
	}

	public AjaxDownloadBehavior(boolean addAntiCache) {
		super();
		this.addAntiCache = addAntiCache;
	}

	/**
	 * Call this method to initiate the download.
	 */
	public void initiate(AjaxRequestTarget target) {
		String url = getCallbackUrl().toString();

		if (addAntiCache) {
			url = url + (url.contains("?") ? "&" : "?");
			url = url + "antiCache=" + System.currentTimeMillis();
		}

		// the timeout is needed to let Wicket release the channel
		target.appendJavaScript("setTimeout(\"window.location.href='" + url + "'\", 100);");
	}

	public void onRequest() {
		ResourceRequestHandler handler = new ResourceRequestHandler(getResource(), null);
		getComponent().getRequestCycle().scheduleRequestHandlerAfterCurrent(handler);
	}

	/**
	 * Override this method for a file name which will let the browser prompt with a save/open dialog.
	 *
	 */
	protected abstract String getFileName();

	/**
	 * Hook method providing the actual resource stream.
	 */
	protected abstract IResource getResource();
}
