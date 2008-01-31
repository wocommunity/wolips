package org.objectstyle.wolips.componenteditor.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate2;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.objectstyle.wolips.componenteditor.part.ComponentEditor;
import org.objectstyle.wolips.locate.LocatePlugin;
import org.objectstyle.wolips.locate.result.LocalizedComponentsLocateResult;
import org.objectstyle.wolips.workbenchutilities.WorkbenchUtilitiesPlugin;

public class OpenComponentAction extends Action implements IWorkbenchWindowActionDelegate, IActionDelegate2 {
	private Object _selectedObject;

	public OpenComponentAction() {
		setText("Open Component");
		setDescription("Open a Component");
		setToolTipText("Open a Component");
	}

	public void run() {
		runWithEvent(null);
	}

	public void runWithEvent(Event e) {
		IJavaProject javaProject = null;
		if (_selectedObject instanceof IJavaElement) {
			IJavaElement javaElement = (IJavaElement) _selectedObject;
			javaProject = javaElement.getJavaProject();
		} else if (_selectedObject instanceof IResource) {
			IProject project = ((IResource) _selectedObject).getProject();
			javaProject = JavaCore.create(project);
		} else {
			IEditorPart editorPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
			if (editorPart != null) {
				IEditorInput editorInput = editorPart.getEditorInput();
				if (editorInput instanceof IFileEditorInput) {
					IFile file = ((IFileEditorInput) editorInput).getFile();
					javaProject = JavaCore.create(file.getProject());
				}
			}
		}

		Shell parent = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		if (javaProject == null) {
			ErrorDialog.openError(parent, "Select a Project", "You must have selected an object within a project before using Open Component.", Status.OK_STATUS);
		}
		else {
			SelectionDialog dialog = new WOElementSelectionDialog(parent, javaProject, PlatformUI.getWorkbench().getProgressService());
			dialog.setTitle("Open Component");
			dialog.setMessage("Select a Component to Open");

			int result = dialog.open();
			if (result != IDialogConstants.OK_ID) {
				return;
			}
			Object[] typeNames = dialog.getResult();
			if (typeNames != null && typeNames.length > 0) {
				for (int i = 0; i < typeNames.length; i++) {
					String typeName = (String) typeNames[i];
					OpenComponentAction.openComponentWithTypeNamed(javaProject, typeName);
				}
			}
		}
	}

	public void run(IAction action) {
		run();
	}

	public void dispose() {
		// DO NOTHING
	}

	public void init(IWorkbenchWindow window) {
		// DO NOTHING
	}

	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			_selectedObject = ((IStructuredSelection) selection).getFirstElement();
		}
	}

	public void runWithEvent(IAction action, Event event) {
		runWithEvent(event);
	}

	public void init(IAction action) {
		// DO NOTHING
	}
	
	public static void openComponentWithTypeNamed(IJavaProject javaProject, String typeName) {
		try {
			IType type = javaProject.findType(typeName);
			if (type != null) {
				JavaUI.openInEditor(type, true, true);
				LocalizedComponentsLocateResult componentsLocateResults = LocatePlugin.getDefault().getLocalizedComponentsLocateResult(type.getUnderlyingResource());
				IFile wodFile = componentsLocateResults.getFirstWodFile();
				if (wodFile != null) {
					WorkbenchUtilitiesPlugin.open(wodFile, ComponentEditor.ID);
				}
			}
		} catch (Throwable e1) {
			e1.printStackTrace();
		}
	}
}
