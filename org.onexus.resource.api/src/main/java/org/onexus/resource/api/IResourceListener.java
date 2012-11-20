package org.onexus.resource.api;

public interface IResourceListener {

    public void onProjectCreate(Project project);

    public void onProjectChange(Project project);

    public void onProjectDelete(Project project);

    public void onResourceCreate(Resource resource);

    public void onResourceChange(Resource resource);

    public void onResourceDelete(Resource resource);
}
