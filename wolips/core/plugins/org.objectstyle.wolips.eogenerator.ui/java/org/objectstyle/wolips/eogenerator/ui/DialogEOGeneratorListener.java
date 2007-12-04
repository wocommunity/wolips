package org.objectstyle.wolips.eogenerator.ui;

import org.eclipse.core.resources.IFile;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.objectstyle.wolips.eogenerator.core.model.IEOGeneratorListener;
import org.objectstyle.wolips.eogenerator.ui.dialogs.EOGeneratorResultsDialog;

public class DialogEOGeneratorListener implements IEOGeneratorListener {
	private StringBuffer _output;
	private Shell _shell;
	private boolean _succeeded;

	public DialogEOGeneratorListener(Shell shell) {
		_shell = shell;
	}
	
	public Shell getShell() {
		return _shell;
	}
	
	public void eogeneratorStarted() {
		_output = new StringBuffer();
		_succeeded = true;
	}

	public void eogeneratorFailed(IFile eogenFile, String results) {
		appendLines(eogenFile, results);
		_succeeded = false;
	}

	public void eogeneratorSucceeded(IFile eogenFile, String results) {
		appendLines(eogenFile, results);
	}

	public void eogeneratorFinished() {
		if (_output.length() > 0 && !_succeeded) {
			final String output = _output.toString();
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					EOGeneratorResultsDialog resultsDialog = new EOGeneratorResultsDialog(DialogEOGeneratorListener.this.getShell(), output.toString());
					resultsDialog.open();
				}
			});
		}
	}

	protected void appendLines(IFile eogenFile, String results) {
		_output.append(eogenFile.getLocation().toOSString());
		_output.append(":\n");
		if (results.length() == 0) {
			_output.append("\tFinished.\n");
		}
		else {
			for (String line : results.split("\n")) {
				_output.append("\t");
				_output.append(line);
				_output.append("\n");
			}
		}
		_output.append("\n\n");
	}
}
