/*
 * Created on 20.06.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.objectstyle.wolips.core.plugin;

import org.eclipse.ant.core.AntCorePlugin;
import org.objectstyle.wolips.logging.WOLipsLog;

/**
 * @author uli
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class CheckAntCorePatch extends AWOLips {

	private String informUserString =
		"WOLips\n\n"
			+ "To avoid frequent crashes don't forget to install the patched org.eclipse.core.ant plugin.\n\n"
			+ "It's available as a separate download and from the optional folder in the download.";

	public void check() {
		String string =
			AntCorePlugin.getPlugin().getDescriptor().getProviderName();
		WOLipsLog.log("AntCorePlugin.getPlugin().getDescriptor().getProviderName(); " + string);
		if (!string.endsWith("objectstyle.org"))
			WOLipsPlugin.informUser(this.getShell(), informUserString);
	}
}
