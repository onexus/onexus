package org.onexus.website.api.widgets.download.formats;

import org.onexus.collection.api.IEntityTable;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;

public interface IDownloadFormat extends Serializable {

    public String getTitle();

    public String getFileName();

    public String getContentType();

    public Long getMaxRowsLimit();

    public void write(IEntityTable table, OutputStream ouputStream) throws IOException;
}
