/* ====================================================================
 *
 * The ObjectStyle Group Software License, Version 1.0
 *
 * Copyright (c) 2002 - 2004 The ObjectStyle Group,
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
package org.objectstyle.wolips.datasets.adaptable;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * @author ulrich
 * @deprecated Use org.objectstyle.wolips.core.* instead.
 */
public class ProjectNatures extends ProjectBuilder {
	private final String TargetBuilderNatureID = "org.objectstyle.wolips.targetbuilder.targetbuildernature";

	private final String INCREMENTAL_FRAMEWORK_NATURE_ID = "org.objectstyle.wolips.incrementalframeworknature";

	private final String ANT_FRAMEWORK_NATURE_ID = "org.objectstyle.wolips.antframeworknature";

	private final String INCREMENTAL_APPLICATION_NATURE_ID = "org.objectstyle.wolips.incrementalapplicationnature";

	private final String ANT_APPLICATION_NATURE_ID = "org.objectstyle.wolips.antapplicationnature";

	private final String[] WOLIPS_NATURES = { this.INCREMENTAL_FRAMEWORK_NATURE_ID, this.ANT_FRAMEWORK_NATURE_ID, this.INCREMENTAL_APPLICATION_NATURE_ID, this.ANT_APPLICATION_NATURE_ID };

	/**
	 * @param project
	 */
	protected ProjectNatures(IProject project) {
		super(project);
	}

	/**
	 * @return Returns true is the project has a WOLips nature.
	 * @throws CoreException
	 */
	public boolean isWOLipsProject() throws CoreException {
		return this.hasWOLipsNature();
	}

	/**
	 * @param natureID
	 * @return boolean
	 */
	private boolean isWOLipsNature(String natureID) {
		for (int i = 0; i < this.WOLIPS_NATURES.length; i++) {
			if (this.WOLIPS_NATURES[i].equals(natureID)) {
				return true;
			}
		}
		return false;
	}

	private void addTargetBuilder() throws CoreException {
		if (this.isTargetBuilderInstalled())
			return;
		IProjectDescription description = this.getIProject().getDescription();
		String[] natures = description.getNatureIds();
		String[] newNatures = new String[natures.length + 1];
		System.arraycopy(natures, 0, newNatures, 0, natures.length);
		newNatures[natures.length] = this.TargetBuilderNatureID;
		description.setNatureIds(newNatures);
		this.getIProject().setDescription(description, null);
	}

	/**
	 * @param value
	 * @throws CoreException
	 */
	public void useTargetBuilder(boolean value) throws CoreException {
		this.removeTargetBuilder();
		if (value)
			this.addTargetBuilder();
		// HACK ak we need to call setDescription to update the .project files
		// so we just call it every time...
		this.getIProject().setDescription(this.getIProject().getDescription(), null);
	}

	/**
	 * @param nature
	 * @return boolean
	 * @throws CoreException
	 */
	private boolean projectHasNature(String nature) throws CoreException {
		if (!this.getIProject().isOpen() || !this.getIProject().isAccessible())
			return false;
		return this.getIProject().hasNature(nature);
	}

	/**
	 * @return true if at least one of the WOLips natures is installed.
	 * @throws CoreException
	 */
	public boolean hasWOLipsNature() throws CoreException {
		if (!this.getIProject().isOpen() || !this.getIProject().isAccessible())
			return false;
		return (this.isApplication() || this.isFramework());
	}

	/**
	 * @return true only if one of the WOLips application natures is installed.
	 *         False does not mean that this is a framework.
	 * @throws CoreException
	 */
	public boolean isApplication() throws CoreException {
		return (projectHasNature(this.ANT_APPLICATION_NATURE_ID) || projectHasNature(this.INCREMENTAL_APPLICATION_NATURE_ID));
	}

	/**
	 * @return true only if one of the WOLips framework natures is installed.
	 *         False does not mean that this is an application.
	 * @throws CoreException
	 */
	public boolean isFramework() throws CoreException {
		return (this.projectHasNature(this.ANT_FRAMEWORK_NATURE_ID) || this.projectHasNature(this.INCREMENTAL_FRAMEWORK_NATURE_ID));
	}

	/**
	 * @return true only if one of the WOLips ant natures is installed. False
	 *         does not mean that this is an incremental nature.
	 * @throws CoreException
	 */
	public boolean isAnt() throws CoreException {
		return (this.projectHasNature(this.ANT_FRAMEWORK_NATURE_ID) || this.projectHasNature(this.ANT_APPLICATION_NATURE_ID));
	}

	/**
	 * @return true only if one of the WOLips incremental natures is installed.
	 *         False does not mean that this is an ant nature.
	 * @throws CoreException
	 */
	public boolean isIncremental() throws CoreException {
		return (this.projectHasNature(this.INCREMENTAL_APPLICATION_NATURE_ID) || this.projectHasNature(this.INCREMENTAL_FRAMEWORK_NATURE_ID));
	}

	/**
	 * @param isFramework
	 *            Replaces any currently set WOLips natures with the Ant Nature
	 *            for Framework or Application.
	 * @throws CoreException
	 */
	public void setAntNature(boolean isFramework) throws CoreException {
		if (isFramework) {
			this.setWONature(this.ANT_FRAMEWORK_NATURE_ID, null);
		} else {
			this.setWONature(this.ANT_APPLICATION_NATURE_ID, null);
		}
	}

	/**
	 * @param isFramework
	 * @param buildArgs
	 *            currently does nothing Replaces any currently set WOLips
	 *            natures with the incremental Nature for Framework or
	 *            Application.
	 * @throws CoreException
	 */
	public void setIncrementalNature(boolean isFramework, Map buildArgs) throws CoreException {
		if (isFramework) {
			this.setWONature(this.INCREMENTAL_FRAMEWORK_NATURE_ID, buildArgs);
		} else {
			this.setWONature(this.INCREMENTAL_APPLICATION_NATURE_ID, buildArgs);
		}
	}

	private void setWONature(String natureID, Map args) throws CoreException {
		if (null == args) {
			args = Collections.EMPTY_MAP;
		}
		IProjectDescription desc = this.getIProject().getDescription();
		// add / remove natures as needed, avoid setting the project description
		// more than once
		{
			boolean needsSave = false;
			List naturesList = new ArrayList(Arrays.asList(desc.getNatureIds()));
			if (!naturesList.contains(natureID)) {
				Iterator iter = naturesList.iterator();
				while (iter.hasNext()) {
					if (this.isWOLipsNature((String) iter.next())) {
						iter.remove();
					}
				}
				naturesList.add(natureID);
				desc.setNatureIds((String[]) naturesList.toArray(new String[naturesList.size()]));
				needsSave = true;
			}
			List buildersList = new ArrayList(Arrays.asList(desc.getBuildSpec()));
			for (Iterator builders = buildersList.iterator(); builders.hasNext();) {
				ICommand command = (ICommand) builders.next();
				String name = command.getBuilderName();
				if (name.equals(ProjectBuilder.INCREMENTAL_BUILDER_ID)) {
					command.setArguments(args);
					needsSave = true;
					desc.setBuildSpec((ICommand[]) buildersList.toArray(new ICommand[0]));
				}
			}
			if (needsSave) {
				_setDescription(this.getIProject(), desc);
			}
		}
	}

	private void _setDescription(final IProject f_project, final IProjectDescription f_desc) {
		_showProgress(new IRunnableWithProgress() {
			public void run(IProgressMonitor pm) {
				try {
					f_project.setDescription(f_desc, pm);
				} catch (CoreException up) {
					pm.done();
				}
			}
		});
	}

	private void _showProgress(IRunnableWithProgress rwp) {
		IWorkbench workbench = PlatformUI.getWorkbench();
		Shell shell = null;
		if (null != workbench) {
			IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
			if (null != window) {
				shell = window.getShell();
			}
		}
		ProgressMonitorDialog pmd = new ProgressMonitorDialog(shell);
		try {
			pmd.run(true, true, rwp);
		} catch (InvocationTargetException e) {
			// handle exception
			e.printStackTrace();
		} catch (InterruptedException e) {
			// handle cancelation
			e.printStackTrace();
		}
	}

	/**
	 * @return IProjectNature[]
	 * @throws CoreException
	 */
	public IProjectNature[] getWOLipsNatures() throws CoreException {
		Vector naturesVector = new Vector();
		for (int i = 0; i < this.WOLIPS_NATURES.length; i++) {
			if (this.projectHasNature(this.WOLIPS_NATURES[i])) {
				naturesVector.add(this.getIProject().getNature(this.WOLIPS_NATURES[i]));
			}
		}
		if (naturesVector.size() == 0)
			return new IProjectNature[0];
		return (IProjectNature[]) naturesVector.toArray(new IProjectNature[naturesVector.size()]);
	}

	/**
	 * @return String[]
	 * @throws CoreException
	 */
	private String[] getProjectNatures() throws CoreException {
		return this.getIProject().getDescription().getNatureIds();
	}

	/**
	 * Remove all WOLips Natures and in consequence, their builders -- the
	 * Natures do that in .deconfigure
	 * 
	 * @throws CoreException
	 */
	public void removeWOLipsNatures() throws CoreException {
		String[] projectNatures = this.getProjectNatures();
		List naturesList = new ArrayList(Arrays.asList(projectNatures));
		for (int i = 0; i < projectNatures.length; i++) {
			if (this.isWOLipsNature(projectNatures[i])) {
				naturesList.remove(projectNatures[i]);
			}
		}
		projectNatures = new String[naturesList.size()];
		projectNatures = (String[]) naturesList.toArray(projectNatures);
		IProjectDescription desc = this.getIProject().getDescription();
		desc.setNatureIds(projectNatures);
		_setDescription(this.getIProject(), desc);
	}

	/**
	 * Calls configure on all WOLips natures.
	 * 
	 * @throws CoreException
	 */
	public void callConfigure() throws CoreException {
		// This is needed by the project wizard
		IProjectNature[] projectNatures = this.getWOLipsNatures();
		for (int i = 0; i < projectNatures.length; i++) {
			projectNatures[i].configure();
		}
	}

	/**
	 * @return May return null.
	 * @throws CoreException
	 */
	public IProjectNature getIncrementalNature() throws CoreException {
		if (this.getIProject().hasNature(this.INCREMENTAL_APPLICATION_NATURE_ID)) {
			return this.getIProject().getNature(this.INCREMENTAL_APPLICATION_NATURE_ID);
		} else if (this.getIProject().hasNature(this.INCREMENTAL_FRAMEWORK_NATURE_ID)) {
			return this.getIProject().getNature(this.INCREMENTAL_FRAMEWORK_NATURE_ID);
		}
		return null;
	}

	/**
	 * @return May return null.
	 * @throws CoreException
	 */
	public IProjectNature getTargetbuilderNature() throws CoreException {
		return this.getIProject().getNature(this.TargetBuilderNatureID);
	}
}