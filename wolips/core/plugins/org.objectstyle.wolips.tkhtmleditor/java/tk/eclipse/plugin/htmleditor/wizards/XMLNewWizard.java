package tk.eclipse.plugin.htmleditor.wizards;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import tk.eclipse.plugin.htmleditor.HTMLPlugin;

/**
 * XML creation wizard. Users could create new XML file by the following sequence:
 * <ol>
 *   <li>input container and file name.</li>
 *   <li>input PublicID and SystemID of the schema</li>
 *   <li>select root tag when schema is specified</li>
 * </ol>
 * <p>
 *   If local DTD is specified in the DTD preference page,
 *   they are displayed at the DTD selection combo box as proposals.
 * </p>
 * @author Naoki Takezoe
 */
public class XMLNewWizard extends Wizard implements INewWizard {
	
	private IStructuredSelection selection;
	private XMLNewWizardPage page1;
	private XMLDTDWizardPage page2;
	
	public XMLNewWizard() {
		super();
		setNeedsProgressMonitor(true);
		setWindowTitle(HTMLPlugin.getResourceString("XMLNewWizardPage.Title"));
	}
	
	public void addPages() {
		page1 = new XMLNewWizardPage("page1",selection);
		page1.setFileName("newfile.xml");
		page1.setTitle(HTMLPlugin.getResourceString("XMLNewWizardPage.Title"));
		page1.setDescription(HTMLPlugin.getResourceString("XMLNewWizardPage.Description"));
		
		page2 = new XMLDTDWizardPage("page2", page1);
		addPage(page1);
		addPage(page2);
	}
	
	public boolean performFinish() {
		page1.setSchemaInfo(page2.getUseDTD(),page2.getPublicID(),page2.getSystemID(),
				page2.getUseXSD(),page2.getSchemaURI(),page2.getDocumentRoot());
		
		IFile file = page1.createNewFile();
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
	
//	private InputStream getInitialContents(){
//		return null;
//	}
}
