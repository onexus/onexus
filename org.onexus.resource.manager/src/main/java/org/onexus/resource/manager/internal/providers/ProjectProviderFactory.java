package org.onexus.resource.manager.internal.providers;

import org.onexus.resource.api.IResourceSerializer;
import org.onexus.resource.manager.internal.PluginLoader;

import java.io.File;

public class ProjectProviderFactory {

    private IResourceSerializer serializer;
    private PluginLoader pluginLoader;

    public ProjectProviderFactory(IResourceSerializer serializer, PluginLoader pluginLoader) {
        super();

        this.pluginLoader = pluginLoader;
        this.serializer = serializer;
    }

    public ProjectProvider newProjectProvider(String projectUri, File projectFolder) {

        ProjectProvider provider;

        if (projectUri.endsWith(".git")) {
            provider = new GitProjectProvider(projectUri, projectFolder);
        } else if (projectUri.endsWith(".zip")) {
            provider = new ZipProjectProvider(projectUri, projectFolder);
        } else {
            provider = new FolderProjectProvider(projectUri, projectFolder);
        }

        provider.setSerializer(serializer);
        provider.setPluginLoader(pluginLoader);

        return provider;
    }
}
