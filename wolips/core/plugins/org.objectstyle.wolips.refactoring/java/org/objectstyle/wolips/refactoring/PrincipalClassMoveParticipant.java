/*
 * ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0
 * 
 * Copyright (c) 2005 The ObjectStyle Group and individual authors of the
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

package org.objectstyle.wolips.refactoring;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.MoveParticipant;
import org.objectstyle.wolips.core.resources.types.project.IProjectAdapter;

/**
 * Changes the package name of a WOLips project's Principal Class when it gets
 * moved.
 * 
 * @author mschrag
 */
public class PrincipalClassMoveParticipant extends MoveParticipant {
	private IProjectAdapter myProject;

	private String myName;

	private IJavaElement myJavaDestination;

	public PrincipalClassMoveParticipant() {
		super();
	}

	protected boolean initialize(Object _element) {
		myProject = PrincipalClassMoveParticipant.getInitializedProject(_element);
		boolean initialized = false;
		if (myProject != null) {
			Object destination = getArguments().getDestination();
			if (destination instanceof IPackageFragment || destination instanceof IType) {
				myName = ((IType) _element).getElementName();
				myJavaDestination = (IJavaElement) destination;
				initialized = true;
			}
		}
		return initialized;
	}

	public String getName() {
		return "Move Principal Class";
	}

	public RefactoringStatus checkConditions(IProgressMonitor _pm, CheckConditionsContext _context) throws OperationCanceledException {
		RefactoringStatus refactoringStatus = new RefactoringStatus();
		return refactoringStatus;
	}

	public Change createChange(IProgressMonitor _pm) throws CoreException, OperationCanceledException {
		Change change = null;
		if (myProject != null) {
			// String newName = myJavaDestination.getElementName();
			String newFullyQualifiedName;
			if (myJavaDestination instanceof IType) {
				newFullyQualifiedName = ((IType) myJavaDestination).getFullyQualifiedName() + '$' + myName;
			} else if (myJavaDestination instanceof IPackageFragment) {
				IPackageFragment destinationPackage = (IPackageFragment) myJavaDestination;
				if (destinationPackage.isDefaultPackage()) {
					newFullyQualifiedName = myName;
				} else {
					System.out.println("PrincipalClassMoveParticipant.createChange: " + myJavaDestination.getElementName() + ", " + myName);
					newFullyQualifiedName = myJavaDestination.getElementName() + '.' + myName;
				}
			} else {
				newFullyQualifiedName = null;
			}
			if (newFullyQualifiedName != null) {
				change = new PrincipalClassChange(myProject, newFullyQualifiedName);
			}
		}
		return change;
	}

	public static IProjectAdapter getInitializedProject(Object _element) {
		IProjectAdapter initializedProject = null;
		try {
			if (_element instanceof IType) {
				IType sourceType = (IType) _element;
				IProjectAdapter project = (IProjectAdapter) sourceType.getJavaProject().getProject().getAdapter(IProjectAdapter.class);
				String principalClass = project.getBuildProperties().getPrincipalClass(true);
				String fullyQualifiedName = sourceType.getFullyQualifiedName();
				if (principalClass != null && principalClass.equals(fullyQualifiedName)) {
					initializedProject = project;
				}
			} else if (_element instanceof IPackageFragment) {
				IPackageFragment packageFragment = (IPackageFragment) _element;
				IProjectAdapter project = (IProjectAdapter) packageFragment.getJavaProject().getProject().getAdapter(IProjectAdapter.class);
				String principalClass = project.getBuildProperties().getPrincipalClass(true);
				if (principalClass != null) {
					int dotIndex = principalClass.lastIndexOf('.');
					if (dotIndex != -1) {
						String principalClassPackage = principalClass.substring(0, dotIndex);
						String packageName = packageFragment.getElementName();
						if (principalClassPackage.equals(packageName)) {
							initializedProject = project;
						}
					}
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return initializedProject;
	}

}
