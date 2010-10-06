package org.objectstyle.wolips.eomodeler.editors.openEntity;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.ide.IIDEHelpContextIds;
import org.objectstyle.wolips.baseforplugins.util.FilesystemFolder;
import org.objectstyle.wolips.baseforplugins.util.ResourceUtilities;
import org.objectstyle.wolips.baseforplugins.util.URLUtils;
import org.objectstyle.wolips.eomodeler.EOModelerPerspectiveFactory;
import org.objectstyle.wolips.eomodeler.actions.OpenEntityModelerAction;
import org.objectstyle.wolips.eomodeler.core.model.EOEntity;
import org.objectstyle.wolips.eomodeler.core.model.EOModel;
import org.objectstyle.wolips.eomodeler.core.model.EOModelGroup;
import org.objectstyle.wolips.eomodeler.editors.EOModelEditor;

/**
 * Implements the open resource action. Opens a dialog prompting for a file and
 * opens the selected file in an editor.
 * 
 * @since 2.1
 */
public class OpenEntityHandler extends Action implements IHandler, IWorkbenchWindowActionDelegate {

	/**
	 * The identifier of the parameter storing the file path.
	 */
	private static String PARAM_ID_FILE_PATH = "filePath"; //$NON-NLS-1$

	/**
	 * A collection of objects listening to changes to this manager. This
	 * collection is <code>null</code> if there are no listeners.
	 */
	private transient ListenerList listenerList = null;

	/**
	 * Creates a new instance of the class.
	 */
	public OpenEntityHandler() {
		super();
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IIDEHelpContextIds.OPEN_WORKSPACE_FILE_ACTION);
	}

	public void addHandlerListener(final IHandlerListener listener) {
		if (listenerList == null) {
			listenerList = new ListenerList(ListenerList.IDENTITY);
		}

		listenerList.add(listener);
	}

	public void dispose() {
		listenerList = null;
	}

	public Object execute(final ExecutionEvent event) throws ExecutionException {
		EOModelEditor modelEditor = null;
		final List<IResource> files = new ArrayList<IResource>();

		if (event.getParameter(PARAM_ID_FILE_PATH) == null) {
			// Prompt the user for the resource to open.
			Object[] result = queryFileResource();

			if (result != null) {
				if (result.length == 2 && result[0] instanceof EOModelEditor) {
					modelEditor = (EOModelEditor)result[0];
					result = (Object[])result[1];
				}
				if (result != null) {
					for (int i = 0; i < result.length; i++) {
						files.add((IResource)result[i]);
					}
				}
			}

		} else {
			// Use the given parameter.
			final IResource resource = (IResource) event.getObjectParameterForExecution(PARAM_ID_FILE_PATH);
			if (!(resource instanceof IFile)) {
				throw new ExecutionException("filePath parameter must identify a file"); //$NON-NLS-1$
			}
			files.add(resource);
		}

		if (files.size() > 0) {

			final IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			if (window == null) {
				throw new ExecutionException("no active workbench window"); //$NON-NLS-1$
			}

			final IWorkbenchPage page = window.getActivePage();
			if (page == null) {
				throw new ExecutionException("no active workbench page"); //$NON-NLS-1$
			}

			for (Iterator it = files.iterator(); it.hasNext();) {
				IResource resource = (IResource)it.next();
				IFile file = null;
				if (!"eomodeld".equals(resource.getFileExtension()) && resource instanceof IFile) {
					if (modelEditor != null) {
						EOEntity entity = modelEditor.getModel().getModelGroup().getEntityNamed(ResourceUtilities.getFileNameWithoutExtension(resource));
						if (entity != null) {
							modelEditor.setSelection(new StructuredSelection(entity));
						}
					}
					else {
						file = (IFile)resource;//resource.getParent().getFile(new Path("index.eomodeld"));
						OpenEntityModelerAction.openResourceIfPossible(null, file);
					}
				}
				else if (resource instanceof IContainer) {
					if (modelEditor != null) {
						EOModel model = modelEditor.getModel().getModelGroup().getModelNamed(ResourceUtilities.getFileNameWithoutExtension(resource.getName()));
						if (model != null) {
							modelEditor.setSelection(new StructuredSelection(model));
						}
					}
					else {
						file = ((IContainer)resource).getFile(new Path("index.eomodeld"));
						OpenEntityModelerAction.openResourceIfPossible(null, file);
					}
				}
				else {
					file = null;
				}
			}
		}

		return null;
	}

	public void init(final IWorkbenchWindow window) {
		// Do nothing.
	}

	/**
	 * Query the user for the resources that should be opened
	 * 
	 * @return the resource that should be opened.
	 */
	private Object[] queryFileResource() {
		final IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window == null) {
			return null;
		}
		final Shell parent = window.getShell();
		IContainer input = ResourcesPlugin.getWorkspace().getRoot();
		
		EOModelEditor modelEditor = null;
		IWorkbenchPage activePage = window.getActivePage();
		if (activePage != null) {
			IPerspectiveDescriptor perspective = window.getActivePage().getPerspective();
			if (perspective != null && EOModelerPerspectiveFactory.EOMODELER_PERSPECTIVE_ID.equals(perspective.getId())) {
				IEditorPart editorPart = activePage.getActiveEditor();
				if (editorPart instanceof EOModelEditor) {
					modelEditor = (EOModelEditor)editorPart;
					EOModel editingModel = modelEditor.getModel();
					if (editingModel != null) {
						List<File> modelFolders = new LinkedList<File>();
						EOModelGroup modelGroup = editingModel.getModelGroup();
						for (EOModel model : modelGroup.getModels()) {
							File modelFolder = URLUtils.cheatAndTurnIntoFile(model.getModelURL()).getParentFile();
							modelFolders.add(modelFolder);
						}
						if (modelFolders.size() > 0) {
							input = new FilesystemFolder(new File("/"), modelFolders);
						}
					}
				}
			}
		}

		final OpenEntityDialog dialog = new OpenEntityDialog(parent, input, modelEditor == null);
		final int resultCode = dialog.open();
		if (resultCode != Window.OK) {
			return null;
		}

		Object[] result = dialog.getResult();
		if (modelEditor != null) {
			result = new Object[] { modelEditor, result };
		}
		return result;
	}

	public void removeHandlerListener(final IHandlerListener listener) {
		if (listenerList != null) {
			listenerList.remove(listener);

			if (listenerList.isEmpty()) {
				listenerList = null;
			}
		}
	}

	public void run(final IAction action) {
		try {
			execute(new ExecutionEvent());
		} catch (final ExecutionException e) {
			// TODO Do something meaningful and poignant.
		}
	}

	public void selectionChanged(final IAction action, ISelection selection) {
		// Do nothing.
	}
}
