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

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author ulrich
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class BuildLaunchEngine extends AbstractEngine {
	private String vmInstallTypeId;
	private String vmInstallName;
	private String attrLocation;
	private String workingDirectory;
	private IProject project;
	private TemplateFolder[] templateFolder;
	private TemplateFolder selectedTemplateFolder;
	/**
	 * @return Returns the attrLocation.
	 */
	public String getAttrLocation() {
		return attrLocation;
	}
	/**
	 * @param attrLocation The attrLocation to set.
	 */
	public void setAttrLocation(String attrLocation) {
		this.attrLocation = attrLocation;
	}
	/**
	 * @return Returns the vmInstallName.
	 */
	public String getVmInstallName() {
		return vmInstallName;
	}
	/**
	 * @param vmInstallName The vmInstallName to set.
	 */
	public void setVmInstallName(String vmInstallName) {
		this.vmInstallName = vmInstallName;
	}
	/**
	 * @return Returns the vmInstallTypeId.
	 */
	public String getVmInstallTypeId() {
		return vmInstallTypeId;
	}
	/**
	 * @param vmInstallTypeId The vmInstallTypeId to set.
	 */
	public void setVmInstallTypeId(String vmInstallTypeId) {
		this.vmInstallTypeId = vmInstallTypeId;
	}
	/**
	 * @return Returns the workingDirectory.
	 */
	public String getWorkingDirectory() {
		return workingDirectory;
	}
	/**
	 * @param workingDirectory The workingDirectory to set.
	 */
	public void setWorkingDirectory(String workingDirectory) {
		this.workingDirectory = workingDirectory;
	}
	
	/**
	 * inits the engine
	 */
	public void init() throws Exception {
		templateFolder = TemplateEnginePlugin.getTemplateFolder(TemplateEnginePlugin.WOComponent);
		selectedTemplateFolder = templateFolder[0];
		super.init();
	}
	
	public void run(IProgressMonitor monitor) throws InvocationTargetException {
		TemplateEngine templateEngine = new TemplateEngine();
		try {
			templateEngine.init();
		} catch (Exception e) {
			TemplateEnginePlugin.log(e);
			throw new InvocationTargetException(e);
		}
		this.addTemplate(new TemplateDefinition(
				"buildlaunch/build.launch.vm", this.getProject().getLocation().toOSString(), this.getProjectName() + "."
						+ "build.launch","build.launch"));
		try {
			super.run(monitor);
		} catch (Exception e) {
			TemplateEnginePlugin.log(e);
			throw new InvocationTargetException(e);
		}
	}
	/**
	 * @return Returns the project.
	 */
	public IProject getProject() {
		return project;
	}
	/**
	 * @param project The project to set.
	 */
	public void setProject(IProject project) {
		this.project = project;
	}
}
