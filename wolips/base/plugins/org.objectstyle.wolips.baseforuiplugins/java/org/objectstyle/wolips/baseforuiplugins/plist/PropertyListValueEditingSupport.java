package org.objectstyle.wolips.baseforuiplugins.plist;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.objectstyle.wolips.baseforplugins.util.ComparisonUtils;
import org.objectstyle.wolips.baseforuiplugins.utils.WOTextCellEditor;

public class PropertyListValueEditingSupport extends EditingSupport {
	private IPropertyListChangeListener _listener;

	private TreeViewer _treeViewer;

	private TextCellEditor _textCellEditor;

	public PropertyListValueEditingSupport(TreeViewer viewer, IPropertyListChangeListener listener) {
		super(viewer);
		_listener = listener;
		_treeViewer = viewer;
		_textCellEditor = new WOTextCellEditor(_treeViewer.getTree());
	}

	protected boolean canEdit(Object element) {
		return !((PropertyListPath) element).isCollectionValue();
	}

	protected CellEditor getCellEditor(Object element) {
		return _textCellEditor;
	}

	protected Object getValue(Object element) {
		PropertyListPath path = (PropertyListPath) element;
		return path.convertValueToType(PropertyListPath.Type.String);
	}

	protected void setValue(Object element, Object value) {
		PropertyListPath path = (PropertyListPath) element;
		String strValue = (String) value;
		Object convertedValue = PropertyListPath.convertValueFromTypeToType(path.getKeyPath(), strValue, PropertyListPath.Type.String, path.getType(), path.getFactory());

		Object oldValue = path.getValue();
		if (!ComparisonUtils.equals(oldValue, convertedValue)) {
			boolean parentChanged = path.setValue(convertedValue);
			if (parentChanged) {
				_listener.pathChanged(path.getParent().getKeyPath(), oldValue, convertedValue);
				_treeViewer.refresh(path.getParent());
			} else {
				_listener.pathChanged(path.getKeyPath(), oldValue, convertedValue);
				_treeViewer.refresh(path);
			}
		}
	}
}
