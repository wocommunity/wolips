package org.objectstyle.wolips.eomodeler.eclipse;

import org.eclipse.ui.IStartup;

public class EntityModelerStartup implements IStartup {

	public void earlyStartup() {
		PackageExplorerDoubleClickHandler doubleClickHandler = new PackageExplorerDoubleClickHandler();
		doubleClickHandler.registerListeners();
	}

}
