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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.internal.boot.URLContentFilter;
import org.eclipse.core.internal.plugins.PluginClassLoader;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.ElementChangedEvent;
import org.eclipse.jdt.core.IElementChangedListener;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.objectstyle.wolips.env.Environment;
import org.objectstyle.wolips.ide.WOClasspathUpdater;
import org.objectstyle.wolips.io.FileStringScanner;
import org.objectstyle.wolips.listener.JavaElementChangeListener;
import org.objectstyle.wolips.listener.ResourceChangeListener;
import org.objectstyle.wolips.project.PBProjectUpdater;

/**
 * The main plugin class to be used in the desktop.
 * 
 * @author uli
 */
public class WOLipsPlugin extends AbstractUIPlugin implements IStartup {
	private static WOLipsPlugin plugin;
	private Hashtable projectUpdater;
	private static IResourceChangeListener resourceChangeListener;
	private static IElementChangedListener javaElementChangeListener;
	
	//Resource bundle.
	private static DocumentBuilder documentBuilder;
	/**
	 * Set this variable to true to get debug output
	 */
	public static final boolean debug = false;
	
	public static final String ID_ELEMENT_CREATION_ACTION_SET= "org.objectstyle.wolips.ElementCreationActionSet";

	/**
	 * The constructor.
	 */
	public WOLipsPlugin(IPluginDescriptor descriptor) {
		super(descriptor);
		plugin = this;
		this.loadFoundationClasses();
	}
	
	/**
	 * Adds listeners for resource and java classpath changes to keep
	 * webobjects project file synchronized.
	 * <br>
	 * @see org.eclipse.ui.IStartup#earlyStartup()
	 */
	public void earlyStartup() {

		// add resource change listener to update project file on resource changes
		resourceChangeListener = new ResourceChangeListener();
		getWorkspace().addResourceChangeListener(
			resourceChangeListener,
			IResourceChangeEvent.PRE_AUTO_BUILD);

		// add element change listener to update project file on classpath changes
		javaElementChangeListener = new JavaElementChangeListener();
		JavaCore.addElementChangedListener(
			javaElementChangeListener,
			ElementChangedEvent.PRE_AUTO_BUILD);
	}
	
	private void loadFoundationClasses() {
		ClassLoader aClassLoader = this.getClass().getClassLoader();
		URLContentFilter[] theURLContentFilter = new URLContentFilter[1];
		theURLContentFilter[0] = new URLContentFilter(true);
		URL[] theUrls = new URL[1];
		try {
			theUrls[0] = new URL(Environment.foundationJarPath());
			((PluginClassLoader)aClassLoader).addURLs(theUrls, theURLContentFilter, null, null);
		}
		catch (Exception anException) {
			WOLipsPlugin.log(anException);
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
		Hashtable options = JavaCore.getOptions();
	}
	
	/**
	 * Returns the workspace instance.
	 */
	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

	/**
	 * Returns an ImageDescriptor.
	 */
	public ImageDescriptor getImageDescriptor(String name) {
		try {
			URL url= new URL(getDescriptor().getInstallURL(), name);
			return ImageDescriptor.createFromURL(url);
		} catch (MalformedURLException e) {
			return ImageDescriptor.getMissingImageDescriptor();
		}
	}	
	
	/**
	 * Returns the PluginID.
	 */
	public static String getPluginId() {
		return getDefault().getDescriptor().getUniqueIdentifier();
	}	
	
	/**
	 * Prints an IStatus.
	 */
	public static void log(IStatus status) {
		getDefault().getLog().log(status);
	}

	/**
	 * Prints a message.
	 */
	public static void log(String message) {
		log(new Status(IStatus.ERROR, getPluginId(), IStatus.ERROR, message, null));
	}

	/**
	 * Prints a Throwable.
	 */
	public static void log(Throwable e) {
		log(new Status(IStatus.ERROR, getPluginId(), IStatus.ERROR, "Internal Error", e)); //$NON-NLS-1$
	}
	
	/**
	 * If WOLips.debug is true this method prints a String to the console.
	 */
	public static void debug(String aString) {
		if(WOLipsPlugin.debug) System.out.println(aString);
	}
	
	/**
	 * If WOLips.debug is true this method prints an Exception to the console.
	 */
	public static void debug(Throwable aThrowable) {
		if(WOLipsPlugin.debug);
		System.out.println("Exception: " + aThrowable);
	}
	
	
	/**
	 * Returns a PBProjectUpdater for an given IProject. Never returns null;
	 */
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

	/**
	 * @return Returns the active page.
	 */
	public IWorkbenchPage getActivePage() {
    	return getActiveWorkbenchWindow().getActivePage();
  	}
  	
  	/**
  	 * @return Returns the active workbench shell.
  	 */
  	public Shell getActiveWorkbenchShell() {
    	return getActiveWorkbenchWindow().getShell();
  	}

	/**
	 * @return Returns the the active workbench window.
	 */
  	public IWorkbenchWindow getActiveWorkbenchWindow() {
    	return getWorkbench().getActiveWorkbenchWindow();
  	}

	/**
	 * @return Returns the active editor java input.
	 */
	public static IJavaElement getActiveEditorJavaInput() {

    IWorkbenchPage page = getDefault().getActivePage();

    if (page != null) {
      IEditorPart part = page.getActiveEditor();
      if (part != null) {
        IEditorInput editorInput = part.getEditorInput();
        if (editorInput != null) {
          IJavaElement result = (IJavaElement) editorInput.getAdapter(IJavaElement.class);
          if (result == null) {
            IResource nonjava = (IResource) editorInput.getAdapter(IResource.class);
            if (nonjava != null) {
              IContainer parent = nonjava.getParent();
              while (parent != null) {
                result = (IJavaElement) parent.getAdapter(IJavaElement.class);
                if (result != null) {
                  break;
                }
                parent = parent.getParent();
              }
            }
          }
          return result;
        }

      }
    }
    return null;
  }

	public static URL baseURL() {
		return WOLipsPlugin.getDefault().getDescriptor().getInstallURL();
	}
	
	public static void handleException(Shell shell, Throwable target, String message) {
		WOLipsPlugin.debug(target);
		String title = "Error";
		if (message == null) {
			message = target.getMessage();
		}
		if (target instanceof CoreException) {
			IStatus status = ((CoreException) target).getStatus();
			ErrorDialog.openError(shell, title, message, status);
			WOLipsPlugin.log(status);
		} else {
			MessageDialog.openError(shell, title, target.getMessage());
			WOLipsPlugin.log(target);
		}
	}
	
	public static DocumentBuilder documentBuilder() {
		if (documentBuilder == null) {
			try {
				String jaxpPropertyName =
					"javax.xml.parsers.DocumentBuilderFactory";
				if (System.getProperty(jaxpPropertyName) == null) {
					String apacheXercesPropertyValue =
						"org.apache.xerces.jaxp.DocumentBuilderFactoryImpl";
					System.setProperty(
						jaxpPropertyName,
						apacheXercesPropertyValue);
				}

				DocumentBuilderFactory builderFactory =
					DocumentBuilderFactory.newInstance();
				documentBuilder = builderFactory.newDocumentBuilder();

			} catch (ParserConfigurationException pce) {
				WOLipsPlugin.log(pce);
				documentBuilder = null;
			} catch (FactoryConfigurationError fce) {
				WOLipsPlugin.log(fce);
				documentBuilder = null;
			}

		}
		return documentBuilder;
	}
	
	public static String getJavaBuildFilter() {
		try {
			URL aStarterURL = WOLipsPlugin.getDefault().getDescriptor().getInstallURL();
			URL aURL = new URL(aStarterURL, "JavaBuildFilter.txt");
			String aJavaBuildFilterFile = Platform.asLocalURL(aURL).getFile();
			String contents = FileStringScanner.stringFromFile(new File(aJavaBuildFilterFile));
			WOLipsPlugin.debug("JavaBuildFilter.txt: " + contents);
			return contents;
		}
		catch (Exception anException) {
			WOLipsPlugin.log(anException);
		}
		return "";
	}
	
	public static synchronized ArrayList arrayListFromCSV(String csvString) {
		if (csvString == null || csvString.length() == 0) {
			return new ArrayList();
		}
		StringTokenizer valueTokenizer = new StringTokenizer(csvString, ",");
		ArrayList resultList = new ArrayList(valueTokenizer.countTokens());
		while (valueTokenizer.hasMoreElements()) {
			resultList.add(valueTokenizer.nextElement());
		}
		return resultList;
	}
	
	public static String getId() {
		return getDefault().getDescriptor().getUniqueIdentifier();
	}
}
