package org.objectstyle.wolips.componenteditor.inspector;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableColumn;
import org.objectstyle.wolips.baseforuiplugins.utils.ListContentProvider;
import org.objectstyle.wolips.bindings.utils.BindingReflectionUtils;
import org.objectstyle.wolips.bindings.wod.BindingValueKey;
import org.objectstyle.wolips.bindings.wod.BindingValueKeyPath;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;

public class WOBrowserColumn extends Composite implements ISelectionProvider, ISelectionChangedListener {
	private List<ISelectionChangedListener> _listeners = new LinkedList<ISelectionChangedListener>();

	private IType _type;

	private TableViewer _keysViewer;

	private Font _typeNameFont;

	private BindingsDragHandler _lineDragHandler;

	private IWOBrowserDelegate _delegate;

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
		typeName.setBackground(getBackground());
		Font originalFont = typeName.getFont();
		FontData[] fontData = originalFont.getFontData();
		_typeNameFont = new Font(originalFont.getDevice(), fontData[0].getName(), fontData[0].getHeight(), SWT.BOLD);
		typeName.setFont(_typeNameFont);
		GridData typeNameData = new GridData(GridData.FILL_HORIZONTAL);
		typeNameData.horizontalIndent = 3;
		typeName.setLayoutData(typeNameData);

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

		_lineDragHandler = new BindingsDragHandler(this);
		_lineDragHandler.register();
		_keysViewer.addDragSupport(DND.DROP_COPY, new Transfer[] { LocalSelectionTransfer.getTransfer() }, _lineDragHandler);
	}

	public void setDelegate(IWOBrowserDelegate delegate) {
		_delegate = delegate;
	}

	public IWOBrowserDelegate getDelegate() {
		return _delegate;
	}

	@Override
	public void dispose() {
		_typeNameFont.dispose();
		if (_lineDragHandler != null) {
			_lineDragHandler.dispose();
			_lineDragHandler = null;
		}
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