package org.objectstyle.wolips.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;
import org.objectstyle.wolips.images.WOLipsPluginImages;

/**
 * @author mnolte
 *
 */
public abstract class WOProjectCreationWizard
	extends BasicNewProjectResourceWizard {

	protected IWorkbench workbench;

	/**
	 * Constructor for WOProjectCreationWizard.
	 */
	public WOProjectCreationWizard() {
		super();
	}

	/** (non-Javadoc)
	 * Method declared on INewWizard
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		super.init(workbench, selection);
		this.workbench = workbench;
		setDefaultPageImageDescriptor(
			WOLipsPluginImages.WOPROJECT_WIZARD_BANNER);
	}

}
