/*
 * ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0
 * 
 * Copyright (c) 2006 The ObjectStyle Group and individual authors of the
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
package org.objectstyle.wolips.eomodeler.actions;

import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.objectstyle.wolips.eomodeler.Messages;
import org.objectstyle.wolips.eomodeler.model.AbstractEOAttributePath;
import org.objectstyle.wolips.eomodeler.utils.EOModelUtils;
import org.objectstyle.wolips.eomodeler.utils.ErrorUtils;

public class FlattenAction implements IWorkbenchWindowActionDelegate, IObjectActionDelegate {
	private IWorkbenchWindow _window;

	private AbstractEOAttributePath _attributePath;

	public void dispose() {
		// DO NOTHING
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		_window = targetPart.getSite().getWorkbenchWindow();
	}

	public void init(IWorkbenchWindow window) {
		_window = window;
	}

	public void selectionChanged(IAction action, ISelection selection) {
		_attributePath = null;
		if (selection instanceof IStructuredSelection) {
			Object selectedObject = ((IStructuredSelection) selection).getFirstElement();
			if (selectedObject instanceof AbstractEOAttributePath) {
				_attributePath = (AbstractEOAttributePath) selectedObject;
			}
		}
	}

	public void run(IAction action) {
		try {
			if (_attributePath != null) {
				FlattenOperation operation = new FlattenOperation(_attributePath);
				operation.addContext(EOModelUtils.getUndoContext(_attributePath));
				IOperationHistory operationHistory = PlatformUI.getWorkbench().getOperationSupport().getOperationHistory();
				operationHistory.execute(operation, null, null);
			} else {
				MessageDialog.openError(_window.getShell(), Messages.getString("EORelationship.noRelationshipOrAttributeSelectedTitle"), Messages.getString("EORelationship.noRelationshipOrAttributeSelectedMessage"));//$NON-NLS-1$
			}
		} catch (Throwable e) {
			ErrorUtils.openErrorDialog(Display.getDefault().getActiveShell(), e);
		}
	}
}
