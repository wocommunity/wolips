/* ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0 
 *
 * Copyright (c) 2004 - 2006 The ObjectStyle Group 
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
package org.objectstyle.wolips.ui.actions;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.window.Window;
import org.objectstyle.wolips.core.resources.types.project.IProjectPatternsets;
import org.objectstyle.wolips.workbenchutilities.actions.AbstractActionOnIResource;

/**
 * @author ulrich
 */
public abstract class AbstractPatternsetAction extends AbstractActionOnIResource {

	public IProjectPatternsets getProject() {
		return (IProjectPatternsets) this.getIProject().getAdapter(IProjectPatternsets.class);
	}

	/**
	 * @return the pattern
	 */
	public String getPattern() {
		String name = this.getActionResource().getName();
		String extension = this.getActionResource().getFileExtension();
		if (name != null && name.length() == 0)
			name = null;
		if (extension != null && extension.length() == 0)
			extension = null;
		if (name == null && extension == null) {
			return null;
		}
		if (name != null && extension != null && name.equals("." + extension))
			extension = null;
		String pattern = null;
		if (name != null && extension != null) {
			MessageDialogWithToggle messageDialogWithToggle = MessageDialogWithToggle.openOkCancelConfirm(this.part.getSite().getShell(), "Add pattern", "Add all resources with extension " + extension, "add by extension (otherwise by name)", true, null, null);
			if (messageDialogWithToggle.getReturnCode() == Window.CANCEL)
				return null;
			if (messageDialogWithToggle.getToggleState()) {
				if (this.getActionResource() instanceof IContainer)
					pattern = "**/*." + extension + "/**";
				pattern = "**/*." + extension;
			} else {
				if (this.getActionResource() instanceof IContainer)
					pattern = "**/" + name + "." + extension + "/**";
				pattern = "**/" + name + "." + extension;
			}
		}
		if (name != null) {
			if (this.getActionResource() instanceof IContainer)
				pattern = "**/" + name + "/**";
			pattern = "**/" + name;
		}
		if (extension != null) {
			if (this.getActionResource() instanceof IContainer)
				pattern = "**/*." + extension + "/**";
			pattern = "**/*." + extension;
		}
		return pattern;
	}

	public void run(IAction action) {
		TouchAllFilesOperation touchAllFilesOperation = new TouchAllFilesOperation(this.getIProject());
		try {
			touchAllFilesOperation.run(new NullProgressMonitor());
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}