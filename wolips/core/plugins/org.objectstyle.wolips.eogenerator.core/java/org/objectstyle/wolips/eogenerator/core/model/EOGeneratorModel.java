/*
 * ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0
 * 
 * Copyright (c) 2006 The ObjectStyle Group and individual authors of the
 * software. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * 3. The end-user documentation included with the redistribution, if any, must
 * include the following acknowlegement: "This product includes software
 * developed by the ObjectStyle Group (http://objectstyle.org/)." Alternately,
 * this acknowlegement may appear in the software itself, if and wherever such
 * third-party acknowlegements normally appear.
 * 
 * 4. The names "ObjectStyle Group" and "Cayenne" must not be used to endorse or
 * promote products derived from this software without prior written permission.
 * For written permission, please contact andrus@objectstyle.org.
 * 
 * 5. Products derived from this software may not be called "ObjectStyle" nor
 * may "ObjectStyle" appear in their names without prior written permission of
 * the ObjectStyle Group.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * OBJECTSTYLE GROUP OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 * 
 * This software consists of voluntary contributions made by many individuals on
 * behalf of the ObjectStyle Group. For more information on the ObjectStyle
 * Group, please see <http://objectstyle.org/>.
 *  
 */
package org.objectstyle.wolips.eogenerator.core.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.objectstyle.wolips.preferences.Preferences;

public class EOGeneratorModel {
	public static final String EOGENERATOR_PATH = "eogeneratorPath";

	public static final String MODELS = "models";

	public static final String REF_MODELS = "refModels";

	public static final String DESTINATION = "destination";

	public static final String SUBCLASS_DESTINATION = "subclassDestination";

	public static final String TEMPLATE_DIR = "templateDir";

	public static final String JAVA_TEMPLATE = "javaTemplate";

	public static final String SUBCLASS_JAVA_TEMPLATE = "subclassJavaTemplate";

	public static final String DEFINES = "defines";

	public static final String PACKAGE_DIRS = "packageDirs";

	public static final String JAVA = "java";

	public static final String JAVA_CLIENT = "javaClient";

	public static final String VERBOSE = "verbose";

	public static final String PREFIX = "prefix";

	public static final String FILENAME_TEMPLATE = "filenameTemplate";

	public static final String CUSTOM_SETTINGS = "customSettings";

	public static final String DIRTY = "dirty";

	private PropertyChangeSupport _propertyChangeSupport;
	
	private IProject _project;

	private String _eogeneratorPath;

	private List<EOModelReference> _models;

	private List<EOModelReference> _refModels;

	private String _destination;

	private String _subclassDestination;

	private String _templateDir;

	private String _javaTemplate;

	private String _subclassJavaTemplate;

	private List<Define> _defines;

	private Boolean _packageDirs;

	private Boolean _java;

	private Boolean _javaClient;

	private Boolean _verbose;

	private String _prefix;

	private String _filenameTemplate;

	private List<String> _customSettings;

	private boolean _dirty;

	public EOGeneratorModel(IProject project, String lineInfo) throws ParseException {
		this(project);
		readFromString(lineInfo);
	}

	public EOGeneratorModel(IProject project) {
		this();
		_project = project;
	}

	public EOGeneratorModel() {
		_propertyChangeSupport = new PropertyChangeSupport(this);
		_models = new LinkedList<EOModelReference>();
		_refModels = new LinkedList<EOModelReference>();
		_defines = new LinkedList<Define>();
		_customSettings = new LinkedList<String>();
	}
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		_propertyChangeSupport.addPropertyChangeListener(listener);
	}

	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		_propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		_propertyChangeSupport.removePropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		_propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
	}

	public void writeToFile(IFile file, IProgressMonitor monitor) throws CoreException, IOException {
		String eogenFileContents = writeToString(Preferences.getEOGeneratorPath(), Preferences.getEOGeneratorTemplateDir(), Preferences.getEOGeneratorJavaTemplate(), Preferences.getEOGeneratorSubclassJavaTemplate());
		InputStream stream = new ByteArrayInputStream(eogenFileContents.getBytes("UTF-8"));
		if (file.exists()) {
			file.setContents(stream, true, true, monitor);
		} else {
			file.create(stream, true, monitor);
		}
		stream.close();
		setDirty(false);
	}

	public String writeToString(String _defaultEOGeneratorPath, String _defaultTemplateDir, String _defaultJavaTemplate, String _defaultSubclassJavaTemplate) {
		StringBuffer sb = new StringBuffer();

		sb.append(escape(getEOGeneratorPath(_defaultEOGeneratorPath), false));

		append(sb, "-destination", _destination);
		append(sb, "-filenameTemplate", _filenameTemplate);
		append(sb, "-java", _java);
		append(sb, "-javaclient", _javaClient);
		append(sb, "-javaTemplate", getJavaTemplate(_defaultJavaTemplate));

		Iterator modelsIter = _models.iterator();
		while (modelsIter.hasNext()) {
			EOModelReference model = (EOModelReference) modelsIter.next();
			append(sb, "-model", model.getPath(_project));
		}

		append(sb, "-packagedirs", _packageDirs);
		append(sb, "-prefix", _prefix);

		Iterator refModelsIter = _refModels.iterator();
		while (refModelsIter.hasNext()) {
			EOModelReference refModel = (EOModelReference) refModelsIter.next();
			append(sb, "-refmodel", refModel.getPath(_project));
		}

		append(sb, "-subclassDestination", _subclassDestination);
		append(sb, "-subclassJavaTemplate", getSubclassJavaTemplate(_defaultSubclassJavaTemplate));
		append(sb, "-templatedir", getTemplateDir(_defaultTemplateDir));
		append(sb, "-verbose", _verbose);

		Iterator definesIter = _defines.iterator();
		while (definesIter.hasNext()) {
			Define define = (Define) definesIter.next();
			String name = define.getName();
			String value = define.getValue();
			append(sb, "-define-" + name, value);
		}

		return sb.toString();
	}

	public void readFromString(String _str) throws ParseException {
		_models.clear();
		_refModels.clear();
		_defines.clear();
		_customSettings.clear();
		CommandLineTokenizer tokenizer = new CommandLineTokenizer(_str);
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			if (_eogeneratorPath == null) {
				_eogeneratorPath = token;
			} else if (token.startsWith("-")) {
				if ("-destination".equalsIgnoreCase(token)) {
					_destination = nextTokenValue(token, tokenizer);
				} else if ("-filenameTemplate".equalsIgnoreCase(token)) {
					_filenameTemplate = nextTokenValue(token, tokenizer);
				} else if ("-java".equalsIgnoreCase(token)) {
					_java = Boolean.TRUE;
				} else if ("-javaclient".equalsIgnoreCase(token)) {
					_javaClient = Boolean.TRUE;
				} else if ("-javaTemplate".equalsIgnoreCase(token)) {
					_javaTemplate = nextTokenValue(token, tokenizer);
				} else if ("-model".equalsIgnoreCase(token)) {
					String modelPath = nextTokenValue(token, tokenizer);
					_models.add(new EOModelReference(new Path(modelPath)));
				} else if ("-packagedirs".equalsIgnoreCase(token)) {
					_packageDirs = Boolean.TRUE;
				} else if ("-prefix".equalsIgnoreCase(token)) {
					_prefix = nextTokenValue(token, tokenizer);
				} else if ("-refmodel".equalsIgnoreCase(token)) {
					String refModelPath = nextTokenValue(token, tokenizer);
					_refModels.add(new EOModelReference(new Path(refModelPath)));
				} else if ("-subclassDestination".equalsIgnoreCase(token)) {
					_subclassDestination = nextTokenValue(token, tokenizer);
				} else if ("-subclassJavaTemplate".equalsIgnoreCase(token)) {
					_subclassJavaTemplate = nextTokenValue(token, tokenizer);
				} else if ("-templatedir".equalsIgnoreCase(token)) {
					_templateDir = nextTokenValue(token, tokenizer);
				} else if ("-verbose".equalsIgnoreCase(token)) {
					_verbose = Boolean.TRUE;
				} else if (token.startsWith("-define-")) {
					String name = token.substring("-define-".length());
					String value = nextTokenValue(token, tokenizer);
					Define define = new Define(name, value);
					_defines.add(define);
				} else {
					_customSettings.add(token);
				}
			} else {
				_customSettings.add(token);
			}
		}
		_dirty = false;
	}

	protected void append(StringBuffer _buffer, String _name, Boolean _value) {
		if (_value != null && _value.booleanValue()) {
			_buffer.append(" ");
			_buffer.append(_name);
		}
	}

	protected void append(StringBuffer _buffer, String _name, String _value) {
		if (_value != null && _value.trim().length() > 0) {
			_buffer.append(" ");
			_buffer.append(_name);
			_buffer.append(" ");
			_buffer.append(escape(_value, true));
		}
	}

	protected String escape(String _value, boolean _quotes) {
		String value;
		if (_value == null) {
			value = null;
		} else if (_value.indexOf(' ') == -1 && _value.trim().length() > 0) {
			value = _value;
		} else if (_quotes) {
			StringBuffer valueBuffer = new StringBuffer();
			valueBuffer.append("\"");
			valueBuffer.append(_value);
			valueBuffer.append("\"");
			value = valueBuffer.toString();
		} else {
			value = _value.replaceAll(" ", "\\ ");
		}
		return value;
	}

	protected String nextTokenValue(String _token, CommandLineTokenizer _tokenizer) throws ParseException {
		if (!_tokenizer.hasMoreTokens()) {
			throw new ParseException(_token + " must be followed by a value.", -1);
		}
		String token = _tokenizer.nextToken();
		return token;
	}

	public IProject getProject() {
		return _project;
	}

	public List<Define> getDefines() {
		return _defines;
	}

	public void setDefines(List<Define> defines) {
		List<Define> oldDefines = _defines;
		_defines = defines;
		_propertyChangeSupport.firePropertyChange(EOGeneratorModel.DEFINES, oldDefines, _defines);
		setDirty(true);
	}

	public String getDestination() {
		return _destination;
	}

	public void setDestination(String destination) {
		if (isNew(_destination, destination)) {
			String oldDestination = _destination;
			_destination = destination;
			_propertyChangeSupport.firePropertyChange(EOGeneratorModel.DESTINATION, oldDestination, _destination);
			setDirty(true);
		}
	}

	public String getEOGeneratorPath(String defaultEOGeneratorPath) {
		String eoGeneratorPath = _eogeneratorPath;
		if (_eogeneratorPath == null || _eogeneratorPath.trim().length() == 0) {
			eoGeneratorPath = defaultEOGeneratorPath;
		}
		return eoGeneratorPath;
	}

	public void setEOGeneratorPath(String eogeneratorPath) {
		setEOGeneratorPath(eogeneratorPath, false);
	}

	public void setEOGeneratorPath(String eogeneratorPath, boolean markAsDirty) {
		if (isNew(_eogeneratorPath, eogeneratorPath)) {
			String oldEOGeneratorPath = _eogeneratorPath;
			_eogeneratorPath = eogeneratorPath;
			_propertyChangeSupport.firePropertyChange(EOGeneratorModel.EOGENERATOR_PATH, oldEOGeneratorPath, _eogeneratorPath);
			setDirty(markAsDirty);
		}
	}

	public Boolean getJavaClient() {
		return isJavaClient();
	}

	public Boolean isJavaClient() {
		return _javaClient;
	}

	public void setJavaClient(Boolean javaClient) {
		Boolean oldJavaClient = _javaClient;
		_javaClient = javaClient;
		_propertyChangeSupport.firePropertyChange(EOGeneratorModel.JAVA_CLIENT, oldJavaClient, _javaClient);
		setDirty(true);
	}

	public Boolean getJava() {
		return isJava();
	}
	
	public Boolean isJava() {
		return _java;
	}

	public void setJava(Boolean java) {
		Boolean oldJava = _java;
		_java = java;
		_propertyChangeSupport.firePropertyChange(EOGeneratorModel.JAVA, oldJava, _java);
		setDirty(true);
	}

	public String getJavaTemplate() {
		return getJavaTemplate(null);
	}
	
	public String getJavaTemplate(String defaultJavaTemplate) {
		String javaTemplate = _javaTemplate;
		if (_javaTemplate == null || _javaTemplate.trim().length() == 0) {
			javaTemplate = defaultJavaTemplate;
		}
		return javaTemplate;
	}

	public void setJavaTemplate(String javaTemplate) {
		if (isNew(_javaTemplate, javaTemplate)) {
			String oldJavaTemplate = _javaTemplate;
			_javaTemplate = javaTemplate;
			_propertyChangeSupport.firePropertyChange(EOGeneratorModel.JAVA_TEMPLATE, oldJavaTemplate, _javaTemplate);
			setDirty(true);
		}
	}

	protected boolean isNew(String _oldValue, String _newValue) {
		boolean isNew;
		if (_oldValue == null && _newValue != null && _newValue.trim().length() > 0) {
			isNew = true;
		} else if (_oldValue != null && !_oldValue.equals(_newValue)) {
			isNew = true;
		} else {
			isNew = false;
		}
		return isNew;
	}

	public List<EOModelReference> getModels() {
		return _models;
	}

	public void setModels(List<EOModelReference> models) {
		List<EOModelReference> oldModels = _models;
		_models = models;
		_propertyChangeSupport.firePropertyChange(EOGeneratorModel.MODELS, oldModels, _models);
		setDirty(true);
	}

	public void addModel(EOModelReference modelReference) {
		List<EOModelReference> oldModels = new LinkedList<EOModelReference>(_models);
		_models.add(modelReference);
		_propertyChangeSupport.firePropertyChange(EOGeneratorModel.MODELS, oldModels, _models);
		setDirty(true);
	}

	public Boolean getPackageDirs() {
		return isPackageDirs();
	}
	
	public Boolean isPackageDirs() {
		return _packageDirs;
	}

	public void setPackageDirs(Boolean packageDirs) {
		Boolean oldPackageDirs = _packageDirs;
		_packageDirs = packageDirs;
		_propertyChangeSupport.firePropertyChange(EOGeneratorModel.PACKAGE_DIRS, oldPackageDirs, _packageDirs);
		setDirty(true);
	}

	public List<EOModelReference> getRefModels() {
		return _refModels;
	}

	public void setRefModels(List<EOModelReference> refModels) {
		List<EOModelReference> oldRefModels = _refModels;
		_refModels = refModels;
		_propertyChangeSupport.firePropertyChange(EOGeneratorModel.REF_MODELS, oldRefModels, _refModels);
		setDirty(true);
	}

	public void addRefModel(EOModelReference modelReference) {
		List<EOModelReference> oldRefModels = new LinkedList<EOModelReference>(_refModels);
		_refModels.add(modelReference);
		_propertyChangeSupport.firePropertyChange(EOGeneratorModel.REF_MODELS, oldRefModels, _refModels);
		setDirty(true);
	}

	public String getSubclassDestination() {
		return _subclassDestination;
	}

	public void setSubclassDestination(String subclassDestination) {
		if (isNew(_subclassDestination, subclassDestination)) {
			String oldSubclassDestination = _subclassDestination;
			_subclassDestination = subclassDestination;
			_propertyChangeSupport.firePropertyChange(EOGeneratorModel.SUBCLASS_DESTINATION, oldSubclassDestination, _subclassDestination);
			setDirty(true);
		}
	}

	public String getSubclassJavaTemplate() {
		return getSubclassJavaTemplate(null);
	}
	
	public String getSubclassJavaTemplate(String defaultSubclassJavaTemplate) {
		String subclassJavaTemplate = _subclassJavaTemplate;
		if (_subclassJavaTemplate == null || _subclassJavaTemplate.trim().length() == 0) {
			subclassJavaTemplate = defaultSubclassJavaTemplate;
		}
		return subclassJavaTemplate;
	}

	public void setSubclassJavaTemplate(String subclassJavaTemplate) {
		if (isNew(_subclassJavaTemplate, subclassJavaTemplate)) {
			String oldSubclassJavaTemplate = _subclassJavaTemplate;
			_subclassJavaTemplate = subclassJavaTemplate;
			_propertyChangeSupport.firePropertyChange(EOGeneratorModel.SUBCLASS_JAVA_TEMPLATE, oldSubclassJavaTemplate, _subclassJavaTemplate);
			setDirty(true);
		}
	}

	public String getTemplateDir() {
		return getTemplateDir(null);
	}
	
	public String getTemplateDir(String defaultTemplateDir) {
		String templateDir = _templateDir;
		if (_templateDir == null || _templateDir.trim().length() == 0) {
			templateDir = defaultTemplateDir;
		}
		if (templateDir != null) {
			templateDir = PathUtils.getRelativePath(_project, new Path(templateDir));
		}
		return templateDir;
	}

	public void setTemplateDir(String templateDir) {
		if (isNew(_templateDir, templateDir)) {
			String oldTemplateDir = _templateDir;
			_templateDir = templateDir;
			_propertyChangeSupport.firePropertyChange(EOGeneratorModel.TEMPLATE_DIR, oldTemplateDir, _templateDir);
			setDirty(true);
		}
	}

	public void setPrefix(String prefix) {
		if (isNew(_prefix, prefix)) {
			String oldPrefix = _prefix;
			_prefix = prefix;
			_propertyChangeSupport.firePropertyChange(EOGeneratorModel.PREFIX, oldPrefix, _prefix);
			setDirty(true);
		}
	}

	public String getPrefix() {
		return _prefix;
	}

	public void setFilenameTemplate(String filenameTemplate) {
		if (isNew(_filenameTemplate, filenameTemplate)) {
			String oldFilenameTemplate = _filenameTemplate;
			_filenameTemplate = filenameTemplate;
			_propertyChangeSupport.firePropertyChange(EOGeneratorModel.FILENAME_TEMPLATE, oldFilenameTemplate, _filenameTemplate);
			setDirty(true);
		}
	}

	public String getFilenameTemplate() {
		return _filenameTemplate;
	}

	public Boolean getVerbose() {
		return isVerbose();
	}
	
	public Boolean isVerbose() {
		return _verbose;
	}

	public void setVerbose(Boolean verbose) {
		Boolean oldVerbose = _verbose;
		_verbose = verbose;
		_propertyChangeSupport.firePropertyChange(EOGeneratorModel.VERBOSE, oldVerbose, _verbose);
		setDirty(true);
	}

	public void setDirty(boolean dirty) {
		boolean oldDirty = _dirty;
		_dirty = dirty;
		_propertyChangeSupport.firePropertyChange(EOGeneratorModel.DIRTY, oldDirty, _dirty);
	}

	public boolean isDirty() {
		return _dirty;
	}

	public boolean isModelReferenced(EOModelReference modelReference) {
		boolean modelReferenced = false;
		String eomodelName = modelReference.getName();
		Iterator modelsIter = _models.iterator();
		while (!modelReferenced && modelsIter.hasNext()) {
			EOModelReference model = (EOModelReference) modelsIter.next();
			modelReferenced = model.getName().equals(eomodelName);
		}
		if (!modelReferenced) {
			Iterator refModelsIter = _refModels.iterator();
			while (!modelReferenced && refModelsIter.hasNext()) {
				EOModelReference model = (EOModelReference) refModelsIter.next();
				modelReferenced = model.getName().equals(eomodelName);
			}
		}
		return modelReferenced;
	}

	public static class Define {
		private String _name;

		private String _value;

		public Define(String name, String value) {
			_name = name;
			_value = value;
		}

		public int hashCode() {
			return _name.hashCode();
		}

		public boolean equals(Object obj) {
			return (obj instanceof Define && ((Define) obj)._name.equals(_name));
		}

		public String getName() {
			return _name;
		}

		public String getValue() {
			return _value;
		}
	}

	public static EOGeneratorModel createModelFromFile(IFile file) throws ParseException, CoreException, IOException {
		file.refreshLocal(IResource.DEPTH_INFINITE, null);
		InputStream eogenFileStream = file.getContents();
		try {
			StringBuffer sb = new StringBuffer();
			BufferedReader br = new BufferedReader(new InputStreamReader(eogenFileStream));
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			String string = sb.toString();
			string.replace('\\', '/');
			EOGeneratorModel model = new EOGeneratorModel(file.getProject(), string);
			model.setEOGeneratorPath(Preferences.getEOGeneratorPath(), false);
			return model;
		} finally {
			eogenFileStream.close();
		}
	}

	/*
	 * public static EOGeneratorModel createDefaultModel(IProject project) {
	 * EOGeneratorModel model = new EOGeneratorModel(project);
	 * model.setJava(Boolean.TRUE); model.setPackageDirs(Boolean.TRUE);
	 * model.setVerbose(Boolean.TRUE); //
	 * model.setEOGeneratorPath(Preferences.getEOGeneratorPath()); //
	 * model.setJavaTemplate(Preferences.getEOGeneratorJavaTemplate()); //
	 * model.setTemplateDir(Preferences.getEOGeneratorTemplateDir()); //
	 * model.setSubclassJavaTemplate(Preferences.getEOGeneratorSubclassJavaTemplate());
	 * try { IJavaProject javaProject = JavaCore.create(project); if
	 * (javaProject != null) { IClasspathEntry[] classpathEntry =
	 * javaProject.getRawClasspath(); for (int classpathEntryNum = 0;
	 * classpathEntryNum < classpathEntry.length; classpathEntryNum++) {
	 * IClasspathEntry entry = classpathEntry[classpathEntryNum]; if
	 * (entry.getEntryKind() == IClasspathEntry.CPE_SOURCE) { IPath path =
	 * entry.getPath(); if (path != null) { IFolder sourceFolder =
	 * project.getWorkspace().getRoot().getFolder(path); IPath
	 * projectRelativePath = sourceFolder.getProjectRelativePath(); String
	 * projectRelativePathStr = projectRelativePath.toPortableString();
	 * model.setDestination(projectRelativePathStr);
	 * model.setSubclassDestination(projectRelativePathStr); } } } } } catch
	 * (JavaModelException e) { e.printStackTrace(); } return model; }
	 */
}
