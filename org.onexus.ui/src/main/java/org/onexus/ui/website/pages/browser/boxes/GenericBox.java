package org.onexus.ui.website.pages.browser.boxes;


import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.onexus.core.IEntity;
import org.onexus.core.resources.Field;

/**
 * Base class to instantiate (creates) new concrete panel.
 * <p/>
 * There is two possibilities. If there is not any viewerClass specified, a
 * generic way to display all data from an IEntity is used (in concrete
 * ElementObjectDisplayPanel)
 *
 * @author armand
 */
public class GenericBox extends AbstractBox {

    public GenericBox(String collectionId, IModel<IEntity> entityModel) {
        super(collectionId, entityModel);

        RepeatingView fieldsRV = new RepeatingView("fields");
        IEntity entity = entityModel.getObject();

        for (Field field : entity.getCollection().getFields()) {

            // Skip fields with null value
            Object value = entity.get(field.getName());
            if (value == null) {
                continue;
            }

            String caption = field.getTitle();
            if (caption == null) {
                caption = field.getName();
            }

            // Create the field container
            WebMarkupContainer fieldContainer = new WebMarkupContainer(fieldsRV.newChildId());
            fieldContainer.add(new Label("caption", caption));
            fieldContainer.add(new Label("value", String.valueOf(value)));
            fieldsRV.add(fieldContainer);

        }
        add(fieldsRV);

    }

}
