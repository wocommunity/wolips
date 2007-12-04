package org.objectstyle.wolips.eogenerator.ui.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class EOGeneratorResultsDialog extends MessageDialog {
	private String myResults;

	public EOGeneratorResultsDialog(Shell _parentShell, String _results) {
		super(_parentShell, "EOGenerator Finished", null, "EOGenerator finished with the following results:", INFORMATION, new String[] { IDialogConstants.OK_LABEL }, 0);
		myResults = _results;
	}
	
	@Override
	protected int getShellStyle() {
		return SWT.CLOSE|SWT.RESIZE;
	}

	protected Control createCustomArea(Composite _parent) {
		StyledText resultsText = new StyledText(_parent, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.WRAP);
		resultsText.setText(myResults);
		resultsText.setEditable(false);
		resultsText.setWordWrap(true);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 500;
		gd.widthHint = 700;
		resultsText.setLayoutData(gd);
		return resultsText;
	}
}
