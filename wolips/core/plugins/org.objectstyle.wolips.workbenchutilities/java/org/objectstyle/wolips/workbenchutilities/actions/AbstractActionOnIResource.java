/* ====================================================================
 *
 * The ObjectStyle Group Software License, Version 1.0
 *
 * Copyright (c) 2002, 2004 The ObjectStyle Group
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

package org.objectstyle.wolips.workbenchutilities.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

/**
 * @author uli
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 */
public abstract class AbstractActionOnIResource implements IObjectActionDelegate {

	private IProject project;

	private IResource actionResource;

	protected IWorkbenchPart part;

	/**
	 * @see java.lang.Object#Object()
	 */
	public AbstractActionOnIResource() {
		super();
	}

	/**
	 * @return Returns the IProject
	 */
	public IProject getIProject() {
		return this.project;
	}

	/**
	 * @return Returns the IResource the action was invoked on
	 */
	public IResource getActionResource() {
		return this.actionResource;
	}

	/**
	 * Method dispose.
	 */
	public void dispose() {
		this.project = null;
		this.actionResource = null;
		this.part = null;
	}

	/**
	 * Has to be implemented in the subclass.
	 */
	public abstract void run(IAction action);

	/**
	 * Resets the project when the selection is changed.
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		Object obj = (((IStructuredSelection) selection).getFirstElement());
		this.project = null;
		this.actionResource = null;
		if (obj != null && obj instanceof IResource) {
			this.actionResource = (IResource) obj;
			this.project = this.actionResource.getProject();
		}
		if (obj != null && obj instanceof ICompilationUnit) {
			this.actionResource = ((ICompilationUnit) obj).getResource();
			this.project = this.actionResource.getProject();
		}
		if (obj != null && obj instanceof IProject) {
			this.actionResource = (IProject) obj;
			this.project = (IProject) this.actionResource;
		}
		if (obj != null && obj instanceof IJavaProject) {
			this.actionResource = ((IJavaProject) obj).getProject();
			this.project = (IProject) this.actionResource;
		}
	}

	/**
	 * (non-Javadoc) Method declared on IObjectActionDelegate
	 * 
	 * @param action
	 * @param targetPart
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		this.part = targetPart;
	}
}
