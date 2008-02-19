package org.objectstyle.wolips.componenteditor.inspector;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import jp.aonir.fuzzyxml.FuzzyXMLDocument;
import jp.aonir.fuzzyxml.FuzzyXMLElement;
import jp.aonir.fuzzyxml.FuzzyXMLParser;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.ISaveablePart;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.Page;
import org.objectstyle.wolips.bindings.Activator;
import org.objectstyle.wolips.bindings.wod.IWodElement;
import org.objectstyle.wolips.bindings.wod.WodProblem;
import org.objectstyle.wolips.componenteditor.part.ComponentEditor;
import org.objectstyle.wolips.templateeditor.TemplateEditor;
import org.objectstyle.wolips.templateeditor.TemplateSourceEditor;
import org.objectstyle.wolips.wodclipse.action.ComponentLiveSearch;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;
import org.objectstyle.wolips.wodclipse.core.util.WodHtmlUtils;
import org.objectstyle.wolips.wodclipse.core.util.WodModelUtils;

public class BindingsInspectorPage extends Page implements IAdaptable, ISelectionListener {
	private Composite _control;

	private Label _elementTypeLabel;

	private Combo _elementTypeField;

	private Label _elementNameLabel;

	private Text _elementNameField;

	private TableViewer _bindingsTableViewer;

	private BindingsLabelProvider _bindingsLabelProvider;

	private BindingsContentProvider _bindingsContentProvider;

	private ComponentEditor _templateEditorPart;

	private PartListener _partListener;

	private ComponentLiveSearch _componentLiveSearch;

	private DataBindingContext _dataBindingContext;

	private IWodElement _wodElement;

	private RefactoringElementModel _refactoringElement;

	private List<WodProblem> _wodProblems;

	public BindingsInspectorPage() {
		_partListener = new PartListener();
	}

	public IWorkbenchPart getTemplateEditorPart() {
		return _templateEditorPart;
	}

	public void setWodElement(IWodElement wodElement) {
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

		_wodElement = wodElement;
		_bindingsLabelProvider.setContext(_wodElement, _wodProblems);

		WodParserCache parserCache = null;
		if (_templateEditorPart != null) {
			try {
				parserCache = _templateEditorPart.getTemplateEditor().getSourceEditor().getParserCache();
				_bindingsContentProvider.setContext(parserCache.getJavaProject(), WodParserCache.getTypeCache());
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

		String elementName = "none";
		boolean elementNameEnabled = false;
		String elementType = "";
		boolean elementTypeEnabled = false;
		if (_wodElement != null) {
			if (_wodElement.isTemporary()) {
				elementName = "inline";
			} else {
				elementName = _wodElement.getElementName();
				elementNameEnabled = true;
			}
			elementType = _wodElement.getElementType();
			elementTypeEnabled = true;
		}
		_elementNameField.setText(elementName);
		_elementNameField.setEnabled(elementNameEnabled);
		_elementTypeField.setText(elementType);
		_elementTypeField.setEnabled(elementTypeEnabled);

		if (parserCache != null) {
			final WodParserCache refactoringParserCache = parserCache;
			_dataBindingContext = new DataBindingContext();
			_refactoringElement = new RefactoringElementModel(_wodElement, parserCache);

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
							} else if (refactoringParserCache.getWodModel().getElementNamed(newName) != null) {
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
				_dataBindingContext.bindValue(SWTObservables.observeText(_elementNameField, SWT.FocusOut), BeansObservables.observeValue(_refactoringElement, RefactoringElementModel.ELEMENT_NAME), elementNameUpdateStrategy, null);
			}

			if (elementTypeEnabled) {
				_componentLiveSearch = new ComponentLiveSearch(parserCache.getJavaProject(), new NullProgressMonitor());
				_componentLiveSearch.attachTo(_elementTypeField);

				UpdateValueStrategy elementTypeUpdateStrategy = new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE);
				elementTypeUpdateStrategy.setBeforeSetValidator(new IValidator() {
					public IStatus validate(Object value) {
						String newName = (String) value;
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
				_dataBindingContext.bindValue(SWTObservables.observeText(_elementTypeField), BeansObservables.observeValue(_refactoringElement, RefactoringElementModel.ELEMENT_TYPE), elementTypeUpdateStrategy, null);
				
				_refactoringElement.addPropertyChangeListener(RefactoringElementModel.ELEMENT_TYPE, new PropertyChangeListener() {
					public void propertyChange(PropertyChangeEvent evt) {
						typeChanged();
					}
				});
			}
		}
	}
	
	protected void typeChanged() {
		_wodElement = getRefactoringElement().getWodElement();
		_wodProblems = null;
		try {
			_wodProblems = WodModelUtils.getProblems(_wodElement, _templateEditorPart.getTemplateEditor().getSourceEditor().getParserCache());
			if (getBindingsTableViewer() != null && !getBindingsTableViewer().getTable().isDisposed()) {
				getBindingsTableViewer().setInput(_wodElement);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public TableViewer getBindingsTableViewer() {
		return _bindingsTableViewer;
	}

	public RefactoringElementModel getRefactoringElement() {
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
	public void createControl(Composite parent) {
		_control = new Composite(parent, SWT.NONE);
		_control.setLayout(new GridLayout(2, false));

		_elementNameLabel = new Label(_control, SWT.NONE);
		_elementNameLabel.setText("Component Name");
		_elementNameField = new Text(_control, SWT.BORDER);
		_elementNameField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		_elementTypeLabel = new Label(_control, SWT.NONE);
		_elementTypeLabel.setText("Component Type");
		_elementTypeField = new Combo(_control, SWT.BORDER);
		_elementTypeField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		_elementTypeField.setVisibleItemCount(5);

		Composite bindingsTableContainer = new Composite(_control, SWT.NONE);
		GridData bindingsTableContainerData = new GridData(GridData.FILL_BOTH);
		bindingsTableContainerData.horizontalSpan = 2;
		bindingsTableContainer.setLayoutData(bindingsTableContainerData);

		_bindingsTableViewer = new TableViewer(bindingsTableContainer, SWT.BORDER | SWT.V_SCROLL | SWT.FULL_SELECTION);
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

		setWodElement(null);
	}

	@Override
	public void dispose() {
		if (_templateEditorPart != null) {
			_templateEditorPart.getSite().getPage().removePartListener(_partListener);
		}
		if (_dataBindingContext != null) {
			_dataBindingContext.dispose();
			_dataBindingContext = null;
		}
		super.dispose();
	}

	@Override
	public Control getControl() {
		return _control;
	}

	@Override
	public void setFocus() {
		_bindingsTableViewer.getControl().setFocus();
	}

	public Object getAdapter(Class adapter) {
		if (ISaveablePart.class.equals(adapter)) {
			return getSaveablePart();
		}
		return null;
	}

	protected ISaveablePart getSaveablePart() {
		if (_templateEditorPart != null) {
			return _templateEditorPart;
		}
		return null;
	}

	public void refresh() {
		if (_bindingsTableViewer == null) {
			return;
		}
		_bindingsTableViewer.setInput(_bindingsTableViewer.getInput());
	}

	/*
	 * (non-Javadoc) Method declared on ISelectionListener.
	 */
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (_bindingsTableViewer == null) {
			return;
		}

		if (_templateEditorPart != null) {
			_templateEditorPart.getSite().getPage().removePartListener(_partListener);
			_templateEditorPart = null;
		}

		if (part instanceof ComponentEditor) {
			_templateEditorPart = (ComponentEditor) part;
		}

		refreshWodElement();

		if (_templateEditorPart != null) {
			_templateEditorPart.getSite().getPage().addPartListener(_partListener);
		}
	}

	protected void refreshWodElement() {
		IWodElement wodElement = null;
		if (_templateEditorPart != null) {
			IEditorPart activeEditor = _templateEditorPart.getActiveEditor();
			if (activeEditor instanceof TemplateEditor) {
				try {
					TemplateSourceEditor templateSourceEditor = ((TemplateEditor) activeEditor).getSourceEditor();
					WodParserCache cache = templateSourceEditor.getParserCache();
					ISelection realSelection = templateSourceEditor.getSelectionProvider().getSelection();
					if (realSelection instanceof ITextSelection) {
						ITextSelection textSelection = (ITextSelection) realSelection;
						FuzzyXMLDocument doc;
						if (templateSourceEditor.isDirty()) {
							FuzzyXMLParser parser = new FuzzyXMLParser(Activator.getDefault().isWO54(), true);
							doc = parser.parse(templateSourceEditor.getHTMLSource());
						} else {
							doc = cache.getHtmlXmlDocument();
						}
						FuzzyXMLElement element = doc.getElementByOffset(textSelection.getOffset());
						if (element != null) {
							wodElement = WodHtmlUtils.getOrCreateWodElement(element, false, cache);
						}
					} else if (realSelection instanceof IStructuredSelection) {
						IStructuredSelection structuredSelection = (IStructuredSelection) realSelection;
						Object obj = structuredSelection.getFirstElement();
						if (obj instanceof FuzzyXMLElement) {
							FuzzyXMLElement element = (FuzzyXMLElement) obj;
							wodElement = WodHtmlUtils.getOrCreateWodElement(element, false, cache);
						}
					}
					_wodProblems = null;
					_wodProblems = WodModelUtils.getProblems(wodElement, cache);
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
		}

		setWodElement(wodElement);
	}

	/**
	 * Part listener which cleans up this page when the template editor part is
	 * closed. This is hooked only when there is a template editor part.
	 */
	protected class PartListener implements IPartListener {
		public void partActivated(IWorkbenchPart part) {
			// DO NOTHING
		}

		public void partBroughtToTop(IWorkbenchPart part) {
			// DO NOTHING
		}

		public void partClosed(IWorkbenchPart part) {
			IWorkbenchPart templateEditorPart = getTemplateEditorPart();
			if (templateEditorPart == part) {
				templateEditorPart = null;
				setWodElement(null);
			}
		}

		public void partDeactivated(IWorkbenchPart part) {
			// DO NOTHING
		}

		public void partOpened(IWorkbenchPart part) {
			// DO NOTHING
		}
	}
}