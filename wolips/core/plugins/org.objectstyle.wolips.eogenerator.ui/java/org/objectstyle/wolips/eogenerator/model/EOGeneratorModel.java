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
package org.objectstyle.wolips.eogenerator.model;

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
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.objectstyle.wolips.preferences.Preferences;

public class EOGeneratorModel {
	private IProject myProject;

	private String myEOGeneratorPath;

	private List myModels;

	private List myRefModels;

	private String myDestination;

	private String mySubclassDestination;

	private String myTemplateDir;

	private String myJavaTemplate;

	private String mySubclassJavaTemplate;

	private List myDefines;

	private Boolean myPackageDirs;

	private Boolean myJava;

	private Boolean myJavaClient;

	private Boolean myVerbose;

	private String myPrefix;

	private String myFilenameTemplate;

	private List myCustomSettings;

	private boolean myDirty;

	public EOGeneratorModel(IProject _project, String _lineInfo) throws ParseException {
		this(_project);
		readFromString(_lineInfo);
	}

	public EOGeneratorModel(IProject _project) {
		this();
		myProject = _project;
	}

	public EOGeneratorModel() {
		myModels = new LinkedList();
		myRefModels = new LinkedList();
		myDefines = new LinkedList();
		myCustomSettings = new LinkedList();
	}

	public void writeToFile(IFile _file, IProgressMonitor _monitor) throws CoreException, IOException {
		String eogenFileContents = writeToString(Preferences.getEOGeneratorPath(), Preferences.getEOGeneratorTemplateDir(), Preferences.getEOGeneratorJavaTemplate(), Preferences.getEOGeneratorSubclassJavaTemplate());
		InputStream stream = new ByteArrayInputStream(eogenFileContents.getBytes("UTF-8"));
		if (_file.exists()) {
			_file.setContents(stream, true, true, _monitor);
		} else {
			_file.create(stream, true, _monitor);
		}
		stream.close();
		setDirty(false);
	}

	public String writeToString(String _defaultEOGeneratorPath, String _defaultTemplateDir, String _defaultJavaTemplate, String _defaultSubclassJavaTemplate) {
		StringBuffer sb = new StringBuffer();

		sb.append(escape(getEOGeneratorPath(_defaultEOGeneratorPath), false));

		append(sb, "-destination", myDestination);
		append(sb, "-filenameTemplate", myFilenameTemplate);
		append(sb, "-java", myJava);
		append(sb, "-javaclient", myJavaClient);
		append(sb, "-javaTemplate", getJavaTemplate(_defaultJavaTemplate));

		Iterator modelsIter = myModels.iterator();
		while (modelsIter.hasNext()) {
			EOModelReference model = (EOModelReference) modelsIter.next();
			append(sb, "-model", model.getPath(myProject));
		}

		append(sb, "-packagedirs", myPackageDirs);
		append(sb, "-prefix", myPrefix);

		Iterator refModelsIter = myRefModels.iterator();
		while (refModelsIter.hasNext()) {
			EOModelReference refModel = (EOModelReference) refModelsIter.next();
			append(sb, "-refmodel", refModel.getPath(myProject));
		}

		append(sb, "-subclassDestination", mySubclassDestination);
		append(sb, "-subclassJavaTemplate", getSubclassJavaTemplate(_defaultSubclassJavaTemplate));
		append(sb, "-templatedir", getTemplateDir(_defaultTemplateDir));
		append(sb, "-verbose", myVerbose);

		Iterator definesIter = myDefines.iterator();
		while (definesIter.hasNext()) {
			Define define = (Define) definesIter.next();
			String name = define.getName();
			String value = define.getValue();
			append(sb, "-define-" + name, value);
		}

		return sb.toString();
	}

	public void readFromString(String _str) throws ParseException {
		myModels.clear();
		myRefModels.clear();
		myDefines.clear();
		myCustomSettings.clear();
		CommandLineTokenizer tokenizer = new CommandLineTokenizer(_str);
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			if (myEOGeneratorPath == null) {
				myEOGeneratorPath = token;
			} else if (token.startsWith("-")) {
				if ("-destination".equalsIgnoreCase(token)) {
					myDestination = nextTokenValue(token, tokenizer);
				} else if ("-filenameTemplate".equalsIgnoreCase(token)) {
					myFilenameTemplate = nextTokenValue(token, tokenizer);
				} else if ("-java".equalsIgnoreCase(token)) {
					myJava = Boolean.TRUE;
				} else if ("-javaclient".equalsIgnoreCase(token)) {
					myJavaClient = Boolean.TRUE;
				} else if ("-javaTemplate".equalsIgnoreCase(token)) {
					myJavaTemplate = nextTokenValue(token, tokenizer);
				} else if ("-model".equalsIgnoreCase(token)) {
					String modelPath = nextTokenValue(token, tokenizer);
					myModels.add(new EOModelReference(new Path(modelPath)));
				} else if ("-packagedirs".equalsIgnoreCase(token)) {
					myPackageDirs = Boolean.TRUE;
				} else if ("-prefix".equalsIgnoreCase(token)) {
					myPrefix = nextTokenValue(token, tokenizer);
				} else if ("-refmodel".equalsIgnoreCase(token)) {
					String refModelPath = nextTokenValue(token, tokenizer);
					myRefModels.add(new EOModelReference(new Path(refModelPath)));
				} else if ("-subclassDestination".equalsIgnoreCase(token)) {
					mySubclassDestination = nextTokenValue(token, tokenizer);
				} else if ("-subclassJavaTemplate".equalsIgnoreCase(token)) {
					mySubclassJavaTemplate = nextTokenValue(token, tokenizer);
				} else if ("-templatedir".equalsIgnoreCase(token)) {
					myTemplateDir = nextTokenValue(token, tokenizer);
				} else if ("-verbose".equalsIgnoreCase(token)) {
					myVerbose = Boolean.TRUE;
				} else if (token.startsWith("-define-")) {
					String name = token.substring("-define-".length());
					String value = nextTokenValue(token, tokenizer);
					Define define = new Define(name, value);
					myDefines.add(define);
				} else {
					myCustomSettings.add(token);
				}
			} else {
				myCustomSettings.add(token);
			}
		}
		myDirty = false;
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
		return myProject;
	}

	public List getDefines() {
		return myDefines;
	}

	public void setDefines(List _defines) {
		myDefines = _defines;
		myDirty = true;
	}

	public String getDestination() {
		return myDestination;
	}

	public void setDestination(String _destination) {
		if (isNew(myDestination, _destination)) {
			myDestination = _destination;
			myDirty = true;
		}
	}

	public String getEOGeneratorPath(String _default) {
		String eoGeneratorPath = myEOGeneratorPath;
		if (myEOGeneratorPath == null || myEOGeneratorPath.trim().length() == 0) {
			eoGeneratorPath = _default;
		}
		return eoGeneratorPath;
	}

	public void setEOGeneratorPath(String _generatorPath) {
		if (isNew(myEOGeneratorPath, _generatorPath)) {
			myEOGeneratorPath = _generatorPath;
			myDirty = true;
		}
	}

	public Boolean isJavaClient() {
		return myJavaClient;
	}

	public void setJavaClient(Boolean _javaClient) {
		myJavaClient = _javaClient;
		myDirty = true;
	}

	public Boolean isJava() {
		return myJava;
	}

	public void setJava(Boolean _java) {
		myJava = _java;
		myDirty = true;
	}

	public String getJavaTemplate(String _default) {
		String javaTemplate = myJavaTemplate;
		if (myJavaTemplate == null || myJavaTemplate.trim().length() == 0) {
			javaTemplate = _default;
		}
		return javaTemplate;
	}

	public void setJavaTemplate(String _javaTemplate) {
		if (isNew(myJavaTemplate, _javaTemplate)) {
			myJavaTemplate = _javaTemplate;
			myDirty = true;
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

	public List getModels() {
		return myModels;
	}

	public void setModels(List _models) {
		myModels = _models;
		myDirty = true;
	}

	public void addModel(EOModelReference _modelReference) {
		myModels.add(_modelReference);
		myDirty = true;
	}

	public Boolean isPackageDirs() {
		return myPackageDirs;
	}

	public void setPackageDirs(Boolean _packageDirs) {
		myPackageDirs = _packageDirs;
		myDirty = true;
	}

	public List getRefModels() {
		return myRefModels;
	}

	public void setRefModels(List _refModels) {
		myRefModels = _refModels;
		myDirty = true;
	}

	public void addRefModel(EOModelReference _modelReference) {
		myRefModels.add(_modelReference);
		myDirty = true;
	}

	public String getSubclassDestination() {
		return mySubclassDestination;
	}

	public void setSubclassDestination(String _subclassDestination) {
		if (isNew(mySubclassDestination, _subclassDestination)) {
			mySubclassDestination = _subclassDestination;
			myDirty = true;
		}
	}

	public String getSubclassJavaTemplate(String _default) {
		String subclassJavaTemplate = mySubclassJavaTemplate;
		if (mySubclassJavaTemplate == null || mySubclassJavaTemplate.trim().length() == 0) {
			subclassJavaTemplate = _default;
		}
		return subclassJavaTemplate;
	}

	public void setSubclassJavaTemplate(String _subclassJavaTemplate) {
		if (isNew(mySubclassJavaTemplate, _subclassJavaTemplate)) {
			mySubclassJavaTemplate = _subclassJavaTemplate;
			myDirty = true;
		}
	}

	public String getTemplateDir(String _default) {
		String templateDir = myTemplateDir;
		if (myTemplateDir == null || myTemplateDir.trim().length() == 0) {
			templateDir = _default;
		}
		if (templateDir != null) {
			templateDir = PathUtils.getRelativePath(myProject, new Path(templateDir));
		}
		return templateDir;
	}

	public void setTemplateDir(String _templateDir) {
		if (isNew(myTemplateDir, _templateDir)) {
			myTemplateDir = _templateDir;
			myDirty = true;
		}
	}

	public void setPrefix(String _prefix) {
		if (isNew(myPrefix, _prefix)) {
			myPrefix = _prefix;
			myDirty = true;
		}
	}

	public String getPrefix() {
		return myPrefix;
	}

	public void setFilenameTemplate(String _filenameTemplate) {
		if (isNew(myFilenameTemplate, _filenameTemplate)) {
			myFilenameTemplate = _filenameTemplate;
			myDirty = true;
		}
	}

	public String getFilenameTemplate() {
		return myFilenameTemplate;
	}

	public Boolean isVerbose() {
		return myVerbose;
	}

	public void setVerbose(Boolean _verbose) {
		myVerbose = _verbose;
		myDirty = true;
	}

	public void setDirty(boolean _dirty) {
		myDirty = _dirty;
	}

	public boolean isDirty() {
		return myDirty;
	}

	public boolean isModelReferenced(EOModelReference _modelReference) {
		boolean modelReferenced = false;
		String eomodelName = _modelReference.getName();
		Iterator modelsIter = myModels.iterator();
		while (!modelReferenced && modelsIter.hasNext()) {
			EOModelReference model = (EOModelReference) modelsIter.next();
			modelReferenced = model.getName().equals(eomodelName);
		}
		if (!modelReferenced) {
			Iterator refModelsIter = myRefModels.iterator();
			while (!modelReferenced && refModelsIter.hasNext()) {
				EOModelReference model = (EOModelReference) refModelsIter.next();
				modelReferenced = model.getName().equals(eomodelName);
			}
		}
		return modelReferenced;
	}

	public static class Define {
		private String myName;

		private String myValue;

		public Define(String _name, String _value) {
			myName = _name;
			myValue = _value;
		}

		public int hashCode() {
			return myName.hashCode();
		}

		public boolean equals(Object _obj) {
			return (_obj instanceof Define && ((Define) _obj).myName.equals(myName));
		}

		public String getName() {
			return myName;
		}

		public String getValue() {
			return myValue;
		}
	}

	public static EOGeneratorModel createModelFromFile(IFile _file) throws ParseException, CoreException, IOException {
		_file.refreshLocal(IResource.DEPTH_INFINITE, null);
		InputStream eogenFileStream = _file.getContents();
		try {
			StringBuffer sb = new StringBuffer();
			BufferedReader br = new BufferedReader(new InputStreamReader(eogenFileStream));
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			EOGeneratorModel model = new EOGeneratorModel(_file.getProject(), sb.toString());
			model.setEOGeneratorPath(Preferences.getEOGeneratorPath());
			return model;
		} finally {
			eogenFileStream.close();
		}
	}

	public static EOGeneratorModel createDefaultModel(IProject _project) {
		EOGeneratorModel model = new EOGeneratorModel(_project);
		model.setJava(Boolean.TRUE);
		model.setPackageDirs(Boolean.TRUE);
		model.setVerbose(Boolean.TRUE);
		// model.setEOGeneratorPath(Preferences.getEOGeneratorPath());
		// model.setJavaTemplate(Preferences.getEOGeneratorJavaTemplate());
		// model.setTemplateDir(Preferences.getEOGeneratorTemplateDir());
		// model.setSubclassJavaTemplate(Preferences.getEOGeneratorSubclassJavaTemplate());
		try {
			IJavaProject javaProject = JavaCore.create(_project);
			if (javaProject != null) {
				IClasspathEntry[] classpathEntry = javaProject.getRawClasspath();
				for (int classpathEntryNum = 0; classpathEntryNum < classpathEntry.length; classpathEntryNum++) {
					IClasspathEntry entry = classpathEntry[classpathEntryNum];
					if (entry.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
						IPath path = entry.getPath();
						if (path != null) {
							IFolder sourceFolder = _project.getWorkspace().getRoot().getFolder(path);
							IPath projectRelativePath = sourceFolder.getProjectRelativePath();
							String projectRelativePathStr = projectRelativePath.toPortableString();
							model.setDestination(projectRelativePathStr);
							model.setSubclassDestination(projectRelativePathStr);
						}
					}
				}
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return model;
	}
}
