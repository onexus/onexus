package org.onexus.website.api.widgets.download.formats;

public abstract class AbstractFormat implements IDownloadFormat {

    private String contentType;
    private String extension;
    private String title;

    public AbstractFormat(String extension, String contentType, String title) {
        this.extension = extension;
        this.contentType = contentType;
        this.title = title;
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
    public String getFileName() {
        return "datafile." + extension;
    }

    public String toString() {
        return getTitle();
    }
}
