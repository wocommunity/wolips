package tk.eclipse.plugin.jseditor.launch;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;

import tk.eclipse.plugin.htmleditor.HTMLPlugin;
import tk.eclipse.plugin.htmleditor.HTMLProjectParams;

/**
 * 
 * @author Naoki Takezoe
 */
public class JavaScriptLaunchShortcut implements ILaunchShortcut {

	public void launch(ISelection selection, String mode) {
		if(selection instanceof IStructuredSelection){
			Object element = ((IStructuredSelection)selection).getFirstElement();
			if(element != null && element instanceof IFile){
				try {
					IFile file = (IFile)element;
					ILaunchConfiguration config = getLaunchConfiguration(file);
					config.launch("run", new NullProgressMonitor());
				} catch(Exception ex){
					HTMLPlugin.logException(ex);
				}
			}
		}

	}

	public void launch(IEditorPart editor, String mode) {
		IEditorInput input = editor.getEditorInput();
		if(input instanceof IFileEditorInput){
			try {
				IFile file = ((IFileEditorInput)input).getFile();
				ILaunchConfiguration config = getLaunchConfiguration(file);
				config.launch("run", new NullProgressMonitor());
			} catch(Exception ex){
				HTMLPlugin.logException(ex);
			}
		}
	}
	
	private ILaunchConfiguration getLaunchConfiguration(IFile file) throws CoreException {
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		
		ILaunchConfiguration[] configs = manager.getLaunchConfigurations();
		String fullPath = file.getLocation().toFile().getAbsolutePath();
		
		for(int i=0;i<configs.length;i++){
			String value = configs[i].getAttribute(JavaScriptLaunchConstants.ATTR_JAVASCRIPT_FILE, "");
			if(value.equals(fullPath)){
				return configs[i];
			}
		}
		
		ILaunchConfigurationType type = manager.getLaunchConfigurationType(
				"tk.eclipse.plugin.jseditor.launch.JavaScriptLaunchConfigurationType");
		
		ILaunchConfigurationWorkingCopy wc = type.newInstance(null, 
				manager.generateUniqueLaunchConfigurationNameFrom(file.getName()));
		
		wc.setAttribute(JavaScriptLaunchConstants.ATTR_JAVASCRIPT_FILE, fullPath);
		
		try {
			HTMLProjectParams params = new HTMLProjectParams(file.getProject());
			String[] javaScripts = params.getJavaScripts();
			List includes = new ArrayList();
			for(int i=0;i<javaScripts.length;i++){
				includes.add(javaScripts[i]);
			}
			
			wc.setAttribute(JavaScriptLaunchConstants.ATTR_JAVASCRIPT_INCLUDES, includes);
		} catch(Exception ex){
			HTMLPlugin.logException(ex);
		}
		
		return wc.doSave();
	}

}
