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

import java.util.Arrays;
import java.util.Set;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.objectstyle.wolips.baseforuiplugins.utils.ErrorUtils;
import org.objectstyle.wolips.eomodeler.Messages;
import org.objectstyle.wolips.eomodeler.core.model.EOModelObject;
import org.objectstyle.wolips.eomodeler.core.model.EOModelReferenceFailure;
import org.objectstyle.wolips.eomodeler.core.utils.EOModelUtils;
import org.objectstyle.wolips.eomodeler.editors.EOModelErrorDialog;

public class DeleteAction extends Action implements IObjectActionDelegate {
	private ISelection mySelection;

	public void dispose() {
		// DO NOTHING
	}

	public void selectionChanged(IAction _action, ISelection _selection) {
		mySelection = _selection;
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		// DO NOTHING
	}

	public void run() {
		Shell activeShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		Object[] selectedObjects = null;
		if (mySelection instanceof IStructuredSelection) {
			selectedObjects = ((IStructuredSelection) mySelection).toArray();
		}
		if (selectedObjects != null) {
			try {
				Set<EOModelReferenceFailure> referenceFailures = EOModelUtils.getReferenceFailures(selectedObjects);
				boolean delete = false;
				if (referenceFailures.isEmpty()) {
					delete = MessageDialog.openConfirm(activeShell, Messages.getString("delete.objectsTitle"), Messages.getString("delete.objectsMessage"));
				}
				else {
					int results = new EOModelErrorDialog(activeShell, referenceFailures, true).open();
					if (results == EOModelErrorDialog.DELETE_ANYWAY_ID) {
						delete = MessageDialog.openConfirm(activeShell, Messages.getString("deleteAnyway.objectsTitle"), Messages.getString("deleteAnyway.objectsMessage"));
						if (delete) {
							Set<EOModelObject> recommendedDeletions = EOModelUtils.getRecommendedDeletions(selectedObjects);
							selectedObjects = recommendedDeletions.toArray();
							
							Set<EOModelReferenceFailure> deleteAnywayReferenceFailures = EOModelUtils.getReferenceFailures(selectedObjects);
							if (!deleteAnywayReferenceFailures.isEmpty()) {
								delete = false;
								new EOModelErrorDialog(activeShell, referenceFailures, false).open();
							}
						}
					}
				}
				
				if (delete) {
					try {
						SimpleCompositeOperation compositeOperation = new SimpleCompositeOperation(EOModelUtils.getOperationLabel("Delete", Arrays.asList(selectedObjects)));
						for (Object obj : selectedObjects) {
							if (obj instanceof EOModelObject) {
								EOModelObject eoModelObject = (EOModelObject) obj;
								DeleteOperation operation = new DeleteOperation(eoModelObject);
								compositeOperation.add(operation);
							}
						}
						compositeOperation.addContext(EOModelUtils.getUndoContext(selectedObjects));
						IOperationHistory operationHistory = PlatformUI.getWorkbench().getOperationSupport().getOperationHistory();
						operationHistory.execute(compositeOperation, null, null);
					} catch (ExecutionException e) {
						throw new RuntimeException("Failed to delete.", e);
					}
				}
			} catch (Throwable t) {
				ErrorUtils.openErrorDialog(Display.getDefault().getActiveShell(), t);
			}
		}
	}

	public void runWithEvent(Event _event) {
		run();
	}

	public void run(IAction _action) {
		run();
	}
}
