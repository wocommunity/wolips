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
package org.objectstyle.wolips;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

import org.apache.tools.ant.types.Environment;
import org.eclipse.core.internal.boot.URLContentFilter;
import org.eclipse.core.internal.plugins.PluginClassLoader;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.objectstyle.wolips.ide.WOClasspathUpdater;
import org.objectstyle.wolips.project.PBProjectUpdater;

/**
 * The main plugin class to be used in the desktop.
 */
public class WOLipsPlugin extends AbstractUIPlugin {
	//The shared instance.
	private static WOLipsPlugin plugin;
	//Resource bundle.
	private ResourceBundle resourceBundle;
	private Hashtable projectUpdater;

	private boolean classesLoaded = false;
	private static String NEXT_ROOT = "NEXT_ROOT";
	/**
	 * The constructor.
	 */
	public WOLipsPlugin(IPluginDescriptor descriptor) {
		super(descriptor);
		plugin = this;
		try {
			resourceBundle= ResourceBundle.getBundle("org.objectstyle.woproject.wolips.WOLipsPluginResources");
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
		this.loadFoundationClasses();
	}

	private void loadFoundationClasses() {
		ClassLoader aClassLoader = this.getClass().getClassLoader();
		URLContentFilter[] theURLContentFilter = new URLContentFilter[1];
		theURLContentFilter[0] = new URLContentFilter(true);
		URL[] theUrls = new URL[1];
		try {
			String aPath = null;
			Properties aEnv = WOLipsPlugin.getEnvVars();
			if(aEnv.containsKey(WOLipsPlugin.NEXT_ROOT)) aPath = aEnv.getProperty(WOLipsPlugin.NEXT_ROOT); 
			System.out.println("aPath" + aPath);
			if(aPath == null) aPath = "/System";
			theUrls[0] = new URL("file://" + aPath + "/Library/Frameworks/JavaFoundation.framework/Resources/Java/javafoundation.jar");
			((PluginClassLoader)aClassLoader).addURLs(theUrls, theURLContentFilter, null, null);
		}
		catch (Exception anException) {
			System.out.println("Error setting up ClassLoader for javafoundation: " + anException.getMessage() + "ex: " + anException);
		}		
	}
	/**
	 * Returns the shared instance.
	 */
	public static WOLipsPlugin getDefault() {
		return plugin;
	}

	public void startup() throws CoreException {
		super.startup();
		WOClasspathUpdater.update();
	}
	
	/**
	 * Returns the workspace instance.
	 */
	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

	/**
	 * Returns the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle= WOLipsPlugin.getDefault().getResourceBundle();
		try {
			return bundle.getString(key);
		} catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}
	
	public ImageDescriptor getImageDescriptor(String name) {
		try {
			URL url= new URL(getDescriptor().getInstallURL(), name);
			return ImageDescriptor.createFromURL(url);
		} catch (MalformedURLException e) {
			return ImageDescriptor.getMissingImageDescriptor();
		}
	}	
	
	public static String getPluginId() {
		return getDefault().getDescriptor().getUniqueIdentifier();
	}	


	public static void log(IStatus status) {
		getDefault().getLog().log(status);
	}


	public static void log(String message) {
		log(new Status(IStatus.ERROR, getPluginId(), IStatus.ERROR, message, null));
	}


	public static void log(Throwable e) {
		log(new Status(IStatus.ERROR, getPluginId(), IStatus.ERROR, "Internal Error", e)); //$NON-NLS-1$
	}

	public PBProjectUpdater getProjectUpdater(IProject aProject) {
		if(projectUpdater == null) projectUpdater = new Hashtable();
		String aProjectName = aProject.getName();
		PBProjectUpdater aProjectUpdater = (PBProjectUpdater)projectUpdater.get(aProjectName);
		if(aProjectUpdater == null) {
			aProjectUpdater = new PBProjectUpdater(aProject);
			projectUpdater.put(aProjectName, aProjectUpdater);
		}
		return aProjectUpdater;	
	}

	public static Properties getEnvVars() throws Exception {
  		Process p = null;
  		Properties envVars = new Properties();
 		Runtime r = Runtime.getRuntime();
  		String OS = System.getProperty("os.name").toLowerCase();
  		// System.out.println(OS);
  		if (OS.indexOf("windows 9") > -1) {
    		p = r.exec( "command.com /c set" );
    	}
  		else if ( (OS.indexOf("nt") > -1) || (OS.indexOf("windows 2000") > -1) ) {
    		p = r.exec( "cmd.exe /c set" );
    	}
  		else {  
    		// our last hope, we assume Unix (thanks to H. Ware for the fix)
    		p = r.exec( "env" );
    	}
  		BufferedReader br = new BufferedReader( new InputStreamReader( p.getInputStream() ) );
  		String line;
  		while( (line = br.readLine()) != null ) {
  			int idx = line.indexOf( '=' );
   			String key = line.substring( 0, idx );
  			String value = line.substring( idx+1 );
   			envVars.setProperty( key, value );
   			// System.out.println( key + " = " + value );
   		}
  	return envVars;
  }
}
