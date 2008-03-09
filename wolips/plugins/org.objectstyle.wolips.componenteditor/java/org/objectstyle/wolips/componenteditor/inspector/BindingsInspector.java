package org.objectstyle.wolips.componenteditor.inspector;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.editors.text.TextEditor;
import org.objectstyle.wolips.baseforplugins.util.ComparisonUtils;
import org.objectstyle.wolips.baseforuiplugins.utils.TableRowDoubleClickHandler;
import org.objectstyle.wolips.baseforuiplugins.utils.WOTextCellEditor;
import org.objectstyle.wolips.bindings.api.IApiBinding;
import org.objectstyle.wolips.bindings.api.Wo;
import org.objectstyle.wolips.bindings.wod.IWodBinding;
import org.objectstyle.wolips.bindings.wod.IWodElement;
import org.objectstyle.wolips.bindings.wod.WodProblem;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;
import org.objectstyle.wolips.wodclipse.core.document.IWOEditor;
import org.objectstyle.wolips.wodclipse.core.document.WodBindingValueHyperlink;
import org.objectstyle.wolips.wodclipse.core.refactoring.RefactoringWodElement;
import org.objectstyle.wolips.wodclipse.core.util.ICursorPositionListener;
import org.objectstyle.wolips.wodclipse.core.util.WodModelUtils;

public class BindingsInspector extends Composite implements ISelectionProvider, ISelectionChangedListener, ICursorPositionListener {
	private TextEditor _lastEditor;

	private Point _lastPosition;

	private Combo _elementTypeField;

	private Text _elementNameField;

	private TableViewer _bindingsTableViewer;

	private BindingsLabelProvider _bindingsLabelProvider;

	private BindingsContentProvider _bindingsContentProvider;

	private DataBindingContext _dataBindingContext;

	private IWodElement _wodElement;

	private RefactoringWodElement _refactoringElement;

	private List<WodProblem> _wodProblems;

	private List<ISelectionChangedListener> _listeners = new LinkedList<ISelectionChangedListener>();

	public BindingsInspector(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(2, false));

		Label elementNameLabel = new Label(this, SWT.NONE);
		elementNameLabel.setText("Component Name");
		_elementNameField = new Text(this, SWT.BORDER);
		_elementNameField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label elementTypeLabel = new Label(this, SWT.NONE);
		elementTypeLabel.setText("Component Type");
		_elementTypeField = new Combo(this, SWT.BORDER);
		_elementTypeField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		_elementTypeField.setVisibleItemCount(5);

		Composite bindingsTableContainer = new Composite(this, SWT.NONE);
		GridData bindingsTableContainerData = new GridData(GridData.FILL_BOTH);
		bindingsTableContainerData.horizontalSpan = 2;
		bindingsTableContainer.setLayoutData(bindingsTableContainerData);

		_bindingsTableViewer = new TableViewer(bindingsTableContainer, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
		_bindingsLabelProvider = new BindingsLabelProvider();
		_bindingsContentProvider = new BindingsContentProvider();
		_bindingsTableViewer.setContentProvider(_bindingsContentProvider);
		_bindingsTableViewer.setLabelProvider(_bindingsLabelProvider);

		TableColumnLayout bindingsTableLayout = new TableColumnLayout();
		bindingsTableContainer.setLayout(bindingsTableLayout);

		Table bindingsTable = _bindingsTableViewer.getTable();
		bindingsTable.setHeaderVisible(true);
		bindingsTable.setLinesVisible(true);

		TableColumn nameColumn = new TableColumn(bindingsTable, SWT.LEFT);
		nameColumn.setText("Attribute");
		bindingsTableLayout.setColumnData(nameColumn, new ColumnWeightData(50, true));

		TableViewerColumn valueViewerColumn = new TableViewerColumn(_bindingsTableViewer, SWT.LEAD);
		TableColumn valueColumn = valueViewerColumn.getColumn();
		valueColumn.setText("Binding");
		bindingsTableLayout.setColumnData(valueColumn, new ColumnWeightData(50, true));
		valueViewerColumn.setEditingSupport(new BindingEditingSupport(_bindingsTableViewer));
		valueViewerColumn.setLabelProvider(_bindingsLabelProvider);

		_bindingsTableViewer.addSelectionChangedListener(this);
		new DoubleClickBindingHandler(_bindingsTableViewer).attach();

		setWodElement(null, null);
	}

	public void setWodElement(IWodElement wodElement, WodParserCache cache) {
		if (_elementNameField.isDisposed() || _elementTypeField.isDisposed() || _bindingsTableViewer.getTable().isDisposed()) {
			return;
		}

		if (_dataBindingContext != null) {
			_dataBindingContext.dispose();
		}

		_wodElement = wodElement;
		_wodProblems = null;
		if (wodElement != null && cache != null) {
			try {
				_wodProblems = WodModelUtils.getProblems(wodElement, cache);
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
		_bindingsLabelProvider.setContext(_wodElement, _wodProblems);

		if (cache != null) {
			try {
				_bindingsContentProvider.setContext(cache.getJavaProject(), WodParserCache.getTypeCache());
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			_bindingsContentProvider.setContext(null, null);
		}

		if (_bindingsTableViewer != null && !_bindingsTableViewer.getControl().isDisposed()) {
			_bindingsTableViewer.setInput(_wodElement);
		} else {
			_bindingsTableViewer.setInput(new Object[0]);
		}

		IApiBinding selectedBinding = (IApiBinding) ((IStructuredSelection) _bindingsTableViewer.getSelection()).getFirstElement();
		if (_wodElement != null && selectedBinding != null) {
			String selectedBindingName = selectedBinding.getName();
			Wo api = _bindingsContentProvider.getApi();
			if (api != null) {
				IApiBinding newBinding = api.getBinding(selectedBindingName);
				if (newBinding != null) {
					_bindingsTableViewer.setSelection(new StructuredSelection(newBinding));
				}
			}
		}

		String elementName = "none";
		boolean elementNameEnabled = false;
		String elementType = "";
		boolean elementTypeEnabled = false;
		if (_wodElement != null) {
			if (_wodElement.isInline()) {
				elementName = "inline";
			} else {
				elementName = _wodElement.getElementName();
				elementNameEnabled = true;
			}
			elementType = _wodElement.getElementType();
			if (elementType == null) {
				elementType = "<unknown>";
			}
			elementTypeEnabled = true;
		}
		_elementNameField.setText(elementName);
		_elementNameField.setEnabled(elementNameEnabled);
		_elementTypeField.setText(elementType);
		_elementTypeField.setEnabled(elementTypeEnabled);

		if (cache != null) {
			_dataBindingContext = new DataBindingContext();
			_refactoringElement = new RefactoringWodElement(_wodElement, cache);

			if (elementNameEnabled) {
				bindElementName(cache);
			}

			if (elementTypeEnabled) {
				bindElementType();
			}
		}
	}

	protected void bindElementType() {
		UpdateValueStrategy elementTypeUpdateStrategy = new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE);
		elementTypeUpdateStrategy.setBeforeSetValidator(new IValidator() {
			public IStatus validate(Object value) {
				String newName = (String) value;
				System.out.println(".validate: " + newName);
				IStatus status = Status.OK_STATUS;
				try {
					if (newName == null || newName.length() == 0) {
						status = ValidationStatus.error("Element types cannot be blank.");
					} else if (newName.contains(" ")) {
						status = ValidationStatus.error("Element types do not allow spaces.");
					}
				} catch (Exception e) {
					status = ValidationStatus.error("Failed to change element type.", e);
				}

				// reset the value back on failure
				if (!status.isOK()) {
					getElementTypeField().setText(getRefactoringElement().getElementType());
				}
				return status;
			}
		});

		// _dataBindingContext.bindValue(SWTObservables.observeText(_elementTypeField),
		// BeansObservables.observeValue(_refactoringElement,
		// RefactoringElementModel.ELEMENT_TYPE),
		// elementTypeUpdateStrategy, null);
		_elementTypeField.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				// DO NOTHING
			}

			public void focusLost(FocusEvent e) {
				try {
					String elementTypeFieldText = getElementTypeField().getText();
					String elementTypeModelText = getRefactoringElement().getElementType();
					if (!ComparisonUtils.equals(elementTypeModelText, elementTypeFieldText, true)) {
						getRefactoringElement().setElementType(elementTypeFieldText);
					}
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
		});
	}

	protected void bindElementName(final WodParserCache refactoringParserCache) {
		UpdateValueStrategy elementNameUpdateStrategy = new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE);
		elementNameUpdateStrategy.setBeforeSetValidator(new IValidator() {
			public IStatus validate(Object value) {
				String newName = (String) value;
				IStatus status = Status.OK_STATUS;
				try {
					if (newName == null || newName.length() == 0) {
						status = ValidationStatus.error("Element names cannot be blank.");
					} else if (newName.contains(" ")) {
						status = ValidationStatus.error("Element names do not allow spaces.");
					} else if (refactoringParserCache.getWodEntry().getModel().getElementNamed(newName) != null) {
						status = ValidationStatus.error("There is already an element named '" + newName + "'.");
					}
				} catch (Exception e) {
					status = ValidationStatus.error("Failed to change element name.", e);
				}

				// reset the value back on failure
				if (!status.isOK()) {
					getElementNameField().setText(getRefactoringElement().getElementName());
				}
				return status;
			}
		});
		_dataBindingContext.bindValue(SWTObservables.observeText(_elementNameField, SWT.FocusOut), BeansObservables.observeValue(_refactoringElement, RefactoringWodElement.ELEMENT_NAME), elementNameUpdateStrategy, null);
	}

	public TableViewer getBindingsTableViewer() {
		return _bindingsTableViewer;
	}

	public void selectionChanged(SelectionChangedEvent event) {
		SelectionChangedEvent wrappedEvent = new SelectionChangedEvent(this, getSelection());
		for (Iterator listeners = _listeners.iterator(); listeners.hasNext();) {
			ISelectionChangedListener listener = (ISelectionChangedListener) listeners.next();
			listener.selectionChanged(wrappedEvent);
		}
	}

	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		_listeners.add(listener);
	}

	public void setSelection(ISelection selection) {
		_bindingsTableViewer.setSelection(selection);
	}

	public ISelection getSelection() {
		return _bindingsTableViewer.getSelection();
	}

	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		_listeners.remove(listener);
	}

	public RefactoringWodElement getRefactoringElement() {
		return _refactoringElement;
	}

	public Text getElementNameField() {
		return _elementNameField;
	}

	public Combo getElementTypeField() {
		return _elementTypeField;
	}

	public IWodElement getWodElement() {
		return _wodElement;
	}
	
	public List<WodProblem> getWodProblems() {
		return _wodProblems;
	}
	
	public TextEditor getLastEditor() {
		return _lastEditor;
	}

	@Override
	public void dispose() {
		if (_dataBindingContext != null) {
			_dataBindingContext.dispose();
			_dataBindingContext = null;
		}
		super.dispose();
	}

	public void refresh() {
		if (_bindingsTableViewer == null) {
			return;
		}
		if (_lastEditor != null && _lastPosition != null) {
			cursorPositionChanged(_lastEditor, _lastPosition);
		}
	}

	public void cursorPositionChanged(TextEditor editor, Point selectionRange) {
		_lastEditor = editor;
		_lastPosition = selectionRange;

		WodParserCache cache = null;
		IWodElement wodElement = null;
		if (editor instanceof IWOEditor) {
			try {
				IWOEditor woEditor = (IWOEditor) editor;
				cache = woEditor.getParserCache();
				wodElement = woEditor.getSelectedElement(true, true);
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}

		setWodElement(wodElement, cache);
	}

	public class BindingEditingSupport extends EditingSupport {
		private TextCellEditor _bindingEditor;

		public BindingEditingSupport(TableViewer viewer) {
			super(viewer);
			_bindingEditor = new WOTextCellEditor(viewer.getTable());
		}

		@Override
		protected boolean canEdit(Object element) {
			return true;
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			return _bindingEditor;
		}

		@Override
		protected Object getValue(Object element) {
			String value = null;
			IApiBinding binding = (IApiBinding) element;
			if (binding != null) {
				IWodElement wodElement = getWodElement();
				if (wodElement != null) {
					IWodBinding wodBinding = wodElement.getBindingNamed(binding.getName());
					if (wodBinding != null) {
						value = wodBinding.getValue();
					}
				}
			}
			if (value == null) {
				value = "";
			}
			return value;
		}

		@Override
		protected void setValue(Object element, Object value) {
			IApiBinding binding = (IApiBinding) element;
			if (binding != null) {
				IWodElement wodElement = getWodElement();
				if (wodElement != null) {
					try {
						getRefactoringElement().setValueForBinding((String) value, binding.getName());
					} catch (Exception e) {
						e.printStackTrace();
					}
					BindingsInspector.this.refresh();
				}
			}
		}

	}

	protected class DoubleClickBindingHandler extends TableRowDoubleClickHandler {
		public DoubleClickBindingHandler(TableViewer viewer) {
			super(viewer);
		}

		protected void emptyDoubleSelectionOccurred() {
			// DO NOTHING
			RefactoringWodElement element = getRefactoringElement();
			if (element != null) {
				try {
					element.addBindingValueNamed("\"\"", "newBinding");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		protected void doubleSelectionOccurred(ISelection selection) {
			IApiBinding binding = (IApiBinding) ((IStructuredSelection) selection).getFirstElement();
			TextEditor lastEditor = getLastEditor();
			if (binding != null && lastEditor instanceof IWOEditor) {
				String value = getWodElement().getBindingValue(binding.getName());
				try {
					WodBindingValueHyperlink.open(value, binding, ((IWOEditor)lastEditor).getParserCache().getComponentType(), true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}