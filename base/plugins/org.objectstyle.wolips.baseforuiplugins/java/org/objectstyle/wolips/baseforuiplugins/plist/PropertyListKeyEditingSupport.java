package org.objectstyle.wolips.baseforuiplugins.plist;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.objectstyle.wolips.baseforplugins.util.ComparisonUtils;
import org.objectstyle.wolips.baseforuiplugins.utils.WOTextCellEditor;

public class PropertyListKeyEditingSupport extends EditingSupport {
	private IPropertyListChangeListener _listener;

	private TreeViewer _treeViewer;

	private TextCellEditor _textCellEditor;

	public PropertyListKeyEditingSupport(TreeViewer viewer, IPropertyListChangeListener listener) {
		super(viewer);
		_listener = listener;
		_treeViewer = viewer;
		_textCellEditor = new WOTextCellEditor(_treeViewer.getTree());
	}

	protected boolean canEdit(Object element) {
		return ((PropertyListPath) element).isRealKey();
	}

	protected CellEditor getCellEditor(Object element) {
		return _textCellEditor;
	}

	protected Object getValue(Object element) {
		return ((PropertyListPath) element).getKey();
	}

	protected void setValue(Object element, final Object key) {
		final PropertyListPath path = (PropertyListPath) element;
		final String oldKeyPath = path.getKeyPath();
		final String oldKey = path.getKey();
		if (!ComparisonUtils.equals(oldKey, key)) {
			if (path.setKey(key)) {
				_listener.pathRenamed(oldKeyPath, path.getParent().getKeyPath());
				PropertyListPath newPath = path.getParent().getChildForKey(key);
				if (newPath != null) {
					_treeViewer.refresh(newPath.getParent());
				}
			}
		}
	}
}
