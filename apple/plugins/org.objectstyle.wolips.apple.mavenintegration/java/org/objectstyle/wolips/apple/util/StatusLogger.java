package org.objectstyle.wolips.apple.util;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.objectstyle.wolips.apple.mavenintegration.AppleMavenPlugin;
/**
*
*/
public class StatusLogger {
	private static StatusLogger _logger;
	/**
	 * @return status logger for error view
	 */
	public static StatusLogger getLogger() {
		if (_logger == null) {
			_logger = new StatusLogger();
		}
		return _logger;
	}
	/**
	 * @param severity
	 * @param code
	 * @param message
	 * @param exception
	 * @return IStatus
	 */
	public IStatus createStatus(int severity, int code, String message, Throwable exception) {
		return new Status(severity, AppleMavenPlugin.getDefault().getClass().getName(), code, message, exception);
	}

	/**
	 * @param severity
	 * @param code
	 * @param message
	 * @param exception
	 */
	public void log (int severity, int code, String message, Throwable exception) {
		log(createStatus(severity, code, message, exception));
	}

	/**
	 * @param status
	 */
	public void log (IStatus status) {
		AppleMavenPlugin.getDefault().getLog().log(status);
	}

	/**
	 * @param message
	 */
	public void logInfo(String message) {
		log(IStatus.INFO, IStatus.INFO, message, null);
	}

	/**
	 * @param message
	 */
	public void logWarning(String message) {
		log(IStatus.WARNING, IStatus.WARNING, message, null);
	}

	/**
	 * @param e
	 */
	public void log(Throwable e) {
		log(createStatus(IStatus.ERROR, IStatus.WARNING, "Plugin exception", e));
	}
}
