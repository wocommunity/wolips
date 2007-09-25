package org.objectstyle.wolips.wizards.actions;

import org.eclipse.ui.INewWizard;
import org.objectstyle.wolips.wizards.D2WebServiceApplicationWizard;

public class OpenD2WebServiceApplicationWizard extends AbstractOpenWizardAction {

	protected INewWizard createWizard() {
		return new D2WebServiceApplicationWizard();
	}
}