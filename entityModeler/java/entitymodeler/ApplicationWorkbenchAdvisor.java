package entitymodeler;

import javax.inject.Inject;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.osgi.service.environment.EnvironmentInfo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.internal.util.PrefUtil;
import org.objectstyle.wolips.eomodeler.EOModelerPerspectiveFactory;
import org.objectstyle.wolips.eomodeler.editors.EOModelEditor;

public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {
	private OpenDocumentEventProcessor _openDocProcessor;
	@Inject private EnvironmentInfo environmentInfo;
	
	public ApplicationWorkbenchAdvisor(OpenDocumentEventProcessor openDocProcessor) {
		_openDocProcessor = openDocProcessor;
	}

	public void eventLoopIdle(Display display) {
		_openDocProcessor.openFiles();
		super.eventLoopIdle(display);
	}

	@Override
	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
		return new ApplicationWorkbenchWindowAdvisor(configurer);
	}

	@Override
	public String getInitialWindowPerspectiveId() {
		return EOModelerPerspectiveFactory.EOMODELER_PERSPECTIVE_ID;
	}

	@Override
	public void preStartup() {
		Location loc = Platform.getInstanceLocation();
		try {
			// loc.setURL(new File("/tmp/.entityModeler").toURL(), false);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		PrefUtil.getAPIPreferenceStore().setValue(IWorkbenchPreferenceConstants.SHOW_TRADITIONAL_STYLE_TABS, false);
		super.preStartup();
	}

	@Override
	public void postStartup() {
		super.postStartup();
		try {
			String[] args = environmentInfo.getNonFrameworkArgs();

			String modelPath = null;
			boolean optionValue = false;
			for (String arg : args) {
				if ("-showlocation".equals(arg)) {
					optionValue = false;
				}
				else if (arg.startsWith("-")) {
					optionValue = true;
				}
				else if (optionValue) {
					optionValue = false;
				}
				else {
					modelPath = arg;
				}
			}

			ApplicationWorkbenchAdvisor.openModelPath(modelPath);
		}
		catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public static void openModelPath(String modelPath) throws CoreException {
		if (modelPath == null) {
			return;
		}

		IFileStore fileStore = EFS.getLocalFileSystem().getStore(new Path(modelPath));

		IFileStore modelFileStore;
		if (fileStore.getName().equals("index.eomodeld")) {
			modelFileStore = fileStore;
		}
		else if (fileStore.getName().endsWith(".eomodeld")) {
			modelFileStore = fileStore.getChild("index.eomodeld");
		}
		else {
			modelFileStore = fileStore;
		}
		IWorkbenchWindow existingWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage existingPage = existingWindow.getActivePage();
		IEditorReference[] editorReferences = existingPage.getEditorReferences();
		if (editorReferences.length == 0) {
			existingPage.openEditor(new FileStoreEditorInput(modelFileStore), EOModelEditor.EOMODEL_EDITOR_ID);
		}
		else {
			IWorkbenchWindow newWindow = PlatformUI.getWorkbench().openWorkbenchWindow(EOModelerPerspectiveFactory.EOMODELER_PERSPECTIVE_ID, null);
			newWindow.getActivePage().openEditor(new FileStoreEditorInput(modelFileStore), EOModelEditor.EOMODEL_EDITOR_ID);
		}
	}
}
