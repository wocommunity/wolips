package org.objectstyle.wolips.componenteditor.inspector;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.typed.BeanProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.databinding.swt.typed.WidgetProperties;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
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
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.editors.text.TextEditor;
import org.objectstyle.wolips.baseforplugins.util.ComparisonUtils;
import org.objectstyle.wolips.baseforuiplugins.utils.WOTextCellEditor;
import org.objectstyle.wolips.bindings.api.IApiBinding;
import org.objectstyle.wolips.bindings.api.Wo;
import org.objectstyle.wolips.bindings.wod.BindingValueKeyPath;
import org.objectstyle.wolips.bindings.wod.IWodBinding;
import org.objectstyle.wolips.bindings.wod.IWodElement;
import org.objectstyle.wolips.bindings.wod.WodProblem;
import org.objectstyle.wolips.wodclipse.core.completion.WodCompletionUtils;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;
import org.objectstyle.wolips.wodclipse.core.document.IWOEditor;
import org.objectstyle.wolips.wodclipse.core.refactoring.RefactoringWodBinding;
import org.objectstyle.wolips.wodclipse.core.refactoring.RefactoringWodElement;
import org.objectstyle.wolips.wodclipse.core.util.ICursorPositionListener;
import org.objectstyle.wolips.wodclipse.core.util.WodModelUtils;

public class BindingsInspector extends Composite implements ISelectionProvider, ISelectionChangedListener, ICursorPositionListener {
	private WodParserCache _cache;

	private TextEditor _lastEditor;

	private Point _lastPosition;

	private Combo _elementTypeField;

	private Text _elementNameField;

	private TableViewer _bindingsTableViewer;

	private Button _addBindingButton;

	private Button _removeBindingButton;

	private Button _addKeyActionButton;

	private BindingsLabelProvider _nameLabelProvider;

	private BindingsLabelProvider _valueLabelProvider;

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
		_bindingsContentProvider = new BindingsContentProvider();
		_bindingsTableViewer.setContentProvider(_bindingsContentProvider);
		// _bindingsTableViewer.setLabelProvider(_bindingsLabelProvider);

		TableColumnLayout bindingsTableLayout = new TableColumnLayout();
		bindingsTableContainer.setLayout(bindingsTableLayout);

		Table bindingsTable = _bindingsTableViewer.getTable();
		bindingsTable.setHeaderVisible(true);
		bindingsTable.setLinesVisible(true);

		ColumnViewerEditorActivationStrategy columnActivationStrategy = new ColumnViewerEditorActivationStrategy(_bindingsTableViewer) {
			protected boolean isEditorActivationEvent(ColumnViewerEditorActivationEvent event) {
				return event.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL || event.eventType == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION || event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC;
			}
		};

		TableViewerEditor.create(_bindingsTableViewer, columnActivationStrategy, ColumnViewerEditor.TABBING_HORIZONTAL | ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR | ColumnViewerEditor.TABBING_VERTICAL | ColumnViewerEditor.KEYBOARD_ACTIVATION);

		TableViewerColumn nameViewerColumn = new TableViewerColumn(_bindingsTableViewer, SWT.LEAD);
		TableColumn nameColumn = nameViewerColumn.getColumn();
		nameColumn.setText("Attribute");
		bindingsTableLayout.setColumnData(nameColumn, new ColumnWeightData(50, true));
		nameViewerColumn.setEditingSupport(new BindingNameEditingSupport(_bindingsTableViewer));
		_nameLabelProvider = new BindingsLabelProvider(0);
		nameViewerColumn.setLabelProvider(_nameLabelProvider);

		TableViewerColumn valueViewerColumn = new TableViewerColumn(_bindingsTableViewer, SWT.LEAD);
		TableColumn valueColumn = valueViewerColumn.getColumn();
		valueColumn.setText("Binding");
		bindingsTableLayout.setColumnData(valueColumn, new ColumnWeightData(50, true));
		valueViewerColumn.setEditingSupport(new BindingValueEditingSupport(_bindingsTableViewer));
		_valueLabelProvider = new BindingsLabelProvider(1);
		valueViewerColumn.setLabelProvider(_valueLabelProvider);

		_bindingsTableViewer.addSelectionChangedListener(this);

		Composite buttonContainer = new Composite(this, SWT.NONE);
		GridData buttonContainerData = new GridData(GridData.FILL_HORIZONTAL);
		buttonContainerData.horizontalSpan = 2;
		buttonContainer.setLayoutData(buttonContainerData);
		RowLayout buttonLayout = new RowLayout(SWT.HORIZONTAL);
		buttonLayout.marginHeight = 0;
		buttonLayout.marginWidth = 0;
		buttonLayout.marginTop = 0;
		buttonLayout.marginLeft = 0;
		buttonLayout.marginBottom = 0;
		buttonLayout.marginRight = 0;
		buttonLayout.justify = true;
		buttonLayout.fill = true;
		buttonLayout.pack = true;
		buttonContainer.setLayout(buttonLayout);

		_addBindingButton = new Button(buttonContainer, SWT.PUSH);
		_addBindingButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

			public void widgetSelected(SelectionEvent e) {
				BindingsInspector.this.addNewBinding();
			}

		});
		_addBindingButton.setText("New Binding");

		_removeBindingButton = new Button(buttonContainer, SWT.PUSH);
		_removeBindingButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

			public void widgetSelected(SelectionEvent e) {
				BindingsInspector.this.removeBinding();
			}

		});
		_removeBindingButton.setText("Remove Binding");

		_addKeyActionButton = new Button(buttonContainer, SWT.PUSH);
		_addKeyActionButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

			public void widgetSelected(SelectionEvent e) {
				BindingsInspector.this.addKey();
			}

		});
		_addKeyActionButton.setText("Add Key");

		setWodElement(null, null);
	}

	public void setWodElement(IWodElement wodElement, WodParserCache cache) {
		if (_elementNameField.isDisposed() || _elementTypeField.isDisposed() || _bindingsTableViewer.getTable().isDisposed()) {
			return;
		}

		if (_dataBindingContext != null) {
			_dataBindingContext.dispose();
		}

		_cache = cache;
		_wodElement = wodElement;
		_wodProblems = null;
		if (wodElement != null && cache != null) {
			try {
				_wodProblems = WodModelUtils.getProblems(wodElement, cache);
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
		_nameLabelProvider.setContext(_wodElement, _wodProblems);
		_valueLabelProvider.setContext(_wodElement, _wodProblems);

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

		enableButtons();
	}

	protected void addNewBinding() {
		RefactoringWodElement element = getRefactoringElement();
		if (element != null) {
			try {
				String newBindingName = RefactoringWodElement.findUnusedBindingName(element.getWodElement(), "newBinding");
				element.addBindingValueNamed("\"\"", null, newBindingName);
				BindingsInspector.this.refresh();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	protected void removeBinding() {
		IApiBinding binding = (IApiBinding) ((IStructuredSelection) _bindingsTableViewer.getSelection()).getFirstElement();
		if (binding != null) {
			RefactoringWodElement element = getRefactoringElement();
			if (element != null) {
				try {
					element.removeBindingNamed(binding.getName());
					BindingsInspector.this.refresh();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	protected void addKey() {
		IApiBinding binding = (IApiBinding) ((IStructuredSelection) _bindingsTableViewer.getSelection()).getFirstElement();
		if (binding != null && _cache != null) {
			try {
				BindingValueKeyPath bindingValueKeyPath = new BindingValueKeyPath(_wodElement.getBindingValue(binding.getName()), _cache);
				if (bindingValueKeyPath.canAddKey()) {
					String name = WodCompletionUtils.addKeyOrAction(bindingValueKeyPath, binding, _cache.getComponentType());
					getRefactoringElement().setValueForBinding(name, binding.getName());
					BindingsInspector.this.refresh();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	protected void enableButtons() {
		if (_wodElement == null) {
			_addBindingButton.setEnabled(false);
			_removeBindingButton.setEnabled(false);
			_addKeyActionButton.setEnabled(false);
		} else {
			_addBindingButton.setEnabled(true);
			IApiBinding binding = (IApiBinding) ((IStructuredSelection) _bindingsTableViewer.getSelection()).getFirstElement();
			if (binding == null || _cache == null) {
				_removeBindingButton.setEnabled(false);
				_addKeyActionButton.setEnabled(false);
			} else {
				_removeBindingButton.setEnabled(true);
				try {
					BindingValueKeyPath bindingValueKeyPath = new BindingValueKeyPath(_wodElement.getBindingValue(binding.getName()), _cache);
					if (bindingValueKeyPath.canAddKey()) {
						_addKeyActionButton.setEnabled(true);
					} else {
						_addKeyActionButton.setEnabled(false);
					}
				} catch (Exception e) {
					e.printStackTrace();
					_addKeyActionButton.setEnabled(false);
				}
			}
		}
	}

	protected void bindElementType() {
		UpdateValueStrategy<Object, String> elementTypeUpdateStrategy = new UpdateValueStrategy<Object,String>(UpdateValueStrategy.POLICY_UPDATE);
		elementTypeUpdateStrategy.setBeforeSetValidator(new IValidator<String>() {
			public IStatus validate(String newName) {
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
						BindingsInspector.this.refresh();
					}
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
		});
	}

	protected void bindElementName(final WodParserCache refactoringParserCache) {
		UpdateValueStrategy<String,String> elementNameUpdateStrategy = new UpdateValueStrategy<String,String>(UpdateValueStrategy.POLICY_UPDATE);
		elementNameUpdateStrategy.setBeforeSetValidator(new IValidator<String>() {
			public IStatus validate(String newName) {
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
		ISWTObservableValue<String> obs = WidgetProperties.text(SWT.FocusOut).observe(_elementNameField);
		IObservableValue<String> beanObs = BeanProperties.value(RefactoringWodElement.class, RefactoringWodElement.ELEMENT_NAME, String.class).observe(_refactoringElement);
		_dataBindingContext.bindValue(obs, beanObs, elementNameUpdateStrategy, null);
	}

	public TableViewer getBindingsTableViewer() {
		return _bindingsTableViewer;
	}

	public void selectionChanged(SelectionChangedEvent event) {
		enableButtons();

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

	public class BindingNameEditingSupport extends EditingSupport {
		private TextCellEditor _nameEditor;

		public BindingNameEditingSupport(TableViewer viewer) {
			super(viewer);
			_nameEditor = new WOTextCellEditor(viewer.getTable());
		}

		@Override
		protected boolean canEdit(Object element) {
			return true;
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			return _nameEditor;
		}

		@Override
		protected Object getValue(Object element) {
			String value = null;
			IApiBinding binding = (IApiBinding) element;
			if (binding != null) {
				value = binding.getName();
			}
			if (value == null) {
				value = "<none>";
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
						RefactoringWodBinding wodBinding = getRefactoringElement().getBindingNamed(binding.getName());
						if (wodBinding != null) {
							wodBinding.setName((String) value);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					BindingsInspector.this.refresh();
				}
			}
		}

	}

	public class BindingValueEditingSupport extends EditingSupport {
		private TextCellEditor _valueEditor;

		public BindingValueEditingSupport(TableViewer viewer) {
			super(viewer);
			_valueEditor = new WOTextCellEditor(viewer.getTable());
		}

		@Override
		protected boolean canEdit(Object element) {
			return true;
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			return _valueEditor;
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
						BindingsInspector.this.refresh();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}

	}
}