package tk.eclipse.plugin.htmleditor;

import java.io.File;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.PropertyPage;

import tk.eclipse.plugin.jseditor.launch.JavaScriptLibraryTable;

/**
 * 
 * @author Naoki Takezoe
 */
public class JavaScriptPropertyPage extends PropertyPage {
	
	private HTMLProjectParams params;
	private JavaScriptLibraryTable tableViewer;
	
	public JavaScriptPropertyPage(){
		super();
		setDescription(HTMLPlugin.getResourceString("JavaScriptPropertyPage.Description"));
	}
	
	protected Control createContents(Composite parent) {
		tableViewer = new JavaScriptLibraryTable(parent);
		try {
			params = new HTMLProjectParams(getProject());
		} catch (Exception ex) {
			HTMLPlugin.logException(ex);
		}
		fillControls();
		return tableViewer.getControl();
	}
	
	private void fillControls(){
		List tableModel = tableViewer.getModel();
		tableModel.clear();
		String[] javaScripts = params.getJavaScripts();
		IWorkspaceRoot wsroot = ResourcesPlugin.getWorkspace().getRoot();
		
		for(int i=0;i<javaScripts.length;i++){
			if(javaScripts[i].startsWith(JavaScriptLibraryTable.PREFIX)){
				IResource resource = wsroot.findMember(javaScripts[i].substring(JavaScriptLibraryTable.PREFIX.length()));
				if(resource!=null && resource instanceof IFile && resource.exists()){
					tableModel.add((IFile)resource);
				}
			} else {
				tableModel.add(new File(javaScripts[i]));
			}
		}
		tableViewer.refresh();
	}
	
	protected void performDefaults() {
		params = new HTMLProjectParams();
		fillControls();
	}
	
	public boolean performOk() {
		// save configuration
		try {
			params = new HTMLProjectParams(getProject());
			List tableModel = tableViewer.getModel();
	
			String[] javaScripts = new String[tableModel.size()];
			for(int i=0;i<tableModel.size();i++){
				Object obj = tableModel.get(i);
				if(obj instanceof File){
					javaScripts[i] = ((File)obj).getAbsolutePath();
				} else if(obj instanceof IFile){
					javaScripts[i] = JavaScriptLibraryTable.PREFIX + ((IFile)obj).getFullPath().toString();
				}
			}
			params.setJavaScripts(javaScripts);
			
			params.save(getProject());
			
		} catch (Exception ex) {
			HTMLPlugin.logException(ex);
			return false;
		}
		return true;
	}
	
	private IProject getProject(){
		return (IProject)getElement();
	}
}
