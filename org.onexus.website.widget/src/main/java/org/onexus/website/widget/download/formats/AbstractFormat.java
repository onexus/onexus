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
package org.onexus.website.widget.download.formats;

import org.apache.wicket.util.string.Strings;

public abstract class AbstractFormat implements IDownloadFormat {

    private String contentType;
    private String extension;
    private String title;

    public AbstractFormat(String extension, String contentType, String title) {
        this.extension = extension;
        this.contentType = contentType;
        this.title = title;
    }

    public String getLabel() {
        return extension;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public Long getMaxRowsLimit() {
        return null;
    }

    @Override
    public String getFileName(String label) {
        return (Strings.isEmpty(label) ? "datafile" : label.trim()) + "." + extension;
    }

    public String toString() {
        return getTitle();
    }
}
