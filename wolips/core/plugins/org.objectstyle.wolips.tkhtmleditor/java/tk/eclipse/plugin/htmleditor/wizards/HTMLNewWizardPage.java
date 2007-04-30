package tk.eclipse.plugin.htmleditor.wizards;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

import tk.eclipse.plugin.htmleditor.HTMLPlugin;
import tk.eclipse.plugin.htmleditor.HTMLUtil;

/**
 * The "New" wizard page allows setting the container for
 * the new file as well as the file name. The page
 * will only accept file name without the extension OR
 * with the extension that matches the expected one (html).
 */
public class HTMLNewWizardPage extends WizardNewFileCreationPage {
	
	private Text titleText;
	private Combo comboDocType;
//	private ISelection selection;
	
	private static DocType[] docTypes = {
			new DocType("",null,null),
			new DocType("HTML 4.01 Strict","-//W3C//DTD HTML 4.01//EN","http://www.w3.org/TR/html4/strict.dtd"),
			new DocType("HTML 4.01 Transitional","-//W3C//DTD HTML 4.01 Transitional//EN","http://www.w3.org/TR/html4/loose.dtd"),
			new DocType("HTML 4.01 Frameset","-//W3C//DTD HTML 4.01 Frameset//EN","http://www.w3.org/TR/html4/frameset.dtd")
	};
	
	public HTMLNewWizardPage(ISelection selection) {
		super("wizardPage",(IStructuredSelection)selection);
		setTitle(HTMLPlugin.getResourceString("HTMLNewWizardPage.Title"));
		setDescription(HTMLPlugin.getResourceString("HTMLNewWizardPage.Description"));
//		this.selection = selection;
	}

	public void createControl(Composite parent) {
		super.createControl(parent);
		this.setFileName("newfile.html");
		Composite container = new Composite((Composite)getControl(),SWT.NONE);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;
		layout.verticalSpacing = 9;
		container.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL|GridData.GRAB_HORIZONTAL));
		
		Label label = new Label(container, SWT.NULL);
		label.setText(HTMLPlugin.getResourceString("HTMLNewWizardPage.InputTitle"));
		titleText = new Text(container, SWT.BORDER | SWT.SINGLE);
		titleText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		label = new Label(container, SWT.NULL);
		label.setText(HTMLPlugin.getResourceString("HTMLNewWizardPage.InputDocType"));
		
		comboDocType = new Combo(container,SWT.READ_ONLY);
		for(int i=0;i<docTypes.length;i++){
			comboDocType.add(docTypes[i].label);
		}
	}
	
	protected InputStream getInitialContents() {
		// DOCTYPE
		int i = comboDocType.getSelectionIndex();
		if(i<0){
			i = 0;
		}
		DocType docType = docTypes[i];
		
		String projectName = getContainerFullPath().segment(0);
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		String charset = HTMLUtil.getProjectCharset(project);
//		try {
//			String projectName = getContainerFullPath().segment(0);
//			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
//			charset = project.getDefaultCharset();
//		} catch(CoreException ex){
//		}
		// Generate HTML
		StringBuffer sb = new StringBuffer();
		if(!docType.label.equals("") && docType.format!=null && !docType.format.equals("")){
			sb.append("<!DOCTYPE HTML PUBLIC \""+docType.format+"\"");
			if(docType.dtd!=null && !docType.dtd.equals("")){
				sb.append(" \"" + docType.dtd + "\"");
			}
			sb.append(">\n");
		}
		sb.append("<html>\n");
		sb.append("<head>\n");
		sb.append("<meta http-equiv=\"Content-Type\" content=\"text/html");
		if(charset!=null){
			sb.append("; charset=" + charset);
		}
		sb.append("\"/>\n");
		sb.append("<title>" + HTMLUtil.escapeHTML(titleText.getText()) + "</title>\n");
		sb.append("</head>\n");
		sb.append("<body>\n");
		sb.append("</body>\n");
		sb.append("</html>\n");		
		return new ByteArrayInputStream(sb.toString().getBytes());
	}
	
	private static class DocType {
		public String label;
		public String format;
		public String dtd;
		public DocType(String label,String format,String dtd){
			this.label  = label;
			this.format = format;
			this.dtd    = dtd;
		}
	}
}