/* ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0 
 *
 * Copyright (c) 2002 - 2004 The ObjectStyle Group 
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
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.FileResourceLoader;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.operation.IRunnableWithProgress;

/**
 * @author ulrich
 */
public class TemplateEngine implements IRunnableWithProgress {
	public static final String WOLIPS_LOADER = "wolips";

	public static final String FILE_LOADER = "file";

	private VelocityContext _context;

	private List<TemplateDefinition> _templates;

	private VelocityEngine _velocityEngine;

	private WOLipsContext _wolipsContext;

	private String _templatePath;
	
	public void init() throws Exception {
		Thread thread = Thread.currentThread();
		ClassLoader loader = thread.getContextClassLoader();
		thread.setContextClassLoader(this.getClass().getClassLoader());
		try {
			/*
			 * create a new instance of the engine
			 */
			_velocityEngine = new VelocityEngine();
			_velocityEngine.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, "org.apache.velocity.runtime.log.NullLogSystem");
	
			/*
			 * initialize the engine
			 */
			String userHomeWOLipsPath = System.getProperty("user.home") + File.separator + "Library" + File.separator + "WOLips";
			URL url = FileLocator.resolve(TemplateEnginePlugin.baseURL());
			String templatePaths = userHomeWOLipsPath + ", ";
			Path path = new Path(url.getPath());
			templatePaths = templatePaths + path.append("templates").toOSString();
	
			_velocityEngine.setProperty("resource.loader", "wolips, file");
	
			// _velocityEngine.setProperty("resource.loader", "wolips");
			_velocityEngine.setProperty("wolips.resource.loader.class", org.objectstyle.wolips.thirdparty.velocity.resourceloader.ResourceLoader.class.getName());
			_velocityEngine.setProperty("wolips.resource.loader.bundle", TemplateEnginePlugin.getDefault().getBundle());
	
			// _velocityEngine.setProperty("resource.loader", "file");
			_velocityEngine.setProperty("file.resource.loader.class", FileResourceLoader.class.getName());
			if (_templatePath != null) {
				_velocityEngine.setProperty("file.resource.loader.path", _templatePath);
			}
			_velocityEngine.init();
		} finally {
			thread.setContextClassLoader(loader);
		}
		_context = new VelocityContext();
		_templates = new LinkedList<TemplateDefinition>();
		_wolipsContext = new WOLipsContext();
		setPropertyForKey(_wolipsContext, WOLipsContext.Key);
	}

	public void setTemplatePath(String loaderPath) {
		_templatePath = loaderPath;
	}

	/**
	 * @param template
	 */
	public void addTemplate(TemplateDefinition template) {
		_templates.add(template);
	}

	/**
	 * @param templateDefinitions
	 */
	public void addTemplates(TemplateDefinition[] templateDefinitions) {
		if (_templates == null) {
			return;
		}
		for (TemplateDefinition templateDefinition : templateDefinitions) {
			_templates.add(templateDefinition);
		}
	}

	/**
	 * Sets the property for the given key.
	 * 
	 * @param property the property value
	 * @param key the property key
	 */
	public void setPropertyForKey(Object property, String key) {
		_context.put(key, property);
	}
	
	/**
	 * Returns the property value for the given key.
	 * 
	 * @param key the key to lookup
	 * @return the property value for the given key
	 */
	public Object getPropertyForKey(String key) {
		return _context.get(key);
	}
	
	/**
	 * Returns the keys from this template engine.
	 * 
	 * @return the keys from this template engine
	 */
	public Object[] getKeys() {
		return _context.getKeys();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void run(IProgressMonitor monitor) throws InvocationTargetException {
		try {
			setDateInContext();
			for (TemplateDefinition templateDefinition : _templates) {
				run(templateDefinition);
			}
		} catch (Exception e) {
			throw new InvocationTargetException(e);
		}
	}

	private void run(TemplateDefinition templateDefinition) {
		Writer writer = null;
		File file = null;
		try {
			/*
			 * make a writer, and merge the template 'against' the context
			 */
			String templateName = templateDefinition.getTemplateName();
			Template template = _velocityEngine.getTemplate(templateName, "UTF-8");
			//writer = new FileWriter(templateDefinition.getDestinationPath());
			file = new File(templateDefinition.getDestinationPath());
			File parentDir = file.getParentFile();
			if (!parentDir.exists()) {
				parentDir.mkdirs();
			}
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
			template.merge(_context, writer);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.flush();
					writer.close();
				} catch (Exception ee) {
					ee.printStackTrace();
				}
			}
		}
	}

	/**
	 * @return Returns the wolipsContext.
	 */
	public WOLipsContext getWolipsContext() {
		return _wolipsContext;
	}

	/**
	 * sets the date in the context
	 */
	private void setDateInContext() {
		DateFormat dateFormat = DateFormat.getDateInstance();
		DateFormat timeFormat = DateFormat.getTimeInstance();
		Date currentDate = Calendar.getInstance().getTime();
		String date = dateFormat.format(currentDate) + " " + timeFormat.format(currentDate);
		setPropertyForKey(date, "Date");
	}
}