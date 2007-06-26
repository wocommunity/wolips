package entitymodeler.wizards;

import java.io.File;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.objectstyle.wolips.eomodeler.core.model.EODatabaseConfig;
import org.objectstyle.wolips.eomodeler.core.model.EOModel;

public class NewEOModelWizard extends Wizard implements INewWizard {
  private NewEOModelPage _mainPage;

  public NewEOModelWizard() {
    super();
  }

  @Override
  public void addPages() {
    // addPage(_mainPage);
  }

  @Override
  public boolean performFinish() {
    //    boolean returnValue = _mainPage.createEOModel();
    //    if (returnValue) {
    //      WizardsPlugin.selectAndReveal(_mainPage.getResourceToReveal());
    //    }
    //    return returnValue;
    return true;
  }

  public void init(IWorkbench workbench, IStructuredSelection selection) {
    //    _mainPage = new EOModelCreationPage(selection);
    //    setWindowTitle(Messages.getString("EOModelCreationWizard.title"));
    //    setDefaultPageImageDescriptor(WizardsPlugin.WOCOMPONENT_WIZARD_BANNER());
    setWindowTitle("New EOModel");
    DirectoryDialog directoryDialog = new DirectoryDialog(workbench.getActiveWorkbenchWindow().getShell());
    directoryDialog.setMessage("Select the directory that will contain this EOModel.");
    String modelPath = directoryDialog.open();
    if (modelPath == null) {
      // close
    }
    else {
      InputDialog modelNameDialog = new InputDialog(workbench.getActiveWorkbenchWindow().getShell(), "New EOModel", "Enter the name of the new EOModel.", "My EOModel", new ModelNameInputValidator());
      int results = modelNameDialog.open();
      if (results == InputDialog.CANCEL) {
        // close
      }
      else {
        String modelName = modelNameDialog.getValue();

        //        Set<EOModelVerificationFailure> failures = new HashSet<EOModelVerificationFailure>();
        //        IContainer parentContainer = (IContainer) _parentResource;
        //        IFolder existingModelFolder = parentContainer.getFolder(new Path(_modelName + ".eomodeld"));
        //        if (existingModelFolder.exists()) {
        //          failures.add(new EOModelVerificationFailure(null, "There's already a model in " + existingModelFolder.getLocation().toOSString() + ".", true, null));
        //          EOModelErrorDialog errors = new EOModelErrorDialog(Display.getDefault().getActiveShell(), failures);
        //          errors.open();
        //          return;
        //        }

        //        boolean createModelGroup = false;
        //        EOModelGroup modelGroup;
        //        try {
        //          modelGroup = IEOModelGroupFactory.Utility.loadModelGroup(_parentResource.getProject(), failures, true, existingModelFolder.getLocation().toFile().toURL());
        //        }
        //        catch (Exception e) {
        //          failures.clear();
        //          failures.add(new EOModelVerificationFailure(null, "Creating empty EOModelGroup for this model because " + e.getMessage(), true, e));
        //          modelGroup = new EOModelGroup();
        //          createModelGroup = true;
        //          EOModelErrorDialog errors = new EOModelErrorDialog(Display.getDefault().getActiveShell(), failures);
        //          errors.open();
        //        }

        try {
          EOModel model = new EOModel(modelName, null);
          model.setEditing(true);
          EODatabaseConfig databaseConfig = new EODatabaseConfig("Default");
          databaseConfig.setAdaptorName("JDBC");
          model.addDatabaseConfig(databaseConfig);
          model.saveToFolder(new File(modelPath));
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
