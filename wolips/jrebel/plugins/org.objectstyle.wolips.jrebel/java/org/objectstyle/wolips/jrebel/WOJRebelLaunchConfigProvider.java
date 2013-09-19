package org.objectstyle.wolips.jrebel;

import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.zeroturnaround.eclipse.launching.launchconfig.IRebelLaunchConfig;
import org.zeroturnaround.eclipse.launching.launchconfig.IRebelLaunchConfigProvider;
import org.zeroturnaround.eclipse.launching.launchconfig.RebelLaunchConfigProviders;
import org.zeroturnaround.eclipse.launching.launchconfig.VMArgsDynArgsRebelLaunchConfig;

public class WOJRebelLaunchConfigProvider implements IRebelLaunchConfigProvider {
	public IRebelLaunchConfig createRebelLaunchConfig(ILaunchConfiguration config) {
		if (RebelLaunchConfigProviders.typeIdMatches(config, "org.objectstyle.wolips.launching.WOLocalJavaApplication")) {
			return new VMArgsDynArgsRebelLaunchConfig();
		}
		return null;
	}

	public boolean shouldDisableJRebel(ILaunch arg0) {
		return false;
	}

}
