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
package org.objectstyle.wolips.core.resources.internal.build;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

public abstract class Nature implements IProjectNature {
	public final static String INCREMENTAL_FRAMEWORK_ID = "org.objectstyle.wolips.incrementalframeworknature";

	public final static String INCREMENTAL_APPLICATION_ID = "org.objectstyle.wolips.incrementalapplicationnature";

	public final static String ANT_FRAMEWORK_ID = "org.objectstyle.wolips.antframeworknature";

	public final static String ANT_APPLICATION_ID = "org.objectstyle.wolips.antapplicationnature";

	private final static String TARGET_BUILDER_ID = "org.objectstyle.wolips.targetbuilder.targetbuildernature";

	private final static String[] NATURES = new String[] { INCREMENTAL_FRAMEWORK_ID, INCREMENTAL_APPLICATION_ID, ANT_FRAMEWORK_ID, ANT_APPLICATION_ID };

	private IProject project;

	public Nature() {
		super();
	}

	public abstract String getBuilderID();

	public void configure() throws CoreException {
		if (!this.isBuilderInstalled(this.getBuilderID())) {
			this.installBuilder(this.getBuilderID());
		}
	}

	public void deconfigure() throws CoreException {
		if (this.isBuilderInstalled(this.getBuilderID())) {
			this.removeBuilder(this.getBuilderID());
		}
	}

	public IProject getProject() {
		return this.project;
	}

	public void setProject(IProject project) {
		this.project = project;
	}

	public abstract boolean isFramework();

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
			desc = this.getProject().getDescription();
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
				this.getProject().setDescription(desc, null);
			}
		} finally {
			desc = null;
			coms = null;
			newIc = null;
			command = null;
		}
	}

	/**
	 * Method removeBuilder.
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
			desc = this.getProject().getDescription();
			coms = desc.getBuildSpec();
			comList = new ArrayList<ICommand>();
			tmp = Arrays.asList(coms);
			comList.addAll(tmp);
			boolean foundJBuilder = false;
			for (int i = 0; i < comList.size(); i++) {
				if (comList.get(i).getBuilderName().equals(aBuilder)) {
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
				this.getProject().setDescription(desc, null);
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
	 * Method isBuilderInstalled.
	 * 
	 * @param anID
	 * @return boolean
	 * @throws CoreException
	 */
	private boolean isBuilderInstalled(String anID) throws CoreException {
		ICommand[] nids = this.getProject().getDescription().getBuildSpec();
		for (int i = 0; i < nids.length; i++) {
			if (nids[i].getBuilderName().equals(anID))
				return true;
		}
		return false;
	}
	
	/**
	 * @param project
	 * @param monitor
	 * @return true when the nature successfully added
	 * @throws CoreException
	 */
	public static boolean addIncrementalFrameworkNatureToProject(IProject project, IProgressMonitor monitor) throws CoreException {
		return addNatureToProject(Nature.INCREMENTAL_FRAMEWORK_ID, project, monitor);
	}

	/**
	 * @param project
	 * @param monitor
	 * @return true when the nature successfully added
	 * @throws CoreException
	 */
	public static boolean addIncrementalApplicationNatureToProject(IProject project, IProgressMonitor monitor) throws CoreException {
		return addNatureToProject(Nature.INCREMENTAL_APPLICATION_ID, project, monitor);
	}

	/**
	 * @param project
	 * @param monitor
	 * @return true when the nature successfully added
	 * @throws CoreException
	 */
	public static boolean addAntFrameworkNatureToProject(IProject project, IProgressMonitor monitor) throws CoreException {
		return addNatureToProject(Nature.ANT_FRAMEWORK_ID, project, monitor);
	}

	/**
	 * @param project
	 * @param monitor
	 * @return true when the nature successfully added
	 * @throws CoreException
	 */
	public static boolean addAntApplicationNatureToProject(IProject project, IProgressMonitor monitor) throws CoreException {
		return addNatureToProject(Nature.ANT_APPLICATION_ID, project, monitor);
	}

	public static boolean addNatureToProject(String id, IProject project, IProgressMonitor monitor) throws CoreException {
		IProjectNature nature = project.getNature(id);
		if (nature == null) {
			IProjectDescription description = project.getDescription();
			List<String> naturesList = new ArrayList<String>(Arrays.asList(description.getNatureIds()));
			naturesList.add(id);
			description.setNatureIds(naturesList.toArray(new String[naturesList.size()]));
			project.setDescription(description, monitor);
		}
		return project.isNatureEnabled(id);
	}

	public static void removeNaturesFromProject(IProject project, IProgressMonitor monitor) throws CoreException {
		setNatureForProject(null, false, project, monitor);
	}
	
	public static void setNatureForProject(String id, boolean useTargetBuilder, IProject project, IProgressMonitor monitor) throws CoreException {
		IProjectDescription description = project.getDescription();
		List<String> naturesList = new ArrayList<String>(Arrays.asList(description.getNatureIds()));
		naturesList.remove(Nature.ANT_APPLICATION_ID);
		naturesList.remove(Nature.ANT_FRAMEWORK_ID);
		naturesList.remove(Nature.INCREMENTAL_APPLICATION_ID);
		naturesList.remove(Nature.INCREMENTAL_FRAMEWORK_ID);
		naturesList.remove(Nature.TARGET_BUILDER_ID);
		if (id != null) {
			naturesList.add(id);
		}
		if (useTargetBuilder) {
			naturesList.add(Nature.TARGET_BUILDER_ID);
		}
		description.setNatureIds(naturesList.toArray(new String[naturesList.size()]));
		project.setDescription(description, monitor);
	}
	
	public static void removeNatureFromProject(String id, IProject project, IProgressMonitor monitor) throws CoreException {
		IProjectNature nature = project.getNature(id);
		if (nature != null) {
			IProjectDescription description = project.getDescription();
			List<String> naturesList = new ArrayList<String>(Arrays.asList(description.getNatureIds()));
			naturesList.remove(id);
			description.setNatureIds(naturesList.toArray(new String[naturesList.size()]));
			project.setDescription(description, monitor);
		}
	}

	/**
	 * @return The wonature if one is installes otherwise null
	 * @throws CoreException
	 */
	public static Nature getNature(IProject project) throws CoreException {
		if (!project.isAccessible() || !project.exists()) {
			return null;
		}
		Nature nature = null;
		for (int i = 0; i < NATURES.length; i++) {
			nature = (Nature) project.getNature(NATURES[i]);
			if (nature != null) {
				return nature;
			}
		}
		return nature;
	}
}
