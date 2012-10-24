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
package org.onexus.ui.workspace.viewers.editor;

import com.thoughtworks.xstream.converters.ErrorWriter;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.util.io.ByteArrayOutputStream;
import org.apache.wicket.validation.IValidationError;
import org.onexus.resource.api.IResourceSerializer;
import org.onexus.resource.api.Resource;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.util.Locale;

public class XmlTextArea extends TextArea<Resource> {

    @Inject
    private transient IResourceSerializer resourceSerializer;

    private IConverter<Resource> converter;
    private String resourceUri;
    private String resourceName;

    public XmlTextArea(String id, IModel<Resource> model) {
        super(id, model);
        setType(Resource.class);

        this.converter = new ResourceConverter();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <C> IConverter<C> getConverter(Class<C> type) {
        return (IConverter<C>) converter;
    }

    @Override
    public void error(IValidationError error) {
        // Hide validation errors
    }

    private class ResourceConverter implements IConverter<Resource> {

        @Override
        public Resource convertToObject(String value, Locale locale) {
            try {
                Resource resource = resourceSerializer.unserialize(Resource.class,
                        new ByteArrayInputStream(value.getBytes()));

                resource.setURI(resourceUri);
                resource.setName(resourceName);

                return resource;
            } catch (Exception e) {
                
                if (e instanceof ErrorWriter) {
                    ErrorWriter ew = (ErrorWriter)e;
                    XmlTextArea.this.error("Error at line " + ew.get("line number") + " on " + ew.get("path"));
                } else {
                     XmlTextArea.this.error(String.valueOf(e.getMessage()));
                }
                throw new ConversionException(e.getMessage());
            }
        }

        @Override
        public String convertToString(Resource value, Locale locale) {
            try {

                resourceUri = value.getURI();
                resourceName = value.getName();

                value.setURI(null);
                value.setName(null);

                ByteArrayOutputStream output = new ByteArrayOutputStream();
                resourceSerializer.serialize(value, output);

                value.setURI(resourceUri);
                value.setName(resourceName);

                return output.toString();
            } catch (Exception e) {
                XmlTextArea.this.error(String.valueOf(e.getMessage()));
                throw new ConversionException(e.getMessage());

            }
        }

    }

}
