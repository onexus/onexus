package org.onexus.ui.website.pages.browser.boxes;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.onexus.core.IEntity;
import org.onexus.core.IResourceManager;
import org.onexus.core.resources.Collection;

import javax.inject.Inject;

public abstract class AbstractBox extends Panel {

    public static final String COMPONENT_ID = "box";

    @Inject
    private IResourceManager resourceManager;
    private String collectionId;

    public AbstractBox(String collectionId, IModel<IEntity> entityModel) {
        super(COMPONENT_ID, entityModel);
        this.collectionId = collectionId;
    }

    public String getTitle() {

        Collection collection = resourceManager.load(Collection.class, collectionId);

        String title = collection.getTitle();

        return (title == null ? collection.getName() : title);
    }

    public String getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    protected IEntity getEntity() {
        return (IEntity) getDefaultModelObject();
    }

    @SuppressWarnings("unchecked")
    public IModel<IEntity> getEntityModel() {
        return (IModel<IEntity>) getDefaultModel();
    }

}
