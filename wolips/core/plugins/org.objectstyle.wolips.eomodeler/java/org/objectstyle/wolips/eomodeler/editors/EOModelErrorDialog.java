package org.objectstyle.wolips.eomodeler.editors;

import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.objectstyle.wolips.eomodeler.core.model.EOModelVerificationFailure;

public class EOModelErrorDialog extends Dialog {
	public static final int DELETE_ANYWAY_ID = 100;

	private EOModelEditor _editor;

	private boolean _showDeleteAnywayButton;

	private Set<? extends EOModelVerificationFailure> _failures;

	public EOModelErrorDialog(Shell parentShell, Set<? extends EOModelVerificationFailure> failures) {
		this(parentShell, failures, false, null);
	}

	public EOModelErrorDialog(Shell parentShell, Set<? extends EOModelVerificationFailure> failures, EOModelEditor editor) {
		this(parentShell, failures, false, editor);
	}

	public EOModelErrorDialog(Shell parentShell, Set<? extends EOModelVerificationFailure> failures, boolean showDeleteAnywayButton) {
		this(parentShell, failures, showDeleteAnywayButton, null);
	}

	public EOModelErrorDialog(Shell parentShell, Set<? extends EOModelVerificationFailure> failures, boolean showDeleteAnywayButton, EOModelEditor editor) {
		super(parentShell);
		_failures = failures;
		_showDeleteAnywayButton = showDeleteAnywayButton;
		_editor = editor;
	}

	protected void configureShell(Shell _newShell) {
		super.configureShell(_newShell);
		_newShell.setText("EOModel Verification Failures");
	}

	@Override
	protected int getShellStyle() {
		return super.getShellStyle() | SWT.RESIZE;
	}

	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);

		if (_editor == null) {
			IEditorPart editorPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
			if (editorPart instanceof EOModelEditor) {
				_editor = (EOModelEditor) editorPart;
			}
		}

		final ScrolledComposite scrollComposite = new ScrolledComposite(composite, SWT.V_SCROLL | SWT.BORDER);
		scrollComposite.setBackground(composite.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		GridData failuresGridData = new GridData(GridData.FILL_BOTH);
		failuresGridData.widthHint = 500;
		failuresGridData.heightHint = 400;
		scrollComposite.setLayoutData(failuresGridData);

		final Composite failuresComposite = new Composite(scrollComposite, SWT.NONE);
		failuresComposite.setBackground(scrollComposite.getBackground());

		int columns = (_editor == null) ? 2 : 3;
		GridLayout layout = new GridLayout(columns, false);
		failuresComposite.setLayout(layout);

		Iterator<? extends EOModelVerificationFailure> failuresIter = _failures.iterator();
		while (failuresIter.hasNext()) {
			EOModelVerificationFailure failure = failuresIter.next();
			Label iconLabel = new Label(failuresComposite, SWT.NONE);
			iconLabel.setBackground(failuresComposite.getBackground());
			if (failure.isWarning()) {
				iconLabel.setImage(composite.getDisplay().getSystemImage(SWT.ICON_WARNING));
			} else {
				iconLabel.setImage(composite.getDisplay().getSystemImage(SWT.ICON_ERROR));
			}
			GridData iconLabelData = new GridData();
			iconLabelData.verticalIndent = 3;
			iconLabelData.verticalAlignment = SWT.BEGINNING;
			iconLabelData.horizontalIndent = 3;
			iconLabel.setLayoutData(iconLabelData);

			Composite groupFailureComposite = new Composite(failuresComposite, SWT.NONE);
			groupFailureComposite.setBackground(failuresComposite.getBackground());
			groupFailureComposite.setLayout(new GridLayout(1, true));
			GridData groupFailureLabelData = new GridData(GridData.FILL_HORIZONTAL);
			//groupFailureLabelData.verticalIndent = 3;
			// failureLabelData.verticalAlignment = SWT.BEGINNING;
			groupFailureLabelData.horizontalIndent = 3;
			groupFailureComposite.setLayoutData(groupFailureLabelData);
			
			Label failedObjectLabel = new Label(groupFailureComposite, SWT.NONE);
			failedObjectLabel.setBackground(failuresComposite.getBackground());
			failedObjectLabel.setText(failure.getFailedObject().getFullyQualifiedName());
			failedObjectLabel.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DIALOG_FONT));
			failedObjectLabel.setForeground(failuresComposite.getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
			failedObjectLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			
			StyledText failureLabel = new StyledText(groupFailureComposite, SWT.WRAP);
			failureLabel.setEditable(false);
			failureLabel.setWordWrap(true);
			failureLabel.setEnabled(false);
			String failureMessage = failure.getMessage();
			failureLabel.setText(failureMessage);
			GridData failureLabelData = new GridData(GridData.FILL_HORIZONTAL);
			//failureLabelData.verticalIndent = 3;
			// failureLabelData.verticalAlignment = SWT.BEGINNING;
			//failureLabelData.horizontalIndent = 3;
			failureLabel.setLayoutData(failureLabelData);

			Button showButton = new Button(failuresComposite, SWT.PUSH);
			showButton.setText("Show");
			GridData showButtonData = new GridData();
			showButtonData.verticalAlignment = SWT.CENTER;
			showButtonData.horizontalIndent = 3;
			showButton.setLayoutData(showButtonData);
			showButton.setData(failure);
			showButton.addSelectionListener(new SelectionListener() {
				public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}

				public void widgetSelected(SelectionEvent e) {
					EOModelVerificationFailure selectedFailure = (EOModelVerificationFailure) ((Widget) e.getSource()).getData();
					if (selectedFailure != null && _editor != null) {
						_editor.setSelection(new StructuredSelection(selectedFailure.getFailedObject()));
						EOModelErrorDialog.this.close();
					}
				}
			});

			Matcher matcher = Pattern.compile("(\\S+: \\S+)").matcher(failureMessage);
			while (matcher.find()) {
				int start = matcher.start(1);
				int end = matcher.end(1);
				StyleRange styleRange = new StyleRange();
				styleRange.start = start;
				styleRange.length = (end - start);
				styleRange.fontStyle = SWT.BOLD;
				// styleRange.foreground = orange;
				failureLabel.setStyleRange(styleRange);
			}

			if (failuresIter.hasNext()) {
				Composite separator = new Composite(failuresComposite, SWT.NONE);
				separator.setBackground(failuresComposite.getDisplay().getSystemColor(SWT.COLOR_GRAY));
				GridData separatorData = new GridData(GridData.FILL_HORIZONTAL);
				separatorData.heightHint = 1;
				separatorData.verticalIndent = 3;
				separatorData.horizontalSpan = columns;
				separator.setLayoutData(separatorData);
			}
		}

		scrollComposite.setContent(failuresComposite);
		scrollComposite.setExpandVertical(true);
		scrollComposite.setExpandHorizontal(true);
		scrollComposite.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				Rectangle r = scrollComposite.getClientArea();
				scrollComposite.setMinSize(failuresComposite.computeSize(r.width, SWT.DEFAULT));
			}
		});

		return composite;
	}

	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		if (_showDeleteAnywayButton) {
			createButton(parent, EOModelErrorDialog.DELETE_ANYWAY_ID, "Delete Anyway", false);
		}
	}

	protected void buttonPressed(int buttonId) {
		if (buttonId == EOModelErrorDialog.DELETE_ANYWAY_ID) {
			setReturnCode(EOModelErrorDialog.DELETE_ANYWAY_ID);
			close();
		} else {
			super.buttonPressed(buttonId);
		}
	}

	protected static class FailureLabelProvider implements ILabelProvider {
		public void addListener(ILabelProviderListener listener) {
			// DO NOTHING
		}

		public void dispose() {
			// DO NOTHING
		}

		public Image getImage(Object element) {
			return null;
		}

		public String getText(Object element) {
			EOModelVerificationFailure failure = (EOModelVerificationFailure) element;
			return failure.getMessage();
		}

		public boolean isLabelProperty(Object element, String property) {
			return true;
		}

		public void removeListener(ILabelProviderListener listener) {
			// DO NOTHING
		}
	}

	protected static class FailureContentProvider implements IStructuredContentProvider {
		public Object[] getElements(Object inputElement) {
			Set failures = (Set) inputElement;
			return failures.toArray();
		}

		public void dispose() {
			// DO NOTHING
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// DO NOTHING
		}
	}
}
