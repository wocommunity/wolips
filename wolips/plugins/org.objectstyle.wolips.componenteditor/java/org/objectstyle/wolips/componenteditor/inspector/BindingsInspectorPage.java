package org.objectstyle.wolips.componenteditor.inspector;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.Page;
import org.objectstyle.wolips.bindings.api.IApiBinding;
import org.objectstyle.wolips.bindings.wod.IWodBinding;
import org.objectstyle.wolips.bindings.wod.IWodElement;
import org.objectstyle.wolips.componenteditor.part.ComponentEditor;
import org.objectstyle.wolips.components.editor.IWebobjectTagListener;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;
import org.objectstyle.wolips.wodclipse.core.util.ICursorPositionListener;

public class BindingsInspectorPage extends Page implements ICursorPositionListener, ISelectionChangedListener, IWebobjectTagListener {
	private ComponentEditor _componentEditor;

	private Composite _container;

	private BindingsInspector _inspector;

	private WOBrowser _browser;

	private boolean _selectionChanging;

	private BindingsDropHandler _browserDelegate;

	public BindingsInspectorPage(ComponentEditor componentEditor) {
		_componentEditor = componentEditor;
		if (componentEditor != null) {
			_componentEditor.getEditorInteraction().addWebObjectTagListener(this);

			_browserDelegate = new BindingsDropHandler(_componentEditor);
		}
	}

	@Override
	public void dispose() {
		if (_browserDelegate != null) {
			_browserDelegate.dispose();
		}
		super.dispose();
	}

	@Override
	public void createControl(Composite parent) {
		_container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = 5;
		layout.marginWidth = 5;
		layout.horizontalSpacing = 10;
		_container.setLayout(layout);
		_container.setBackground(parent.getBackground());

		Group inspectorGroup = new Group(_container, SWT.NONE);
		GridLayout inspectorGroupLayout = new GridLayout(2, false);
		inspectorGroupLayout.marginHeight = 0;
		inspectorGroupLayout.marginWidth = 0;
		inspectorGroup.setLayout(inspectorGroupLayout);
		inspectorGroup.setLayoutData(new GridData(GridData.FILL_VERTICAL));

		_inspector = new BindingsInspector(inspectorGroup, SWT.NONE);
		_inspector.setBackground(_container.getBackground());
		_inspector.addSelectionChangedListener(this);
		GridData inspectorLayoutData = new GridData(GridData.FILL_VERTICAL);
		inspectorLayoutData.widthHint = 350;
		_inspector.setLayoutData(inspectorLayoutData);

		Group browserGroup = new Group(_container, SWT.NONE);
		GridLayout browserGroupLayout = new GridLayout(2, false);
		browserGroupLayout.marginHeight = 0;
		browserGroupLayout.marginWidth = 0;
		browserGroup.setLayout(browserGroupLayout);
		browserGroup.setLayoutData(new GridData(GridData.FILL_BOTH));

		_browser = new WOBrowser(browserGroup, SWT.NONE);
		_browser.setBrowserDelegate(_browserDelegate);
		_browser.setBackground(_container.getBackground());
		_browser.addSelectionChangedListener(this);
		_browser.setLayoutData(new GridData(GridData.FILL_BOTH));
		if (_componentEditor != null) {
			try {
				WodParserCache cache = _componentEditor.getParserCache();
				if (cache != null) {
					_browser.setRootType(cache.getComponentType());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void selectionChanged(SelectionChangedEvent event) {
		if (!_selectionChanging) {
			_selectionChanging = true;
			try {
				if (event.getSource() == _browser) {
					String keyPath = (String) ((IStructuredSelection) event.getSelection()).getFirstElement();
					IApiBinding apiBinding = (IApiBinding) ((IStructuredSelection) _inspector.getBindingsTableViewer().getSelection()).getFirstElement();
					if (apiBinding != null) {
						RefactoringWodElement element = _inspector.getRefactoringElement();
						if (element != null) {
							try {
								element.setValueForBinding(keyPath, apiBinding.getName());
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
					_inspector.refresh();
				} else {
					IApiBinding apiBinding = (IApiBinding) ((IStructuredSelection) event.getSelection()).getFirstElement();
					boolean keyPathSelected = false;
					if (apiBinding != null) {
						IWodElement element = _inspector.getWodElement();
						if (element != null) {
							IWodBinding wodBinding = element.getBindingNamed(apiBinding.getName());
							if (wodBinding != null) {
								if (wodBinding.isKeyPath()) {
									String value = wodBinding.getValue();
									_browser.setSelection(new StructuredSelection(value));
									keyPathSelected = true;
								}
							}
						}
					}

					if (!keyPathSelected) {
						_browser.setSelection(new StructuredSelection());
					}
				}
			} finally {
				_selectionChanging = false;
			}
		}
	}

	@Override
	public Control getControl() {
		return _container;
	}

	@Override
	public void setFocus() {
		_container.setFocus();
	}

	public void webObjectChanged() {
		_inspector.refresh();
	}

	public void webObjectTagSelected(String name) {
		// DO NOTHING
	}

	public void cursorPositionChanged(TextEditor editor, Point selectionRange) {
		_inspector.cursorPositionChanged(editor, selectionRange);
	}
}