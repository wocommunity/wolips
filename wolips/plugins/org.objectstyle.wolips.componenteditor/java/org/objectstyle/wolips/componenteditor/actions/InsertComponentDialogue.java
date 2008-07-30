package org.objectstyle.wolips.componenteditor.actions;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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
import org.objectstyle.wolips.bindings.Activator;
import org.objectstyle.wolips.wodclipse.action.ComponentLiveSearch;

/**
 * <P>
 * This dialogue box is used to get the name of a component that will be
 * inserted both into the template as well as the wod file.
 * </P>
 */

public class InsertComponentDialogue extends Dialog {
	protected Label _componentNameLabel;

	protected Combo _componentNameCombo;

	protected Label _componentInstanceLabel;

	protected Text _componentInstanceNameText;

	protected Button _componentInstanceCreateButton;

	protected Button _componentInstanceCancelButton;

	protected Button _inline;

	protected IJavaProject _project;

	protected InsertComponentSpecification _insertComponentSpecification;

	protected IProgressMonitor _progressMonitor;

	public InsertComponentDialogue(Shell parentShell, IJavaProject project, InsertComponentSpecification insertComponentSpecification) {
		super(parentShell);
		_project = project;
		_insertComponentSpecification = insertComponentSpecification;
		_progressMonitor = new NullProgressMonitor();
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		String componentName = _insertComponentSpecification.getComponentName();
		if (componentName == null) {
			newShell.setText("Insert Component");
		} else {
			newShell.setText("Insert " + componentName);
		}
	}

	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		((GridLayout) composite.getLayout()).numColumns = 2;

		_inline = new Button(composite, SWT.CHECK);
		_inline.setText("Use inline bindings");
		GridData inlineData = new GridData(GridData.FILL_HORIZONTAL);
		inlineData.horizontalSpan = 2;
		_inline.setLayoutData(inlineData);
		_inline.setSelection(Activator.getDefault().useInlineBindings(_project.getProject()));

		_componentInstanceLabel = new Label(composite, SWT.NONE);
		_componentInstanceLabel.setText("WebObject tag name:");
		GridData componentInstanceLabelLayout = new GridData(GridData.FILL_HORIZONTAL);
		_componentInstanceLabel.setLayoutData(componentInstanceLabelLayout);

		_componentInstanceNameText = new Text(composite, SWT.BORDER);
		GridData componentInstanceNameLayout = new GridData(GridData.FILL_HORIZONTAL);
		componentInstanceNameLayout.widthHint = 200;
		_componentInstanceNameText.setLayoutData(componentInstanceNameLayout);
		_componentInstanceNameText.addVerifyListener(new ComponentInstanceNameVerifyListener());
		_componentInstanceNameText.setFocus();

		if (_insertComponentSpecification.getComponentInstanceName() != null) {
			_componentInstanceNameText.setText(_insertComponentSpecification.getComponentInstanceName());
		}
//		else {
//			if (_insertComponentSpecification.getComponentInstanceNameSuffix() != null) {
//				_componentInstanceNameText.setText(_insertComponentSpecification.getComponentInstanceNameSuffix());
//			}
//		}

		if (_insertComponentSpecification.getComponentName() == null) {
			_componentNameLabel = new Label(composite, 0);
			_componentNameLabel.setText("Component type:");
			GridData componentNameLabelLayout = new GridData(GridData.FILL_HORIZONTAL);
			_componentNameLabel.setLayoutData(componentNameLabelLayout);

			_componentNameCombo = new Combo(composite, SWT.BORDER);
			GridData componentNameTextLayout = new GridData(GridData.FILL_HORIZONTAL);
			_componentNameCombo.setLayoutData(componentNameTextLayout);
			new ComponentLiveSearch(_project, _progressMonitor).attachTo(_componentNameCombo);
		}

		_inline.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

			public void widgetSelected(SelectionEvent e) {
				inlineSelectionChanged();
				validate();
			}
		});

		_componentInstanceNameText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				validate();
			}
		});

		if (_componentNameCombo != null) {
			_componentNameCombo.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					validate();
				}
			});
		}

		inlineSelectionChanged();

		return dialogArea;
	}

	protected void inlineSelectionChanged() {
		_componentInstanceNameText.setEnabled(!_inline.getSelection());
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		validate();
	}

	// these two methods handle buttons getting pressed.

	public void cancelPressed() {
		_insertComponentSpecification = null;
		super.cancelPressed();
	}

	public void okPressed() {
		if (_componentNameCombo != null) {
			_insertComponentSpecification.setComponentName(_componentNameCombo.getText());
		}
		_insertComponentSpecification.setComponentInstanceName(_componentInstanceNameText.getText());
		_insertComponentSpecification.setInline(_inline.getSelection());
		Activator.getDefault().setUseInlineBindings(_project.getProject(), _inline.getSelection());
		super.okPressed();
	}

	public InsertComponentSpecification getInsertComponentSpecification() {
		return _insertComponentSpecification;
	}

	protected void validate() {
		boolean isValid = false;

		if (_componentNameCombo == null || !"".equals(_componentNameCombo.getText())) {
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
		} else if (okButton.isEnabled()) {
			okButton.setEnabled(false);
		}
	}
}
