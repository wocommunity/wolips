package org.objectstyle.wolips.componenteditor.actions;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * <P>
 * This dialogue box is used to get the name of a component that will be
 * inserted both into the template as well as the wod file.
 * </P>
 */

public class InsertComponentDialogue extends org.eclipse.jface.dialogs.Dialog {

	public final static int RESULT_CREATE = 1;

	public final static int RESULT_CANCEL = 2;

	// no time to localise :-(
	public final static String LABEL_COMPONENT_INSTANCE_NAME = "Supply a name for your component:";

	// no time to localise :-(
	public final static String LABEL_COMPONENT_NAME = "Component Type:";

	// user interface elements which the user interacts with.

	protected Label _componentNameLabel;

	protected Text _componentNameText;

	protected Label _componentInstanceLabel;

	protected Text _componentInstanceNameText;

	protected Button _componentInstanceCreateButton;

	protected Button _componentInstanceCancelButton;

	protected Button _inline;

	protected InsertComponentSpecification _insertComponentSpecification;

	public InsertComponentDialogue(Shell parentShell, InsertComponentSpecification insertComponentSpecification) {
		super(parentShell);
		_insertComponentSpecification = insertComponentSpecification;
	}

	protected Control createDialogArea(Composite parent) {
		Composite control = (Composite) super.createDialogArea(parent);
		GridLayout layout = new GridLayout(1, true);

		control.setLayout(layout);

		if (_insertComponentSpecification.getComponentName() == null) {
			_componentNameLabel = new Label(control, 0);
			_componentNameLabel.setText(LABEL_COMPONENT_NAME);
	
			_componentNameText = new Text(control, SWT.BORDER);
			_componentNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		}
		
		_inline = new Button(control, SWT.CHECK);
		_inline.setText("Inline bindings?");
		_inline.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		_componentInstanceLabel = new Label(control, 0);
		_componentInstanceLabel.setText(LABEL_COMPONENT_INSTANCE_NAME);

		_componentInstanceNameText = new Text(control, SWT.BORDER);
		_componentInstanceNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		_componentInstanceNameText.addVerifyListener(new ComponentInstanceNameVerifyListener());

		if (null != _insertComponentSpecification.getComponentInstanceName()) {
			_componentInstanceNameText.setText(_insertComponentSpecification.getComponentInstanceName());
		}
		else {
			if (null != _insertComponentSpecification.getComponentInstanceNameSuffix()) {
				_componentInstanceNameText.setText(_insertComponentSpecification.getComponentInstanceNameSuffix());
			}
		}

		_inline.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
			
			public void widgetSelected(SelectionEvent e) {
				_componentInstanceLabel.setEnabled(!_inline.getSelection());
				_componentInstanceNameText.setEnabled(!_inline.getSelection());
			}
		});
		return control;
	}

	// these two methods handle buttons getting pressed.

	public void cancelPressed() {
		_insertComponentSpecification.setComponentInstanceName(null);
		super.cancelPressed();
	}

	public void okPressed() {
		if (_componentNameText != null) {
			_insertComponentSpecification.setComponentName(_componentNameText.getText());
		}
		_insertComponentSpecification.setComponentInstanceName(_componentInstanceNameText.getText());
		_insertComponentSpecification.setInline(_inline.getSelection());
		super.okPressed();
	}

}
