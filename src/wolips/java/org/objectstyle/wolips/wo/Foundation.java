package org.objectstyle.wolips.wo;

import java.net.URL;

import org.eclipse.core.internal.boot.URLContentFilter;
import org.eclipse.core.internal.plugins.PluginClassLoader;
import org.objectstyle.wolips.io.WOLipsLog;
import org.objectstyle.wolips.plugin.WOLipsPlugin;
import org.objectstyle.woproject.env.Environment;


/**
 * @author uli
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class Foundation {

	/**
	 * Loads the foundation classes.
	 */
	public static void loadFoundationClasses() {
		ClassLoader aClassLoader = WOLipsPlugin.getDefault().getClass().getClassLoader();
		URLContentFilter[] theURLContentFilter = new URLContentFilter[1];
		theURLContentFilter[0] = new URLContentFilter(true);
		URL[] theUrls = new URL[1];
		try {
			theUrls[0] = new URL(Environment.foundationJarPath());
			((PluginClassLoader) aClassLoader).addURLs(
				theUrls,
				theURLContentFilter,
				null,
				null);
		} catch (Exception anException) {
			WOLipsLog.log(anException);
		}
		finally {
			aClassLoader = null;
			theURLContentFilter = null;
			theUrls = null;
		}
	}
}
