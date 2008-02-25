package org.objectstyle.wolips.wodclipse.core.refactoring;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class AddActionDialog extends Dialog {
  private Text _nameField;

  private Combo _typeCombo;

  private AddActionInfo _addActionInfo;

  /**
   * Create the dialog
   * 
   * @param parentShell
   */
  public AddActionDialog(AddActionInfo addActionInfo, Shell parentShell) {
    super(parentShell);
    _addActionInfo = addActionInfo;
  }

  /**
   * Create contents of the dialog
   * 
   * @param parent
   */
  @Override
  protected Control createDialogArea(Composite parent) {
    Composite container = (Composite) super.createDialogArea(parent);
    final GridLayout gridLayout = new GridLayout();
    gridLayout.marginBottom = 10;
    gridLayout.marginTop = 10;
    gridLayout.marginRight = 10;
    gridLayout.marginLeft = 10;
    gridLayout.numColumns = 3;
    container.setLayout(gridLayout);

    final Label _nameLabel = new Label(container, SWT.NONE);
    final GridData gd_nameLabel = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
    _nameLabel.setLayoutData(gd_nameLabel);
    _nameLabel.setText("Name:");
    new Label(container, SWT.NONE);

    _nameField = new Text(container, SWT.BORDER);
    final GridData gd_nameButton = new GridData(SWT.FILL, SWT.CENTER, true, false);
    _nameField.setLayoutData(gd_nameButton);

    final Label _typeLabel = new Label(container, SWT.NONE);
    final GridData gd_typeLabel = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
    gd_typeLabel.verticalIndent = 5;
    _typeLabel.setLayoutData(gd_typeLabel);
    _typeLabel.setText("Type:");
    new Label(container, SWT.NONE);

    _typeCombo = new Combo(container, SWT.BORDER);
    _typeCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    //

    _nameField.setText(_addActionInfo.getName());

    if (_addActionInfo.getTypeName() != null) {
      _typeCombo.setText(_addActionInfo.getTypeName());
    }
    //_typeCombo.setItems(_addActionInfo.getEntityNames());

    return container;
  }

  /**
   * Create contents of the button bar
   * 
   * @param parent
   */
  @Override
  protected void createButtonsForButtonBar(Composite parent) {
    createButton(parent, IDialogConstants.OK_ID, "Add", true);
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
  }

  /**
   * Return the initial size of the dialog
   */
  @Override
  protected Point getInitialSize() {
    return new Point(345, 150);
  }

  @Override
  protected void buttonPressed(int buttonId) {
    _addActionInfo.setName(_nameField.getText());
    _addActionInfo.setTypeName(_typeCombo.getText());
    super.buttonPressed(buttonId);
  }

  @Override
  protected void configureShell(Shell newShell) {
    super.configureShell(newShell);
    newShell.setText("Add Key");
  }

  public static void open(AddActionInfo info, Shell shell) throws CoreException {
    AddActionDialog dialog = new AddActionDialog(info, shell);
    if (dialog.open() == IDialogConstants.OK_ID) {
      AddActionOperation.addAction(info);
    }
  }

}
