package org.objectstyle.wolips.baseforuiplugins.plist;

import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.FocusCellOwnerDrawHighlighter;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.TreeViewerEditor;
import org.eclipse.jface.viewers.TreeViewerFocusCellManager;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.objectstyle.wolips.baseforplugins.plist.ParserDataStructureFactory;
import org.objectstyle.wolips.baseforplugins.plist.PropertyListParserException;
import org.objectstyle.wolips.baseforplugins.plist.WOLPropertyListSerialization;
import org.objectstyle.wolips.baseforuiplugins.utils.WOTreeCellNavigationStrategy;

public class PropertyListEditor extends Viewer implements IPropertyListChangeListener, KeyListener {
	private IPropertyListChangeListener _listener;

	private ParserDataStructureFactory _parserDataStructureFactory;

	private TreeViewer _propertyListTree;

	public PropertyListEditor(Composite parent, boolean rootVisible, boolean canEditRootType, Set<String> filteredKeyPaths) {
		_propertyListTree = new TreeViewer(parent, SWT.BORDER | SWT.FULL_SELECTION | SWT.BACKGROUND | SWT.SINGLE | SWT.NO_SCROLL | SWT.V_SCROLL);
		_propertyListTree.getTree().setHeaderVisible(true);
		_propertyListTree.getTree().setLinesVisible(true);
		_propertyListTree.setAutoExpandLevel(2);
		TreeViewerFocusCellManager focusCellManager = new TreeViewerFocusCellManager(_propertyListTree, new FocusCellOwnerDrawHighlighter(_propertyListTree), new WOTreeCellNavigationStrategy());
		ColumnViewerEditorActivationStrategy actSupport = new ColumnViewerEditorActivationStrategy(_propertyListTree) {
			protected boolean isEditorActivationEvent(ColumnViewerEditorActivationEvent event) {
				ViewerCell cell = (ViewerCell) event.getSource();
				boolean isEditorActivationEvent;
				if (cell.getColumnIndex() == 1) {
					isEditorActivationEvent = event.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL || event.eventType == ColumnViewerEditorActivationEvent.MOUSE_CLICK_SELECTION || (event.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED && event.keyCode == ' ') || event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC;
					//isEditorActivationEvent = event.eventType == ColumnViewerEditorActivationEvent.MOUSE_CLICK_SELECTION || (event.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED && event.keyCode == ' ') || event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC;
				} else {
					isEditorActivationEvent = event.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL || event.eventType == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION || (event.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED && event.keyCode == ' ') || event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC;
					//isEditorActivationEvent = event.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL || event.eventType == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION || event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC;
				}
				return isEditorActivationEvent;
			}
		};
		TreeViewerEditor.create(_propertyListTree, focusCellManager, actSupport, ColumnViewerEditor.TABBING_HORIZONTAL | ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR | ColumnViewerEditor.TABBING_VERTICAL | ColumnViewerEditor.KEYBOARD_ACTIVATION | ColumnViewerEditor.KEEP_EDITOR_ON_DOUBLE_CLICK);

		TreeViewerColumn keyColumn = new TreeViewerColumn(_propertyListTree, SWT.NONE);
		keyColumn.getColumn().setWidth(120);
		keyColumn.getColumn().setText("Key");
		keyColumn.setLabelProvider(new PropertyListKeyLabelProvider());
		keyColumn.setEditingSupport(new PropertyListKeyEditingSupport(_propertyListTree, this));

		TreeViewerColumn typeColumn = new TreeViewerColumn(_propertyListTree, SWT.NONE);
		typeColumn.getColumn().setWidth(75);
		typeColumn.getColumn().setText("Type");
		typeColumn.setLabelProvider(new PropertyListTypeLabelProvider());
		typeColumn.setEditingSupport(new PropertyListTypeEditingSupport(_propertyListTree, this, canEditRootType));

		TreeViewerColumn valueColumn = new TreeViewerColumn(_propertyListTree, SWT.NONE);
		valueColumn.getColumn().setWidth(250);
		valueColumn.getColumn().setText("Value");
		valueColumn.setLabelProvider(new PropertyListValueLabelProvider());
		valueColumn.setEditingSupport(new PropertyListValueEditingSupport(_propertyListTree, this));

		_propertyListTree.getTree().addKeyListener(this);

		_parserDataStructureFactory = new StableDataStructureFactory();
		_propertyListTree.setContentProvider(new PropertyListContentProvider(_parserDataStructureFactory, rootVisible, filteredKeyPaths));
	}

	public void setListener(IPropertyListChangeListener listener) {
		_listener = listener;
	}

	public IPropertyListChangeListener getListener() {
		return _listener;
	}

	@Override
	public Control getControl() {
		return _propertyListTree.getTree();
	}

	@Override
	public Object getInput() {
		try {
			return WOLPropertyListSerialization.propertyListFromString(WOLPropertyListSerialization.stringFromPropertyList(_propertyListTree.getInput()), new StableDataStructureFactory());
		} catch (PropertyListParserException e) {
			throw new IllegalArgumentException("Failed to parse property list.", e);
		}
	}

	@Override
	public ISelection getSelection() {
		return _propertyListTree.getSelection();
	}

	@Override
	public void refresh() {
		_propertyListTree.refresh();
	}

	@Override
	public void setInput(Object input) {
		try {
			_propertyListTree.setInput(WOLPropertyListSerialization.propertyListFromString(WOLPropertyListSerialization.stringFromPropertyList(input), new StableDataStructureFactory()));
		} catch (PropertyListParserException e) {
			throw new IllegalArgumentException("Failed to parse property list.", e);
		}
	}

	@Override
	public void setSelection(ISelection selection, boolean reveal) {
		_propertyListTree.setSelection(selection);
	}

	public void pathRenamed(String oldPath, String newPath) {
		if (_listener != null) {
			_listener.pathRenamed(oldPath, newPath);
		}
	}

	public void pathChanged(String path, Object oldValue, Object newValue) {
		if (_listener != null) {
			_listener.pathChanged(path, oldValue, newValue);
		}
	}

	public void pathAdded(String path, Object value) {
		if (_listener != null) {
			_listener.pathAdded(path, value);
		}
	}

	public void pathRemoved(String path, Object value) {
		if (_listener != null) {
			_listener.pathRemoved(path, value);
		}
	}

	public void addRow() {
		PropertyListPath parentPath;
		PropertyListPath selectedPath = (PropertyListPath) ((IStructuredSelection) _propertyListTree.getSelection()).getFirstElement();
		if (selectedPath.isCollectionValue()) {
			if (_propertyListTree.getExpandedState(selectedPath) || selectedPath.getParent() == null) {
				parentPath = selectedPath;
			} else {
				parentPath = selectedPath.getParent();
			}
		} else {
			parentPath = selectedPath.getParent();
		}
		PropertyListPath newChild = parentPath.addRow();
		_propertyListTree.refresh(parentPath);
		_propertyListTree.setSelection(new StructuredSelection(newChild), true);
		if (_listener != null) {
			_listener.pathAdded(newChild.getKeyPath(), newChild.getValue());
		}
		_propertyListTree.editElement(newChild, 0);
	}

	public void deleteRow() {
		PropertyListPath selectedPath = (PropertyListPath) ((IStructuredSelection) _propertyListTree.getSelection()).getFirstElement();
		String keyPath = selectedPath.getKeyPath();
		PropertyListPath parentPath = selectedPath.getParent();
		int index = parentPath.getIndexOf(selectedPath);
		if (selectedPath.delete()) {
			_propertyListTree.refresh(parentPath);
			if (_listener != null) {
				_listener.pathRemoved(keyPath, selectedPath.getValue());
			}

			List<PropertyListPath> children = parentPath.getChildren();
			if (children.size() == 0) {
				_propertyListTree.setSelection(new StructuredSelection(parentPath), true);
			} else if (index == children.size()) {
				_propertyListTree.setSelection(new StructuredSelection(parentPath.getChildAtIndex(index - 1)), true);
			} else {
				_propertyListTree.setSelection(new StructuredSelection(parentPath.getChildAtIndex(index)), true);
			}
		}
	}

	public void moveRowUp() {
		PropertyListPath selectedPath = (PropertyListPath) ((IStructuredSelection) _propertyListTree.getSelection()).getFirstElement();
		PropertyListPath parentPath = selectedPath.getParent();
		int index = parentPath.getIndexOf(selectedPath);
		if (selectedPath.moveUp()) {
			_propertyListTree.refresh(parentPath);
			if (_listener != null) {
				_listener.pathChanged(parentPath.getKeyPath(), null, null);
			}
			_propertyListTree.setSelection(new StructuredSelection(parentPath.getChildAtIndex(index - 1)), true);
		}
	}

	public void moveRowDown() {
		PropertyListPath selectedPath = (PropertyListPath) ((IStructuredSelection) _propertyListTree.getSelection()).getFirstElement();
		PropertyListPath parentPath = selectedPath.getParent();
		int index = parentPath.getIndexOf(selectedPath);
		if (selectedPath.moveDown()) {
			_propertyListTree.refresh(parentPath);
			if (_listener != null) {
				_listener.pathChanged(parentPath.getKeyPath(), null, null);
			}
			_propertyListTree.setSelection(new StructuredSelection(parentPath.getChildAtIndex(index + 1)), true);
		}
	}

	public void keyPressed(KeyEvent e) {
		if (!_propertyListTree.isCellEditorActive()) {
			if (e.keyCode == SWT.CR) {
				addRow();
			} else if (e.keyCode == SWT.DEL) {
				deleteRow();
			} else if (e.keyCode == SWT.ARROW_UP && (e.stateMask & SWT.COMMAND) != 0) {
				moveRowUp();
				e.doit = false;
			} else if (e.keyCode == SWT.ARROW_DOWN && (e.stateMask & SWT.COMMAND) != 0) {
				moveRowDown();
				e.doit = false;
			}
		}
	}

	public void keyReleased(KeyEvent e) {
		// IGNORE
	}
}
