package org.objectstyle.wolips.wizards.template;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.wizard.WizardPage;
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
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.objectstyle.wolips.templateengine.ProjectInput;
import org.objectstyle.wolips.templateengine.ProjectTemplate;

public class TemplateInputsWizardPage extends WizardPage implements SelectionListener, ModifyListener {
	private List<ProjectInput> _inputs;

	private Map<ProjectInput, Label> _questionLabels;

	private Map<ProjectInput, Control> _questionControls;

	private ProjectTemplate _projectTemplate;

	private boolean _projectTemplateChanged;

	public TemplateInputsWizardPage() {
		super("Template Variables");
		setTitle("Template Variables");
		_questionLabels = new HashMap<ProjectInput, Label>();
		_questionControls = new HashMap<ProjectInput, Control>();
	}

	public void setProjectTemplate(ProjectTemplate projectTemplate) {
		if (_projectTemplate != projectTemplate) {
			_projectTemplateChanged = true;
			_projectTemplate = projectTemplate;
			if (_projectTemplate != null) {
				setTitle(_projectTemplate.getName());
				setMessage("This template has the following configuration options.");
			}
		}
	}

	public ProjectTemplate getProjectTemplate() {
		return _projectTemplate;
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setFont(parent.getFont());
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setLayout(new GridLayout(2, false));

		initializeDialogUnits(parent);

		setControl(composite);
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);

		Composite composite = (Composite) getControl();
		if (visible && _projectTemplateChanged) {
			createTemplateInputFields(composite);
		}
		composite.layout(true, true);
	}

	protected void createTemplateInputFields(Composite parent) {
		if (_inputs != null) {
			for (ProjectInput input : _inputs) {
				Label label = _questionLabels.get(input);
				Control control = _questionControls.get(input);
				if (label != null) {
					label.dispose();
				}
				if (control != null) {
					control.dispose();
				}
			}
			_questionLabels.clear();
			_questionControls.clear();
		}

		if (_projectTemplate != null) {
			_inputs = _projectTemplate.getInputs();
		} else {
			_inputs = null;
		}

		_projectTemplateChanged = false;

		if (_inputs != null) {
			for (ProjectInput input : _inputs) {
				input.setValue(null);

				Label label = new Label(parent, SWT.NONE);
				label.setText(input.getQuestion());
				_questionLabels.put(input, label);

				Control control = createControlForInput(input, parent);
				_questionControls.put(input, control);
			}
		}
	}

	protected Control createControlForInput(ProjectInput input, Composite parent) {
		Object value = input.getValue();

		Control control;
		ProjectInput.Type type = input.getType();
		if (input.hasOptions()) {
			Combo combo = new Combo(parent, SWT.READ_ONLY);
			for (ProjectInput.Option option : input.getOptions()) {
				combo.add(option.getName());
			}
			ProjectInput.Option selectedOption = input.getSelectedOption();
			if (selectedOption != null) {
				combo.select(input.getOptions().indexOf(selectedOption));
			}
			combo.addModifyListener(this);
			control = combo;
		} else {
			if (type == ProjectInput.Type.String) {
				control = new Text(parent, SWT.BORDER | SWT.SINGLE);
				((Text) control).setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
				((Text) control).setText((String) value);
				((Text) control).addModifyListener(this);
			} else if (type == ProjectInput.Type.Package) {
				control = new Text(parent, SWT.BORDER | SWT.SINGLE);
				((Text) control).setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
				((Text) control).setText((String) value);
				((Text) control).addModifyListener(this);
			} else if (type == ProjectInput.Type.Integer) {
				control = new Spinner(parent, SWT.NONE);
				if (value != null) {
					((Spinner) control).setSelection(((Integer) value).intValue());
					control.setLayoutData(new GridData());
				}
				((Spinner) control).addModifyListener(this);
			} else if (type == ProjectInput.Type.Boolean) {
				control = new Button(parent, SWT.CHECK);
				if (value != null) {
					((Button) control).setSelection(((Boolean) value).booleanValue());
					control.setLayoutData(new GridData());
				}
				((Button) control).addSelectionListener(this);
			} else {
				throw new IllegalArgumentException("Unknown type " + type + ".");
			}
		}

		return control;
	}

	public void updateModel() {
		if (_inputs != null) {
			for (ProjectInput input : _inputs) {
				Control control = _questionControls.get(input);
				if (input.hasOptions()) {
					int selectedOptionIndex = ((Combo) control).getSelectionIndex();
					ProjectInput.Option selectedOption = input.getOptions().get(selectedOptionIndex);
					input.setSelectedOption(selectedOption);
				} else {
					ProjectInput.Type type = input.getType();
					if (type == ProjectInput.Type.String) {
						input.setValue(((Text) control).getText());
					} else if (type == ProjectInput.Type.Package) {
						input.setValue(((Text) control).getText());
					} else if (type == ProjectInput.Type.Integer) {
						input.setValue(Integer.valueOf(((Spinner) control).getSelection()));
					} else if (type == ProjectInput.Type.Boolean) {
						input.setValue(Boolean.valueOf(((Button) control).getSelection()));
					} else {
						throw new IllegalArgumentException("Unknown type " + type + ".");
					}
				}
			}
		}
	}

	public void widgetDefaultSelected(SelectionEvent e) {
		widgetSelected(e);
	}

	public void widgetSelected(SelectionEvent e) {
		updateModel();
	}

	public void modifyText(ModifyEvent e) {
		updateModel();
	}
}