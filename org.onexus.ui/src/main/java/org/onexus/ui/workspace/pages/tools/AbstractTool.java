package org.onexus.ui.workspace.pages.tools;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

public class AbstractTool<T> extends Panel {

    public AbstractTool(IModel<T> model) {
	super("tool", model);
    }

    @SuppressWarnings("unchecked")
    public IModel<T> getModel() {
	return (IModel<T>) getDefaultModel();
    }

    public T getModelObject() {

	IModel<T> model = getModel();

	return (model == null ? null : model.getObject());
    }

}
