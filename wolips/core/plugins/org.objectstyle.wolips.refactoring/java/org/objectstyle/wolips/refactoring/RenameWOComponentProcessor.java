/* ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0 
 *
 * Copyright (c) 2007 The ObjectStyle Group 
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
package org.objectstyle.wolips.refactoring;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.corext.refactoring.JDTRefactoringDescriptorComment;
import org.eclipse.jdt.internal.corext.refactoring.RefactoringCoreMessages;
import org.eclipse.jdt.internal.corext.refactoring.changes.DynamicValidationStateChange;
import org.eclipse.jdt.internal.corext.util.Messages;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.resource.RenameResourceDescriptor;
import org.eclipse.ltk.internal.core.refactoring.resource.RenameResourceProcessor;
import org.objectstyle.wolips.locate.LocatePlugin;

public class RenameWOComponentProcessor extends RenameResourceProcessor {

	private final IResource _resource;
	
	public RenameWOComponentProcessor(IResource resource) {
		super(resource);
		_resource = resource;
	}
	
	@Override
	public Change createChange(final IProgressMonitor pm) throws JavaModelException {
		pm.beginTask("", 1); //$NON-NLS-1$
		try {
			String project= null;
			if (_resource.getType() != IResource.PROJECT) {
				project= _resource.getProject().getName();
			}
			// TODO Replace Core messages. 
			final String header= Messages.format(RefactoringCoreMessages.RenameResourceChange_descriptor_description, new String[] { _resource.getFullPath().toString(), getNewResourceName()});
			final String description= Messages.format(RefactoringCoreMessages.RenameResourceChange_descriptor_description_short, _resource.getName());
			final String comment= new JDTRefactoringDescriptorComment(project, this, header).asString();
			final int flags= RefactoringDescriptor.STRUCTURAL_CHANGE | RefactoringDescriptor.MULTI_CHANGE | RefactoringDescriptor.BREAKING_CHANGE;
			final RenameResourceDescriptor descriptor= new RenameResourceDescriptor();
			descriptor.setProject(project);
			descriptor.setDescription(description);
			descriptor.setComment(comment);
			descriptor.setFlags(flags);
			descriptor.setResourcePath(_resource.getLocation());
			descriptor.setNewName(getNewResourceName() + ".wo");
			return new DynamicValidationStateChange(new RenameWOComponentChange(descriptor, _resource, getNewResourceName() + ".wo", comment));
		} finally {
			pm.done();
		}
	}

	@Override
	public void setNewResourceName(String newName) {
		String name = LocatePlugin.getDefault().fileNameWithoutExtension(newName);
		super.setNewResourceName(name);
	}
	
	@Override
	public RefactoringStatus validateNewElementName(String newName) {
		//TODO Return custom error message
		IProject project = _resource.getProject();
		IJavaProject jProject = JavaCore.create(project);
		String sourceLevel = jProject.getOption(JavaCore.COMPILER_SOURCE, true);
		String compliance = jProject.getOption(JavaCore.COMPILER_COMPLIANCE, true);
		IStatus status = JavaConventions.validateCompilationUnitName(newName + ".java",
				sourceLevel, compliance);
		if (!status.isOK())
			return RefactoringStatus.create(status);
		return super.validateNewElementName(newName);
	}
}
