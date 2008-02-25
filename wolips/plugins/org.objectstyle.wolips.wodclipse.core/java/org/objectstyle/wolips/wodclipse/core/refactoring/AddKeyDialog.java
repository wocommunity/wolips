package org.objectstyle.wolips.wodclipse.core.refactoring;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class AddKeyDialog extends Dialog {
  private Text _nameField;

  private Button _typeAsGivenButton;

  private Button _arrayOfButton;

  private Button _mutableArrayOfButton;

  private Combo _typeCombo;

  private Button _instanceVariableButton;

  private Button _getMethodButton;

  private Button _prependGetButton;

  private Button _setMethodButton;

  private AddKeyInfo _addKeyInfo;

  /**
   * Create the dialog
   * 
   * @param parentShell
   */
  public AddKeyDialog(AddKeyInfo addKeyInfo, Shell parentShell) {
    super(parentShell);
    _addKeyInfo = addKeyInfo;
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

    _typeAsGivenButton = new Button(container, SWT.RADIO);
    final GridData gd_typeAsGivenButton = new GridData();
    gd_typeAsGivenButton.verticalIndent = 5;
    _typeAsGivenButton.setLayoutData(gd_typeAsGivenButton);
    _typeAsGivenButton.setText("... (type as given)");
    new Label(container, SWT.NONE);
    new Label(container, SWT.NONE);

    _arrayOfButton = new Button(container, SWT.RADIO);
    _arrayOfButton.setText("Array of ...");
    new Label(container, SWT.NONE);
    new Label(container, SWT.NONE);

    _mutableArrayOfButton = new Button(container, SWT.RADIO);
    _mutableArrayOfButton.setText("Mutable array of ...");
    new Label(container, SWT.NONE);
    new Label(container, SWT.NONE);

    _typeCombo = new Combo(container, SWT.BORDER);
    _typeCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

    final Label _generateLabel = new Label(container, SWT.NONE);
    final GridData gd_generateLabel = new GridData(SWT.FILL, SWT.CENTER, false, false, 3, 1);
    gd_generateLabel.verticalIndent = 10;
    _generateLabel.setLayoutData(gd_generateLabel);
    _generateLabel.setText("Generate source code for:");
    new Label(container, SWT.NONE);
    new Label(container, SWT.NONE);

    _instanceVariableButton = new Button(container, SWT.CHECK);
    _instanceVariableButton.setSelection(true);
    _instanceVariableButton.setText("An instance variable");
    new Label(container, SWT.NONE);
    new Label(container, SWT.NONE);

    _getMethodButton = new Button(container, SWT.CHECK);
    _getMethodButton.setSelection(true);
    _getMethodButton.setText("A method getting the value");
    new Label(container, SWT.NONE);
    new Label(container, SWT.NONE);

    _prependGetButton = new Button(container, SWT.CHECK);
    final GridData gd_prependGetButton = new GridData();
    gd_prependGetButton.horizontalIndent = 20;
    _prependGetButton.setLayoutData(gd_prependGetButton);
    _prependGetButton.setText("Prepend \"get\" to method name");
    new Label(container, SWT.NONE);
    new Label(container, SWT.NONE);

    _setMethodButton = new Button(container, SWT.CHECK);
    _setMethodButton.setSelection(true);
    _setMethodButton.setText("A method setting the value");
    //

    _nameField.setText(_addKeyInfo.getName());

    _typeCombo.setItems(_addKeyInfo.getEntityNames());

    if (_addKeyInfo.getParameterTypeName() != null) {
      _typeCombo.setText(_addKeyInfo.getParameterTypeName());
      if (_addKeyInfo.getTypeName() != null) {
        String typeName = _addKeyInfo.getTypeName();
        if ("com.webobjects.foundation.NSArray".equals(typeName)) {
          _arrayOfButton.setSelection(true);
        }
        else {
          _mutableArrayOfButton.setSelection(true);
        }
      }
    }
    else if (_addKeyInfo.getTypeName() != null) {
      _typeCombo.setText(_addKeyInfo.getTypeName());
      _typeAsGivenButton.setSelection(true);
    }
    else {
      _typeCombo.setText("java.lang.String");
      _typeAsGivenButton.setSelection(true);
    }

    _instanceVariableButton.setSelection(_addKeyInfo.isCreateField());
    _getMethodButton.setSelection(_addKeyInfo.isCreateAccessorMethod());
    _prependGetButton.setSelection(_addKeyInfo.isPrependGetToAccessorMethod());
    _setMethodButton.setSelection(_addKeyInfo.isCreateMutatorMethod());

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
    return new Point(345, 358);
  }

  @Override
  protected void buttonPressed(int buttonId) {
    _addKeyInfo.setName(_nameField.getText());
    if (_typeAsGivenButton.getSelection()) {
      _addKeyInfo.setParameterTypeName(null);
      _addKeyInfo.setTypeName(_typeCombo.getText());
    }
    else if (_arrayOfButton.getSelection()) {
      _addKeyInfo.setParameterTypeName(_typeCombo.getText());
      _addKeyInfo.setTypeName("com.webobjects.foundation.NSArray");
    }
    else if (_mutableArrayOfButton.getSelection()) {
      _addKeyInfo.setParameterTypeName(_typeCombo.getText());
      _addKeyInfo.setTypeName("com.webobjects.foundation.NSMutableArray");
    }
    else {
      _addKeyInfo.setParameterTypeName(null);
      _addKeyInfo.setTypeName(null);
    }
    _addKeyInfo.setCreateField(_instanceVariableButton.getSelection());
    _addKeyInfo.setCreateAccessorMethod(_getMethodButton.getSelection());
    _addKeyInfo.setPrependGetToAccessorMethod(_prependGetButton.getSelection());
    _addKeyInfo.setCreateMutatorMethod(_setMethodButton.getSelection());
    super.buttonPressed(buttonId);
  }

  @Override
  protected void configureShell(Shell newShell) {
    super.configureShell(newShell);
    newShell.setText("Add Key");
  }

  public static void open(AddKeyInfo info, Shell shell) throws CoreException {
    AddKeyDialog dialog = new AddKeyDialog(info, shell);
    if (dialog.open() == IDialogConstants.OK_ID) {
      AddKeyOperation.addKey(info);
    }
  }

}
