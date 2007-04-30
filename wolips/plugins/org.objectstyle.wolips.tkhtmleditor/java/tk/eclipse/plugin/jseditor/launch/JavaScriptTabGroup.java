package tk.eclipse.plugin.jseditor.launch;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;

/**
 * 
 * @author Naoki Takezoe
 */
public class JavaScriptTabGroup extends AbstractLaunchConfigurationTabGroup {

	public JavaScriptTabGroup() {
		super();
	}

	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		
		ILaunchConfigurationTab[] tabs = {
			new JavaScriptMainTab(),
			new CommonTab()	
		};
		
		setTabs(tabs);
	}

}
