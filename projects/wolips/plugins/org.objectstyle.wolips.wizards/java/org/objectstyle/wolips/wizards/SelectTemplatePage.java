/*
 * Created on 26.02.2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.objectstyle.wolips.wizards;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;
import org.objectstyle.wolips.templateengine.TemplateFolder;

/**
 * @author ulrich
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class SelectTemplatePage extends WizardPage {

	private TemplateFolder[] templateFolder;
	protected SelectTemplatePage(TemplateFolder[] templateFolder) {
		super("");
		this.templateFolder = templateFolder;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		// TODO Auto-generated method stub
		
	}
}
