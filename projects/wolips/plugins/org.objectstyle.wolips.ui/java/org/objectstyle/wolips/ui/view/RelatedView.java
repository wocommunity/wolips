/*
 * Created on Sep 4, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.objectstyle.wolips.ui.view;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.PackageFragment;
import org.eclipse.jdt.internal.ui.viewsupport.StorageLabelProvider;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.CreateFileAction;
import org.eclipse.ui.actions.DeleteResourceAction;
import org.eclipse.ui.actions.RenameResourceAction;
import org.eclipse.ui.part.ViewPart;
import org.objectstyle.wolips.core.util.WorkbenchUtilities;

/**
 * @author ulrich
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class RelatedView extends ViewPart implements ISelectionListener {

	class ViewContentProvider implements IStructuredContentProvider {

		ICompilationUnit compilationUnit = null;

		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
			if (newInput != null)
				compilationUnit = (ICompilationUnit) newInput;
			else
				compilationUnit = null;

		}

		public void dispose() {

		}

		public Object[] getElements(Object parent) {

			List result = new LinkedList();
			if (compilationUnit != null) {
				try {

					String fileName =
						compilationUnit.getCorrespondingResource().getName();
					fileName = fileName.substring(0, fileName.length() - 5);
					ArrayList list = new ArrayList();
					WorkbenchUtilities.findFilesInResourceByName(
						list,
						compilationUnit.getJavaProject().getProject(),
						fileName + ".wod");
					if (list.size() == 0) {
						IProject[] projects =
							WorkbenchUtilities
								.getWorkspace()
								.getRoot()
								.getProjects();
						int i = 0;
						while (list.size() == 0) {
							WorkbenchUtilities.findFilesInResourceByName(
								list,
								projects[i],
								fileName + ".wod");
							i++;
						}
					}
					result.addAll(list);

				} catch (JavaModelException e) {

					e.printStackTrace();

				}
			}

			return result.toArray();

		}

		ViewContentProvider() {

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

		makeActions();

		hookContextMenu();

		viewer.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {

				if (event.getSelection() instanceof IStructuredSelection) {

					IStructuredSelection selection =
						(IStructuredSelection) event.getSelection();

					if (selection.getFirstElement() instanceof IFile)
						try {

							getViewSite().getPage().openEditor(
								(IFile) selection.getFirstElement());

						} catch (PartInitException _ex) {
						}

				}

			}

		});

		ResourcesPlugin
			.getWorkspace()
			.addResourceChangeListener(new IResourceChangeListener() {

			public void resourceChanged(IResourceChangeEvent event) {

				getViewSite()
					.getWorkbenchWindow()
					.getShell()
					.getDisplay()
					.asyncExec(new Runnable() {

					public void run() {

						viewer.refresh(false);

					}

				});

			}

		});

		getViewSite().getPage().addSelectionListener(this);

	}

	private void hookContextMenu() {

		MenuManager menuMgr = new MenuManager("#PopupMenu");

		menuMgr.setRemoveAllWhenShown(true);

		menuMgr.addMenuListener(new IMenuListener() {

			public void menuAboutToShow(IMenuManager manager) {

				fillContextMenu(manager);

			}

		});

		org.eclipse.swt.widgets.Menu menu =
			menuMgr.createContextMenu(viewer.getControl());

		viewer.getControl().setMenu(menu);

		getSite().registerContextMenu(menuMgr, viewer);

	}

	private void contributeToActionBars() {

		IActionBars bars = getViewSite().getActionBars();

		fillLocalPullDown(bars.getMenuManager());

		fillLocalToolBar(bars.getToolBarManager());

	}

	private void fillLocalPullDown(IMenuManager manager) {

		manager.add(new Separator());

	}

	private void fillContextMenu(IMenuManager manager) {

		ISharedImages images = PlatformUI.getWorkbench().getSharedImages();

		org.eclipse.swt.widgets.Shell shell = getSite().getShell();

		try {

			if (viewer.getInput() != null
				&& ((PackageFragment) viewer.getInput()).getKind() == 1) {

				manager.add(new CreateFileAction(shell));

				ISelection selection = viewer.getSelection();

				if (selection != null && !selection.isEmpty()) {

					RenameResourceAction renameAction =
						new RenameResourceAction(shell);

					renameAction.selectionChanged(
						(IStructuredSelection) viewer.getSelection());

					manager.add(renameAction);

					DeleteResourceAction deleteAction =
						new DeleteResourceAction(shell);

					deleteAction.setDisabledImageDescriptor(
						images.getImageDescriptor("IMG_TOOL_DELETE_DISABLED"));

					deleteAction.setImageDescriptor(
						images.getImageDescriptor("IMG_TOOL_DELETE"));

					deleteAction.setHoverImageDescriptor(
						images.getImageDescriptor("IMG_TOOL_DELETE_HOVER"));

					deleteAction.selectionChanged(
						(IStructuredSelection) viewer.getSelection());

					manager.add(deleteAction);

				}

			}

		} catch (JavaModelException e) {

			e.printStackTrace();

		}

		manager.add(new Separator("Additions"));

	}

	private void fillLocalToolBar(IToolBarManager itoolbarmanager) {

	}

	private void makeActions() {

		doubleClickAction = new Action() {

			public void run() {

				ISelection selection = viewer.getSelection();

				List list = ((IStructuredSelection) selection).toList();
				for (int i = 0; i < list.size(); i++) {
					IResource resource = (IResource) list.get(i);
					if ((resource != null)
						&& (resource.getType() == IResource.FILE))
						WorkbenchUtilities.open((IFile) resource);
				}
			}

		};

	}

	private void hookDoubleClickAction() {

		viewer.addDoubleClickListener(new IDoubleClickListener() {

			public void doubleClick(DoubleClickEvent event) {

				doubleClickAction.run();

			}

		});

	}

	private void showMessage(String message) {

		MessageDialog.openInformation(
			viewer.getControl().getShell(),
			"Resources",
			message);

	}

	public void setFocus() {

		viewer.getControl().setFocus();

	}

	public void selectionChanged(IWorkbenchPart part, ISelection selection) {

		if (selection instanceof IStructuredSelection) {

			IStructuredSelection sel = (IStructuredSelection) selection;

			Object selectedElement = sel.getFirstElement();

			if (selectedElement instanceof ICompilationUnit)
				viewer.setInput(selectedElement);
			else
				viewer.setInput(null);
		}

	}

}
