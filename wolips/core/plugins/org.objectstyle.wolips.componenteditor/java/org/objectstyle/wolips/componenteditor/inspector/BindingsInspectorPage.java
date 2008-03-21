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

public class BindingsInspectorPage extends Page implements ICursorPositionListener, ISelectionChangedListener, IWebobjectTagListener, IWOBrowserDelegate {
	private ComponentEditor _componentEditor;

	private Composite _container;

	private BindingsInspector _inspector;

	private WOBrowser _browser;

	private boolean _selectionChanging;

	private BindingsTextDropHandler _templateDropHandler;

	private BindingsTextDropHandler _wodDropHandler;

	private BindingsInspectorDropHandler _inspectorDropHandler;

	public BindingsInspectorPage(ComponentEditor componentEditor) {
		_componentEditor = componentEditor;
		if (componentEditor != null) {
			_componentEditor.getEditorInteraction().addWebObjectTagListener(this);

			if (_componentEditor.getTemplateEditor() != null) {
				_templateDropHandler = new BindingsTextDropHandler(_componentEditor.getTemplateEditor());
			}
			if (_componentEditor.getWodEditor() != null) {
				_wodDropHandler = new BindingsTextDropHandler(_componentEditor.getWodEditor());
			}
		}
	}

	public void bindingDragCanceled(WOBrowserColumn column) {
		if (_templateDropHandler != null) {
			_templateDropHandler.bindingDragCanceled(column);
		}
		if (_wodDropHandler != null) {
			_wodDropHandler.bindingDragCanceled(column);
		}
		if (_inspectorDropHandler != null) {
			_inspectorDropHandler.bindingDragCanceled(column);
		}
	}

	public void bindingDragging(WOBrowserColumn column, Point dragPoint) {
		if (_templateDropHandler != null) {
			_templateDropHandler.bindingDragging(column, dragPoint);
		}
		if (_wodDropHandler != null) {
			_wodDropHandler.bindingDragging(column, dragPoint);
		}
		if (_inspectorDropHandler != null) {
			_inspectorDropHandler.bindingDragging(column, dragPoint);
		}
	}

	public boolean bindingDropped(WOBrowserColumn column, Point dropPoint, BindingsDragHandler dragHandler) {
		boolean dropFinished = true;
		if (_templateDropHandler != null) {
			dropFinished &= _templateDropHandler.bindingDropped(column, dropPoint, dragHandler);
		}
		if (_wodDropHandler != null) {
			dropFinished &= _wodDropHandler.bindingDropped(column, dropPoint, dragHandler);
		}
		if (_inspectorDropHandler != null) {
			dropFinished &= _inspectorDropHandler.bindingDropped(column, dropPoint, dragHandler);
		}
		_inspector.refresh();
		return dropFinished;
	}

	public void browserColumnAdded(WOBrowserColumn column) {
		if (_templateDropHandler != null) {
			_templateDropHandler.browserColumnAdded(column);
		}
		if (_wodDropHandler != null) {
			_wodDropHandler.browserColumnAdded(column);
		}
		if (_inspectorDropHandler != null) {
			_inspectorDropHandler.browserColumnAdded(column);
		}
	}

	public void browserColumnRemoved(WOBrowserColumn column) {
		if (_templateDropHandler != null) {
			_templateDropHandler.browserColumnRemoved(column);
		}
		if (_wodDropHandler != null) {
			_wodDropHandler.browserColumnRemoved(column);
		}
		if (_inspectorDropHandler != null) {
			_inspectorDropHandler.browserColumnRemoved(column);
		}
	}

	@Override
	public void dispose() {
		if (_templateDropHandler != null) {
			_templateDropHandler.dispose();
		}
		if (_wodDropHandler != null) {
			_wodDropHandler.dispose();
		}
		if (_inspectorDropHandler != null) {
			_inspectorDropHandler.dispose();
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
		_inspectorDropHandler = new BindingsInspectorDropHandler(_inspector);

		Group browserGroup = new Group(_container, SWT.NONE);
		GridLayout browserGroupLayout = new GridLayout(2, false);
		browserGroupLayout.marginHeight = 0;
		browserGroupLayout.marginWidth = 0;
		browserGroup.setLayout(browserGroupLayout);
		browserGroup.setLayoutData(new GridData(GridData.FILL_BOTH));

		_browser = new WOBrowser(browserGroup, SWT.NONE);
		_browser.setBrowserDelegate(this);
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
					// IGNORE
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