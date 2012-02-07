package org.onexus.ui.wizards;

import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.onexus.core.resources.Resource;
import org.onexus.ui.IWizardCreator;

public class WizardViewerTabPanel extends Panel {

    @Inject
    private IWizardsManager wizardsManager;

    private IModel<? extends Resource> resourceModel;
    private WebMarkupContainer wizardContainer;

    public WizardViewerTabPanel(String id, IModel<? extends Resource> model) {
	super(id);
	this.resourceModel = model;

	add(new ListView<IWizardCreator>("wizardList", new WizardCreatorsModel()) {

	    @Override
	    protected void populateItem(final ListItem<IWizardCreator> item) {

		AjaxLink<String> link = new AjaxLink<String>("link") {

		    @Override
		    public void onClick(AjaxRequestTarget target) {
			IWizardCreator wizard = item.getModelObject();
			wizardContainer.addOrReplace(wizard.getPanel("wizard", resourceModel));
			target.add(wizardContainer);
		    }
		};

		link.add(new Label("title", item.getModelObject().getTitle()));
		item.add(link);
	    }
	});

	wizardContainer = new WebMarkupContainer("wizardContainer") {

	    @Override
	    public boolean isVisible() {
		Component wizard = get("wizard");

		if (wizard == null || !wizard.isVisible()) {
		    return false;
		}

		return true;
	    }

	};
	wizardContainer.setOutputMarkupPlaceholderTag(true);
	wizardContainer.add(new EmptyPanel("wizard").setVisible(false));
	add(wizardContainer);
    }

    private class WizardCreatorsModel extends AbstractReadOnlyModel<List<? extends IWizardCreator>> {

	@Override
	public List<? extends IWizardCreator> getObject() {
	    return wizardsManager.getWizardCreators(resourceModel.getObject());
	}

    }

}
