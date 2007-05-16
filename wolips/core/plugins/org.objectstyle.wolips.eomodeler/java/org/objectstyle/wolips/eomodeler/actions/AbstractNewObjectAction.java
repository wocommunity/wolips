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

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.objectstyle.wolips.eomodeler.core.model.EOModelException;
import org.objectstyle.wolips.eomodeler.core.model.EOModelObject;
import org.objectstyle.wolips.eomodeler.core.model.EOModelVerificationFailure;
import org.objectstyle.wolips.eomodeler.core.utils.EOModelUtils;
import org.objectstyle.wolips.eomodeler.utils.ErrorUtils;

public abstract class AbstractNewObjectAction<T extends EOModelObject, U extends EOModelObject> implements IWorkbenchWindowActionDelegate, IObjectActionDelegate {
	private IWorkbenchWindow _window;

	private Class<T> _parentType;

	private T _selectedParent;

	private String _label;
	
	private IStructuredSelection _selection;

	public AbstractNewObjectAction(Class<T> parentType, String label) {
		_label = label;
		_parentType = parentType;
	}

	public String getLabel() {
		return _label;
	}

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
		_selectedParent = null;
		if (selection instanceof IStructuredSelection) {
			_selection = ((IStructuredSelection)selection);
			Object selectedObject = ((IStructuredSelection) selection).getFirstElement();
			if (selectedObject instanceof EOModelObject) {
				_selectedParent = (T) EOModelUtils.getRelated(_parentType, (EOModelObject) selectedObject);
			}
		}
	}
	
	public IStructuredSelection getSelection() {
		return _selection;
	}
	
	public void init(IViewPart view) {
		// DO NOTHING
	}

	public void run(IAction action) {
		try {
			if (_selectedParent != null) {
				NewOperation operation = new NewOperation(_selectedParent);
				operation.addContext(EOModelUtils.getUndoContext(_selectedParent));
				IOperationHistory operationHistory = PlatformUI.getWorkbench().getOperationSupport().getOperationHistory();
				operationHistory.execute(operation, null, null);
			} else {
				MessageDialog.openError(_window.getShell(), getNoSelectionTitle(), getNoSelectionMessage());
			}
		} catch (Throwable e) {
			ErrorUtils.openErrorDialog(Display.getDefault().getActiveShell(), e);
		}
	}

	protected abstract String getNoSelectionTitle();

	protected abstract String getNoSelectionMessage();

	protected abstract U createChild(T parent, Set<EOModelVerificationFailure> failures) throws EOModelException;

	protected class NewOperation extends AbstractOperation {
		private T _parent;

		private U _child;

		public NewOperation(T parent) {
			super(AbstractNewObjectAction.this.getLabel());
			_parent = parent;
		}

		@Override
		public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
			try {
				Set<EOModelVerificationFailure> failures = new HashSet<EOModelVerificationFailure>();
				_child = AbstractNewObjectAction.this.createChild(_parent, failures);
				return Status.OK_STATUS;
			} catch (EOModelException e) {
				throw new ExecutionException("Failed to add new object.", e);
			}
		}

		@Override
		public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
			return execute(monitor, info);
		}

		@Override
		public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
			try {
				Set<EOModelVerificationFailure> failures = new HashSet<EOModelVerificationFailure>();
				_child._removeFromModelParent(failures);
				return Status.OK_STATUS;
			} catch (EOModelException e) {
				throw new ExecutionException("Failed to remove object.", e);
			}
		}
	}
}
