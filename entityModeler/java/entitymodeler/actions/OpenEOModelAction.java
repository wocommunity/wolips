package entitymodeler.actions;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;

import entitymodeler.ApplicationWorkbenchAdvisor;

public class OpenEOModelAction extends Action implements ActionFactory.IWorkbenchAction {
  private IWorkbenchWindow _window;

  public OpenEOModelAction(IWorkbenchWindow window) {
    _window = window;
    setText("&Open ...");
    setAccelerator(SWT.COMMAND | 'O');//SWTKeySupport.convertKeyStrokeToAccelerator(KeyStroke.getInstance("s"));
  }

  @Override
  public void dispose() {
    // DO NOTHING
  }

  @Override
  public void run() {
    FileDialog dialog = new FileDialog(_window.getShell());
    dialog.setFilterPath("*.eomodeld");
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
