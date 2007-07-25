package org.objectstyle.wolips.eomodeler.utils;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Shell;
import org.objectstyle.wolips.eomodeler.Activator;
import org.objectstyle.wolips.eomodeler.Messages;
import org.objectstyle.wolips.eomodeler.core.utils.StringUtils;

public class ErrorUtils {
	public static void openErrorDialog(Shell _parent, String _title, String _message) {
		ErrorUtils.openErrorDialog(_parent, _title, _message, null);
	}

	public static void openErrorDialog(Shell _parent, String _message) {
		ErrorUtils.openErrorDialog(_parent, null, _message, null);
	}

	public static void openErrorDialog(Shell _parent, Throwable _throwable) {
		ErrorUtils.openErrorDialog(_parent, null, null, _throwable);
	}

	public static void openErrorDialog(Shell _parent, String _title, Throwable _throwable) {
		ErrorUtils.openErrorDialog(_parent, _title, null, _throwable);
	}

	public static void openErrorDialog(final Shell _parent, String _title, String _message, Throwable _throwable) {
		final Throwable throwable;
		if (_throwable == null) {
			throwable = null;
		} else {
			_throwable.printStackTrace();
			if (_throwable instanceof InvocationTargetException) {
				throwable = ((InvocationTargetException) _throwable).getCause();
			} else {
				throwable = _throwable;
			}
		}
		final String title;
		if (_title == null) {
			title = Messages.getString("Error");
		} else {
			title = _title;
		}
		final String message;
		if (_message == null) {
			message = StringUtils.getErrorMessage(_throwable);
		} else {
			message = _message;
		}
		_parent.getDisplay().syncExec(new Runnable() {
			public void run() {
				ErrorDialog.openError(_parent, title, message, new Status(IStatus.ERROR, Activator.PLUGIN_ID, -1, message, throwable));
			}
		});
	}
}
