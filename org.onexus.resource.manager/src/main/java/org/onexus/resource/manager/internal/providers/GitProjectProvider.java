package org.onexus.resource.manager.internal.providers;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.lib.ProgressMonitor;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.security.InvalidParameterException;

public class GitProjectProvider extends ProjectProvider {

    private static final Logger log = LoggerFactory.getLogger(GitProjectProvider.class);

    public GitProjectProvider(String projectUrl, File projectFolder) throws InvalidParameterException {
        super(projectUrl, projectFolder);

        if (!projectFolder.exists()) {
            importProject();
        }
    }

    @Override
    protected void importProject() {

        try {

            File projectFolder = getProjectFolder();

            boolean cloneDone = true;

            if (!projectFolder.exists()) {
                projectFolder.mkdir();
                cloneDone = false;
            }

            FileRepositoryBuilder builder = new FileRepositoryBuilder();
            Repository repository = builder.setWorkTree(projectFolder).readEnvironment().build();
            Git git = new Git(repository);


            if (cloneDone) {
                PullCommand pull = git.pull();
                pull.call();

            } else {
                CloneCommand clone = git.cloneRepository();
                clone.setBare(false);
                clone.setCloneAllBranches(true);
                clone.setDirectory(projectFolder).setURI(getProjectUrl().toString());
                clone.call();

                //TODO Checkout branch
            }

        } catch (Exception e) {
            log.error("Importing project '" + getProjectUrl() + "'", e);
            throw new InvalidParameterException("Error importing project. " + e.getMessage());
        }
    }

    private class LogProgressMonitor implements ProgressMonitor {

        @Override
        public void start(int totalTasks) {
        }

        @Override
        public void beginTask(String title, int totalWork) {
            log.info(title + " #" + totalWork);
        }

        @Override
        public void update(int completed) {
        }

        @Override
        public void endTask() {
        }

        @Override
        public boolean isCancelled() {
            return false;
        }
    }
}
