/* ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0 
 *
 * Copyright (c) 2004 The ObjectStyle Group 
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

package org.objectstyle.wolips.templateengine;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.operation.IRunnableWithProgress;

/**
 * @author ulrich
 */
public abstract class AbstractEngine implements IRunnableWithProgress {
	private String projectName;

	private VelocityContext context = null;

	private List<TemplateDefinition> templates = null;

	private VelocityEngine velocityEngine = null;

	/**
	 * @throws Exception
	 */
	public void init() throws Exception {
		/*
		 * create a new instance of the engine
		 */
		this.velocityEngine = new VelocityEngine();//jar.resource.loader.path
		this.velocityEngine.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, "org.apache.velocity.runtime.log.NullLogSystem");
		/*
		 * initialize the engine
		 */
		String userHomeWOLipsPath = System.getProperty("user.home") + File.separator + "Library" + File.separator + "WOLips";
		URL url = null;
		url = Platform.resolve(TemplateEnginePlugin.baseURL());
		String templatePaths = userHomeWOLipsPath + ", ";
		Path path = new Path(url.getPath());
		templatePaths = templatePaths + path.append("templates").toOSString();
		this.velocityEngine.setProperty("resource.loader", "wolips");
		this.velocityEngine.setProperty("wolips.resource.loader.class", "org.objectstyle.wolips.thirdparty.velocity.resourceloader.ResourceLoader");
		this.velocityEngine.setProperty("wolips.resource.loader.bundle", TemplateEnginePlugin.getDefault().getBundle());
//		this.velocityEngine.setProperty("jar.resource.loader.path", "jar:" + TemplateEnginePlugin.getDefault().getBundle().getResource("plugin.xml").getFile());
		this.velocityEngine.init();
		this.context = new VelocityContext();
		this.templates = new ArrayList<TemplateDefinition>();
		this.setPropertyForKey(this, WOLipsContext.Key);
//		SAXBuilder builder;
//		Document myContext = null;
//		try {
//			builder = new SAXBuilder();
//			myContext = builder.build(userHomeWOLipsPath + File.separator + "MyContext.xml");
//		} catch (Exception ee) {
//			// We can ignore this exception, it`s thrown if the xml document is
//			// not found.
//			// Per default there is no such file
//			builder = null;
//			myContext = null;
//		}
//		if (myContext != null) {
//			this.setPropertyForKey(myContext, "MyContext");
//		}
	}

	/**
	 * @param template
	 */
	public void addTemplate(TemplateDefinition template) {
		this.templates.add(template);
	}

	/**
	 * @param templateDefinitions
	 */
	public void addTemplates(TemplateDefinition[] templateDefinitions) {
		if (this.templates == null) {
			return;
		}
		for (int i = 0; i < templateDefinitions.length; i++) {
			TemplateDefinition templateDefinition = templateDefinitions[i];
			this.templates.add(templateDefinition);
		}
	}

	/**
	 * @param property
	 * @param key
	 */
	public void setPropertyForKey(Object property, String key) {
		this.context.put(key, property);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void run(IProgressMonitor monitor) throws InvocationTargetException {
		try {
			for (int i = 0; i < this.templates.size(); i++) {
				TemplateDefinition templateDefinition = this.templates.get(i);
				this.run(templateDefinition);
			}
		} catch (Exception e) {
			throw new InvocationTargetException(e);
		}
	}

	private void run(TemplateDefinition templateDefinition) {
		Writer writer = null;
		File file = null;
		String encoding = templateDefinition.getEncoding();
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		try {
			/*
			 * make a writer, and merge the template 'against' the context
			 */
			String templateName = templateDefinition.getTemplateName();
			Template template = this.velocityEngine.getTemplate(templateName);
			file = new File(templateDefinition.getDestinationPath());
			File parentDir = file.getParentFile();
			if (!parentDir.exists()) {
				parentDir.mkdirs();
			}
			IContainer folder = root.getContainerForLocation(new Path(parentDir.getPath()));
			folder.refreshLocal(IResource.DEPTH_ZERO, null);
			// Keep charset of component folder and HTML template in sync
			System.out.println(folder + "/" + file);
			if ("wo".equals(folder.getFileExtension()) && file.getPath().endsWith("html") 
					&& !encoding.equals(folder.getDefaultCharset(false))) {
				folder.setDefaultCharset(encoding, null);
			}
			writer = new BufferedWriter(
						new OutputStreamWriter(new FileOutputStream(file), encoding));
			template.merge(this.context, writer);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.flush();
					writer.close();
					IFile ifile = root.getFileForLocation(new Path(file.getPath()));
					ifile.refreshLocal(IResource.DEPTH_ZERO, null);
					if (!encoding.equals(ifile.getCharset(false))) {
						ifile.setCharset(encoding, null);
					}

				} catch (Exception ee) {
					ee.printStackTrace();
				}
			}
			this.setPropertyForKey(null, WOLipsContext.Key);
		}
	}

	/**
	 * @return Returns the projectName.
	 */
	public String getProjectName() {
		return this.projectName;
	}

	/**
	 * @param projectName
	 *            The projectName to set.
	 */
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	/**
	 * @return Returns the plugin name.
	 */
	public String getPluginName() {
		return TemplateEnginePlugin.getPluginId();
	}

	/**
	 * sets the date in the context
	 */
	public void setDateInContext() {
		DateFormat dateFormat = DateFormat.getDateInstance();
		DateFormat timeFormat = DateFormat.getTimeInstance();
		Date currentDate = Calendar.getInstance().getTime();
		String date = dateFormat.format(currentDate) + " " + timeFormat.format(currentDate);
		this.setPropertyForKey(date, "Date");

	}
}