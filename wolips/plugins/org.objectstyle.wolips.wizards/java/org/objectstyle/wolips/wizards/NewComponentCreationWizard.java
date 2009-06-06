
package org.objectstyle.wolips.wizards;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jdt.internal.ui.wizards.NewElementWizard;
import org.eclipse.jdt.internal.ui.wizards.NewWizardMessages;

/**
 * @author ldeck
 */
public class NewComponentCreationWizard extends NewElementWizard {
	
	private boolean fOpenEditorOnFinish;
	private NewComponentCreationPage fPage;

	/**
	 * 
	 */
	public NewComponentCreationWizard() {
		this(null, true);
	}
	
	/**
	 * @param page
	 * @param openEditorOnFinish
	 */
	public NewComponentCreationWizard(NewComponentCreationPage page, boolean openEditorOnFinish) {
		setDefaultPageImageDescriptor(WizardsPlugin.WOCOMPONENT_CONTROLLER_WIZARD_BANNER());
		setDialogSettings(JavaPlugin.getDefault().getDialogSettings());
		setWindowTitle(Messages.getString("WOComponentCreationWizard.title"));
		this.fPage = page;
		this.fOpenEditorOnFinish = openEditorOnFinish;
	}
	
	/**
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	public void addPages() {
		super.addPages();
		if (this.fPage == null) {
			this.fPage = new NewComponentCreationPage();
			this.fPage.init(getSelection());
		}
		addPage(this.fPage);
	}
	
	/**
	 * @see org.eclipse.jdt.internal.ui.wizards.NewElementWizard#canRunForked()
	 */
	protected boolean canRunForked() {
		return !this.fPage.isEnclosingTypeSelected();
	}

	/**
	 * @see org.eclipse.jdt.internal.ui.wizards.NewElementWizard#finishPage(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected void finishPage(IProgressMonitor monitor) throws InterruptedException, CoreException {
		this.fPage.createType(monitor);
	}
	
	/**
	 * @see org.eclipse.jdt.internal.ui.wizards.NewElementWizard#getCreatedElement()
	 */
	@Override
	public IJavaElement getCreatedElement() {
		return this.fPage.getCreatedType();
	}

	/**
	 * @see org.eclipse.jdt.internal.ui.wizards.NewElementWizard#performFinish()
	 */
	public boolean performFinish() {
		warnAboutTypeCommentDeprecation();
		boolean result = super.performFinish();
		if (result) {
			IResource resource = this.fPage.getModifiedResource();
			if (resource != null) {
				selectAndReveal(resource);
				if (this.fOpenEditorOnFinish) {
					openResource((IFile)resource);
				}
			}
		}
		return result;
	}

}
