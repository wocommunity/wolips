package org.objectstyle.wolips.componenteditor.actions;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.objectstyle.wolips.core.resources.types.api.Binding;

public class InsertHtmlTagAction extends InsertHtmlAndWodAction {
	@Override
	public boolean canHaveComponentContent(String componentName) {
		return true;
	}

	@Override
	public Binding[] getRequiredBindings(String componentName) {
		return new Binding[0];
	}

	@Override
	protected InsertComponentSpecification getComponentSpecification() {
		InsertComponentSpecification ics = null;
		IWorkbenchWindow ww = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		InputDialog dialog = new InputDialog(ww.getShell(), "New HTML Tag", "Enter the HTML tag:", "", null);
		if (dialog.open() == Window.OK) {
			ics = new InsertComponentSpecification("");
			ics.setRequiredBindings(getRequiredBindings(""));
			ics.setComponentName("");
			ics.setComponentInstanceNameSuffix("");
			ics.setInline(true);
			ics.setTagName(dialog.getValue());
		}
		return ics;
	}
}
