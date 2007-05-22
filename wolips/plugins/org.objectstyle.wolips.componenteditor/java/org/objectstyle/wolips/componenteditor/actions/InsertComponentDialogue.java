package org.objectstyle.wolips.componenteditor.actions;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.objectstyle.wolips.wodclipse.core.completion.WodCompletionProposal;
import org.objectstyle.wolips.wodclipse.core.completion.WodCompletionUtils;

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
	public final static String LABEL_COMPONENT_INSTANCE_NAME = "WebObject Tag Name:";

	// no time to localise :-(
	public final static String LABEL_COMPONENT_NAME = "Component name:";

	// user interface elements which the user interacts with.

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

	protected Object _completionLock = new Object();

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

		if (_insertComponentSpecification.getComponentName() == null) {
			_componentNameLabel = new Label(control, 0);
			_componentNameLabel.setText(LABEL_COMPONENT_NAME);
			GridData componentNameLabelLayout = new GridData(GridData.FILL_HORIZONTAL);
			_componentNameLabel.setLayoutData(componentNameLabelLayout);

			_componentNameCombo = new Combo(control, SWT.BORDER);
			GridData componentNameTextLayout = new GridData(GridData.FILL_HORIZONTAL);
			_componentNameCombo.setLayoutData(componentNameTextLayout);
			_componentNameCombo.addModifyListener(new ModifyListener() {
				private String _lastSearch;

				public void modifyText(ModifyEvent e) {
					Point selection = _componentNameCombo.getSelection();
					final String partialName;
					if (selection != null && selection.x != selection.y) {
						partialName = _componentNameCombo.getText().substring(0, selection.x);
					} else {
						partialName = _componentNameCombo.getText();
					}
					if (_lastSearch == null || !_lastSearch.equals(partialName)) {
						_lastSearch = partialName;
						_progressMonitor.setCanceled(true);

						WorkspaceJob job = new WorkspaceJob("Searching for components named '" + partialName + "*' ...") {
							public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
								synchronized (_completionLock) {
									_progressMonitor.setCanceled(false);
									try {
										final Set<WodCompletionProposal> proposals = new HashSet<WodCompletionProposal>();
										WodCompletionUtils.fillInElementTypeCompletionProposals(_project, partialName, 0, partialName.length(), proposals, false, _progressMonitor);
										if (!_progressMonitor.isCanceled()) {
											Display.getDefault().asyncExec(new Runnable() {
												public void run() {
													_componentNameCombo.removeAll();
													for (WodCompletionProposal elementName : proposals) {
														_componentNameCombo.add(elementName.getDisplayString());
													}
												}
											});
										}
									} catch (OperationCanceledException t) {
										// ignore
									} catch (Throwable t) {
										t.printStackTrace();
									}
									return Status.OK_STATUS;
								}
							}
						};
						job.schedule();
					}
				}
			});
		}

		_inline = new Button(control, SWT.CHECK);
		_inline.setText("Use inline bindings, or enter webobject tag name:");
		_inline.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		_componentInstanceNameText = new Text(control, SWT.BORDER);
		GridData componentInstanceNameLayout = new GridData(GridData.FILL_HORIZONTAL);
		_componentInstanceNameText.setLayoutData(componentInstanceNameLayout);
		_componentInstanceNameText.addVerifyListener(new ComponentInstanceNameVerifyListener());

		if (null != _insertComponentSpecification.getComponentInstanceName()) {
			_componentInstanceNameText.setText(_insertComponentSpecification.getComponentInstanceName());
		} else {
			if (null != _insertComponentSpecification.getComponentInstanceNameSuffix()) {
				_componentInstanceNameText.setText(_insertComponentSpecification.getComponentInstanceNameSuffix());
			}
		}

		_inline.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

			public void widgetSelected(SelectionEvent e) {
				// _componentInstanceLabel.setEnabled(!_inline.getSelection());
				_componentInstanceNameText.setEnabled(!_inline.getSelection());
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
		if (_componentNameCombo != null) {
			_insertComponentSpecification.setComponentName(_componentNameCombo.getText());
		}
		_insertComponentSpecification.setComponentInstanceName(_componentInstanceNameText.getText());
		_insertComponentSpecification.setInline(_inline.getSelection());
		super.okPressed();
	}

	public InsertComponentSpecification getInsertComponentSpecification() {
		return _insertComponentSpecification;
	}
}
