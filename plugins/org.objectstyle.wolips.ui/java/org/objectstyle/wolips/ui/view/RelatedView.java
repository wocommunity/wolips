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

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.viewsupport.AppearanceAwareLabelProvider;
import org.eclipse.jdt.internal.ui.viewsupport.JavaElementImageProvider;
import org.eclipse.jdt.ui.JavaElementLabels;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.OpenWithMenu;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.navigator.ShowInNavigatorAction;
import org.objectstyle.wolips.datasets.project.WOLipsCore;
import org.objectstyle.wolips.datasets.resources.IWOLipsResource;
import org.objectstyle.wolips.ui.UIPlugin;

/**
 * @author ulrich To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public final class RelatedView extends ViewPart implements ISelectionListener,
		IPartListener {
	private boolean forceOpenInTextEditor = false;

	protected class ViewContentProvider implements ITreeContentProvider {

		Object input = null;

		private Object lastParent;

		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
			this.input = newInput;
		}

		public void dispose() {
			return;
		}

		public Object[] getElements(Object parent) {
			IWOLipsResource wolipsResource = null;
			// MS: If we add the dependency it is a circular dependency, so that
			// sucks ... We'll just do it Reflection-Style.
			if (parent != null
					&& parent
							.getClass()
							.getName()
							.equals(
									"org.objectstyle.wolips.components.input.ComponentEditorFileEditorInput")) {
				try {
					parent = parent.getClass().getMethod("getFile", null)
							.invoke(parent, null);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// System.out.println("ViewContentProvider.getElements: " +
				// parent);
			} else if (parent != null
					&& parent.getClass().getName()
							.startsWith("org.eclipse.wst")) {
				// MS: Total hack ... HTML editor returns its element as a
				// TextImpl. I'm not how to get back to the IResource from
				// that. It just so happens that there's always a previous
				// selection that is something we CAN use
				parent = lastParent;
				// try {
				// Object structuredDocRegion =
				// parent.getClass().getMethod("getFirstStructuredDocumentRegion",
				// null).invoke(parent, null);
				// Object structuredDoc =
				// structuredDocRegion.getClass().getMethod("getParentDocument",
				// null).invoke(structuredDocRegion, null);
				// System.out.println("ViewContentProvider.getElements: " +
				// structuredDoc);
				// }
				// catch (Exception e) {
				// e.printStackTrace();
				// }
			}
			if (parent instanceof IFileEditorInput) {
				IFileEditorInput input = (IFileEditorInput) parent;
				try {
					// HACK AK: we should use sth more generic here
					if ("java".equals(input.getFile().getFileExtension())) {
						parent = JavaCore.createCompilationUnitFrom(input
								.getFile());
					}
				} catch (Exception ex) {
					UIPlugin.getDefault().log(ex);
				}
			}
			if (parent instanceof IMember) {
				parent = ((IMember) parent).getCompilationUnit();
			}
			if (parent instanceof IResource) {
				wolipsResource = WOLipsCore.getWOLipsModel().getWOLipsResource(
						(IResource) parent);
				// getViewer().setInput(wolipsResource);
			} else if (parent instanceof ICompilationUnit) {
				wolipsResource = WOLipsCore.getWOLipsModel()
						.getWOLipsCompilationUnit((ICompilationUnit) parent);
			}
			List result = new LinkedList();
			if (wolipsResource != null) {
				try {
					List list = (wolipsResource).getRelatedResources();
					result.addAll(list);

				} catch (Exception e) {
					UIPlugin.getDefault().log(e);
				}
			}
			lastParent = parent;
			return result.toArray();
		}

		ViewContentProvider() {
			super();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
		 */
		public Object[] getChildren(Object parentElement) {
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
		 */
		public Object getParent(Object element) {
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
		 */
		public boolean hasChildren(Object element) {
			return false;
		}
	}

	class ViewLabelProvider extends AppearanceAwareLabelProvider implements
			ITableLabelProvider {
		public ViewLabelProvider() {
			super(AppearanceAwareLabelProvider.DEFAULT_TEXTFLAGS
					| JavaElementLabels.P_COMPRESSED,
					AppearanceAwareLabelProvider.DEFAULT_IMAGEFLAGS
							| JavaElementImageProvider.SMALL_ICONS);
			addLabelDecorator(PlatformUI.getWorkbench().getDecoratorManager()
					.getLabelDecorator());
		}

		public String getColumnText(Object _element, int _columnIndex) {
			String text = null;
			if (_element instanceof IResource) {
				IResource resource = (IResource) _element;
				String ext = resource.getFileExtension();
				if (ext != null) {
					if ("java".equalsIgnoreCase(ext)) {
						text = "Java";
					} else {
						text = ext.toUpperCase();
					}
				}
			}
			if (text == null) {
				text = getText(_element);
			}
			return text;
		}

		public Image getColumnImage(Object _element, int _columnIndex) {
			return getImage(_element);
		}
	}

	class NameSorter extends ViewerSorter {

		NameSorter() {
			super();
		}

	}

	TableViewer viewer;

	private Action doubleClickAction;

	/**
	 * 
	 */
	public RelatedView() {
		super();
	}

	private Action openInEditorAction;

	private Action showInNavigatorAction;

	public void createPartControl(Composite parent) {

		this.viewer = new TableViewer(parent, 770);

		this.viewer.setContentProvider(new ViewContentProvider());

		this.viewer.setLabelProvider(new ViewLabelProvider());
		// new DecoratingLabelProvider(
		// new LabelProvider(), getSite().getWorkbenchWindow()
		// .getWorkbench().getDecoratorManager()
		// .getLabelDecorator()));

		this.viewer.setSorter(new NameSorter());
		this.viewer.getTable().addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.COMMAND || e.keyCode == SWT.ALT)
					setForceOpenInTextEditor(true);
			}

			public void keyReleased(KeyEvent e) {
				setForceOpenInTextEditor(false);
			}
		});

		this.showInNavigatorAction = new ShowInNavigatorAction(this
				.getViewSite().getPage(), this.viewer);
		this.openInEditorAction = new Action() {

			public void run() {

				ISelection selection = getViewer().getSelection();

				List list = ((IStructuredSelection) selection).toList();
				for (int i = 0; i < list.size(); i++) {
					Object object = list.get(i);
					IWOLipsResource wolipsResource = null;
					if (object != null) {
						if (object instanceof IResource) {
							wolipsResource = WOLipsCore.getWOLipsModel()
									.getWOLipsResource((IResource) object);
						} else if (object instanceof ICompilationUnit) {
							wolipsResource = WOLipsCore.getWOLipsModel()
									.getWOLipsCompilationUnit(
											(ICompilationUnit) object);
						}
						if (wolipsResource != null) {
							wolipsResource.open(isForceOpenInTextEditor());
						}
					}
				}
			}

		};

		this.doubleClickAction = this.openInEditorAction;

		this.viewer.addDoubleClickListener(new IDoubleClickListener() {

			public void doubleClick(DoubleClickEvent event) {
				getDoubleClickAction().run();
				setForceOpenInTextEditor(false);
			}

		});

		ResourcesPlugin.getWorkspace().addResourceChangeListener(
				new IResourceChangeListener() {

					public void resourceChanged(IResourceChangeEvent event) {
						synchronized (viewer) {
							IViewSite viewSite = getViewSite();
							if (viewSite == null)
								return;
							IWorkbenchWindow workbenchWindow = viewSite
									.getWorkbenchWindow();
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
		this.selectionChanged(null, getViewSite().getPage().getSelection());
		createContextMenu();
	}

	/**
	 * Creates a pop-up menu on the given control
	 * 
	 * @param menuControl
	 *            the control with which the pop-up menu will be associated
	 */
	private void createContextMenu() {
		Control menuControl = this.viewer.getControl();
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
		getSite().registerContextMenu(menuMgr, this.viewer);
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
		menu.add(showInNavigatorAction);
		List list = ((IStructuredSelection) getViewer().getSelection())
				.toList();
		for (int i = 0; i < list.size(); i++) {
			Object object = list.get(i);
			if (object != null) {
				if (object instanceof IResource) {
					IResource resource = (IResource) object;
					OpenWithMenu action = new OpenWithMenu(getViewSite()
							.getPage(), resource);
					menu.add(action);
					// AK: I can
					// OpenEditorActionGroup group = new
					// OpenEditorActionGroup(this);
					// group.fillContextMenu(menu);
				} else if (object instanceof ICompilationUnit) {
					ICompilationUnit unit = (ICompilationUnit) object;
					OpenWithMenu action = new OpenWithMenu(getViewSite()
							.getPage(), unit);
					menu.add(action);
				}
			}
		}
		menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	public void setFocus() {
		this.viewer.getControl().setFocus();
	}

	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		synchronized (viewer) {
			if (selection != null && selection instanceof IStructuredSelection) {

				IStructuredSelection sel = (IStructuredSelection) selection;

				Object selectedElement = sel.getFirstElement();
				Object viewerInput = this.viewer.getInput();
				if (viewerInput == null
						|| (!viewerInput.equals(selectedElement)))
					this.viewer.setInput(selectedElement);
			}
		}
	}

	protected TableViewer getViewer() {
		return this.viewer;
	}

	/**
	 * @return force open in text editor
	 */
	protected boolean isForceOpenInTextEditor() {
		return this.forceOpenInTextEditor;
	}

	/**
	 * @param b
	 */
	protected void setForceOpenInTextEditor(boolean b) {
		this.forceOpenInTextEditor = b;
	}

	/**
	 * @return the double click action
	 */
	protected Action getDoubleClickAction() {
		return this.doubleClickAction;
	}

	public void partActivated(IWorkbenchPart part) {
		if (part instanceof IEditorPart) {
			IEditorInput input = ((IEditorPart) part).getEditorInput();
			if (input instanceof IFileEditorInput) {
				this.viewer.setInput(input);
			}
		}
	}

	public void partClosed(IWorkbenchPart part) {
		return;
	}

	public void partOpened(IWorkbenchPart part) {
		if (part instanceof IEditorPart) {
			IEditorInput input = ((IEditorPart) part).getEditorInput();
			if (input instanceof IFileEditorInput) {
				this.viewer.setInput(input);
			}
		}
	}

	public void partDeactivated(IWorkbenchPart part) {
		return;
	}

	public void partBroughtToTop(IWorkbenchPart part) {
		return;
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