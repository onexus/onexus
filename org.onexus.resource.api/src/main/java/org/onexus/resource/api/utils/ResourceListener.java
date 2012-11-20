package org.onexus.resource.api.utils;

import org.onexus.resource.api.IResourceListener;
import org.onexus.resource.api.Project;
import org.onexus.resource.api.Resource;

/**
 * Convenience {@link IResourceListener} implementation that does nothing.
 */
public abstract class ResourceListener implements IResourceListener {

    @Override
    public void onProjectCreate(Project project) {
    }

    @Override
    public void onProjectChange(Project project) {
    }

    @Override
    public void onProjectDelete(Project project) {
    }

    @Override
    public void onResourceCreate(Resource resource) {
    }

    @Override
    public void onResourceChange(Resource resource) {
    }

    @Override
    public void onResourceDelete(Resource resource) {
    }
}
