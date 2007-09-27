package entitymodeler;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
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
    
    Display display = PlatformUI.createDisplay();
    display.addListener(43, new Listener() {
    //display.addListener(SWT.ExternalOpen, new Listener() {
      public void handleEvent(Event event) {
        String[] modelPaths = (String[])event.data;
        if (modelPaths != null) {
          for (String modelPath : modelPaths) {
            try {
              ApplicationWorkbenchAdvisor.openModelPath(modelPath);
            }
            catch (CoreException e) {
              e.printStackTrace();
            }
          }
        }
        System.out.println(".run: external open");
      }
    });

    try {
      int returnCode = PlatformUI.createAndRunWorkbench(display, new ApplicationWorkbenchAdvisor());
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
