package org.objectstyle.wolips.actions;

import java.io.File;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.objectstyle.wolips.ui.WOFrameworkDialogWrapper;
import org.objectstyle.wolips.wizards.Messages;
import org.objectstyle.wolips.wo.WOVariables;

/**
 * Adding WOFrameworks
 * 
 * @author mnolte
 *
 */
public class WOFrameworkAction extends ActionOnIProject {

	private static String WOSystemFrameworkAddID = "WOSystemFramework.Add.ID";
	private static String WOLocalFrameworkAddID = "WOLocalFramework.Add.ID";

	/**
	 * Constructor for WOFrameworkAction.
	 */
	public WOFrameworkAction() {
		super();
	}

	/**
	 * Runs the action.
	 */
	public void run(IAction action) {
		if (project() != null) {
			WOFrameworkDialogWrapper frameworkDialog = null;
			IJavaProject javaProject = JavaCore.create(project());
			if (action.getId().equals(WOSystemFrameworkAddID)) {
				frameworkDialog =
					new WOFrameworkDialogWrapper(
						this.part,
						javaProject,
						new File(WOVariables.libraryDir(), "Frameworks"));
			} else if (action.getId().equals(WOLocalFrameworkAddID)) {
				frameworkDialog =
					new WOFrameworkDialogWrapper(
						this.part,
						javaProject,
						new File(WOVariables.localLibraryDir(), "Frameworks"));
			}
			if (frameworkDialog != null) {
				frameworkDialog.executeDialog();
				return;
			}

			MessageDialog.openInformation(
				this.part.getSite().getShell(),
				Messages.getString("ErrorDialog.title"),
				Messages.getString("ErrorDialog.invalid.selection"));

		}
	}
}
