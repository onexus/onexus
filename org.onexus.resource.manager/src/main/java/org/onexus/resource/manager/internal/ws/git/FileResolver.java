package org.onexus.resource.manager.internal.ws.git;

import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryCache;
import org.eclipse.jgit.lib.RepositoryCache.FileKey;
import org.eclipse.jgit.transport.resolver.RepositoryResolver;
import org.eclipse.jgit.transport.resolver.ServiceNotEnabledException;
import org.eclipse.jgit.util.FS;
import org.onexus.resource.api.IResourceManager;
import org.onexus.resource.api.ORI;
import org.onexus.resource.api.Project;
import org.onexus.resource.manager.internal.ResourceManager;
import org.onexus.resource.manager.internal.providers.AbstractProjectProvider;
import org.onexus.resource.manager.internal.providers.GitProjectProvider;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default resolver serving from the local filesystem.
 *
 * @param <C>
 *            type of connection
 */
public class FileResolver<C> implements RepositoryResolver<C> {

    private ResourceManager resourceManager;
    private final Map<String, Repository> exports;

    /** Initialize an empty file based resolver. */
    public FileResolver(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
        this.exports = new ConcurrentHashMap<String, Repository>();
    }

    public Repository open(final C req, final String name)
            throws RepositoryNotFoundException, ServiceNotEnabledException {
        if (isUnreasonableName(name))
            throw new RepositoryNotFoundException(name);

        Repository db = exports.get(nameWithDotGit(name));
        if (db != null) {
            db.incrementOpen();
            return db;
        }

        ORI projectORI = null;
        for (Project project : resourceManager.getProjects()) {
            if (project.getName().equals(name)) {
                projectORI = project.getORI();
                break;
            }
        }

        if (projectORI == null) {
            throw new RepositoryNotFoundException(name);
        }

        AbstractProjectProvider projectProvider = resourceManager.getProjectsContainer().getProjectProvider(projectORI.toString());

        if (!(projectProvider instanceof GitProjectProvider)) {
            throw new RepositoryNotFoundException(name);
        }

        File projectFolder = projectProvider.getProjectFolder();
        File dir = FileKey.resolve(projectFolder, FS.DETECTED);
        if (dir == null) {
            throw new RepositoryNotFoundException(name);
        }


        try {
            FileKey key = FileKey.exact(dir, FS.DETECTED);
            db = RepositoryCache.open(key, true);
        } catch (IOException e) {
            throw new RepositoryNotFoundException(name, e);
        }

        return db;

    }

    /**
     * Add a single repository to the set that is exported by this daemon.
     * <p>
     * The existence (or lack-thereof) of <code>git-daemon-export-ok</code> is
     * ignored by this method. The repository is always published.
     *
     * @param name
     *            name the repository will be published under.
     * @param db
     *            the repository instance.
     */
    public void exportRepository(String name, Repository db) {
        exports.put(nameWithDotGit(name), db);
    }

    private static String nameWithDotGit(String name) {
        if (name.endsWith(Constants.DOT_GIT_EXT))
            return name;
        return name + Constants.DOT_GIT_EXT;
    }

    private static boolean isUnreasonableName(final String name) {
        if (name.length() == 0)
            return true; // no empty paths

        if (name.indexOf('\\') >= 0)
            return true; // no windows/dos style paths
        if (new File(name).isAbsolute())
            return true; // no absolute paths

        if (name.startsWith("../"))
            return true; // no "l../etc/passwd"
        if (name.contains("/../"))
            return true; // no "foo/../etc/passwd"
        if (name.contains("/./"))
            return true; // "foo/./foo" is insane to ask
        if (name.contains("//"))
            return true; // double slashes is sloppy, don't use it

        return false; // is a reasonable name
    }

}

