package entitymodeler;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.objectstyle.wolips.eomodeler.EOModelerPerspectiveFactory;

/**
 * This class controls all aspects of the application's execution
 */
public class Application implements IApplication {
	public void stop() {
		// DO NOTHING
	}

	public Object start(IApplicationContext context) throws Exception {
		EOModelerPerspectiveFactory.setLocked(true);

		OpenDocumentEventProcessor openDocProcessor = new OpenDocumentEventProcessor();

		Display display = PlatformUI.createDisplay();
		display.addListener(SWT.OpenDocument, openDocProcessor);

		try {
			int returnCode = PlatformUI.createAndRunWorkbench(display, new ApplicationWorkbenchAdvisor(openDocProcessor));
			if (returnCode == PlatformUI.RETURN_RESTART) {
				return IApplication.EXIT_RESTART;
			}
			return IApplication.EXIT_OK;
		}
		finally {
			display.dispose();
		}
	}
}
