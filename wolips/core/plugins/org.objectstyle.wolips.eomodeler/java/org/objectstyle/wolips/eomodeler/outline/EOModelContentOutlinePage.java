/*
 * ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0
 * 
 * Copyright (c) 2006 The ObjectStyle Group and individual authors of the
 * software. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * 3. The end-user documentation included with the redistribution, if any, must
 * include the following acknowlegement: "This product includes software
 * developed by the ObjectStyle Group (http://objectstyle.org/)." Alternately,
 * this acknowlegement may appear in the software itself, if and wherever such
 * third-party acknowlegements normally appear.
 * 
 * 4. The names "ObjectStyle Group" and "Cayenne" must not be used to endorse or
 * promote products derived from this software without prior written permission.
 * For written permission, please contact andrus@objectstyle.org.
 * 
 * 5. Products derived from this software may not be called "ObjectStyle" nor
 * may "ObjectStyle" appear in their names without prior written permission of
 * the ObjectStyle Group.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * OBJECTSTYLE GROUP OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 * 
 * This software consists of voluntary contributions made by many individuals on
 * behalf of the ObjectStyle Group. For more information on the ObjectStyle
 * Group, please see <http://objectstyle.org/>.
 *  
 */
package org.objectstyle.wolips.eomodeler.outline;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.TreeViewerEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;
import org.objectstyle.wolips.eomodeler.Activator;
import org.objectstyle.wolips.eomodeler.core.model.EOModel;
import org.objectstyle.wolips.eomodeler.editors.EOModelClipboardHandler;
import org.objectstyle.wolips.eomodeler.editors.EOModelEditor;

public class EOModelContentOutlinePage extends ContentOutlinePage implements PropertyChangeListener {
	private EOModelTreeViewUpdater _updater;

	private EOModelEditor _editor;

	private EOModelClipboardHandler _clipboardHandler;

	private ToggleNonClassPropertiesAction _toggleNonClassPropertiesAction;

	private ToggleModelGroupAction _toggleModelGroupAction;

	private Menu _contextMenu;

	private boolean _selectedWithOutline;

	public EOModelContentOutlinePage(EOModelEditor editor) {
		_selectedWithOutline = true;
		_clipboardHandler = new EOModelClipboardHandler();
		_editor = editor;
	}

	protected void updateClipboardHandler() {
		IPageSite site = getSite();
		if (site != null && _editor != null) {
			IActionBars actionBars = site.getActionBars();
			_clipboardHandler.attach(actionBars, _editor);
		}
	}

	public void createControl(Composite parent) {
		super.createControl(parent);

		TreeViewer treeViewer = getTreeViewer();
		
		
		//TreeColumnLayout outlineTreeLayout = new TreeColumnLayout();
		//_treeComposite.setLayout(outlineTreeLayout);

		final TreeViewerColumn column = new TreeViewerColumn(treeViewer, SWT.NONE);
		column.setLabelProvider(new EOModelOutlineColumnLabelProvider(treeViewer));
		column.setEditingSupport(new EOModelOutlineEditingSupport(treeViewer));
		column.getColumn().setWidth(400);
		//outlineTreeLayout.setColumnData(column.getColumn(), new ColumnWeightData(100, true));

		ColumnViewerEditorActivationStrategy strategy = new ColumnViewerEditorActivationStrategy(treeViewer) {
			@Override
			protected boolean isEditorActivationEvent(ColumnViewerEditorActivationEvent event) {
				return event.eventType == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION;
			}
		};
		/*
		TreeViewerEditor.create(treeViewer, null, strategy, ColumnViewerEditor.DEFAULT);
		parent.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				super.controlResized(e);
				Composite widget = (Composite)e.getSource();
				column.getColumn().setWidth(widget.getSize().x);
				System.out.println(".controlResized: " + widget.getSize());
			}
		});
		*/


		if (_updater != null) {
			_updater.dispose();
		}
		_updater = new EOModelTreeViewUpdater(treeViewer, new EOModelOutlineContentProvider(true, true, true, true, true, true, true, true));
		_updater.setModel(_editor.getModel());
		updateClipboardHandler();
		// AK: commenting prevents an error in swt
		// setFocus();

		IActionBars actionBars = getSite().getActionBars();
		IToolBarManager toolBarManager = actionBars.getToolBarManager();
		_toggleNonClassPropertiesAction = new ToggleNonClassPropertiesAction();
		toolBarManager.add(_toggleNonClassPropertiesAction);
		_toggleModelGroupAction = new ToggleModelGroupAction();
		toolBarManager.add(_toggleModelGroupAction);

		MenuManager menuManager = new MenuManager();
		menuManager.setRemoveAllWhenShown(true);
		menuManager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager m) {
				m.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
			}
		});
		Tree tree = treeViewer.getTree();
		_contextMenu = menuManager.createContextMenu(treeViewer.getTree());
		tree.setMenu(_contextMenu);
		getSite().registerContextMenu("org.objectstyle.wolips.eomodeler.outline", menuManager, treeViewer);

		if (_editor.getModel() == null) {
			treeViewer.setInput(new EOModelLoading(null));
			treeViewer.expandAll();
		}
	}
	
	@SuppressWarnings("unchecked")
	public void propertyChange(PropertyChangeEvent evt) {
		TreeViewer treeViewer = getTreeViewer();
		if (treeViewer != null) {
			Set<EOModel> oldModels = (Set<EOModel>) evt.getOldValue();
			Set<EOModel> newModels = new HashSet<EOModel>((Set<EOModel>) evt.getNewValue());
			newModels.removeAll(oldModels);
			if (newModels.size() == 1) {
				EOModel newModel = newModels.iterator().next();
				Object input = treeViewer.getInput();
				if (input instanceof EOModelLoading) {
					((EOModelLoading) input).setModel(newModel);
				}
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						getTreeViewer().refresh(getTreeViewer().getInput());
						getTreeViewer().expandAll();
					}
				});
			}
		}
	}

	@Override
	public void dispose() {
		if (_contextMenu != null && !_contextMenu.isDisposed()) {
			_contextMenu.dispose();
			_contextMenu = null;
		}
		if (_updater != null) {
			_updater.dispose();
		}
		super.dispose();
	}

	public void init(IPageSite pageSite) {
		super.init(pageSite);
		updateClipboardHandler();
	}

	public boolean isSelectedWithOutline() {
		return _selectedWithOutline;
	}

	public void selectionChanged(SelectionChangedEvent event) {
		super.selectionChanged(event);
		_clipboardHandler.selectionChanged(event);
	}

	public void setSelection(ISelection selection) {
		_selectedWithOutline = false;
		try {
			super.setSelection(selection);
		} finally {
			_selectedWithOutline = true;
		}
	}

	public EOModelTreeViewUpdater getUpdater() {
		return _updater;
	}

	public class ToggleModelGroupAction extends Action {
		private boolean _showModelGroup;

		public ToggleModelGroupAction() {
			_showModelGroup = true;
			refreshUI();
			setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Activator.EOMODEL_ICON));
		}

		public void toggleChanged() {
			_showModelGroup = !_showModelGroup;
			refreshUI();
		}

		public void refreshUI() {
			if (_showModelGroup) {
				getUpdater().showModel();
				setToolTipText("Show ModelGroup");
				setChecked(false);
			} else {
				getUpdater().showModelGroup();
				setToolTipText("Show Model");
				setChecked(true);
			}
		}

		@Override
		public void run() {
			toggleChanged();
		}
	}

	public class ToggleNonClassPropertiesAction extends Action {
		private boolean _showNonClassProperties;

		public ToggleNonClassPropertiesAction() {
			_showNonClassProperties = false;
			refreshUI();
			setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Activator.CLASS_PROPERTY_ICON));
		}

		public void toggleChanged() {
			_showNonClassProperties = !_showNonClassProperties;
			refreshUI();
		}

		public void refreshUI() {
			if (_showNonClassProperties) {
				getUpdater().showNonClassProperties();
				setToolTipText("Hide Non-Class Properties");
				setChecked(true);
			} else {
				getUpdater().hideNonClassProperties();
				setToolTipText("Show Non-Class Properties");
				setChecked(false);
			}
		}

		@Override
		public void run() {
			toggleChanged();
		}
	}
}
