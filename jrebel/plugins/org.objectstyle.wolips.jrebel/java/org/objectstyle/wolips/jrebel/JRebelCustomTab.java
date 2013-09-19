package org.objectstyle.wolips.jrebel;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.zeroturnaround.eclipse.launching.launchconfig.VMArgsDynArgsRebelLaunchConfig;
import org.zeroturnaround.eclipse.ui.tabs.JRebelTab;

public class JRebelCustomTab extends JRebelTab {

	
	private void initAdaptor() {
		if (adaptor == null) {
			adaptor = new VMArgsDynArgsRebelLaunchConfig();
		}
	}
	
	@Override
	public void initializeFrom(ILaunchConfiguration config) {
		initAdaptor();
		super.initializeFrom(config);
	}
	
	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy config) {
		initAdaptor();
		super.setDefaults(config);
	}
}
