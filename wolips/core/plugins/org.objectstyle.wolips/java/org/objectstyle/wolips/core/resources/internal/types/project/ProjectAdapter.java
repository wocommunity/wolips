/* ====================================================================
 *
 * The ObjectStyle Group Software License, Version 1.0
 *
 * Copyright (c) 2005 - 2006 The ObjectStyle Group,
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
package org.objectstyle.wolips.core.resources.internal.types.project;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.objectstyle.wolips.core.CorePlugin;
import org.objectstyle.wolips.core.resources.internal.types.AbstractResourceAdapter;
import org.objectstyle.wolips.core.resources.types.IPBDotProjectOwner;
import org.objectstyle.wolips.core.resources.types.file.IPBDotProjectAdapter;
import org.objectstyle.wolips.core.resources.types.folder.IBuildAdapter;
import org.objectstyle.wolips.core.resources.types.folder.IDotApplicationAdapter;
import org.objectstyle.wolips.core.resources.types.folder.IDotFrameworkAdapter;
import org.objectstyle.wolips.core.resources.types.folder.IProductAdapter;
import org.objectstyle.wolips.core.resources.types.folder.IWoprojectAdapter;
import org.objectstyle.wolips.core.resources.types.project.IProjectAdapter;
import org.objectstyle.wolips.variables.VariablesPlugin;

public class ProjectAdapter extends AbstractResourceAdapter implements IProjectAdapter {

	private IProject underlyingProject;

	private boolean isFramework;

	/**
	 * Comment for <code>BuilderNotFound</code>
	 */
	public static final int BuilderNotFound = -1;

	protected static final String TARGET_BUILDER_ID = "org.objectstyle.wolips.targetbuilder.targetbuilder";

	protected static final String INCREMENTAL_BUILDER_ID = "org.objectstyle.wolips.incrementalbuilder";

	private static final String ANT_BUILDER_ID = "org.objectstyle.wolips.antbuilder";

	public ProjectAdapter(IProject project, boolean isFramework) {
		super(project);
		this.underlyingProject = project;
		this.isFramework = isFramework;
	}

	public IProject getUnderlyingProject() {
		return this.underlyingProject;
	}

	public boolean isFramework() {
		return this.isFramework;
	}

	public boolean isApplication() {
		return !this.isFramework();
	}

	public IPBDotProjectAdapter getPBDotProjectAdapter() {
		IContainer underlyingContainer = this.getUnderlyingProject();
		IResource pbDotProjectResource = underlyingContainer.getFile(new Path(IPBDotProjectAdapter.FILE_NAME));
		IPBDotProjectAdapter pbDotProjectAdapter = (IPBDotProjectAdapter) pbDotProjectResource.getAdapter(IPBDotProjectAdapter.class);
		return pbDotProjectAdapter;
	}

	public IWoprojectAdapter getWoprojectAdapter() {
		IContainer underlyingContainer = this.getUnderlyingProject();
		IFolder wprojectFolder = null;
		IWoprojectAdapter wprojectAdapter = null;
		wprojectFolder = underlyingContainer.getFolder(new Path(IWoprojectAdapter.FOLDER_NAME));
		if (wprojectFolder.exists()) {
			wprojectAdapter = (IWoprojectAdapter) wprojectFolder.getAdapter(IWoprojectAdapter.class);
			if (wprojectAdapter != null) {
				return wprojectAdapter;
			}
		}
		wprojectFolder = underlyingContainer.getFolder(new Path(IWoprojectAdapter.FOLDER_NAME_DEPRECATED));
		if (wprojectFolder.exists()) {
			wprojectAdapter = (IWoprojectAdapter) wprojectFolder.getAdapter(IWoprojectAdapter.class);
		}
		return wprojectAdapter;

	}

	public IPBDotProjectOwner getPBDotProjectOwner(IResource resource) {
		if (resource == this.getUnderlyingProject()) {
			return this;
		}
		return super.getPBDotProjectOwner(resource);
	}

	public IPBDotProjectOwner getPBDotProjectOwner() {
		return this;
	}

	public boolean hasParentPBDotProjectAdapter() {
		return false;
	}

	private IFolder getBuildFolder() {
		// :TODO what if we have both folder
		IResource resource = this.getUnderlyingProject().getFolder(IBuildAdapter.FILE_NAME_DIST);
		if (resource.exists() && resource instanceof IFolder) {
			return (IFolder) resource;
		}
		resource = this.getUnderlyingProject().getFolder(IBuildAdapter.FILE_NAME_BUILD);
		if (resource.exists() && resource instanceof IFolder) {
			return (IFolder) resource;
		}
		return null;
	}

	public IBuildAdapter getBuildAdapter() {
		IResource resource = this.getBuildFolder();
		if (resource == null) {
			return null;
		}
		return (IBuildAdapter) resource.getAdapter(IBuildAdapter.class);
	}

	public IDotApplicationAdapter getDotApplicationAdapter() {
		IResource resource = this.getUnderlyingProject().getFolder(this.getUnderlyingResource().getProject().getName() + "." + IDotApplicationAdapter.FILE_NAME_EXTENSION);
		return (IDotApplicationAdapter) resource.getAdapter(IDotApplicationAdapter.class);
	}

	public IDotFrameworkAdapter getDotFrameworkAdapter() {
		IResource resource = this.getUnderlyingProject().getFolder(this.getUnderlyingResource().getProject().getName() + "." + IDotFrameworkAdapter.FILE_NAME_EXTENSION);
		return (IDotFrameworkAdapter) resource.getAdapter(IDotFrameworkAdapter.class);
	}

	public IProductAdapter getProductAdapter() {
		IProject project = this.getUnderlyingResource().getProject();
		IProjectAdapter projectAdapter = (IProjectAdapter) project.getAdapter(IProjectAdapter.class);
		if (projectAdapter.isFramework()) {
			return this.getDotFrameworkAdapter();
		}

		return this.getDotApplicationAdapter();
	}

	public List getFrameworkPaths() {
		ArrayList list = new ArrayList();
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for (int i = 0; i < projects.length; i++) {
			if (isFrameworkReference(projects[i])) {
				list.add(projects[i].getLocation());
			}
		}
		try {
			IJavaProject javaProject = JavaCore.create(this.getUnderlyingProject());
			list.addAll(toFrameworkPaths(javaProject.getResolvedClasspath(false)));
		} catch (JavaModelException e) {
			CorePlugin.getDefault().log(e);
		}
		return list;
	}

	public List getFrameworkNames() {
		Set frameworkNamesSet = new TreeSet();
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for (int i = 0; i < projects.length; i++) {
			if (isFrameworkReference(projects[i])) {
				frameworkNamesSet.add(projects[i].getName() + "." + "framework");
			}
		}
		try {
			IJavaProject javaProject = JavaCore.create(this.getUnderlyingProject());
			frameworkNamesSet.addAll(this.toFrameworkNames(javaProject.getResolvedClasspath(false)));
		} catch (JavaModelException e) {
			CorePlugin.getDefault().log(e);
		}
		return new LinkedList(frameworkNamesSet);
	}

	public String getFrameworkName(IPath frameworkPath) {
		String frameworkName = null;
		if (ResourcesPlugin.getWorkspace().getRoot().getContainerForLocation(frameworkPath) instanceof IProject) {
			frameworkName = frameworkPath.lastSegment() + ".framework";
		} else {
			frameworkName = frameworkPath.lastSegment();
		}
		return frameworkName;
	}

	private List toFrameworkPaths(IClasspathEntry[] classpathEntries) {
		ArrayList arrayList = new ArrayList();
		for (int i = 0; i < classpathEntries.length; i++) {
			IPath path = classpathEntries[i].getPath();
			IPath choppedFrameworkPath = null;
			int count = path.segmentCount();
			for (int pathElementNum = 0; pathElementNum < count && choppedFrameworkPath == null; pathElementNum++) {
				String segment = path.segment(pathElementNum);
				if (segment.endsWith("." + "framework")) {
					choppedFrameworkPath = path.removeLastSegments(count - pathElementNum - 1);
				}
			}
			if (choppedFrameworkPath != null && !choppedFrameworkPath.lastSegment().startsWith("JavaVM")) {
				arrayList.add(choppedFrameworkPath);
			}
		}
		return arrayList;
	}

	private List toFrameworkNames(IClasspathEntry[] classpathEntries) {
		List pathsList = toFrameworkPaths(classpathEntries);
		ArrayList namesList = new ArrayList(pathsList.size());
		Iterator pathsIter = pathsList.iterator();
		while (pathsIter.hasNext()) {
			IPath path = (IPath) pathsIter.next();
			String name = this.getFrameworkName(path);
			namesList.add(name);
		}
		return namesList;
	}

	/**
	 * Method isTheLaunchAppOrFramework.
	 * 
	 * @param iProject
	 * @return boolean
	 */
	public boolean isFrameworkReference(IProject iProject) {
		boolean isFrameworkReference;
		IJavaProject javaProject = null;
		try {
			javaProject = JavaCore.create(this.getUnderlyingProject());
			if (javaProject == null) {
				isFrameworkReference = false;
			} else {
				IProjectAdapter project = (IProjectAdapter) iProject.getAdapter(IProjectAdapter.class);
				isFrameworkReference = project != null && project.isFramework() && projectIsReferencedByProject(iProject, javaProject.getProject());
			}
		} catch (Exception e) {
			CorePlugin.getDefault().log(e);
			isFrameworkReference = false;
		}
		return isFrameworkReference;
	}

	public boolean projectIsReferencedByProject(IProject child, IProject mother) {
		IProject[] projects = null;
		try {
			projects = mother.getReferencedProjects();
		} catch (Exception e) {
			CorePlugin.getDefault().log(e);
			return false;
		}
		for (int i = 0; i < projects.length; i++) {
			if (projects[i].equals(child))
				return true;
		}
		return false;
	}

	/**
	 * Installs the target builder.
	 * 
	 * @param position
	 * @throws CoreException
	 */
	public void installTargetBuilder(int position) throws CoreException {
		if (!this.isTargetBuilderInstalled())
			this.installBuilderAtPosition(ProjectAdapter.TARGET_BUILDER_ID, position, null);
	}

	/**
	 * Removes the target builder.
	 * 
	 * @return postion of TargetBuilder if not found
	 *         IBuilderAccessor.BuilderNotFoundwill be returned.
	 * @throws CoreException
	 */
	public int removeTargetBuilder() throws CoreException {
		if (!this.isTargetBuilderInstalled())
			return ProjectAdapter.BuilderNotFound;
		int returnValue = this.positionForBuilder(ProjectAdapter.TARGET_BUILDER_ID);
		this.removeBuilder(ProjectAdapter.TARGET_BUILDER_ID);
		return returnValue;
	}

	/**
	 * Installs the ant builder.
	 * 
	 * @throws CoreException
	 */
	public void installAntBuilder() throws CoreException {
		if (!this.isAntBuilderInstalled())
			this.installBuilder(ProjectAdapter.ANT_BUILDER_ID);
	}

	/**
	 * Removes the ant builder.
	 * 
	 * @throws CoreException
	 */
	public void removeAntBuilder() throws CoreException {
		if (this.isAntBuilderInstalled())
			this.removeBuilder(ProjectAdapter.ANT_BUILDER_ID);
	}

	/**
	 * Installs the incremetal builder.
	 * 
	 * @throws CoreException
	 */
	public void installIncrementalBuilder() throws CoreException {
		if (!this.isIncrementalBuilderInstalled())
			this.installBuilder(ProjectAdapter.INCREMENTAL_BUILDER_ID);
	}

	/**
	 * Removes the incremental builder.
	 * 
	 * @throws CoreException
	 */
	public void removeIncrementalBuilder() throws CoreException {
		if (this.isIncrementalBuilderInstalled())
			this.removeBuilder(ProjectAdapter.INCREMENTAL_BUILDER_ID);
	}

	/**
	 * Installs the java builder.
	 * 
	 * @throws CoreException
	 */
	public void installJavaBuilder() throws CoreException {
		if (!this.isJavaBuilderInstalled())
			this.installBuilder(JavaCore.BUILDER_ID);
	}

	/**
	 * Installs the java builder.
	 * 
	 * @param position
	 * @throws CoreException
	 */
	public void installJavaBuilder(int position) throws CoreException {
		if (!this.isJavaBuilderInstalled())
			this.installBuilderAtPosition(JavaCore.BUILDER_ID, position, null);
	}

	/**
	 * Removes the incremental builder.
	 * 
	 * @return postion of JavaBuilder if not found
	 *         IBuilderAccessor.BuilderNotFoundwill be returned.
	 * @throws CoreException
	 */
	public int removeJavaBuilder() throws CoreException {
		if (!this.isJavaBuilderInstalled())
			return ProjectAdapter.BuilderNotFound;
		int returnValue = this.positionForBuilder(JavaCore.BUILDER_ID);
		this.removeBuilder(JavaCore.BUILDER_ID);
		return returnValue;
	}

	/**
	 * @return Return true if the target builder is installed.
	 */
	public boolean isTargetBuilderInstalled() {
		return this.isBuilderInstalled(ProjectAdapter.TARGET_BUILDER_ID);
	}

	/**
	 * @return Return true if the ant builder is installed.
	 */
	public boolean isAntBuilderInstalled() {
		return this.isBuilderInstalled(ProjectAdapter.ANT_BUILDER_ID);
	}

	/**
	 * @return Return true if the incremental builder is installed.
	 */
	public boolean isIncrementalBuilderInstalled() {
		return this.isBuilderInstalled(ProjectAdapter.INCREMENTAL_BUILDER_ID);
	}

	/**
	 * @return Return true if the java builder is installed.
	 */
	public boolean isJavaBuilderInstalled() {
		return this.isBuilderInstalled(JavaCore.BUILDER_ID);
	}

	/**
	 * @return The builer args.
	 */
	public Map getBuilderArgs() {
		Map result = null;
		try {
			IProjectDescription desc = this.getUnderlyingProject().getDescription();
			List cmdList = Arrays.asList(desc.getBuildSpec());
			Iterator iter = cmdList.iterator();
			while (iter.hasNext()) {
				ICommand cmd = (ICommand) iter.next();
				if (this.isWOLipsBuilder(cmd.getBuilderName())) {
					result = cmd.getArguments();
					break;
				}
			}
		} catch (Exception up) {
			// if anything went wrong, we simply don't have any args (yet)
			// might wanna log the exception, though
		}
		if (null == result) {
			// this doesn't exist pre-JDK1.3, is that a problem?
			result = Collections.EMPTY_MAP;
			// result = new HashMap();
		}
		return (result);
	}

	/**
	 * @param name
	 *            Name of a build command
	 * @return boolean whether this is one of ours
	 */
	private boolean isWOLipsBuilder(String name) {
		return (name.equals(ProjectAdapter.INCREMENTAL_BUILDER_ID) || name.equals(ProjectAdapter.ANT_BUILDER_ID));
	}

	/**
	 * Method removeJavaBuilder.
	 * 
	 * @param aBuilder
	 * @throws CoreException
	 */
	private void removeBuilder(String aBuilder) throws CoreException {
		IProjectDescription desc = null;
		ICommand[] coms = null;
		ArrayList<ICommand> comList = null;
		List<ICommand> tmp = null;
		ICommand[] newCom = null;
		try {
			desc = this.getUnderlyingProject().getDescription();
			coms = desc.getBuildSpec();
			comList = new ArrayList<ICommand>();
			tmp = Arrays.asList(coms);
			comList.addAll(tmp);
			boolean foundJBuilder = false;
			for (int i = 0; i < comList.size(); i++) {
				if ((comList.get(i)).getBuilderName().equals(aBuilder)) {
					comList.remove(i);
					foundJBuilder = true;
				}
			}
			if (foundJBuilder) {
				newCom = new ICommand[comList.size()];
				for (int i = 0; i < comList.size(); i++) {
					newCom[i] = comList.get(i);
				}
				desc.setBuildSpec(newCom);
				this.getUnderlyingProject().setDescription(desc, null);
			}
		} finally {
			desc = null;
			coms = null;
			comList = null;
			tmp = null;
			newCom = null;
		}
	}

	/**
	 * Method installBuilder.
	 * 
	 * @param aBuilder
	 * @throws CoreException
	 */
	private void installBuilder(String aBuilder) throws CoreException {
		IProjectDescription desc = null;
		ICommand[] coms = null;
		ICommand[] newIc = null;
		ICommand command = null;
		try {
			desc = this.getUnderlyingProject().getDescription();
			coms = desc.getBuildSpec();
			boolean foundJBuilder = false;
			for (int i = 0; i < coms.length; i++) {
				if (coms[i].getBuilderName().equals(aBuilder)) {
					foundJBuilder = true;
				}
			}
			if (!foundJBuilder) {
				command = desc.newCommand();
				command.setBuilderName(aBuilder);
				newIc = new ICommand[coms.length + 1];
				System.arraycopy(coms, 0, newIc, 0, coms.length);
				newIc[coms.length] = command;
				desc.setBuildSpec(newIc);
				this.getUnderlyingProject().setDescription(desc, null);
			}
		} finally {
			desc = null;
			coms = null;
			newIc = null;
			command = null;
		}
	}

	/**
	 * Method isBuilderInstalled.
	 * 
	 * @param anID
	 * @return boolean
	 */
	private boolean isBuilderInstalled(String anID) {
		try {
			ICommand[] nids = this.getUnderlyingProject().getDescription().getBuildSpec();
			for (int i = 0; i < nids.length; i++) {
				if (nids[i].getBuilderName().equals(anID))
					return true;
			}
		} catch (Exception anException) {
			CorePlugin.getDefault().log(anException);
			return false;
		}
		return false;
	}

	/**
	 * Method positionForBuilder.
	 * 
	 * @param aBuilder
	 * @return int
	 * @throws CoreException
	 */
	private int positionForBuilder(String aBuilder) throws CoreException {
		IProjectDescription desc = null;
		ICommand[] coms = null;
		try {
			desc = this.getUnderlyingProject().getDescription();
			coms = desc.getBuildSpec();
			for (int i = 0; i < coms.length; i++) {
				if (coms[i].getBuilderName().equals(aBuilder))
					return i;
			}
		} finally {
			desc = null;
			coms = null;
		}
		return BuilderNotFound;
	}

	/**
	 * Method installBuilderAtPosition.
	 * 
	 * @param aBuilder
	 * @param installPos
	 * @param arguments
	 * @throws CoreException
	 */
	private void installBuilderAtPosition(String aBuilder, int installPos, Map arguments) throws CoreException {
		if (installPos == ProjectAdapter.BuilderNotFound) {
			CorePlugin.getDefault()

			.log("Somebody tries to install builder: " + aBuilder + " at an illegal position. This may happen if the removed builder does not exist.");
			return;
		}
		IProjectDescription desc = this.getUnderlyingProject().getDescription();
		ICommand[] coms = desc.getBuildSpec();
		Map args = arguments;
		if (args == null)
			args = new HashMap();
		for (int i = 0; i < coms.length; i++) {
			if (coms[i].getBuilderName().equals(aBuilder) && coms[i].getArguments().equals(args))
				return;
		}
		ICommand[] newIc = null;
		ICommand command = desc.newCommand();
		command.setBuilderName(aBuilder);
		command.setArguments(args);
		newIc = new ICommand[coms.length + 1];
		if (installPos <= 0) {
			System.arraycopy(coms, 0, newIc, 1, coms.length);
			newIc[0] = command;
		} else if (installPos >= coms.length) {
			System.arraycopy(coms, 0, newIc, 0, coms.length);
			newIc[coms.length] = command;
		} else {
			System.arraycopy(coms, 0, newIc, 0, installPos);
			newIc[installPos] = command;
			System.arraycopy(coms, installPos, newIc, installPos + 1, coms.length - installPos);
		}
		desc.setBuildSpec(newIc);
		this.getUnderlyingProject().setDescription(desc, null);
	}

	/**
	 * @return null if the project is not an application othewise invokes the
	 *         same method on ProjectAdapter
	 */
	public IPath getWorkingDir() {
		IPath path = null;
		if (this.isAntBuilderInstalled()) {
			path = this.getUnderlyingProject().getFolder("dist").getLocation();
		} else {
			path = this.getUnderlyingProject().getFolder("build").getLocation();
		}
		path = path.append(this.getUnderlyingProject().getName() + ".woa");
		return path;
	}

	private Properties getBuildProperties() throws CoreException, IOException {
		Properties properties = new Properties();
		IFile file = this.getUnderlyingProject().getFile("build.properties");
		if (file.exists()) {
			InputStream inputStream = file.getContents();
			properties.load(inputStream);
			inputStream.close();
		}
		return properties;
	}

	private void setBuildProperties(Properties properties) throws CoreException, IOException {
		if (this.getBuildProperties().equals(properties))
			return;
		IFile file = this.getUnderlyingProject().getFile("build.properties");
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		properties.store(byteArrayOutputStream, null);
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
		file.setContents(byteArrayInputStream, true, true, new NullProgressMonitor());
	}

	/**
	 * @return generate webxml
	 */
	public boolean getWebXML() {
		String returnValue = null;
		try {
			returnValue = (String) this.getBuildProperties().get("webXML");
		} catch (CoreException e) {
			CorePlugin.getDefault().log(e);
		} catch (IOException e) {
			CorePlugin.getDefault().log(e);
		}
		if (returnValue == null || !"true".equalsIgnoreCase(returnValue)) {
			return false;
		}
		return true;
	}

	/**
	 * @param webXML
	 *            generate webxml
	 */
	public void setWebXML(boolean webXML) {
		try {
			Properties properties = this.getBuildProperties();
			if (!webXML) {
				properties.put("webXML", "false");
			} else {
				properties.put("webXML", "true");
			}
			this.setBuildProperties(properties);
		} catch (CoreException e) {
			CorePlugin.getDefault().log(e);
		} catch (IOException e) {
			CorePlugin.getDefault().log(e);
		}
	}

	/**
	 * @param convertNullValueToEmptyString
	 * @return webxml custom content
	 */
	public String getWebXML_CustomContent(boolean convertNullValueToEmptyString) {
		String returnValue = null;
		try {
			returnValue = (String) this.getBuildProperties().get("webXML_CustomContent");
		} catch (CoreException e) {
			CorePlugin.getDefault().log(e);
		} catch (IOException e) {
			CorePlugin.getDefault().log(e);
		}
		if (convertNullValueToEmptyString && returnValue == null) {
			return "";
		}
		return returnValue;
	}

	/**
	 * @param webXML_CustomContent
	 *            webxml custom content
	 */
	public void setWebXML_CustomContent(String webXML_CustomContent) {
		try {
			Properties properties = this.getBuildProperties();
			if (webXML_CustomContent == null) {
				properties.put("webXML_CustomContent", "");
			} else {
				properties.put("webXML_CustomContent", webXML_CustomContent);
			}
			this.setBuildProperties(properties);
		} catch (CoreException e) {
			CorePlugin.getDefault().log(e);
		} catch (IOException e) {
			CorePlugin.getDefault().log(e);
		}
	}

	public String getEOGeneratorArgs(boolean convertNullValueToEmptyString) {
		String returnValue = null;
		try {
			returnValue = (String) this.getBuildProperties().get("eogeneratorArgs");
		} catch (CoreException e) {
			CorePlugin.getDefault().log(e);
		} catch (IOException e) {
			CorePlugin.getDefault().log(e);
		}
		if (convertNullValueToEmptyString && returnValue == null) {
			return "";
		}
		return returnValue;
	}

	public void setEOGeneratorArgs(String eogeneratorArgs) {
		try {
			Properties properties = this.getBuildProperties();
			if (eogeneratorArgs == null) {
				properties.put("eogeneratorArgs", "");
			} else {
				properties.put("eogeneratorArgs", eogeneratorArgs);
			}
			this.setBuildProperties(properties);
		} catch (CoreException e) {
			CorePlugin.getDefault().log(e);
		} catch (IOException e) {
			CorePlugin.getDefault().log(e);
		}
	}

	/**
	 * @param convertNullValueToEmptyString
	 * @return principalClass.
	 */
	public String getPrincipalClass(boolean convertNullValueToEmptyString) {
		String returnValue = null;
		try {
			returnValue = (String) this.getBuildProperties().get("principalClass");
		} catch (CoreException e) {
			CorePlugin.getDefault().log(e);
		} catch (IOException e) {
			CorePlugin.getDefault().log(e);
		}
		if (convertNullValueToEmptyString && returnValue == null) {
			return "";
		}
		return returnValue;
	}

	/**
	 * @param principalClass
	 *            the principalClass for the Info.plist
	 */
	public void setPrincipalClass(String principalClass) {
		try {
			Properties properties = this.getBuildProperties();
			if (principalClass == null) {
				properties.put("principalClass", "");
			} else {
				properties.put("principalClass", principalClass);
			}
			this.setBuildProperties(properties);
		} catch (CoreException e) {
			CorePlugin.getDefault().log(e);
		} catch (IOException e) {
			CorePlugin.getDefault().log(e);
		}
	}

	/**
	 * @param convertNullValueToEmptyString
	 * @return The CustomContent for the Info.plist
	 */
	public String getCustomInfoPListContent(boolean convertNullValueToEmptyString) {
		String returnValue = null;
		try {
			returnValue = (String) this.getBuildProperties().get("customInfoPListContent");
		} catch (CoreException e) {
			CorePlugin.getDefault().log(e);
		} catch (IOException e) {
			CorePlugin.getDefault().log(e);
		}
		if (convertNullValueToEmptyString && returnValue == null) {
			return "";
		}
		return returnValue;
	}

	/**
	 * @param customInfoPListContent
	 *            The CustomContent for the Info.plist
	 */
	public void setCustomInfoPListContent(String customInfoPListContent) {
		try {
			Properties properties = this.getBuildProperties();
			if (customInfoPListContent == null) {
				properties.put("customInfoPListContent", "");
			} else {
				properties.put("customInfoPListContent", customInfoPListContent);
			}
			this.setBuildProperties(properties);
		} catch (CoreException e) {
			CorePlugin.getDefault().log(e);
		} catch (IOException e) {
			CorePlugin.getDefault().log(e);
		}
	}

	/**
	 * @param convertNullValueToEmptyString
	 * @return The EOAdaptorClassName for the Info.plist
	 */
	public String getEOAdaptorClassName(boolean convertNullValueToEmptyString) {
		String returnValue = null;
		try {
			returnValue = (String) this.getBuildProperties().get("eoAdaptorClassName");
		} catch (CoreException e) {
			CorePlugin.getDefault().log(e);
		} catch (IOException e) {
			CorePlugin.getDefault().log(e);
		}
		if (convertNullValueToEmptyString && returnValue == null) {
			return "";
		}
		return returnValue;
	}

	/**
	 * @param eoAdaptorClassName
	 *            the eoadaptorclassname for the Info.plist
	 */
	public void setEOAdaptorClassName(String eoAdaptorClassName) {
		try {
			Properties properties = this.getBuildProperties();
			if (eoAdaptorClassName == null) {
				properties.put("eoAdaptorClassName", "");
			} else {
				properties.put("eoAdaptorClassName", eoAdaptorClassName);
			}
			this.setBuildProperties(properties);
		} catch (CoreException e) {
			CorePlugin.getDefault().log(e);
		} catch (IOException e) {
			CorePlugin.getDefault().log(e);
		}

	}

	/**
	 * @return
	 * @throws CoreException
	 */
	public IPath getWOJavaArchive() throws CoreException {
		IResource resource = null;
		IPath path = null;
		String projectName = this.getUnderlyingProject().getName();
		// String projectNameLC = projectName.toLowerCase();
		// I'd rather use the knowledge from the IncrementalNature, but
		// that fragment is not
		// visible here (so I can't use the class, I think) [hn3000]
		if (this.isFramework()) {
			if (this.isAntBuilderInstalled()) {
				resource = getJar("dist/", ".framework/");
				if (!resource.exists())
					resource = getJar("", ".framework/");
			} else if (this.isIncrementalBuilderInstalled()) {
				resource = this.getUnderlyingProject().getFolder("build/" + projectName + ".framework/Resources/Java");
			}
			if (resource != null && resource.exists()) {
				path = resource.getLocation();
			} else {
				IPath externalBuildRoot = VariablesPlugin.getDefault().getExternalBuildRoot();
				if (externalBuildRoot != null) {
					path = externalBuildRoot.append(projectName + ".framework/Resources/Java/" + projectName + ".jar");
				}
			}
		} else if (this.isApplication()) { // must be application
			IFolder wdFolder = null;
			if (this.isAntBuilderInstalled()) {
				wdFolder = this.getUnderlyingProject().getFolder("dist");
			} else {
				wdFolder = this.getUnderlyingProject().getFolder("build");
			}
			if (wdFolder != null && wdFolder.exists()) {
				IResource[] members = wdFolder.members();
				for (int i = 0; i < members.length; i++) {
					IResource member = members[i];
					if (member.getType() == IResource.FOLDER && member.getName().endsWith(".woa")) {
						wdFolder = (IFolder) member;
						break;
					}
				}
			}
			if (wdFolder != null && wdFolder.exists()) {
				IFolder javaFolder = wdFolder.getFolder("Contents/Resources/Java");
				if (this.isAntBuilderInstalled()) {
					resource = javaFolder.findMember(wdFolder.getName().substring(0, wdFolder.getName().length() - 4).toLowerCase() + ".jar");
					if (!resource.exists())
						resource = getJar("", ".woa/Contents/");
				} else if (this.isIncrementalBuilderInstalled()) {
					resource = javaFolder;
				}
			}
			if (resource != null && (resource.exists())) {
				path = resource.getLocation();
			} else {
				IPath externalBuildRoot = VariablesPlugin.getDefault().getExternalBuildRoot();
				if (externalBuildRoot != null) {
					path = externalBuildRoot.append(projectName + ".woa/Contents/Resources/Java/" + projectName + ".jar");
				}
			}
		}
		return path;
	}

	private IResource getJar(String prefix, String postfix) {
		IResource result = null;
		String projectName = this.getUnderlyingProject().getName();
		result = this.getUnderlyingProject().getFile(prefix + projectName + postfix + "Resources/Java/" + projectName + ".jar");
		if (result == null || !result.exists()) {
			result = this.getUnderlyingProject().getFile(prefix + projectName + postfix + "Resources/Java/" + projectName.toLowerCase() + ".jar");
		}
		return result;
	}
}
