package org.objectstyle.wolips.componenteditor.inspector;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.objectstyle.wolips.bindings.api.IApiBinding;
import org.objectstyle.wolips.wodclipse.core.refactoring.RefactoringWodElement;

public class BindingsInspectorDropHandler extends AbstractBindingsDropHandler<BindingsInspector, TableItem, Object, Table> {
	private BindingsInspector _inspector;

	public BindingsInspectorDropHandler(BindingsInspector inspector) {
		super(inspector.getBindingsTableViewer().getTable());
		_inspector = inspector;
	}

	@Override
	protected Object _addHoverAnnotation(TableItem selectedItem) {
		return selectedItem;
	}

	@Override
	protected void _removeHoverAnnotation(Object annotation) {
		// DO NOTHING
	}

	@Override
	protected IAutoscroller createAutoscroller(Table editorControl) {
		return new NoOpAutoscroller();
	}

	@Override
	protected void dropFromColumnAtPoint(WOBrowserColumn column, Point dropPoint) throws Exception {
		try {
			Point controlDropPoint = getEditorControl().toControl(dropPoint);
			TableItem selectedItem = getSelectedItemAtPoint(_inspector, controlDropPoint);
			if (selectedItem != null) {
				IApiBinding binding = getBindingForItem(selectedItem);
				if (binding != null) {
					String droppedKeyPath = column.getSelectedKeyPath();
					RefactoringWodElement element = _inspector.getRefactoringElement();
					if (element != null) {
						try {
							element.setValueForBinding(droppedKeyPath, binding.getName());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		} finally {
			removeHoverAnnotation();
		}
	}

	@Override
	protected BindingsInspector getSelectedContainerAtPoint(Point point, boolean forDrop) throws Exception {
		return _inspector;
	}

	@Override
	protected TableItem getSelectedItemAtPoint(BindingsInspector container, Point point) {
		TableViewer tableViewer = container.getBindingsTableViewer();
		Table table = tableViewer.getTable();
		TableItem item = table.getItem(point);
		return item;
	}

	@Override
	protected Rectangle getSelectionRectangle(TableItem item) {
		TableViewer tableViewer = _inspector.getBindingsTableViewer();
		Table table = tableViewer.getTable();
		Rectangle rowBounds = item.getBounds();
		Rectangle bounds = new Rectangle(5, rowBounds.y, table.getBounds().width - 25, rowBounds.height);
		return bounds;
	}

	@Override
	protected boolean isSelectedItemChanged(TableItem oldItem, TableItem newItem) {
		return oldItem == null || newItem == null || !getBindingForItem(oldItem).getName().equals(getBindingForItem(newItem).getName());
	}

	protected IApiBinding getBindingForItem(TableItem item) {
		IApiBinding binding;
		if (item == null) {
			binding = null;
		} else {
			TableViewer tableViewer = _inspector.getBindingsTableViewer();
			Table table = tableViewer.getTable();
			int index = table.indexOf(item);
			if (index == -1) {
				binding = null;
			} else {
				binding = (IApiBinding) tableViewer.getElementAt(index);
			}
		}
		return binding;
	}
}
