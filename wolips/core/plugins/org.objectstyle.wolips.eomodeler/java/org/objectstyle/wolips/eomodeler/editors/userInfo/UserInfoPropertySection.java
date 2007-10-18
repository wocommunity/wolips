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
package org.objectstyle.wolips.eomodeler.editors.userInfo;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.objectstyle.wolips.eomodeler.Messages;
import org.objectstyle.wolips.eomodeler.core.model.EOModelParserDataStructureFactory;
import org.objectstyle.wolips.eomodeler.core.model.IUserInfoable;
import org.objectstyle.wolips.eomodeler.core.utils.NotificationMap;
import org.objectstyle.wolips.eomodeler.core.wocompat.PropertyListSerialization;
import org.objectstyle.wolips.eomodeler.utils.AddRemoveButtonGroup;
import org.objectstyle.wolips.eomodeler.utils.EMTextCellEditor;
import org.objectstyle.wolips.eomodeler.utils.StayEditingCellEditorListener;
import org.objectstyle.wolips.eomodeler.utils.TableUtils;

public class UserInfoPropertySection extends AbstractPropertySection {
	public static final String KEY = "key";

	public static final String VALUE = "value";

	public static final String[] COLUMNS = { UserInfoPropertySection.KEY, UserInfoPropertySection.VALUE };

	private TableViewer myUserInfoTableViewer;

	private Text myValueText;

	private AddRemoveButtonGroup myAddRemoveButtonGroup;

	private IUserInfoable myUserInfoable;

	private UserInfoListener myUserInfoListener;

	private Object mySelectedKey;

	private boolean myValueTextDirty;

	public UserInfoPropertySection() {
		myUserInfoListener = new UserInfoListener();
	}

	public void createControls(Composite _parent, TabbedPropertySheetPage _tabbedPropertySheetPage) {
		super.createControls(_parent, _tabbedPropertySheetPage);
		Composite composite = getWidgetFactory().createFlatFormComposite(_parent);

		myUserInfoTableViewer = TableUtils.createTableViewer(composite, SWT.BORDER | SWT.FLAT | SWT.FULL_SELECTION | SWT.SINGLE, "UserInfo", UserInfoPropertySection.COLUMNS, new UserInfoContentProvider(), new UserInfoLabelProvider(UserInfoPropertySection.COLUMNS), new ViewerSorter());

		CellEditor[] cellEditors = new CellEditor[UserInfoPropertySection.COLUMNS.length];
		cellEditors[TableUtils._getColumnNumber(UserInfoPropertySection.COLUMNS, UserInfoPropertySection.KEY)] = new EMTextCellEditor(myUserInfoTableViewer.getTable());
		cellEditors[TableUtils._getColumnNumber(UserInfoPropertySection.COLUMNS, UserInfoPropertySection.VALUE)] = new EMTextCellEditor(myUserInfoTableViewer.getTable());
		myUserInfoTableViewer.setCellModifier(new UserInfoCellModifier(myUserInfoTableViewer));
		myUserInfoTableViewer.setCellEditors(cellEditors);
		
		new StayEditingCellEditorListener(myUserInfoTableViewer, TableUtils._getColumnNumber(UserInfoPropertySection.COLUMNS, UserInfoPropertySection.KEY));
		new StayEditingCellEditorListener(myUserInfoTableViewer, TableUtils._getColumnNumber(UserInfoPropertySection.COLUMNS, UserInfoPropertySection.VALUE));

		FormData tableFormData = new FormData();
		tableFormData.left = new FormAttachment(0, 5);
		tableFormData.right = new FormAttachment(100, -5);
		tableFormData.top = new FormAttachment(0, 5);
		tableFormData.bottom = new FormAttachment(50, 0);
		myUserInfoTableViewer.getTable().setLayoutData(tableFormData);

		myValueText = getWidgetFactory().createText(composite, "", SWT.BORDER | SWT.WRAP | SWT.MULTI | SWT.V_SCROLL);
		FormData textFormData = new FormData();
		textFormData.left = new FormAttachment(0, 5);
		textFormData.right = new FormAttachment(100, -5);
		textFormData.top = new FormAttachment(myUserInfoTableViewer.getTable(), 5);
		textFormData.bottom = new FormAttachment(70);
		myValueText.setLayoutData(textFormData);
		ValueTextListener valueTextListener = new ValueTextListener();
		myValueText.addModifyListener(valueTextListener);
		myValueText.addFocusListener(valueTextListener);

		myAddRemoveButtonGroup = new AddRemoveButtonGroup(composite, new AddEntryHandler(), new RemoveEntriesHandler());
		FormData buttonGroupFormData = new FormData();
		buttonGroupFormData.top = new FormAttachment(myValueText, 5);
		buttonGroupFormData.left = new FormAttachment(0, 5);
		buttonGroupFormData.right = new FormAttachment(100, -5);
		myAddRemoveButtonGroup.setLayoutData(buttonGroupFormData);

		// updateValueText(null);
	}

	public void setInput(IWorkbenchPart _part, ISelection _selection) {
		super.setInput(_part, _selection);
		removeListeners();
		NotificationMap userInfo;
		if (_selection instanceof IStructuredSelection) {
			myUserInfoable = (IUserInfoable) ((IStructuredSelection) _selection).getFirstElement();
			userInfo = myUserInfoable.getUserInfo();
			if (userInfo == null) {
				userInfo = new NotificationMap();
				myUserInfoable.setUserInfo(userInfo);
			}
			userInfo.addPropertyChangeListener(myUserInfoListener);
		} else {
			myUserInfoable = null;
			userInfo = null;
		}
		myUserInfoTableViewer.setInput(userInfo);
		((UserInfoLabelProvider) myUserInfoTableViewer.getLabelProvider()).setUserInfo(userInfo);
		((UserInfoCellModifier) myUserInfoTableViewer.getCellModifier()).setUserInfo(userInfo);
		myUserInfoTableViewer.addSelectionChangedListener(new UserInfoSelectionListener());
		refresh();
	}

	protected void removeListeners() {
		if (myUserInfoable != null) {
			updateUserInfoFromText();
			myUserInfoable.getUserInfo().removePropertyChangeListener(myUserInfoListener);
		}
	}

	public void dispose() {
		super.dispose();
		removeListeners();
	}

	public void refresh() {
		super.refresh();
		myUserInfoTableViewer.refresh();
		TableUtils.packTableColumns(myUserInfoTableViewer);
		updateTextFromUserInfo();
	}

	public void refresh(String _propertyName) {
		myUserInfoTableViewer.refresh(_propertyName);
		TableUtils.packTableColumns(myUserInfoTableViewer);
		updateTextFromUserInfo();
	}

	public boolean shouldUseExtraSpace() {
		return true;
	}

	public IUserInfoable getUserInfoable() {
		return myUserInfoable;
	}

	public TableViewer getUserInfoTableViewer() {
		return myUserInfoTableViewer;
	}

	public void setValueTextDirty(boolean _dirty) {
		myValueTextDirty = _dirty;
	}

	public void removeSelectedEntries() {
		mySelectedKey = null;
		IStructuredSelection selection = (IStructuredSelection) myUserInfoTableViewer.getSelection();
		Object[] selectedKeys = selection.toArray();
		for (int i = 0; i < selectedKeys.length; i++) {
			Object selectedKey = selectedKeys[i];
			myUserInfoable.getUserInfo().remove(selectedKey);
		}
		updateTextFromUserInfo();
	}

	public void addEntry() {
		String key = Messages.getString("UserInfoPropertySection.newKey");
		String value = Messages.getString("UserInfoPropertySection.newValue");
		boolean unusedNameFound = (myUserInfoable.getUserInfo().get(key) == null);
		String unusedKey = key;
		for (int dupeNameNum = 1; !unusedNameFound; dupeNameNum++) {
			unusedKey = key + dupeNameNum;
			unusedNameFound = (myUserInfoable.getUserInfo().get(unusedKey) == null);
		}

		myUserInfoable.getUserInfo().put(unusedKey, value);
		myUserInfoTableViewer.setSelection(new StructuredSelection(unusedKey), true);
	}

	protected void setSelectedKey(Object _key) {
		updateUserInfoFromText();
		mySelectedKey = _key;
		updateTextFromUserInfo();
	}

	protected void updateTextFromUserInfo() {
		if (myUserInfoable != null && mySelectedKey != null) {
			Object valueObj = myUserInfoable.getUserInfo().get(mySelectedKey);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PropertyListSerialization.propertyListToStream(baos, valueObj);
			String valueStr;
			try {
				valueStr = new String(baos.toByteArray(), "UTF-8");
				myValueText.setText(valueStr);
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
			myValueText.setEnabled(true);
		} else {
			myValueText.setText("");
			myValueText.setEnabled(false);
		}
		myValueTextDirty = false;
	}

	protected void updateUserInfoFromText() {
		if (mySelectedKey != null && myValueTextDirty) {
			try {
				String valueStr = myValueText.getText().trim();
				if (!valueStr.startsWith("(") && !valueStr.startsWith("{") && !valueStr.startsWith("\"")) {
					valueStr = "\"" + valueStr + "\"";
				}
				Object valueObj = PropertyListSerialization.propertyListFromStream(new ByteArrayInputStream(valueStr.getBytes("UTF-8")), new EOModelParserDataStructureFactory());
				myUserInfoable.getUserInfo().put(mySelectedKey, valueObj);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		myValueTextDirty = false;
	}

	protected class ValueTextListener implements ModifyListener, FocusListener {
		public void modifyText(ModifyEvent _e) {
			UserInfoPropertySection.this.setValueTextDirty(true);
		}

		public void focusGained(FocusEvent _e) {
			// DO NOTHING
		}

		public void focusLost(FocusEvent _e) {
			UserInfoPropertySection.this.updateUserInfoFromText();
		}
	}

	protected class UserInfoSelectionListener implements ISelectionChangedListener {
		public void selectionChanged(SelectionChangedEvent _event) {
			IStructuredSelection selection = (IStructuredSelection) _event.getSelection();
			UserInfoPropertySection.this.setSelectedKey(selection.getFirstElement());
		}
	}

	protected class AddEntryHandler implements SelectionListener {
		public void widgetDefaultSelected(SelectionEvent _e) {
			widgetSelected(_e);
		}

		public void widgetSelected(SelectionEvent _e) {
			UserInfoPropertySection.this.addEntry();
		}
	}

	protected class RemoveEntriesHandler implements SelectionListener {
		public void widgetDefaultSelected(SelectionEvent _e) {
			widgetSelected(_e);
		}

		public void widgetSelected(SelectionEvent _e) {
			UserInfoPropertySection.this.removeSelectedEntries();
		}
	}

	protected class UserInfoListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent _event) {
			String propertyName = _event.getPropertyName();
			if (propertyName == NotificationMap.CONTENTS) {
				UserInfoPropertySection.this.refresh();
			} else {
				UserInfoPropertySection.this.refresh(propertyName);
			}
		}
	}
}
