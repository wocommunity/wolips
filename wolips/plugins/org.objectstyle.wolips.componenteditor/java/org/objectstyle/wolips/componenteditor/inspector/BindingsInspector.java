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
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
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
import org.objectstyle.wolips.bindings.api.IApiBinding;
import org.objectstyle.wolips.bindings.api.Wo;
import org.objectstyle.wolips.bindings.wod.IWodElement;
import org.objectstyle.wolips.bindings.wod.WodProblem;
import org.objectstyle.wolips.wodclipse.action.ComponentLiveSearch;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;
import org.objectstyle.wolips.wodclipse.core.document.IWOEditor;
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

	// private ComponentEditor _componentEditor;

	private ComponentLiveSearch _componentLiveSearch;

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

		_bindingsTableViewer = new TableViewer(bindingsTableContainer, SWT.MULTI |  SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
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

		TableColumn valueColumn = new TableColumn(bindingsTable, SWT.LEFT);
		valueColumn.setText("Binding");
		bindingsTableLayout.setColumnData(valueColumn, new ColumnWeightData(50, true));

		_bindingsTableViewer.addSelectionChangedListener(this);

		setWodElement(null, null);
	}

	public void setWodElement(IWodElement wodElement, WodParserCache cache) {
		if (_elementNameField.isDisposed() || _elementTypeField.isDisposed() || _bindingsTableViewer.getTable().isDisposed()) {
			return;
		}

		if (_componentLiveSearch != null) {
			_componentLiveSearch.detachFrom(_elementTypeField);
			_componentLiveSearch = null;
		}
		if (_dataBindingContext != null) {
			_dataBindingContext.dispose();
		}

		IApiBinding selectedBinding = (IApiBinding) ((IStructuredSelection) _bindingsTableViewer.getSelection()).getFirstElement();

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
			final WodParserCache refactoringParserCache = cache;
			_dataBindingContext = new DataBindingContext();
			_refactoringElement = new RefactoringWodElement(_wodElement, refactoringParserCache);

			if (elementNameEnabled) {
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

			if (elementTypeEnabled) {
				_componentLiveSearch = new ComponentLiveSearch(cache.getJavaProject(), new NullProgressMonitor());
				// _componentLiveSearch.attachTo(_elementTypeField);

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

				// _dataBindingContext.bindValue(SWTObservables.observeText(_elementTypeField),
				// BeansObservables.observeValue(_refactoringElement,
				// RefactoringElementModel.ELEMENT_TYPE),
				// elementTypeUpdateStrategy, null);

				// _refactoringElement.addPropertyChangeListener(RefactoringElementModel.ELEMENT_TYPE,
				// new PropertyChangeListener() {
				// public void propertyChange(PropertyChangeEvent evt) {
				// typeChanged();
				// }
				// });
			}
		}
	}

	// protected void typeChanged() {
	// _wodElement = getRefactoringElement().getWodElement();
	// _wodProblems = null;
	// try {
	// _wodProblems = WodModelUtils.getProblems(_wodElement,
	// _componentEditor.getTemplateEditor().getSourceEditor().getParserCache());
	// if (getBindingsTableViewer() != null &&
	// !getBindingsTableViewer().getTable().isDisposed()) {
	// getBindingsTableViewer().setInput(_wodElement);
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

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
		// _bindingsTableViewer.setInput(_bindingsTableViewer.getInput());
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
				wodElement = woEditor.getSelectedElement(false);
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}

		setWodElement(wodElement, cache);
	}
}