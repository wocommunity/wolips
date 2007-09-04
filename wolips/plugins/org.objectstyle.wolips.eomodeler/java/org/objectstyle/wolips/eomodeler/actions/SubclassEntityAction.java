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
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.objectstyle.wolips.eomodeler.Messages;
import org.objectstyle.wolips.eomodeler.core.model.EOEntity;
import org.objectstyle.wolips.eomodeler.core.model.EOModel;
import org.objectstyle.wolips.eomodeler.core.model.IEOEntityRelative;
import org.objectstyle.wolips.eomodeler.core.model.InheritanceType;
import org.objectstyle.wolips.eomodeler.core.utils.EOModelUtils;
import org.objectstyle.wolips.eomodeler.editors.entity.SubclassEntityDialog;
import org.objectstyle.wolips.eomodeler.utils.ErrorUtils;

public class SubclassEntityAction implements IWorkbenchWindowActionDelegate, IObjectActionDelegate {
	private EOEntity _entity;

	private EOModel _model;

	private IWorkbenchWindow _window;

	public void init(IWorkbenchWindow window) {
		_window = window;
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		_window = targetPart.getSite().getWorkbenchWindow();
	}

	public void dispose() {
		// DO NOTHING
	}

	public void selectionChanged(IAction action, ISelection selection) {
		_model = null;
		_entity = null;
		if (selection instanceof IStructuredSelection) {
			Object selectedObject = ((IStructuredSelection) selection).getFirstElement();
			if (selectedObject instanceof EOModel) {
				_model = (EOModel) selectedObject;
			} else if (selectedObject instanceof IEOEntityRelative) {
				_entity = ((IEOEntityRelative) selectedObject).getEntity();
				_model = _entity.getModel();
			}
		}
	}

	public void run(IAction action) {
		if (_model != null) {
			SubclassEntityDialog dialog = new SubclassEntityDialog(_window.getShell(), _model, _entity, _model.getModelGroup().getEditingModel());
			dialog.setBlockOnOpen(true);
			int results = dialog.open();
			if (results == Window.OK) {
				String entityName = dialog.getEntityName();
				if (entityName != null && entityName.trim().length() > 0) {
					try {
						EOEntity parentEntity = dialog.getParentEntity();
						InheritanceType inheritanceType = dialog.getInheritanceType();
						String restrictingQualifier = dialog.getRestrictingQualifier();
						EOModel destinationModel = dialog.getDestinationModel();
						SubclassOperation operation = new SubclassOperation(parentEntity, inheritanceType, destinationModel, entityName, restrictingQualifier);
						operation.addContext(EOModelUtils.getUndoContext(_model));
						IOperationHistory operationHistory = PlatformUI.getWorkbench().getOperationSupport().getOperationHistory();
						operationHistory.execute(operation, null, null);
					} catch (Throwable e) {
						ErrorUtils.openErrorDialog(Display.getDefault().getActiveShell(), e);
					}
				} else {
					MessageDialog.openError(_window.getShell(), Messages.getString("Subclass.noEntityNameTitle"), Messages.getString("Subclass.noEntityNameMessage"));//$NON-NLS-1$
				}
			}
		} else {
			MessageDialog.openError(_window.getShell(), Messages.getString("Subclass.noModelSelectedTitle"), Messages.getString("Subclass.noModelSelectedMessage"));//$NON-NLS-1$
		}
	}
}
