/* ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0 
 *
 * Copyright (c) 2002 - 2006 The ObjectStyle Group 
 * and individual authors of the software.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:  
 *       "This product includes software developed by the 
 *        ObjectStyle Group (http://objectstyle.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "ObjectStyle Group" and "Cayenne" 
 *    must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written 
 *    permission, please contact andrus@objectstyle.org.
 *
 * 5. Products derived from this software may not be called "ObjectStyle"
 *    nor may "ObjectStyle" appear in their names without prior written
 *    permission of the ObjectStyle Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE OBJECTSTYLE GROUP OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the ObjectStyle Group.  For more
 * information on the ObjectStyle Group, please see
 * <http://objectstyle.org/>.
 *
 */
package org.objectstyle.wolips.ui.view;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.OpenWithMenu;
import org.eclipse.ui.part.IContributedContentsView;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.navigator.ShowInNavigatorAction;
import org.objectstyle.wolips.datasets.project.WOLipsCore;
import org.objectstyle.wolips.datasets.resources.IWOLipsResource;
import org.objectstyle.wolips.workbenchutilities.WorkbenchUtilitiesPlugin;

/**
 * @author ulrich
 */
public final class RelatedView extends ViewPart implements ISelectionListener, IPartListener {
	private TableViewer _viewer;

	private Action _doubleClickAction;

	private Action _openInEditorAction;

	private Action _showInNavigatorAction;

	public RelatedView() {
		super();
	}

	public void createPartControl(Composite parent) {
		Composite viewerContainer = new Composite(parent, SWT.NONE);
		
		_viewer = new TableViewer(viewerContainer, SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
		_viewer.getTable().setLinesVisible(false);
		
		RelatedContentProvider relatedContentProvider = new RelatedContentProvider();
		_viewer.setContentProvider(relatedContentProvider);

		RelatedLabelProvider relatedLabelProvider = new RelatedLabelProvider();
		_viewer.setLabelProvider(relatedLabelProvider);
		relatedContentProvider.setLabelProvider(relatedLabelProvider);

		_viewer.setSorter(new ViewerSorter());

		TableColumnLayout relatedTableLayout = new TableColumnLayout();
		viewerContainer.setLayout(relatedTableLayout);

		TableColumn typeColumn = new TableColumn(_viewer.getTable(), SWT.LEFT);
		relatedTableLayout.setColumnData(typeColumn, new ColumnPixelData(68));
		
		TableColumn nameColumn = new TableColumn(_viewer.getTable(), SWT.LEFT);
		relatedTableLayout.setColumnData(nameColumn, new ColumnWeightData(90, true));

		_showInNavigatorAction = new ShowInNavigatorAction(getViewSite().getPage(), _viewer);
		_openInEditorAction = new Action() {

			public void run() {

				ISelection selection = getViewer().getSelection();

				List list = ((IStructuredSelection) selection).toList();
				for (int i = 0; i < list.size(); i++) {
					Object object = list.get(i);
					IWOLipsResource wolipsResource = null;
					if (object != null) {
						if (object instanceof IResource) {
							IResource resource = (IResource) object;
							wolipsResource = WOLipsCore.getWOLipsModel().getWOLipsResource((IResource) object);
							if (wolipsResource != null) {
								wolipsResource.open();
							} else if (resource.getType() == IResource.FILE) {
								WorkbenchUtilitiesPlugin.open((IFile) resource);
							}
						} else if (object instanceof ICompilationUnit) {
							wolipsResource = WOLipsCore.getWOLipsModel().getWOLipsCompilationUnit((ICompilationUnit) object);
							wolipsResource.open();
						}
					}
				}
			}

		};

		_doubleClickAction = _openInEditorAction;

		_viewer.addDoubleClickListener(new IDoubleClickListener() {

			public void doubleClick(DoubleClickEvent event) {
				getDoubleClickAction().run();
			}

		});

		ResourcesPlugin.getWorkspace().addResourceChangeListener(new IResourceChangeListener() {

			public void resourceChanged(IResourceChangeEvent event) {
				synchronized (_viewer) {
					IViewSite viewSite = getViewSite();
					if (viewSite == null)
						return;
					IWorkbenchWindow workbenchWindow = viewSite.getWorkbenchWindow();
					if (workbenchWindow == null)
						return;
					Shell shell = workbenchWindow.getShell();
					if (shell == null)
						return;
					Display display = shell.getDisplay();
					if (display == null)
						return;
					display.asyncExec(new Runnable() {

						public void run() {
							TableViewer tableViewer = getViewer();
							if (!tableViewer.getTable().isDisposed()) {
								tableViewer.refresh(false);
							}
						}

					});
				}
			}

		});

		getViewSite().getPage().addSelectionListener(this);
		getViewSite().getPage().addPartListener(this);
		selectionChanged(null, getViewSite().getPage().getSelection());
		createContextMenu();
	}

	/**
	 * Creates a pop-up menu on the given control
	 * 
	 * @param menuControl
	 *            the control with which the pop-up menu will be associated
	 */
	private void createContextMenu() {
		Control menuControl = _viewer.getControl();
		MenuManager menuMgr = new MenuManager("#PopUp"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(menuControl);
		menuControl.setMenu(menu);
		// register the context menu such that other plugins may contribute to
		// it
		getSite().registerContextMenu(menuMgr, _viewer);
	}

	/**
	 * Adds actions to the context menu
	 * 
	 * @param viewer
	 *            the viewer who's menu we're configuring
	 * @param menu
	 *            The menu to contribute to
	 */
	void fillContextMenu(IMenuManager menu) {
		menu.add(new Separator());
		menu.add(_showInNavigatorAction);
		List list = ((IStructuredSelection) getViewer().getSelection()).toList();
		for (int i = 0; i < list.size(); i++) {
			Object object = list.get(i);
			if (object != null) {
				if (object instanceof IResource) {
					IResource resource = (IResource) object;
					OpenWithMenu action = new OpenWithMenu(getViewSite().getPage(), resource);
					menu.add(action);
					// AK: I can
					// OpenEditorActionGroup group = new
					// OpenEditorActionGroup(this);
					// group.fillContextMenu(menu);
				} else if (object instanceof ICompilationUnit) {
					ICompilationUnit unit = (ICompilationUnit) object;
					OpenWithMenu action = new OpenWithMenu(getViewSite().getPage(), unit);
					menu.add(action);
				}
			}
		}
		menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	public void setFocus() {
		_viewer.getControl().setFocus();
	}

	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		synchronized (_viewer) {
			if (selection != null && selection instanceof IStructuredSelection) {
				IStructuredSelection sel = (IStructuredSelection) selection;

				Object selectedElement = sel.getFirstElement();
				Object viewerInput = _viewer.getInput();
				if (viewerInput == null || (!viewerInput.equals(selectedElement))) {
					_viewer.setInput(selectedElement);
				}
			}
		}
	}

	protected TableViewer getViewer() {
		return _viewer;
	}

	/**
	 * @return the double click action
	 */
	protected Action getDoubleClickAction() {
		return _doubleClickAction;
	}

	public void partActivated(IWorkbenchPart part) {
		IWorkbenchPart contributingPart;
		if (part instanceof IEditorPart) {
			contributingPart = part;
		}
		else {
			IContributedContentsView contentsView = (IContributedContentsView)part.getAdapter(IContributedContentsView.class);
			if (contentsView != null) {
				contributingPart = contentsView.getContributingPart();
			}
			else {
				contributingPart = part;
			}
		}
		if (contributingPart instanceof IEditorPart) {
			IEditorInput input = ((IEditorPart) contributingPart).getEditorInput();
			if (input instanceof IFileEditorInput) {
				_viewer.setInput(((IFileEditorInput)input).getFile());
			}
		}
	}

	public void partClosed(IWorkbenchPart part) {
		// DO NOTHING
	}

	public void partOpened(IWorkbenchPart part) {
		IWorkbenchPart contributingPart;
		if (part instanceof IEditorPart) {
			contributingPart = part;
		}
		else {
			IContributedContentsView contentsView = (IContributedContentsView)part.getAdapter(IContributedContentsView.class);
			if (contentsView != null) {
				contributingPart = contentsView.getContributingPart();
			}
			else {
				contributingPart = part;
			}
		}
		if (contributingPart instanceof IEditorPart) {
			IEditorInput input = ((IEditorPart) contributingPart).getEditorInput();
			if (input instanceof IFileEditorInput) {
				_viewer.setInput(((IFileEditorInput)input).getFile());
			}
		}
	}

	public void partDeactivated(IWorkbenchPart part) {
		// DO NOTHING
	}

	public void partBroughtToTop(IWorkbenchPart part) {
		// DO NOTHING
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchPart#dispose()
	 */
	public void dispose() {
		getViewSite().getPage().removeSelectionListener(this);
		getViewSite().getPage().removePartListener(this);
		super.dispose();
	}
}