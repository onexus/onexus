package org.onexus.website.api.utils.visible;

public class MockVisible implements IVisible {

    private String visible;

    public MockVisible(String visible) {
        this.visible = visible;
    }

    @Override
    public String getVisible() {
        return visible;
    }
}
