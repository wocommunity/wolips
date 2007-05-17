package org.objectstyle.wolips.eomodeler.eclipse;

import org.eclipse.ui.IStartup;
import org.eclipse.ui.PlatformUI;

public class EntityModelerStartup implements IStartup {

	public void earlyStartup() {
		System.out.println("EntityModelerStartup.earlyStartup: STARTUP");
		PackageExplorerDoubleClickHandler doubleClickHandler = new PackageExplorerDoubleClickHandler();
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().addPageListener(doubleClickHandler);
	}

}
