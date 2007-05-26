package tk.eclipse.plugin.jseditor.launch;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import tk.eclipse.plugin.htmleditor.HTMLPlugin;

/**
 * 
 * @author Naoki Takezoe
 */
public class JavaScriptMainTab extends AbstractLaunchConfigurationTab {
	
	private JavaScriptLibraryTable tableViewer;
	private Text file;
	
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout(1, false));
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		createFileGroup(composite);
		createIncludeGroup(composite);
		
		setControl(composite);
	}
	
	private void createFileGroup(Composite composite){
		Group fileGroup = new Group(composite, SWT.NULL);
		fileGroup.setText(HTMLPlugin.getResourceString("Launcher.JavaScript.Label.ScriptFile"));
		fileGroup.setLayout(new GridLayout(2, false));
		fileGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		file = new Text(fileGroup, SWT.BORDER);
		file.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		file.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				updateLaunchConfigurationDialog();
			}
		});
		
		Button browseFile = new Button(fileGroup, SWT.PUSH);
		browseFile.setText(HTMLPlugin.getResourceString("Button.Browse"));
		browseFile.addSelectionListener(new SelectionAdapter(){
			@Override
      public void widgetSelected(SelectionEvent evt){
				String text = browseJavaScriptFile();
				if(text!=null){
					file.setText(text);
					updateLaunchConfigurationDialog();
				}
			}
		});
	}
	
	private void createIncludeGroup(Composite composite){
		Group includeGroup = new Group(composite, SWT.NULL);
		includeGroup.setText(HTMLPlugin.getResourceString("Launcher.JavaScript.Label.IncludeFiles"));
		includeGroup.setLayout(new GridLayout(2, false));
		includeGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		tableViewer = new JavaScriptLibraryTable(includeGroup){
			@Override
      protected void modelChanged(){
				updateLaunchConfigurationDialog();
			}
		};
		
//		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
//		gd.heightHint = 80;
//		includeList.setLayoutData(gd);
//		
//		Composite includeButtons = new Composite(includeGroup, SWT.NULL);
//		GridLayout layout = new GridLayout(1, false);
//		layout.marginHeight = 0;
//		layout.marginWidth = 0;
//		includeButtons.setLayout(layout);
//		includeButtons.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
//		
//		Button addInclude = new Button(includeButtons, SWT.PUSH);
//		addInclude.setText(HTMLPlugin.getResourceString("Button.Add"));
//		addInclude.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//		addInclude.addSelectionListener(new SelectionAdapter(){
//			public void widgetSelected(SelectionEvent evt){
//				FileDialog dialog = new FileDialog(getShell(), SWT.OPEN|SWT.MULTI);
//				dialog.setFilterExtensions(new String[]{"*.js"});
//				String result = dialog.open();
//				if(result!=null){
//					String dir = dialog.getFilterPath();
//					String[] fileNames = dialog.getFileNames();
//					for(int i=0;i<fileNames.length;i++){
//						includeList.add(new File(dir, fileNames[i]).getAbsolutePath());
//					}
//					updateLaunchConfigurationDialog();
//				}
//			}
//		});
//		
//		Button removeInclude = new Button(includeButtons, SWT.PUSH);
//		removeInclude.setText(HTMLPlugin.getResourceString("Button.Remove"));
//		removeInclude.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//		removeInclude.addSelectionListener(new SelectionAdapter(){
//			public void widgetSelected(SelectionEvent evt){
//				includeList.remove(includeList.getSelectionIndices());
//				updateLaunchConfigurationDialog();
//			}
//		});
	}
	
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
	}

	@SuppressWarnings("unchecked")
  public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			String scriptFile = configuration.getAttribute(
					JavaScriptLaunchConstants.ATTR_JAVASCRIPT_FILE, "");
			file.setText(scriptFile);
			
			java.util.List includes = configuration.getAttribute(
					JavaScriptLaunchConstants.ATTR_JAVASCRIPT_INCLUDES, Collections.EMPTY_LIST);
			List<Object> tableModel = tableViewer.getModel();
			tableModel.clear();
			IWorkspaceRoot wsroot = ResourcesPlugin.getWorkspace().getRoot();
			for(int i=0;i<includes.size();i++){
				String path = (String)includes.get(i);
				if(path.startsWith(JavaScriptLibraryTable.PREFIX)){
					IResource resource = wsroot.findMember(path.substring(JavaScriptLibraryTable.PREFIX.length()));
					if(resource!=null && resource instanceof IFile && resource.exists()){
						tableModel.add(resource);
					}
				} else {
					tableModel.add(new File(path));
				}
			}
			tableViewer.refresh();
		} catch(Exception ex){
			HTMLPlugin.logException(ex);
		}
	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(JavaScriptLaunchConstants.ATTR_JAVASCRIPT_FILE, file.getText());
		
		List tableModel = tableViewer.getModel();
		List<String> includeFiles = new ArrayList<String>();
		for(int i=0;i<tableModel.size();i++){
			Object obj = tableModel.get(i);
			if(obj instanceof File){
				includeFiles.add(((File)obj).getAbsolutePath());
			} else if(obj instanceof IFile){
				includeFiles.add(JavaScriptLibraryTable.PREFIX + ((IFile)obj).getFullPath().toString());
			}
		}
		configuration.setAttribute(JavaScriptLaunchConstants.ATTR_JAVASCRIPT_INCLUDES, includeFiles);
	}

	public String getName() {
		return HTMLPlugin.getResourceString("Launcher.JavaScript.Tabs.Main");
	}

	@Override
  public Image getImage() {
		return HTMLPlugin.getDefault().getImageRegistry().get(HTMLPlugin.ICON_JAVASCRIPT);
	}
	
	/**
	 * Browse the JavaScript from the workspace and
	 * returns the absolute path of selected JavaScript file.
	 * 
	 * @return the absolute path of selected JavaScript file.
	 */
	private static String browseJavaScriptFile() {
		ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				new WorkbenchLabelProvider(), new WorkbenchContentProvider());
		dialog.setInput(ResourcesPlugin.getWorkspace());
		dialog.addFilter(new ViewerFilter(){
	    	@Override
        public boolean select(Viewer viewer, Object parentElement, Object element){
				if(element instanceof IProject || element instanceof IFolder){
					return true;
				}
				if(element instanceof IFile){
					if(((IFile)element).getName().endsWith(".js")){
						return true;
					}
				}
				return false;
			}
		});
		dialog.setAllowMultiple(false);
		dialog.setTitle(HTMLPlugin.getResourceString("Launcher.JavaScript.Dialog.SelectFile"));
		
		if (dialog.open() == Dialog.OK) {
			IFile file = (IFile) dialog.getFirstResult();
			return file.getLocation().toString();
		}
		
		return null;
	}
	
}
