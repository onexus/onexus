package org.onexus.ui.workspace.viewers.tree;

import org.apache.wicket.extensions.markup.html.repeater.tree.ITreeProvider;
import org.apache.wicket.model.IModel;
import org.onexus.resource.api.IResourceManager;
import org.onexus.resource.api.Folder;
import org.onexus.resource.api.Project;
import org.onexus.resource.api.Resource;
import org.onexus.resource.api.utils.ResourceUtils;
import org.onexus.ui.api.OnexusWebApplication;
import org.onexus.ui.api.pages.resource.ResourceModel;

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

        Project project = resourceManager.getProject( ResourceUtils.getProjectURI(resource.getURI()) );

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

                if (o1 == null) {
                    return 1;
                }

                if (o2 == null) {
                    return 0;
                }

                // First folders
                boolean f1 = (o1 instanceof Folder);
                boolean f2 = (o2 instanceof Folder);

                if (f1 && !f2) {
                    return 0;
                }

                if (f2 && !f1) {
                    return 1;
                }

                return o1.getName().compareTo(o2.getName());

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
