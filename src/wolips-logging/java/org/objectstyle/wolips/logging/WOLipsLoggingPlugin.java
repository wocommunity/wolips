/* ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0 
 *
 * Copyright (c) 2002 The ObjectStyle Group 
 * and individual authors of the software.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:  
 *       "This product includes software developed by the 
 *        ObjectStyle Group (http://objectstyle.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "ObjectStyle Group" and "Cayenne" 
 *    must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written 
 *    permission, please contact andrus@objectstyle.org.
 *
 * 5. Products derived from this software may not be called "ObjectStyle"
 *    nor may "ObjectStyle" appear in their names without prior written
 *    permission of the ObjectStyle Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE OBJECTSTYLE GROUP OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the ObjectStyle Group.  For more
 * information on the ObjectStyle Group, please see
 * <http://objectstyle.org/>.
 *
 */
package java.org.objectstyle.wolips.logging;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
/**
 * The main plugin class to be used in the desktop.
 * 
 * @author uli
 * @author markus
 */
public class WOLipsLoggingPlugin extends Plugin {
	public static Log log;
	//The plugin.
	private static WOLipsLoggingPlugin plugin;
	private static String PLUGIN_ID = "org.objectstyle.wolips.logging";
	/**
	 * Set this variable to true to get debug output
	 */
	public static final boolean debug = true;
	/**
	 * The constructor.
	 */
	//The constructur is very sensitive. Make sure that your stuff works.
	//If this cunstructor fails, the whole plugin will be disabled.
	public WOLipsLoggingPlugin(IPluginDescriptor descriptor) {
		super(descriptor);
		plugin = this;
		try {
		// set log factory
		System.setProperty(
			"org.apache.commons.logging.LogFactory",
			"org.objectstyle.wolips.logging.WOLipsLogFactory");
		// set own logger
		log = LogFactory.getLog(WOLipsLoggingPlugin.class);
		}
		catch(Exception exception) {
			System.out.println("Exception in WOLips constructor: " + exception.getMessage());
		}
	}
	/**
	 * Returns the shared instance.
	 */
	public static WOLipsLoggingPlugin getDefault() {
		if (plugin == null) {
			// ensure plugin instance is always available using id
			return new WOLipsLoggingPlugin(
				Platform
					.getPlugin(PLUGIN_ID)
					.getDescriptor());
		}
		return plugin;
	}
	/**
	 * Method baseURL.
	 * @return URL
	 */
	public static URL baseURL() {
		return WOLipsLoggingPlugin.getDefault().getDescriptor().getInstallURL();
	}
	/**
	 * Returns the PluginID.
	 */
	public static String getPluginId() {
		if (plugin != null) {
			return getDefault().getDescriptor().getUniqueIdentifier();
		} else
			return PLUGIN_ID;
	}
}
