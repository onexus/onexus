/**
 *  Copyright 2011 Universitat Pompeu Fabra.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */
package org.onexus.ui.wizards;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.wizard.FinishButton;
import org.apache.wicket.extensions.wizard.IDefaultButtonProvider;
import org.apache.wicket.extensions.wizard.IWizard;
import org.apache.wicket.extensions.wizard.IWizardStep;
import org.apache.wicket.extensions.wizard.Wizard;
import org.apache.wicket.extensions.wizard.WizardButtonBar;
import org.apache.wicket.markup.html.form.Form;

public abstract class AbstractWizard extends Wizard {
    
    public AbstractWizard(String id) {
	super(id);
    }

    @Override
    public void onCancel() {
	setVisible(false);
    }

    @Override
    public void onFinish() {
	setVisible(false);
    }
    

    @Override
    protected Component newButtonBar(String id) {
	return new MyWizardButtonBar(id, this);
    }
    
    

    @Override
    protected Component newFeedbackPanel(String id) {
	return super.newFeedbackPanel(id).setOutputMarkupId(true);
    }

    @Override
    protected <E> Form<E> newForm(String id) {
	
	Form<E> form = super.newForm(id);
	form.setOutputMarkupId(true);
	
	return form;
	
    }



    /**
     * 
     * This is a hack, because we expect that the finish button is enabled only
     * if the last step is completed. By default, finish button is always
     * enabled on the last step.
     * 
     * @author Jordi Deu-Pons
     * 
     */
    public final static class MyWizardButtonBar extends WizardButtonBar implements IDefaultButtonProvider {

	public MyWizardButtonBar(String id, IWizard wizard) {
	    super(id, wizard);
	    replace(new FinishButton("finish", wizard) {

		@Override
		public boolean isEnabled() {
		    IWizardStep activeStep = getWizardModel().getActiveStep();
		    return ((activeStep != null) && getWizardModel().isLastStep(activeStep) && activeStep.isComplete());
		}

	    });
	}

    }

}
