package org.objectstyle.wolips.componenteditor.actions;


import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.objectstyle.wolips.wodclipse.action.ComponentLiveSearch;

/**
 * <P>
 * This dialogue box is used to get the name of a component that will be
 * inserted both into the template as well as the wod file.
 * </P>
 */

public class InsertComponentDialogue extends Dialog {

	public final static int RESULT_CREATE = 1;

	public final static int RESULT_CANCEL = 2;

	// user interface elements which the user interacts with.

	protected Label _componentNameLabel;

	protected Combo componentNameCombo;

	protected Label _componentInstanceLabel;

	protected Text _componentInstanceNameText;

	protected Button _componentInstanceCreateButton;

	protected Button _componentInstanceCancelButton;

	protected Button _inline;

	protected IJavaProject _project;

	protected InsertComponentSpecification _insertComponentSpecification;

	protected IProgressMonitor _progressMonitor;

	// no time to localise :-(
	private final static String LABEL_COMPONENT_INSTANCE_NAME = "WebObject Tag Name:";

	// no time to localise :-(
	private final static String LABEL_COMPONENT_NAME = "Component Type Name:";
	
	public InsertComponentDialogue(Shell parentShell, IJavaProject project, InsertComponentSpecification insertComponentSpecification) {
		super(parentShell);
		_project = project;
		_insertComponentSpecification = insertComponentSpecification;
		_progressMonitor = new NullProgressMonitor();
	}

	protected Control createDialogArea(Composite parent) {
		Composite control = (Composite) super.createDialogArea(parent);
		GridLayout layout = new GridLayout(1, true);
		layout.marginTop = 10;
		layout.marginRight = 10;
		layout.marginBottom = 10;
		layout.marginLeft = 10;

		control.setLayout(layout);

		_componentInstanceLabel = new Label(control, 0);
		_componentInstanceLabel.setText(LABEL_COMPONENT_INSTANCE_NAME);
		GridData componentInstanceLabelLayout = new GridData(GridData.FILL_HORIZONTAL);
		_componentInstanceLabel.setLayoutData(componentInstanceLabelLayout);

		_componentInstanceNameText = new Text(control, SWT.BORDER);
		GridData componentInstanceNameLayout = new GridData(GridData.FILL_HORIZONTAL);
		_componentInstanceNameText.setLayoutData(componentInstanceNameLayout);
		_componentInstanceNameText.addVerifyListener(new ComponentInstanceNameVerifyListener());

		if (_insertComponentSpecification.getComponentInstanceName() != null) {
			_componentInstanceNameText.setText(_insertComponentSpecification.getComponentInstanceName());
		} 
//		else {
//			if (_insertComponentSpecification.getComponentInstanceNameSuffix() != null) {
//				_componentInstanceNameText.setText(_insertComponentSpecification.getComponentInstanceNameSuffix());
//			}
//		}

		_inline = new Button(control, SWT.CHECK);
		_inline.setText("Use inline bindings, or enter webobject tag name:");
		_inline.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		if (_insertComponentSpecification.getComponentName() == null) {
			_componentNameLabel = new Label(control, 0);
			_componentNameLabel.setText(LABEL_COMPONENT_NAME);
			GridData componentNameLabelLayout = new GridData(GridData.FILL_HORIZONTAL);
			_componentNameLabel.setLayoutData(componentNameLabelLayout);

			componentNameCombo = new Combo(control, SWT.BORDER);
			GridData componentNameTextLayout = new GridData(GridData.FILL_HORIZONTAL);
			componentNameCombo.setLayoutData(componentNameTextLayout);
			new ComponentLiveSearch(_project, _progressMonitor).attachTo(componentNameCombo);
		}
		
		_inline.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

			public void widgetSelected(SelectionEvent e) {
				_componentInstanceNameText.setEnabled(!_inline.getSelection());
				validate();
			}
		});
		
		_componentInstanceNameText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				validate();
			}
		});

		componentNameCombo.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				validate();
			}
		});

		// We need this because the OK button doesn't exist yet
		control.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				validate();
			}
		});
		
		return control;
	}
	
	// these two methods handle buttons getting pressed.

	public void cancelPressed() {
		_insertComponentSpecification = null;
		super.cancelPressed();
	}

	public void okPressed() {
		if (componentNameCombo != null) {
			_insertComponentSpecification.setComponentName(componentNameCombo.getText());
		}
		_insertComponentSpecification.setComponentInstanceName(_componentInstanceNameText.getText());
		_insertComponentSpecification.setInline(_inline.getSelection());
		super.okPressed();
	}

	public InsertComponentSpecification getInsertComponentSpecification() {
		return _insertComponentSpecification;
	}
	
	
	protected void validate() {
		boolean isValid = false;
		
		if (!"".equals(componentNameCombo.getText())) {
			if (_inline.getSelection()) {
				isValid = true;
			} else if (!"".equals(_componentInstanceNameText.getText())) {
				isValid = true;
			}
		}
		
		Button okButton = getButton(IDialogConstants.OK_ID);
		if (okButton == null) {
			return;
		}
		if (isValid) {
			if (!okButton.isEnabled()) {
				okButton.setEnabled(true);
			}
		} else if (okButton.isEnabled()){
			okButton.setEnabled(false);
		}
	}
}
