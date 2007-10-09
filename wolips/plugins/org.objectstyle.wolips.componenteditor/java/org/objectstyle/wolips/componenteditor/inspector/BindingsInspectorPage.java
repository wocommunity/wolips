package org.objectstyle.wolips.componenteditor.inspector;

import java.util.List;

import jp.aonir.fuzzyxml.FuzzyXMLDocument;
import jp.aonir.fuzzyxml.FuzzyXMLElement;
import jp.aonir.fuzzyxml.FuzzyXMLParser;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.NullProgressMonitor;
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

	private IWodElement _wodElement;

	private List<WodProblem> _wodProblems;

	private ComponentEditor _templateEditorPart;

	private PartListener _partListener;

	private ComponentLiveSearch _componentLiveSearch;

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
		}
		_wodElement = wodElement;
		_bindingsLabelProvider.setContext(_wodElement, _wodProblems);
		WodParserCache parserCache = null;
		if (_templateEditorPart != null) {
			try {
				parserCache = _templateEditorPart.getTemplateEditor().getSourceEditor().getParserCache();
				_bindingsContentProvider.setContext(parserCache.getJavaProject(), parserCache.getTypeCache());
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
		if (_wodElement != null) {
			if (_wodElement.isTemporary()) {
				_elementNameField.setText("inline");
				_elementNameField.setEnabled(false);
			} else {
				_elementNameField.setText(_wodElement.getElementName());
				_elementNameField.setEnabled(true);
			}
			_elementTypeField.setText(_wodElement.getElementType());
			_elementTypeField.setEnabled(true);
		} else {
			_elementNameField.setText("none");
			_elementNameField.setEnabled(false);
			_elementTypeField.setText("");
			_elementTypeField.setEnabled(false);
		}
		if (parserCache != null) {
			_componentLiveSearch = new ComponentLiveSearch(parserCache.getJavaProject(), new NullProgressMonitor());
			_componentLiveSearch.attachTo(_elementTypeField);
		}
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
		// bindingsTableContainer.setLayout(new GridLayout());

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
		// GridData bindingsTableData = new GridData(GridData.FILL_BOTH);
		// //bindingsTableData.horizontalSpan = 2;
		// bindingsTable.setLayoutData(bindingsTableData);

		TableColumn nameColumn = new TableColumn(bindingsTable, SWT.LEFT);
		nameColumn.setText("Attribute");
		bindingsTableLayout.setColumnData(nameColumn, new ColumnWeightData(50, true));
		// nameColumn.setWidth(100);

		TableColumn valueColumn = new TableColumn(bindingsTable, SWT.LEFT);
		valueColumn.setText("Binding");
		// valueColumn.setWidth(150);
		bindingsTableLayout.setColumnData(valueColumn, new ColumnWeightData(50, true));

		setWodElement(null);
	}

	@Override
	public void dispose() {
		super.dispose();
		if (_templateEditorPart != null) {
			_templateEditorPart.getSite().getPage().removePartListener(_partListener);
		}
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
		// calling setInput on the viewer will cause the model to refresh
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

		IWodElement wodElement = null;
		if (part instanceof ComponentEditor) {
			_templateEditorPart = (ComponentEditor) part;

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

		if (_templateEditorPart != null) {
			_templateEditorPart.getSite().getPage().addPartListener(_partListener);
		}
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