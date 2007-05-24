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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
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
import org.objectstyle.wolips.workbenchutilities.WorkbenchUtilitiesPlugin;

/**
 * @author ulrich
 */
public final class RelatedView extends ViewPart implements ISelectionListener, IPartListener {
	protected class ViewContentProvider implements ITreeContentProvider {
		private ViewLabelProvider labelProvider;

		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
			// DO NOTHING
		}

		public void dispose() {
			return;
		}

		public Object[] getElements(Object parent) {
			Object actualParent = parent;
			IWOLipsResource wolipsResource = null;
			// MS: If we add the dependency it is a circular dependency, so that
			// sucks ... We'll just do it Reflection-Style.
			if (actualParent != null && actualParent.getClass().getName().equals("org.objectstyle.wolips.components.input.ComponentEditorFileEditorInput")) {
				try {
					actualParent = actualParent.getClass().getMethod("getFile", (Class[])null).invoke(actualParent, (Object[])null);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// System.out.println("ViewContentProvider.getElements: " +
				// parent);
			}
			if (actualParent instanceof IFileEditorInput) {
				IFileEditorInput input = (IFileEditorInput) actualParent;
				try {
					// HACK AK: we should use sth more generic here
					if ("java".equals(input.getFile().getFileExtension())) {
						actualParent = JavaCore.createCompilationUnitFrom(input.getFile());
					}
				} catch (Exception ex) {
					UIPlugin.getDefault().log(ex);
				}
			}
			if (actualParent instanceof IMember) {
				actualParent = ((IMember) actualParent).getCompilationUnit();
			}
			if (actualParent instanceof IResource) {
				wolipsResource = WOLipsCore.getWOLipsModel().getWOLipsResource((IResource) actualParent);
				// getViewer().setInput(wolipsResource);
			} else if (actualParent instanceof ICompilationUnit) {
				wolipsResource = WOLipsCore.getWOLipsModel().getWOLipsCompilationUnit((ICompilationUnit) actualParent);
			}
			List<IResource> result = new LinkedList<IResource>();
			if (wolipsResource != null) {
				try {
					List<IResource> list = wolipsResource.getRelatedResources();
					result.addAll(list);

				} catch (Exception e) {
					UIPlugin.getDefault().log(e);
				}
			} else if(actualParent != null && actualParent instanceof IResource) {
				try {
					final IResource resource = (IResource)actualParent;
					final List<IResource> list = new ArrayList<IResource>();
					IContainer lproj = resource.getParent();
					if(lproj != null && "lprog".equals(lproj.getFileExtension())) {
						IContainer p = lproj.getParent();
						p.accept(new IResourceProxyVisitor() {

							public boolean visit(IResourceProxy proxy) throws CoreException {
								if(proxy.getName().endsWith(".lproj")) {
									IContainer f = (IContainer) proxy.requestResource();
									IResource m = f.findMember(resource.getName());
									if(m != null) {
										list.add(m);
									}
								}
								return true;
							}
							
						}, IResource.DEPTH_ONE);
						result.addAll(list);
					}

				} catch (Exception e) {
					UIPlugin.getDefault().log(e);
				}
			}
			Object[] resultList = result.toArray();
			// labelProvider needs the element list to check for duplicate
			// filenames
			labelProvider.setResultList(resultList);
			return resultList;
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

	class ViewLabelProvider extends AppearanceAwareLabelProvider implements ITableLabelProvider {
		private Set<IResource> duplicateResourceSet;

		public ViewLabelProvider() {
			super(AppearanceAwareLabelProvider.DEFAULT_TEXTFLAGS | JavaElementLabels.P_COMPRESSED, AppearanceAwareLabelProvider.DEFAULT_IMAGEFLAGS | JavaElementImageProvider.SMALL_ICONS);
			addLabelDecorator(PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator());
		}

		public void setResultList(Object[] items) {
			int length = items.length;
			duplicateResourceSet = new HashSet<IResource>(length);
			Map<String, IResource> filenameToItemMap = new HashMap<String, IResource>(length);
			int i = length;
			while (i-- > 0) {
				if (!(items[i] instanceof IResource)) {
					continue;
				}
				IResource thisResource = (IResource) items[i];
				IResource otherResource = filenameToItemMap.get(thisResource.getName());
				if (otherResource != null) {
					duplicateResourceSet.add(thisResource);
					duplicateResourceSet.add(otherResource);
				}
				filenameToItemMap.put(thisResource.getName(), thisResource);
			}
		}

		public String getColumnText(Object _element, int _columnIndex) {
			String text = null;
			if (_element instanceof IResource) {
				IResource resource = (IResource) _element;
				String ext = resource.getFileExtension();
				String name = resource.getName();
				if (ext != null) {
					if ("java".equalsIgnoreCase(ext)) {
						text = "Java";
					} else {
						if(!ext.matches("^wod|wo|woo|html|api$")) {
							text = ext.toUpperCase();
							if(resource.getParent() != null && resource.getParent().getFileExtension() != null 
									&& resource.getParent().getFileExtension().equals("lproj")) {
								text = resource.getParent().getName().replaceAll("\\.lproj", "");
							}
						} else {
							text = ext.toUpperCase();
						}
					}
					text += " (" + name + ")";
					if ("eomodeld".equalsIgnoreCase(ext)) {
						text = name;
					}
					if (duplicateResourceSet.contains(resource)) {
						text += " - " + resource.getProject().getName();
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

		ViewContentProvider viewContentProvider = new ViewContentProvider();
		this.viewer.setContentProvider(viewContentProvider);

		ViewLabelProvider viewLabelProvider = new ViewLabelProvider();
		this.viewer.setLabelProvider(viewLabelProvider);
		viewContentProvider.labelProvider = viewLabelProvider;

		this.viewer.setSorter(new NameSorter());

		this.showInNavigatorAction = new ShowInNavigatorAction(this.getViewSite().getPage(), this.viewer);
		this.openInEditorAction = new Action() {

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

		this.doubleClickAction = this.openInEditorAction;

		this.viewer.addDoubleClickListener(new IDoubleClickListener() {

			public void doubleClick(DoubleClickEvent event) {
				getDoubleClickAction().run();
			}

		});

		ResourcesPlugin.getWorkspace().addResourceChangeListener(new IResourceChangeListener() {

			public void resourceChanged(IResourceChangeEvent event) {
				synchronized (viewer) {
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
		this.viewer.getControl().setFocus();
	}

	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		synchronized (viewer) {
			if (selection != null && selection instanceof IStructuredSelection) {

				IStructuredSelection sel = (IStructuredSelection) selection;

				Object selectedElement = sel.getFirstElement();
				Object viewerInput = this.viewer.getInput();
				if (viewerInput == null || (!viewerInput.equals(selectedElement)))
					this.viewer.setInput(selectedElement);
			}
		}
	}

	protected TableViewer getViewer() {
		return this.viewer;
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