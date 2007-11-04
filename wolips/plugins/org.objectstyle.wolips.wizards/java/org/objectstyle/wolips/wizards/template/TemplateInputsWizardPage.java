package org.objectstyle.wolips.wizards.template;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
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

public class TemplateInputsWizardPage extends WizardPage {
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

		if (visible) {
			if (_projectTemplateChanged) {
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

						Label label = new Label(composite, SWT.NONE);
						label.setText(input.getQuestion());
						_questionLabels.put(input, label);

						Object value = input.getValue();

						Control control;
						ProjectInput.Type type = input.getType();
						if (input.hasOptions()) {
							Combo combo = new Combo(composite, SWT.READ_ONLY);
							for (ProjectInput.Option option : input.getOptions()) {
								combo.add(option.getName());
							}
							ProjectInput.Option selectedOption = input.getSelectedOption();
							if (selectedOption != null) {
								combo.select(input.getOptions().indexOf(selectedOption));
							}
							control = combo;
						} else {
							if (type == ProjectInput.Type.String) {
								control = new Text(composite, SWT.BORDER | SWT.SINGLE);
								((Text) control).setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
								((Text) control).setText((String) value);
							} else if (type == ProjectInput.Type.Package) {
								control = new Text(composite, SWT.BORDER | SWT.SINGLE);
								((Text) control).setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
								((Text) control).setText((String) value);
							} else if (type == ProjectInput.Type.Integer) {
								control = new Spinner(composite, SWT.NONE);
								if (value != null) {
									((Spinner) control).setSelection(((Integer) value).intValue());
									control.setLayoutData(new GridData());
								}
							} else if (type == ProjectInput.Type.Boolean) {
								control = new Button(composite, SWT.CHECK);
								if (value != null) {
									((Button) control).setSelection(((Boolean) value).booleanValue());
									control.setLayoutData(new GridData());
								}
							} else {
								throw new IllegalArgumentException("Unknown type " + type + ".");
							}
						}
						_questionControls.put(input, control);
					}
				}
			}
		} else {
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

		composite.layout(true, true);
	}
}