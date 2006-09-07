package org.objectstyle.wolips.eogenerator.ui.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class EOGeneratorResultsDialog extends MessageDialog {
  private String myResults;

  public EOGeneratorResultsDialog(Shell _parentShell, String _results) {
    super(_parentShell, "EOGenerator Finished", null, "EOGenerator finished with the following results:", INFORMATION, new String[] { IDialogConstants.OK_LABEL }, 0);
    myResults = _results;
  }

  protected Control createCustomArea(Composite _parent) {
    Text resultsText = new Text(_parent, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
    resultsText.setText(myResults);
    resultsText.setEditable(false);
    GridData gd = new GridData(GridData.FILL_BOTH);
    gd.heightHint = 300;
    gd.widthHint = 500;
    resultsText.setLayoutData(gd);
    return resultsText;
  }
}
