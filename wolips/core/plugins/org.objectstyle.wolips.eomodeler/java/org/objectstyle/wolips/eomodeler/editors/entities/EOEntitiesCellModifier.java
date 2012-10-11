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
package org.objectstyle.wolips.eomodeler.editors.entities;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.objectstyle.wolips.baseforuiplugins.utils.KeyComboBoxCellEditor;
import org.objectstyle.wolips.eomodeler.Messages;
import org.objectstyle.wolips.eomodeler.core.model.EOEntity;
import org.objectstyle.wolips.eomodeler.core.model.EOModel;
import org.objectstyle.wolips.eomodeler.utils.TablePropertyCellModifier;
import org.objectstyle.wolips.eomodeler.utils.TableUtils;

public class EOEntitiesCellModifier extends TablePropertyCellModifier {
	private static final String NO_PARENT_VALUE = Messages.getString("EOEntitiesCellModifier.noParent");

	private CellEditor[] myCellEditors;

	private List myEntityNames;

	public EOEntitiesCellModifier(TableViewer _modelTableViewer, CellEditor[] _cellEditors) {
		super(_modelTableViewer);
		myCellEditors = _cellEditors;
	}

	protected boolean _canModify(Object _element, String _property) {
		if (EOEntity.PARENT.equals(_property)) {
			EOModel model = (EOModel) getTableViewer().getInput();
			myEntityNames = new LinkedList(model.getModelGroup().getEntityNames());
			myEntityNames.add(0, EOEntitiesCellModifier.NO_PARENT_VALUE);
			String[] entityNames = (String[]) myEntityNames.toArray(new String[myEntityNames.size()]);
			int columnNumber = TableUtils.getColumnNumberForTablePropertyNamed(EOEntity.class.getName(), _property);
			if (columnNumber != -1) {
				KeyComboBoxCellEditor cellEditor = (KeyComboBoxCellEditor) myCellEditors[columnNumber];
				cellEditor.setItems(entityNames);
			}
		}
		return true;
	}

	public Object getValue(Object _element, String _property) {
		EOEntity entity = (EOEntity) _element;
		Object value = null;
		if (EOEntity.PARENT.equals(_property)) {
			EOEntity parent = entity.getParent();
			String parentName;
			if (parent == null) {
				parentName = EOEntitiesCellModifier.NO_PARENT_VALUE;
			} else {
				parentName = parent.getName();
			}
			value = Integer.valueOf(myEntityNames.indexOf(parentName));
		} else {
			value = super.getValue(_element, _property);
		}
		return value;
	}

	protected boolean _modify(Object _element, String _property, Object _value) throws Throwable {
		boolean modified = false;
		EOEntity entity = (EOEntity) _element;
		if (EOEntity.PARENT.equals(_property)) {
			Integer parentNameIndex = (Integer) _value;
			int parentNameIndexInt = parentNameIndex.intValue();
			String parentName = (parentNameIndexInt == -1) ? null : (String) myEntityNames.get(parentNameIndexInt);
			if (EOEntitiesCellModifier.NO_PARENT_VALUE.equals(parentName)) {
				entity.setParent(null);
			} else {
				EOEntity parent = entity.getModel().getModelGroup().getEntityNamed(parentName);
				entity.setParent(parent);
			}
			modified = true;
		}
		return modified;
	}
}
