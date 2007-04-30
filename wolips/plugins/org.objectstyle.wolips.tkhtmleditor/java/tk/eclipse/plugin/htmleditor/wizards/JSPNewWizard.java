package tk.eclipse.plugin.htmleditor.wizards;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import tk.eclipse.plugin.htmleditor.HTMLPlugin;

public class JSPNewWizard extends Wizard implements INewWizard {
	
	private JSPNewWizardPage page;
	private ISelection selection;

	public JSPNewWizard() {
		super();
		setNeedsProgressMonitor(true);
		setWindowTitle(HTMLPlugin.getResourceString("JSPNewWizardPage.Title"));
	}
	
	public void addPages(){
		page = new JSPNewWizardPage(selection);
		addPage(page);
	}
	
	public boolean performFinish() {
		IFile file = page.createNewFile();
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

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}
}
