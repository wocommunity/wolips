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

package org.objectstyle.woproject.pb;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.objectstyle.cayenne.wocompat.PropertyListSerialization;

/**
 * A <b>PBProject</b> represents a ProjectBuilder project file
 * traditionally called <code>PB.project</code>.
 * 
 * 
 * @author uli
 * @author Andrei Adamchik
 */
public class PBProject {
	public static final String DEFAULT_APP_PROJECT = "pbindex/woapp/PB.project";
	public static final String DEFAULT_FRAMEWORK_PROJECT =
		"pbindex/woframework/PB.project";

	public static final String DYNAMIC_CODE_GEN = "DYNAMIC_CODE_GEN";
	public static final String FILESTABLE = "FILESTABLE";
	public static final String CLASSES = "CLASSES";
	public static final String FRAMEWORKS = "FRAMEWORKS";
	public static final String FRAMEWORKSEARCH = "FRAMEWORKSEARCH";
	public static final String OTHER_LINKED = "OTHER_LINKED";
	public static final String OTHER_SOURCES = "OTHER_SOURCES";
	public static final String WOAPP_RESOURCES = "WOAPP_RESOURCES";
	public static final String WOCOMPONENTS = "WO_COMPONENTS";
	public static final String WEB_SERVER_RESOURCES = "WEBSERVER_RESOURCES";
	public static final String PROJECTNAME = "PROJECTNAME";
	public static final String PROJECTTYPE = "PROJECTTYPE";
	public static final String PROJECTVERSION = "PROJECTVERSION";
	public static final String SUBPROJECTS = "SUBPROJECTS";
	public static final String YES = "YES";
	public static final String NO = "NO";

	protected boolean isFramework;
	protected File projectFile;
	protected Map pbProject;
	protected Map filesTable;

	/**
	 * Creates a new PBProject object
	 * with an associated project file assumed to be called "PB.project"
	 * and located in the current directory. If file does not exist,
	 * PBProject object is initialized using default template.
	 */
	public PBProject(boolean isFramework) throws IOException {
		this(new File("PB.project"), isFramework);
	}

	/**
	 * Creates a new PBProject object
	 * with an associated project file. If file does not exist,
	 * PBProject object is initialized using default template.
	 */
	public PBProject(File projectFile, boolean isFramework) throws IOException {
		this.projectFile = projectFile;
		this.isFramework = isFramework;

		if (projectFile == null) {
			throw new NullPointerException("Project file is null.");
		}

		this.update();
	}

	public String getDefaultTemplate() {
		return (isFramework) ? DEFAULT_FRAMEWORK_PROJECT : DEFAULT_APP_PROJECT;
	}

	/**
	 * Updates itself from the underlying <code>PB.project</code> file.
	 * If the file does not exist, uses a default template to load a 
	 * skeleton project.
	 */
	public void update() throws IOException {
		if (projectFile == null || !projectFile.exists()) {
			InputStream in =
				this.getClass().getClassLoader().getResourceAsStream(
					getDefaultTemplate());
			pbProject = (Map) PropertyListSerialization.propertyListFromStream(in);
			in = null;
		} else {
			pbProject = (Map) PropertyListSerialization.propertyListFromFile(projectFile);
		}
		
		if(pbProject == null) {
			throw new IOException("Error reading project file: " + projectFile);
		}

		readFilesTable();
	}

	/**
	 * Stores changes made to this object in the underlying PB.project file.
	 */
	public void saveChanges() throws IOException {
		this.saveFilesTable();
		PropertyListSerialization.propertyListToFile(projectFile, pbProject);
	}

	public boolean isDynamicCodeGen() {
		return PBProject.YES.equals(pbProject.get(PBProject.DYNAMIC_CODE_GEN));
	}

	public void setDynamicCodeGen(boolean aBoolean) {
		String flag = (aBoolean) ? PBProject.YES : PBProject.NO;
		pbProject.put(PBProject.DYNAMIC_CODE_GEN, flag);
	}

	public List getClasses() {
		return (List) getFilesTable().get(PBProject.CLASSES);
	}

	public void setClasses(List anArray) {
		getFilesTable().put(PBProject.CLASSES, anArray);
	}

	public List getWebServerResources() {
		return (List) getFilesTable().get(PBProject.WEB_SERVER_RESOURCES);
	}

	public void setWebServerResources(List anArray) {
		getFilesTable().put(PBProject.WEB_SERVER_RESOURCES, anArray);
	}

	public List getFrameworkSearch() {
		return (List) pbProject.get(PBProject.FRAMEWORKSEARCH);
	}

	public void setFrameworkSearch(List anArray) {
		pbProject.put(PBProject.FRAMEWORKSEARCH, anArray);
	}

	public List getFrameworks() {
		return (List) getFilesTable().get(PBProject.FRAMEWORKS);
	}

	public void setFrameworks(List anArray) {
		getFilesTable().put(PBProject.FRAMEWORKS, anArray);
	}
	public List getSubprojects() {
		return (List) getFilesTable().get(PBProject.SUBPROJECTS);
	}

	public void setSubprojects(List anArray) {
		getFilesTable().put(PBProject.SUBPROJECTS, anArray);
	}

	public List getOtherLinked() {
		return (List) getFilesTable().get(PBProject.OTHER_LINKED);
	}

	public void setOtherLinked(List anArray) {
		getFilesTable().put(PBProject.OTHER_LINKED, anArray);
	}

	public List getOtherSources() {
		return (List) getFilesTable().get(PBProject.OTHER_SOURCES);
	}

	public void setOtherSources(List anArray) {
		getFilesTable().put(PBProject.OTHER_SOURCES, anArray);
	}

	public List getWoAppResources() {
		return (List) getFilesTable().get(PBProject.WOAPP_RESOURCES);
	}

	public void setWoAppResources(List anArray) {
		getFilesTable().put(PBProject.WOAPP_RESOURCES, anArray);
	}

	public List getWoComponents() {
		return (List) getFilesTable().get(PBProject.WOCOMPONENTS);
	}

	public void setWoComponents(List anArray) {
		getFilesTable().put(PBProject.WOCOMPONENTS, anArray);
	}

	public String getProjectName() {
		return (String) pbProject.get(PBProject.PROJECTNAME);
	}

	public void setProjectName(String aString) {
		pbProject.put(PBProject.PROJECTNAME, aString);
	}

	public String getProjectType() {
		return (String) pbProject.get(PBProject.PROJECTTYPE);
	}

	public void setProjectType(String aString) {
		pbProject.put(PBProject.PROJECTTYPE, aString);
	}

	public String getProjectVersion() {
		return (String) pbProject.get(PBProject.PROJECTVERSION);
	}

	public void setProjectVersion(String aString) {
		pbProject.put(PBProject.PROJECTVERSION, aString);
	}

	protected Map getFilesTable() {
		return filesTable;
	}

	protected void saveFilesTable() {
		pbProject.put(PBProject.FILESTABLE, filesTable);
	}

	protected void readFilesTable() {
		filesTable = (Map) pbProject.get(PBProject.FILESTABLE);
	}

	/**
	 * Returns the projectFile.
	 * @return File
	 */
	public File getProjectFile() {
		return projectFile;
	}

	/**
	 * Sets the projectFile.
	 * @param projectFile The projectFile to set
	 */
	public void setProjectFile(File projectFile) {
		this.projectFile = projectFile;
	}
	/**
	 * Method forgetAll deletes all classes, other linked, other sources,
	 * webserver resources and wocomponent entries.
	 */
	public void forgetAllFiles() {
		this.setClasses(new ArrayList());
		this.setOtherLinked(new ArrayList());
		this.setOtherSources(new ArrayList());
		this.setWebServerResources(new ArrayList());
		this.setWoComponents(new ArrayList());
	}
}
