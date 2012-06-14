package org.onexus.ui.viewers.tree;

import org.apache.wicket.extensions.markup.html.repeater.tree.ITreeProvider;
import org.apache.wicket.model.IModel;
import org.onexus.core.IResourceManager;
import org.onexus.core.resources.Folder;
import org.onexus.core.resources.Project;
import org.onexus.core.resources.Resource;
import org.onexus.core.utils.ResourceUtils;
import org.onexus.ui.OnexusWebApplication;
import org.onexus.ui.workspace.pages.ResourceModel;

import javax.inject.Inject;
import java.util.*;

public class ResourceTreeProvider implements ITreeProvider<Resource> {

    private IModel<? extends Resource> currentResource;

    @Inject
    public IResourceManager resourceManager;

    private final static Iterator<Resource> EMPTY_ITERATOR = (new ArrayList<Resource>(0)).iterator();

    public ResourceTreeProvider(IModel<? extends Resource> resource) {
        super();

        OnexusWebApplication.inject(this);

        this.currentResource = resource;

    }

    @Override
    public Iterator<? extends Resource> getRoots() {

        Resource resource = currentResource.getObject();

        if (resource == null) {
            return EMPTY_ITERATOR;
        }

        Project project = resourceManager.load(Project.class, ResourceUtils.getProjectURI(resource.getURI()));

        if (project == null) {
            return EMPTY_ITERATOR;
        }

        return Arrays.asList(project).iterator();
    }

    @Override
    public boolean hasChildren(Resource node) {

        if (node instanceof Project) {
            return true;
        }

        if (Folder.class.isAssignableFrom(node.getClass())) {
            return true;
        }

        return false;
    }

    @Override
    public Iterator<? extends Resource> getChildren(Resource node) {

        if (node == null) {
            return EMPTY_ITERATOR;
        }

        List<Resource> children = resourceManager.loadChildren(Resource.class, node.getURI());

        Collections.sort(children, new Comparator<Resource>() {
            @Override
            public int compare(Resource o1, Resource o2) {
                return (o1!=null && (o1 instanceof Folder)) ? 0 : 1;
            }
        });

        if (children == null) {
            return EMPTY_ITERATOR;
        }

        return children.iterator();
    }


    @Override
    public IModel<Resource> model(Resource object) {
        return new ResourceModel(object);
    }

    @Override
    public void detach() {
    }
}
