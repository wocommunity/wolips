package org.objectstyle.wolips.baseforuiplugins.plist;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.objectstyle.wolips.baseforplugins.util.ComparisonUtils;
import org.objectstyle.wolips.baseforuiplugins.utils.KeyComboBoxCellEditor;

public class PropertyListTypeEditingSupport extends EditingSupport {
	private IPropertyListChangeListener _listener;

	private TreeViewer _treeViewer;

	private KeyComboBoxCellEditor _typeCellEditor;

	private boolean _canEditRoot;

	public PropertyListTypeEditingSupport(TreeViewer viewer, IPropertyListChangeListener listener, boolean canEditRoot) {
		super(viewer);
		_listener = listener;
		_treeViewer = viewer;
		_canEditRoot = canEditRoot;
		String[] items = new String[] { PropertyListPath.Type.Array.getName(), PropertyListPath.Type.Dictionary.getName(), PropertyListPath.Type.Boolean.getName(), PropertyListPath.Type.Data.getName(), PropertyListPath.Type.Date.getName(), PropertyListPath.Type.Number.getName(), PropertyListPath.Type.String.getName() };
		_typeCellEditor = new KeyComboBoxCellEditor(_treeViewer.getTree(), items, SWT.READ_ONLY);
		_typeCellEditor.getComboBox().setVisibleItemCount(items.length);
	}

	protected boolean canEdit(Object element) {
		PropertyListPath path = (PropertyListPath) element;
		return _canEditRoot || path.getParent() != null;
	}

	protected CellEditor getCellEditor(Object element) {
		return _typeCellEditor;
	}

	protected Object getValue(Object element) {
		String name = ((PropertyListPath) element).getType().getName();
		return _typeCellEditor.getComboBox().indexOf(name);
	}

	protected void setValue(Object element, Object value) {
		Integer selectedTypeIndex = (Integer) value;
		if (selectedTypeIndex != -1) {
			String selectedTypeName = _typeCellEditor.getComboBox().getItem(selectedTypeIndex);
			PropertyListPath.Type selectedType = null;
			for (PropertyListPath.Type type : PropertyListPath.Type.values()) {
				if (type.getName().equals(selectedTypeName)) {
					selectedType = type;
					break;
				}
			}
			if (selectedType != null) {
				PropertyListPath path = (PropertyListPath) element;
				Object oldValue = path.getValue();
				PropertyListPath.Type oldType = path.getType();
				if (!ComparisonUtils.equals(oldType, selectedType)) {
					boolean parentChanged = path.setType(selectedType);
					Object newValue = path.getValue();
					_listener.pathChanged(path.getKeyPath(), oldValue, newValue);
					if (parentChanged) {
						_treeViewer.refresh(path.getParent());
					} else {
						_treeViewer.refresh(path);
					}
				}
			}
		}
	}
}
