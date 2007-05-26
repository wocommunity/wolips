package tk.eclipse.plugin.htmleditor.wizards;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import tk.eclipse.plugin.htmleditor.HTMLPlugin;

/**
 * This is a sample new wizard. Its role is to create a new file 
 * resource in the provided container. If the container resource
 * (a folder or a project) is selected in the workspace 
 * when the wizard is opened, it will accept it as the target
 * container. The wizard creates one file with the extension
 * "html". If a sample multi-page editor (also available
 * as a template) is registered for the same extension, it will
 * be able to open it.
 */
public class HTMLNewWizard extends Wizard implements INewWizard {
	
	private HTMLNewWizardPage _page;
	private ISelection _selection;

	/**
	 * Constructor for HTMLNewWizard.
	 */
	public HTMLNewWizard() {
		super();
		setNeedsProgressMonitor(true);
		setWindowTitle(HTMLPlugin.getResourceString("HTMLNewWizardPage.Title"));
	}
	
	/**
	 * Adding the page to the wizard.
	 */

	@Override
  public void addPages() {
		_page = new HTMLNewWizardPage(_selection);
		addPage(_page);
	}

	/**
	 * This method is called when 'Finish' button is pressed in
	 * the wizard. We will create an operation and run it
	 * using wizard as execution context.
	 */
	@Override
  public boolean performFinish() {
		IFile file = _page.createNewFile();
		if(file==null){
			return false;
		}
		try {
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			IDE.openEditor(page, file, true);
		} catch(PartInitException ex){
			HTMLPlugin.logException(ex);
			return false;
		}
		return true;
	}

	/**
	 * We will accept the selection in the workbench to see if
	 * we can initialize from it.
	 * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this._selection = selection;
	}
}