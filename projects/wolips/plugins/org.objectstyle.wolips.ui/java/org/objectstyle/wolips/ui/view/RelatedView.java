/*
 * Created on Sep 4, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.objectstyle.wolips.ui.view;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.internal.ui.viewsupport.StorageLabelProvider;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.objectstyle.wolips.core.logging.WOLipsLog;
import org.objectstyle.wolips.core.project.WOLipsCore;
import org.objectstyle.wolips.core.resources.IWOLipsResource;

/**
 * @author ulrich
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public final class RelatedView extends ViewPart implements ISelectionListener {
	private boolean forceOpenInTextEditor = false;

	protected class ViewContentProvider implements ITreeContentProvider {

		Object input = null;

		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
			input = newInput;
		}

		public void dispose() {

		}

		public Object[] getElements(Object parent) {
			IWOLipsResource wolipsResource = null;

			if (parent instanceof IResource) {
				wolipsResource =
					WOLipsCore.getWOLipsModel().getWOLipsResource(
						(IResource) parent);
				getViewer().setInput(wolipsResource);
			} else if (parent instanceof ICompilationUnit) {
				wolipsResource =
					WOLipsCore.getWOLipsModel().getWOLipsCompilationUnit(
						(ICompilationUnit) parent);
			}
			List result = new LinkedList();
			if (wolipsResource != null) {
				try {
					List list = (wolipsResource).getRelatedResources();
					result.addAll(list);

				} catch (Exception e) {
					WOLipsLog.log(e);
				}
			}
			return result.toArray();
		}
		ViewContentProvider() {
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
		 */
		public Object[] getChildren(Object parentElement) {
			// TODO Auto-generated method stub
			return null;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
		 */
		public Object getParent(Object element) {
			// TODO Auto-generated method stub
			return null;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
		 */
		public boolean hasChildren(Object element) {
			// TODO Auto-generated method stub
			return false;
		}
	}

	class ViewLabelProvider
		extends LabelProvider
		implements ITableLabelProvider {

		public String getColumnText(Object obj, int index) {

			if (obj instanceof IStorage) {

				IStorage s = (IStorage) obj;

				return s.getName();

			} else {

				return getText(obj);

			}

		}

		public Image getColumnImage(Object obj, int index) {

			return getImage(obj);

		}

		public Image getImage(Object obj) {

			return PlatformUI.getWorkbench().getSharedImages().getImage(
				"IMG_OBJ_ELEMENTS");

		}

		ViewLabelProvider() {

		}

	}

	class NameSorter extends ViewerSorter {

		NameSorter() {

		}

	}

	private TableViewer viewer;

	private Action doubleClickAction;

	public RelatedView() {
		super();

	}

	public void createPartControl(Composite parent) {

		viewer = new TableViewer(parent, 770);

		viewer.setContentProvider(new ViewContentProvider());

		viewer.setLabelProvider(
			new DecoratingLabelProvider(
				new StorageLabelProvider(),
				getSite()
					.getWorkbenchWindow()
					.getWorkbench()
					.getDecoratorManager()
					.getLabelDecorator()));

		viewer.setSorter(new NameSorter());
		viewer.getTable().addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.COMMAND || e.keyCode == SWT.ALT)
					setForceOpenInTextEditor(true);
			}
			public void keyReleased(KeyEvent e) {
				setForceOpenInTextEditor(false);
			}
		});

		doubleClickAction = new Action() {

			public void run() {

				ISelection selection = getViewer().getSelection();

				List list = ((IStructuredSelection) selection).toList();
				for (int i = 0; i < list.size(); i++) {
					Object object = list.get(i);
					IWOLipsResource wolipsResource = null;
					if (object != null) {
						if (object instanceof IResource) {
							wolipsResource =
								WOLipsCore.getWOLipsModel().getWOLipsResource(
									(IResource) object);
						} else if (object instanceof ICompilationUnit) {
							wolipsResource =
								WOLipsCore
									.getWOLipsModel()
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
		viewer.addDoubleClickListener(new IDoubleClickListener() {

			public void doubleClick(DoubleClickEvent event) {
				getDoubleClickAction().run();
				setForceOpenInTextEditor(false);
			}

		});

		ResourcesPlugin
			.getWorkspace()
			.addResourceChangeListener(new IResourceChangeListener() {

			public void resourceChanged(IResourceChangeEvent event) {
				IViewSite viewSite = getViewSite();
				if (viewSite == null)
					return;
				IWorkbenchWindow workbenchWindow =
					viewSite.getWorkbenchWindow();
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

						getViewer().refresh(false);

					}

				});

			}

		});

		getViewSite().getPage().addSelectionListener(this);

	}

	public void setFocus() {

		viewer.getControl().setFocus();

	}

	public void selectionChanged(IWorkbenchPart part, ISelection selection) {

		if (selection instanceof IStructuredSelection) {

			IStructuredSelection sel = (IStructuredSelection) selection;

			Object selectedElement = sel.getFirstElement();
			viewer.setInput(selectedElement);

		}
	}

	protected TableViewer getViewer() {
		return viewer;
	}
	/**
	 * @return
	 */
	protected boolean isForceOpenInTextEditor() {
		return forceOpenInTextEditor;
	}

	/**
	 * @param b
	 */
	protected void setForceOpenInTextEditor(boolean b) {
		forceOpenInTextEditor = b;
	}

	/**
	 * @return
	 */
	protected Action getDoubleClickAction() {
		return doubleClickAction;
	}
}
