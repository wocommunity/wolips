/*
 * Created on 15.02.2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package org.objectstyle.wolips.devstudio.prepare;

import java.net.URL;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.objectstyle.wolips.builder.RunAnt;
import org.objectstyle.wolips.plugin.IWOLipsPluginConstants;
import org.objectstyle.wolips.plugin.WOLipsPlugin;
/**
 * @author uli
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
public class PrepareDevStudio {
	private PrepareDevStudio() {
	}

	/**
	 * Method writePropertiesFileToUserHome.
	 */
	private static void buildDevStudio() throws Exception {
		URL relativeBuildFile = null;
		URL buildFile = null;
		IProgressMonitor monitor = null;
		try {
			relativeBuildFile =
				new URL(
					WOLipsPlugin.baseURL(),
					IWOLipsPluginConstants.build_user_home_properties);
			buildFile = Platform.asLocalURL(relativeBuildFile);
			monitor = new NullProgressMonitor();
			RunAnt runAnt = new RunAnt();
			runAnt.asAnt(buildFile.getFile().toString(), monitor, null);
		} finally {
			relativeBuildFile = null;
			buildFile = null;
			monitor = null;
		}
	}

}
