package entitymodeler.actions;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;

import entitymodeler.ApplicationWorkbenchAdvisor;

public class OpenAction extends Action implements ActionFactory.IWorkbenchAction {
  private IWorkbenchWindow _window;

  public OpenAction(IWorkbenchWindow window) {
    _window = window;
    setText("&Open ...");
  }

  public void dispose() {
    // DO NOTHING
  }

  @Override
  public void run() {
    DirectoryDialog dialog = new DirectoryDialog(_window.getShell());
    //dialog.setFilterPath("*.eomodeld");
    String selectedDirectory = dialog.open();
    if (selectedDirectory != null) {
      try {
        ApplicationWorkbenchAdvisor.openModelPath(selectedDirectory);
      }
      catch (CoreException e) {
        e.printStackTrace();
      }
    }
  }
}
