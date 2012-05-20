package org.onexus.ui.ws.response;

import org.apache.wicket.request.Response;
import org.apache.wicket.request.resource.AbstractResource;
import org.apache.wicket.request.resource.IResource;
import org.onexus.ui.OnexusWebApplication;


public abstract class AbstractResponse extends AbstractResource.ResourceResponse {

    public AbstractResponse() {
        super();
        OnexusWebApplication.inject(this);

        setWriteCallback(new AbstractResource.WriteCallback() {
            @Override
            public void writeData(IResource.Attributes attributes) {
                AbstractResponse.this.writeData(attributes.getResponse());
            }
        });
    }

    protected abstract void writeData(Response response);


}
