package tk.eclipse.plugin.htmleditor;

import java.util.ArrayList;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.internal.ui.wizards.TypedElementSelectionValidator;
import org.eclipse.jdt.internal.ui.wizards.TypedViewerFilter;
import org.eclipse.jdt.internal.ui.wizards.buildpaths.FolderSelectionDialog;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;


/**
 * This is a project preference page.
 * 
 * @author Naoki Takezoe
 */
public class HTMLProjectPropertyPage extends PropertyPage {
	
	private Text textWebAppRoot;
	private TableItem checkValidateXML;
	private TableItem checkValidateHTML;
	private TableItem checkValidateJSP;
	private TableItem checkValidateDTD;
	private TableItem checkValidateJS;
	private TableItem checkTaskTag;
	private Button checkUseDTD;
	private Button checkRemoveMarkers;
	private HTMLProjectParams params;
	private Table table;

	public HTMLProjectPropertyPage() {
		super();
	}

	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		addControls(composite);
		
		try {
			params = new HTMLProjectParams(getProject());
			fillControls();
		} catch (Exception ex) {
			HTMLPlugin.logException(ex);
		}
		
		return composite;
	}
	
	private void addControls(Composite parent) {
		Composite composite = createDefaultComposite(parent);
		
		Composite panel = new Composite(composite, SWT.NULL);
		panel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		panel.setLayout(new GridLayout(3, false));
		
		Label labelWebAppRoot = new Label(panel, SWT.NONE);
		labelWebAppRoot.setText(HTMLPlugin.getResourceString("HTMLProjectPropertyPage.Root"));
		textWebAppRoot = new Text(panel, SWT.SINGLE | SWT.BORDER);
		textWebAppRoot.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Button button = new Button(panel, SWT.BUTTON1);
		button.setText(HTMLPlugin.getResourceString("Button.Browse"));
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				selectFolder();
			}
		});
		
		checkUseDTD = new Button(composite,SWT.CHECK);
		checkUseDTD.setText(HTMLPlugin.getResourceString("HTMLProjectPropertyPage.UseDTD"));
		
		// spacer
		new Label(composite, SWT.NULL);
		
		Group group = new Group(composite, SWT.NULL);
		group.setText(HTMLPlugin.getResourceString("HTMLProjectPropertyPage.Validation"));
		group.setLayoutData(new GridData(GridData.FILL_BOTH));
		group.setLayout(new GridLayout(1, false));
		
		table = new Table(group, SWT.CHECK|SWT.BORDER);
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		checkValidateHTML = new TableItem(table, SWT.NULL);
		checkValidateHTML.setText(HTMLPlugin.getResourceString("HTMLProjectPropertyPage.ValidateHTML"));
		
		checkValidateJSP = new TableItem(table,SWT.NULL);
		checkValidateJSP.setText(HTMLPlugin.getResourceString("HTMLProjectPropertyPage.ValidateJSP"));
		
		checkValidateXML = new TableItem(table,SWT.NULL);
		checkValidateXML.setText(HTMLPlugin.getResourceString("HTMLProjectPropertyPage.ValidateXML"));
		
		checkValidateDTD = new TableItem(table, SWT.NULL);
		checkValidateDTD.setText(HTMLPlugin.getResourceString("HTMLProjectPropertyPage.ValidateDTD"));
		
		checkValidateJS = new TableItem(table, SWT.NULL);
		checkValidateJS.setText(HTMLPlugin.getResourceString("HTMLProjectPropertyPage.ValidateJS"));
		
		checkTaskTag = new TableItem(table, SWT.NULL);
		checkTaskTag.setText(HTMLPlugin.getResourceString("HTMLProjectPropertyPage.DetectTaskTags"));
		
		checkRemoveMarkers = new Button(group, SWT.CHECK);
		checkRemoveMarkers.setText(HTMLPlugin.getResourceString("HTMLProjectPropertyPage.RemoveMarkers"));
	}
	
	private Composite createDefaultComposite(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout(1, false);
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		return composite;
	}
	
	/**
	 * Sets values of HTMLProjectParams to controls.
	 */
	private void fillControls() {
		textWebAppRoot.setText(params.getRoot());
		checkUseDTD.setSelection(params.getUseDTD());
		checkValidateHTML.setChecked(params.getValidateHTML());
		checkValidateJSP.setChecked(params.getValidateJSP());
		checkValidateXML.setChecked(params.getValidateXML());
		checkValidateDTD.setChecked(params.getValidateDTD());
		checkValidateJS.setChecked(params.getValidateJavaScript());
		checkRemoveMarkers.setSelection(params.getRemoveMarkers());
		checkTaskTag.setChecked(params.getDetectTaskTag());
	}
	
	protected void performDefaults() {
		params = new HTMLProjectParams();
		fillControls();
	}
	
	public boolean performOk() {
		// TODO input check?
		
		try {
			// save configuration
			params = new HTMLProjectParams(getProject());
			params.setRoot(textWebAppRoot.getText());
			params.setUseDTD(checkUseDTD.getSelection());
			params.setValidateHTML(checkValidateHTML.getChecked());
			params.setValidateJSP(checkValidateJSP.getChecked());
			params.setValidateXML(checkValidateXML.getChecked());
			params.setValidateDTD(checkValidateDTD.getChecked());
			params.setValidateJavaScript(checkValidateJS.getChecked());
			params.setDetectTaskTag(checkTaskTag.getChecked());
			params.setRemoveMarkers(checkRemoveMarkers.getSelection());
			
			params.save(getProject());
		} catch (Exception ex) {
			HTMLPlugin.logException(ex);
			return false;
		}
		return true;
	}
	
	private void selectFolder() {
		try {
			IProject currProject = getProject();
			IWorkspaceRoot wsroot = ResourcesPlugin.getWorkspace().getRoot();
			IResource init = null;
			if (params.getRoot() != null) {
				init = wsroot.findMember(currProject.getName() + params.getRoot());
			}
			Class[] acceptedClasses = new Class[] { IProject.class, IFolder.class };
			ISelectionStatusValidator validator = new TypedElementSelectionValidator(acceptedClasses, false);
			IProject[] allProjects = wsroot.getProjects();
			ArrayList rejectedElements = new ArrayList(allProjects.length);
			for (int i = 0; i < allProjects.length; i++) {
				if (!allProjects[i].equals(currProject)) {
					rejectedElements.add(allProjects[i]);
				}
			}
			ViewerFilter filter = new TypedViewerFilter(acceptedClasses, rejectedElements.toArray());
			
			FolderSelectionDialog dialog = new FolderSelectionDialog(
					getShell(),
					new WorkbenchLabelProvider(), 
					new WorkbenchContentProvider());
			
			dialog.setTitle(HTMLPlugin.getResourceString("HTMLProjectPropertyPage.WebRoot"));
			dialog.setMessage(HTMLPlugin.getResourceString("HTMLProjectPropertyPage.WebRoot"));
			
			dialog.setInput(wsroot);
			dialog.setValidator(validator);
			dialog.addFilter(filter);
			dialog.setInitialSelection(init);
			if (dialog.open() == Dialog.OK) {
				textWebAppRoot.setText(getFolderName(dialog.getFirstResult()));
			}
			
		} catch (Throwable t) {
			HTMLPlugin.openAlertDialog(t.toString());
		}
	}
	
	private IProject getProject(){
		return (IProject)getElement(); //.getAdapter(IProject.class);
	}
	
	private String getFolderName(Object result) throws CoreException {
		if (result instanceof IFolder) {
			IFolder folder = (IFolder) result;
			String folderName = folder.getLocation().toString();
			String projectPath = getProject().getLocation().toString();
			if (folderName.length() <= projectPath.length()) {
				return folderName;
			} else {
				return folderName.substring(projectPath.length());
			}
		}
		return "/";
	}

}
