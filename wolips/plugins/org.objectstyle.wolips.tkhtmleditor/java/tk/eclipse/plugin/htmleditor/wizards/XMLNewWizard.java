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
	
	private IStructuredSelection _selection;
	private XMLNewWizardPage _page1;
	private XMLDTDWizardPage _page2;
	
	public XMLNewWizard() {
		super();
		setNeedsProgressMonitor(true);
		setWindowTitle(HTMLPlugin.getResourceString("XMLNewWizardPage.Title"));
	}
	
	@Override
  public void addPages() {
		_page1 = new XMLNewWizardPage("page1",_selection);
		_page1.setFileName("newfile.xml");
		_page1.setTitle(HTMLPlugin.getResourceString("XMLNewWizardPage.Title"));
		_page1.setDescription(HTMLPlugin.getResourceString("XMLNewWizardPage.Description"));
		
		_page2 = new XMLDTDWizardPage("page2", _page1);
		addPage(_page1);
		addPage(_page2);
	}
	
	@Override
  public boolean performFinish() {
		_page1.setSchemaInfo(_page2.getUseDTD(),_page2.getPublicID(),_page2.getSystemID(),
				_page2.getUseXSD(),_page2.getSchemaURI(),_page2.getDocumentRoot());
		
		IFile file = _page1.createNewFile();
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
		this._selection = selection;
	}
	
//	private InputStream getInitialContents(){
//		return null;
//	}
}
