package org.onexus.website.api.utils;

import org.apache.wicket.Application;
import org.apache.wicket.Session;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.AbstractResource;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.util.io.Streams;
import org.onexus.data.api.IDataManager;
import org.onexus.data.api.IDataStreams;
import org.onexus.resource.api.ORI;
import org.onexus.website.api.WebsiteApplication;
import org.onexus.website.api.WebsiteModel;
import org.ops4j.pax.wicket.api.PaxWicketBean;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DataResource extends AbstractResource {

    private final static String DATA_MOUNT_POINT = "data";

    @PaxWicketBean(name = "dataManager")
    private IDataManager dataManager;

    private String projectUrl;

    public DataResource(String projectUrl) {
        this.projectUrl = projectUrl;
    }

    @Override
    protected ResourceResponse newResourceResponse(Attributes attributes) {
        return new DataResponse();
    }

    private IDataManager getDataManager() {
        if (dataManager == null) {
            WebsiteApplication.inject(this);
        }
        return dataManager;
    }

    public static ResourceReference getResourceReference() {

        ResourceReference reference = Application.get().getSharedResources().get(Application.class, DATA_MOUNT_POINT, null, null, null, true);

        if (reference == null) {
            DataResource projectResource = new DataResource(WebsiteApplication.get().getWebsiteOri().getProjectUrl());
            WebApplication.get().getSharedResources().add(DATA_MOUNT_POINT, projectResource);
            reference = Application.get().getSharedResources().get(Application.class, DATA_MOUNT_POINT, null, null, null, true);
            WebApplication.get().mountResource(DATA_MOUNT_POINT, reference);
        }

        return reference;
    }


    public static PageParameters buildPageParameters(ORI resourceUri) {

        PageParameters parameters = new PageParameters();

        String segments[] = resourceUri.getPath().substring(1).split("/");

        for (int i=0; i < segments.length; i++) {
            parameters.set(i, segments[i]);
        }

        return parameters;
    }

    private class DataResponse extends ResourceResponse {

        public DataResponse() {
            super();

            setWriteCallback(new AbstractResource.WriteCallback() {
                @Override
                public void writeData(IResource.Attributes attributes) {
                    DataResponse.this.writeData(attributes);
                }
            });
        }

        protected void writeData(IResource.Attributes attributes) {

            final Response response = attributes.getResponse();

            OutputStream s = new OutputStream() {
                @Override
                public void write(int b) throws IOException {
                    response.write(new byte[]{(byte) b});
                }

                @Override
                public void write(byte[] b) throws IOException {
                    response.write(b);
                }

                @Override
                public void write(byte[] b, int off, int len) throws IOException {
                    response.write(b, off, len);
                }
            };
            try {

                ORI resource;

                StringBuilder path = new StringBuilder();
                PageParameters params = attributes.getParameters();
                for (int i = 0; i < params.getIndexedCount(); i++) {
                    path.append('/').append(params.get(i));
                }

                resource = new ORI(projectUrl, path.toString());
                IDataStreams dataStreams = getDataManager().load(resource);

                for (InputStream stream : dataStreams) {
                    Streams.copy(stream, s);
                }

            } catch (IOException e) {
                throw new WicketRuntimeException(e);
            }


        }

    }

}
