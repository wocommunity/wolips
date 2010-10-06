/* ====================================================================
 *
 * The ObjectStyle Group Software License, Version 1.0
 *
 * Copyright (c) 2005 The ObjectStyle Group,
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
package org.objectstyle.wolips.core.resources.internal.types.file;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.objectstyle.woenvironment.pb.PBProject;
import org.objectstyle.wolips.core.CorePlugin;
import org.objectstyle.wolips.core.resources.types.ILocalizedPath;
import org.objectstyle.wolips.core.resources.types.file.IPBDotProjectAdapter;
import org.objectstyle.wolips.core.resources.types.folder.IDotSubprojAdapter;
import org.objectstyle.wolips.core.resources.types.project.ProjectAdapter;

public class PBDotProjectAdapter extends AbstractFileAdapter implements IPBDotProjectAdapter {
	// local framework search for PB.project
	private static final String DefaultLocalFrameworkSearch = "$(NEXT_ROOT)$(LOCAL_LIBRARY_DIR)/Frameworks";

	private boolean saveRequired = false;

	private boolean rebuildRequired = false;

	private PBProject pbProject;

	public PBDotProjectAdapter(IFile underlyingFile) {
		super(underlyingFile);
		this.initPBProject();
	}

	public boolean isRebuildRequired() {
		return rebuildRequired;
	}

	public void save(IProgressMonitor monitor) {
		if (this.pbProject == null) {
			return;
		}
		if (!this.saveRequired) {
			return;
		}
		this.saveRequired = false;
		try {
			this.pbProject.saveChanges();
			this.getUnderlyingFile().refreshLocal(IResource.DEPTH_ZERO, monitor);
		} catch (Exception up) {
			CorePlugin.getDefault().debug(this.getClass().getName() + "Error while saving PB.project: " + this.getUnderlyingFile(), up);
		}
	}

	private ProjectAdapter getProjectAdapter() {
		IProject project = this.getUnderlyingFile().getProject();
		ProjectAdapter projectAdapter = (ProjectAdapter) project.getAdapter(ProjectAdapter.class);
		return projectAdapter;
	}

	private void initPBProject() {
		ProjectAdapter projectAdapter = this.getProjectAdapter();
		try {
			this.pbProject = new PBProject(this.getUnderlyingFile().getLocation().toOSString(), projectAdapter.isFramework());
			this.pbProject.update();
			IFile underlyingFile = getUnderlyingFile();
			if (!underlyingFile.exists()) {
				this.rebuildRequired = true;
				pbProject.saveChanges();
				try {
					underlyingFile.refreshLocal(IResource.DEPTH_ONE, null);
				} catch (CoreException e) {
					CorePlugin.getDefault().debug(this.getClass().getName() + "Error while refreshing PB.project: " + underlyingFile, e);
				}
			}
			addLocalFrameworkSectionToPBProject();
			syncProjectName();
		} catch (Exception e) {
			CorePlugin.getDefault().debug(this.getClass().getName() + "Error while loading PB.project: " + this.getUnderlyingFile(), e);
		}
	}

	public void cleanTables() {
		this.pbProject.setClasses(new ArrayList());
		this.pbProject.setWebServerResources(new ArrayList());
		this.pbProject.setWoAppResources(new ArrayList());
		this.pbProject.setWoComponents(new ArrayList());
		this.saveRequired = true;
	}

	/**
	 * Method syncProjectName.
	 */
	public void syncProjectName() {
		String projectName = this.getUnderlyingResource().getProject().getName();
		if (!projectName.equals(this.pbProject.getProjectName())) {
			this.pbProject.setProjectName(projectName);
			this.saveRequired = true;
		}
	}

	/**
	 * 
	 */
	public void addLocalFrameworkSectionToPBProject() {
		List<String> actualFrameworkSearch = this.pbProject.getFrameworkSearch();
		if (actualFrameworkSearch == null) {
			this.pbProject.setFrameworkSearch(new ArrayList());
			actualFrameworkSearch = this.pbProject.getFrameworkSearch();
		}
		if (!actualFrameworkSearch.contains(PBDotProjectAdapter.DefaultLocalFrameworkSearch)) {
			actualFrameworkSearch.add(PBDotProjectAdapter.DefaultLocalFrameworkSearch);
		}
	}

	public void save() {
		if (this.pbProject != null && this.saveRequired) {
			try {
				this.pbProject.saveChanges();
				this.saveRequired = false;
			} catch (Exception e) {
				CorePlugin.getDefault().debug(this.getClass().getName() + " Error while saving PB.project: " + this.getUnderlyingFile(), e);
			}
		}
	}

	private void addToListIfListNotContains(List<Object> list, Object object) {
		if (!list.contains(object)) {
			list.add(object);
			this.saveRequired = true;
		}
	}

	private void removeFromListIfListNotContains(List list, Object object) {
		if (list != null && list.contains(object)) {
			list.remove(object);
			this.saveRequired = true;
		}
	}

	public void addClass(ILocalizedPath localizedPath) {
		if (this.pbProject == null) {
			return;
		}
		List classes = this.pbProject.getClasses();
		if (classes == null) {
			this.pbProject.setClasses(new ArrayList());
			classes = this.pbProject.getClasses();
		}
		addToListIfListNotContains(classes, localizedPath.getResourcePath());
	}

	public void removeClass(ILocalizedPath localizedPath) {
		if (this.pbProject == null) {
			return;
		}
		removeFromListIfListNotContains(this.pbProject.getClasses(), localizedPath.getResourcePath());
	}

	public void addWoComponent(ILocalizedPath localizedPath) {
		if (this.pbProject == null) {
			return;
		}
		List woComponents = this.pbProject.getWoComponents(localizedPath.getLanguage());
		if (woComponents == null) {
			this.pbProject.setWoComponents(new ArrayList(), localizedPath.getLanguage());
			woComponents = this.pbProject.getWoComponents(localizedPath.getLanguage());
		}
		addToListIfListNotContains(woComponents, localizedPath.getResourcePath());
	}

	public void removeWoComponent(ILocalizedPath localizedPath) {
		if (this.pbProject == null) {
			return;
		}
		removeFromListIfListNotContains(this.pbProject.getWoComponents(localizedPath.getLanguage()), localizedPath.getResourcePath());
	}

	public void addWoappResource(ILocalizedPath localizedPath) {
		if (this.pbProject == null) {
			return;
		}
		List woAppResourcesComponents = this.pbProject.getWoAppResources(localizedPath.getLanguage());
		if (woAppResourcesComponents == null) {
			this.pbProject.setWoAppResources(new ArrayList(), localizedPath.getLanguage());
			woAppResourcesComponents = this.pbProject.getWoAppResources(localizedPath.getLanguage());
		}
		addToListIfListNotContains(woAppResourcesComponents, localizedPath.getResourcePath());
	}

	public void removeWoappResource(ILocalizedPath localizedPath) {
		if (this.pbProject == null) {
			return;
		}
		removeFromListIfListNotContains(this.pbProject.getWoAppResources(localizedPath.getLanguage()), localizedPath.getResourcePath());
	}

	public void addWebServerResource(ILocalizedPath localizedPath) {
		if (this.pbProject == null) {
			return;
		}
		List wsResourcesComponents = this.pbProject.getWebServerResources(localizedPath.getLanguage());
		if (wsResourcesComponents == null) {
			this.pbProject.setWebServerResources(new ArrayList(), localizedPath.getLanguage());
			wsResourcesComponents = this.pbProject.getWebServerResources(localizedPath.getLanguage());
		}
		addToListIfListNotContains(wsResourcesComponents, localizedPath.getResourcePath());
	}

	public void removeWebServerResource(ILocalizedPath localizedPath) {
		if (this.pbProject == null) {
			return;
		}
		removeFromListIfListNotContains(this.pbProject.getWebServerResources(localizedPath.getLanguage()), localizedPath.getResourcePath());
	}

	public void updateFrameworkNames(List frameworkNames) {
		if (this.pbProject == null) {
			return;
		}
		List frameworks = pbProject.getFrameworks();
		if (frameworks == null) {
			this.pbProject.setFrameworks(new ArrayList());
			frameworks = this.pbProject.getFrameworks();
		}
		boolean set = false;
		int existingNamesLength = frameworks.size();
		int newNamesLength = frameworkNames.size();
		if (existingNamesLength != newNamesLength) {
			set = true;
		} else {
			for (int i = 0; i < newNamesLength; i++) {
				String currentName = (String) frameworkNames.get(i);
				if (!frameworks.contains(currentName)) {
					set = true;
					break;
				}
			}
		}
		if (set) {
			pbProject.setFrameworks(frameworkNames);
			this.saveRequired = true;
		}
	}

	public void addSubproject(IDotSubprojAdapter dotSubprojAdapter) {
		if (this.pbProject == null) {
			return;
		}
		List subProjects = this.pbProject.getSubprojects();
		if (subProjects == null) {
			this.pbProject.setSubprojects(new ArrayList());
			subProjects = this.pbProject.getSubprojects();
		}
		addToListIfListNotContains(subProjects, dotSubprojAdapter.getUnderlyingFolder().getName());
	}

	public void removeSubproject(IDotSubprojAdapter dotSubprojAdapter) {
		if (this.pbProject == null) {
			return;
		}
		removeFromListIfListNotContains(this.pbProject.getSubprojects(), dotSubprojAdapter.getUnderlyingFolder().getName());

	}

}
