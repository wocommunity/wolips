package org.objectstyle.wolips.componenteditor.inspector;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableColumn;
import org.objectstyle.wolips.baseforuiplugins.utils.ListContentProvider;
import org.objectstyle.wolips.bindings.utils.BindingReflectionUtils;
import org.objectstyle.wolips.bindings.wod.BindingValueKey;
import org.objectstyle.wolips.bindings.wod.BindingValueKeyPath;
import org.objectstyle.wolips.componenteditor.ComponenteditorPlugin;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;

public class WOBrowser extends ScrolledComposite implements ISelectionChangedListener, ISelectionProvider {
	private Composite _browser;

	private List<WOBrowserColumn> _columns;

	private List<ISelectionChangedListener> _listeners = new LinkedList<ISelectionChangedListener>();

	public WOBrowser(Composite parent, int style) {
		super(parent, SWT.H_SCROLL | style);
		_columns = new LinkedList<WOBrowserColumn>();

		_browser = new Composite(this, SWT.NONE);
		_browser.setBackground(parent.getBackground());
		// _browser.setLayoutData(new GridData(GridData.FILL_BOTH));

		setContent(_browser);
		setExpandVertical(true);

		GridLayout browserLayout = new GridLayout(1, false);
		browserLayout.horizontalSpacing = 0;
		browserLayout.marginWidth = 3;
		browserLayout.marginHeight = 3;
		browserLayout.horizontalSpacing = 5;
		_browser.setLayout(browserLayout);
	}

	public WOBrowserColumn setRootType(IType type) throws JavaModelException {
		disposeToColumn(-1);
		return addType(type);
	}

	public WOBrowserColumn addType(IType type) throws JavaModelException {
		WOBrowserColumn newColumn = null;
		if (type != null) {
			newColumn = new WOBrowserColumn(type, _browser, SWT.NONE);
			newColumn.addSelectionChangedListener(this);
			GridData columnLayoutData = new GridData(GridData.FILL_BOTH);
			newColumn.setLayoutData(columnLayoutData);
			_columns.add(newColumn);
			((GridLayout) _browser.getLayout()).numColumns = _columns.size();

			_browser.pack();

			for (WOBrowserColumn column : _columns) {
				Object selectedElement = ((IStructuredSelection) column.getSelection()).getFirstElement();
				if (selectedElement != null) {
					column.getViewer().reveal(selectedElement);
				}
			}

			getHorizontalBar().setSelection(getHorizontalBar().getMaximum());
			layout();
		}
		return newColumn;
	}

	public void disposeToColumn(int columnIndex) {
		for (int columnNum = _columns.size() - 1; columnNum > columnIndex; columnNum--) {
			WOBrowserColumn column = _columns.get(columnNum);
			column.dispose();
			_columns.remove(columnNum);
		}

		_browser.pack();
	}

	public WOBrowserColumn selectKeyInColumn(BindingValueKey selectedKey, WOBrowserColumn column) {
		WOBrowserColumn addedColumn = null;

		int columnIndex = _columns.indexOf(column);
		if (columnIndex != -1) {
			disposeToColumn(columnIndex);
		}

		if (selectedKey == null) {
			//System.out.println("WOBrowserPage.selectionChanged: none");
		} else {
			try {
				if (!selectedKey.isLeaf()) {
					IType nextType = selectedKey.getNextType();
					if (nextType != null) {
						addedColumn = addType(nextType);
					}
				}
			} catch (JavaModelException e) {
				e.printStackTrace();
			}
		}

		return addedColumn;
	}

	public void selectionChanged(SelectionChangedEvent event) {
		WOBrowserColumn selectedColumn = (WOBrowserColumn) event.getSource();

		IStructuredSelection selection = (IStructuredSelection) event.getSelection();
		BindingValueKey selectedKey = (BindingValueKey) selection.getFirstElement();

		selectKeyInColumn(selectedKey, selectedColumn);
		
		SelectionChangedEvent wrappedEvent = new SelectionChangedEvent(this, getSelection());
		for (Iterator listeners = _listeners.iterator(); listeners.hasNext();) {
			ISelectionChangedListener listener = (ISelectionChangedListener) listeners.next();
			listener.selectionChanged(wrappedEvent);
		}
	}

	public String getSelectedKeyPath() {
		StringBuffer keyPath = new StringBuffer();
		for (WOBrowserColumn column : _columns) {
			BindingValueKey key = column.getSelectedKey();
			if (key != null) {
				if (keyPath.length() > 0) {
					keyPath.append('.');
				}
				keyPath.append(key.getBindingName());
			}
		}
		return keyPath.toString();
	}

	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		_listeners.add(listener);
	}

	public ISelection getSelection() {
		return new StructuredSelection(getSelectedKeyPath());
	}

	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		_listeners.remove(listener);
	}

	public void setSelection(ISelection selection) {
		String selectedKeyPath = (String) ((IStructuredSelection) selection).getFirstElement();
		if (selectedKeyPath == null) {
			WOBrowserColumn column = _columns.get(0);
			selectKeyInColumn(null, column);
			column.setSelection(new StructuredSelection());
		} else {
			try {
				BindingValueKeyPath bindingValueKeyPath = new BindingValueKeyPath(selectedKeyPath, _columns.get(0).getType());
				disposeToColumn(0);
				if (bindingValueKeyPath.isValid()) {
					for (BindingValueKey bindingValueKey : bindingValueKeyPath.getBindingKeys()) {
						WOBrowserColumn column = _columns.get(_columns.size() - 1);
						column.setSelection(new StructuredSelection(bindingValueKey));
						
						WOBrowserColumn newColumn = selectKeyInColumn(bindingValueKey, column);
						if (newColumn == null) {
							break;
						}
					}
				}
			} catch (JavaModelException e) {
				e.printStackTrace();
			}
		}
	}

	public static class WOBrowserColumn extends Composite implements ISelectionProvider, ISelectionChangedListener {
		private List<ISelectionChangedListener> _listeners = new LinkedList<ISelectionChangedListener>();

		private IType _type;

		private TableViewer _keysViewer;

		private Font _typeNameFont;

		public WOBrowserColumn(IType type, Composite parent, int style) throws JavaModelException {
			super(parent, style);
			setBackground(parent.getBackground());

			GridLayout layout = new GridLayout(1, false);
			layout.marginWidth = 0;
			layout.marginHeight = 0;
			layout.marginTop = 0;
			setLayout(layout);

			_type = type;

			Label typeName = new Label(this, SWT.NONE);
			Font originalFont = typeName.getFont();
			FontData[] fontData = originalFont.getFontData();
			_typeNameFont = new Font(originalFont.getDevice(), fontData[0].getName(), fontData[0].getHeight(), SWT.BOLD);
			typeName.setFont(_typeNameFont);

			// typeName.setFont(typeName.getFont().)
			typeName.setText(type.getElementName());

			Composite tableContainer = new Composite(this, SWT.NONE);
			tableContainer.setBackground(parent.getBackground());
			tableContainer.setLayoutData(new GridData(GridData.FILL_BOTH));

			_keysViewer = new TableViewer(tableContainer, SWT.FULL_SELECTION | SWT.SINGLE | SWT.V_SCROLL | SWT.BORDER);
			_keysViewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
			_keysViewer.addSelectionChangedListener(this);
			_keysViewer.setContentProvider(new ListContentProvider());
			_keysViewer.setLabelProvider(new WOBrowserColumnLabelProvider());

			TableColumnLayout keysLayout = new TableColumnLayout();
			tableContainer.setLayout(keysLayout);

			TableColumn nameColumn = new TableColumn(_keysViewer.getTable(), SWT.NONE);
			nameColumn.setText("Column 1");
			// nameColumn.setMoveable(true);
			keysLayout.setColumnData(nameColumn, new ColumnWeightData(0, 200));

			TableColumn iconColumn = new TableColumn(_keysViewer.getTable(), SWT.NONE);
			iconColumn.setText("Column 2");
			// iconColumn.setMoveable(true);
			keysLayout.setColumnData(iconColumn, new ColumnWeightData(0, 20));

			BindingValueKeyPath bindingValueKeyPath = new BindingValueKeyPath("", type, type.getJavaProject(), WodParserCache.getTypeCache());
			List<BindingValueKey> bindingValueKeys = bindingValueKeyPath.getPartialMatchesForLastBindingKey(true);
			List<BindingValueKey> filteredBindingValueKeys = BindingReflectionUtils.filterSystemBindingValueKeys(bindingValueKeys, true);
			Set<BindingValueKey> uniqueBingingValueKeys = new TreeSet<BindingValueKey>(filteredBindingValueKeys);
			List<BindingValueKey> sortedBindingValueKeys = new LinkedList<BindingValueKey>(uniqueBingingValueKeys);

			_keysViewer.setInput(sortedBindingValueKeys);
			tableContainer.pack();
		}

		@Override
		public void dispose() {
			_typeNameFont.dispose();
			super.dispose();
		}

		@Override
		public boolean setFocus() {
			return _keysViewer.getControl().setFocus();
		}

		public IType getType() {
			return _type;
		}

		public TableViewer getViewer() {
			return _keysViewer;
		}

		public void addSelectionChangedListener(ISelectionChangedListener listener) {
			_listeners.add(listener);
		}

		public ISelection getSelection() {
			return _keysViewer.getSelection();
		}

		public void removeSelectionChangedListener(ISelectionChangedListener listener) {
			_listeners.remove(listener);
		}

		public void setSelection(ISelection selection) {
			_keysViewer.setSelection(selection, true);
		}

		public void selectionChanged(SelectionChangedEvent event) {
			SelectionChangedEvent wrappedEvent = new SelectionChangedEvent(this, event.getSelection());
			for (Iterator listeners = _listeners.iterator(); listeners.hasNext();) {
				ISelectionChangedListener listener = (ISelectionChangedListener) listeners.next();
				listener.selectionChanged(wrappedEvent);
			}
		}

		public BindingValueKey getSelectedKey() {
			BindingValueKey selectedKey = null;
			IStructuredSelection selection = (IStructuredSelection) getSelection();
			if (selection != null) {
				selectedKey = (BindingValueKey) selection.getFirstElement();
			}
			return selectedKey;
		}
	}

	public static class WOBrowserColumnLabelProvider extends BaseLabelProvider implements ILabelProvider, ITableLabelProvider {
		public Image getImage(Object element) {
			return getColumnImage(element, 0);
		}

		public String getText(Object element) {
			return getColumnText(element, 0);
		}

		public Image getColumnImage(Object element, int columnIndex) {
			Image image = null;
			if (columnIndex == 1) {
				BindingValueKey bindingValueKey = (BindingValueKey) element;
				if (bindingValueKey != null) {
					try {
						if (!bindingValueKey.isLeaf()) {
							image = ComponenteditorPlugin.getDefault().getImage(ComponenteditorPlugin.TO_ONE_ICON);
						}
					} catch (JavaModelException e) {
						e.printStackTrace();
					}
				}
			}
			return image;
		}

		public String getColumnText(Object element, int columnIndex) {
			String text = null;
			if (columnIndex == 0) {
				BindingValueKey bindingValueKey = (BindingValueKey) element;
				if (bindingValueKey != null) {
					text = bindingValueKey.getBindingName();
					int minWidth = 40;
					if (text != null && text.length() < minWidth) {
						StringBuffer textBuffer = new StringBuffer(text);
						for (int i = text.length(); i < minWidth; i++) {
							textBuffer.append(' ');
						}
						text = textBuffer.toString();
					}
				}
				if (text == null) {
					text = "<unknown>";
				}
			} else if (columnIndex == 1) {
				text = null;
			}
			return text;
		}
	}

}
