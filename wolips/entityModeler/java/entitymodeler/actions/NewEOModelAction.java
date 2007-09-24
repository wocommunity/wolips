package entitymodeler.actions;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.objectstyle.wolips.eomodeler.core.model.EODatabaseConfig;
import org.objectstyle.wolips.eomodeler.core.model.EOModel;
import org.objectstyle.wolips.eomodeler.core.model.EOModelGroup;
import org.objectstyle.wolips.eomodeler.core.model.EOModelVerificationFailure;
import org.objectstyle.wolips.eomodeler.core.model.IEOModelGroupFactory;
import org.objectstyle.wolips.eomodeler.editors.EOModelErrorDialog;

import entitymodeler.ApplicationWorkbenchAdvisor;

public class NewEOModelAction extends Action implements ActionFactory.IWorkbenchAction {
  private IWorkbenchWindow _window;

  public NewEOModelAction(IWorkbenchWindow window) {
    _window = window;
    setText("&New");
    setAccelerator(SWT.COMMAND | 'N');
  }

  public void dispose() {
    // DO NOTHING
  }

  @Override
  public void run() {
    DirectoryDialog directoryDialog = new DirectoryDialog(_window.getShell());
    directoryDialog.setMessage("Select the directory that will contain this EOModel.");
    String modelContainerPath = directoryDialog.open();
    if (modelContainerPath == null) {
      // close
    }
    else {
      InputDialog modelNameDialog = new InputDialog(_window.getShell(), "New EOModel", "Enter the name of the new EOModel.", "My EOModel", new ModelNameInputValidator());
      int results = modelNameDialog.open();
      if (results == InputDialog.CANCEL) {
        // close
      }
      else {
        String modelName = modelNameDialog.getValue();

        Set<EOModelVerificationFailure> failures = new HashSet<EOModelVerificationFailure>();
        //IContainer parentContainer = (IContainer) _parentResource;
        File modelFolder = new File(modelContainerPath, modelName + ".eomodeld");
        if (modelFolder.exists()) {
          failures.add(new EOModelVerificationFailure(null, "There's already a model in " + modelFolder + ".", true, null));
          EOModelErrorDialog errors = new EOModelErrorDialog(Display.getDefault().getActiveShell(), failures);
          errors.open();
          return;
        }

        boolean createModelGroup = false;
        EOModelGroup modelGroup = new EOModelGroup();
        try {
          IEOModelGroupFactory.Utility.loadModelGroup(modelFolder, modelGroup, failures, true, modelFolder.toURL(), new NullProgressMonitor());
        }
        catch (Exception e) {
          failures.clear();
          failures.add(new EOModelVerificationFailure(null, "Creating empty EOModelGroup for this model because " + e.getMessage(), true, e));
          modelGroup = new EOModelGroup();
          createModelGroup = true;
          EOModelErrorDialog errors = new EOModelErrorDialog(Display.getDefault().getActiveShell(), failures);
          errors.open();
        }

        try {
          EOModel model = new EOModel(modelName);
          //model.setEditing(true);
          EODatabaseConfig databaseConfig = new EODatabaseConfig("Default");
          databaseConfig.setAdaptorName("JDBC");
          model.addDatabaseConfig(databaseConfig);
          model.saveToFolder(modelFolder);

          ApplicationWorkbenchAdvisor.openModelPath(modelFolder.getAbsolutePath());
        }
        catch (Throwable t) {
          t.printStackTrace();
        }
      }
    }
  }

  protected static class ModelNameInputValidator implements IInputValidator {
    public String isValid(String newText) {
      return null;
    }
  }
}
