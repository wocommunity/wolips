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

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.objectstyle.wolips.eomodeler.model.EOModelObject;
import org.objectstyle.wolips.eomodeler.utils.EOModelUtils;
import org.objectstyle.wolips.eomodeler.utils.ErrorUtils;

public class CopyAction extends Action implements IWorkbenchWindowActionDelegate, IObjectActionDelegate {
	private ISelection _selection;

	public CopyAction() {
		// DO NOTHING
	}

	public void dispose() {
		// DO NOTHING
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		// DO NOTHING
	}

	public void init(IWorkbenchWindow window) {
		// DO NOTHING
	}

	public void selectionChanged(IAction action, ISelection selection) {
		_selection = selection;
	}

	public void run() {
		try {
			Object[] selectedObjects = null;
			if (_selection instanceof IStructuredSelection) {
				selectedObjects = ((IStructuredSelection) _selection).toArray();
			}
			List<EOModelObject> selectedObjectsList = new LinkedList<EOModelObject>();
			if (selectedObjects != null) {
				for (Object selectedObject : selectedObjects) {
					if (selectedObject instanceof EOModelObject) {
						EOModelObject<?> object = (EOModelObject) selectedObject;
						selectedObjectsList.add(object._cloneModelObject());
					}
				}
				CopyOperation operation = new CopyOperation(selectedObjectsList);
				operation.addContext(EOModelUtils.getUndoContext(selectedObjects));
				IOperationHistory operationHistory = PlatformUI.getWorkbench().getOperationSupport().getOperationHistory();
				operationHistory.execute(operation, null, null);
			}
		} catch (Throwable t) {
			ErrorUtils.openErrorDialog(Display.getDefault().getActiveShell(), t);
		}
	}

	public void runWithEvent(Event event) {
		run();
	}

	public void run(IAction action) {
		run();
	}

	protected class CopyOperation extends AbstractOperation {
		private List<EOModelObject> _objects;

		private ISelection _previousSelection;

		private long _previousSelectionTime;

		public CopyOperation(List<EOModelObject> objects) {
			super(EOModelUtils.getOperationLabel("Copy", objects));
			_objects = objects;
		}

		@Override
		public IStatus execute(IProgressMonitor monitor, IAdaptable info) {
			_previousSelection = LocalSelectionTransfer.getTransfer().getSelection();
			_previousSelectionTime = LocalSelectionTransfer.getTransfer().getSelectionSetTime();
			LocalSelectionTransfer.getTransfer().setSelection(new StructuredSelection(_objects));
			LocalSelectionTransfer.getTransfer().setSelectionSetTime(System.currentTimeMillis());
			return Status.OK_STATUS;
		}

		@Override
		public IStatus redo(IProgressMonitor monitor, IAdaptable info) {
			return execute(monitor, info);
		}

		@Override
		public IStatus undo(IProgressMonitor monitor, IAdaptable info) {
			LocalSelectionTransfer.getTransfer().setSelection(_previousSelection);
			LocalSelectionTransfer.getTransfer().setSelectionSetTime(_previousSelectionTime);
			return Status.OK_STATUS;
		}

	}
}
