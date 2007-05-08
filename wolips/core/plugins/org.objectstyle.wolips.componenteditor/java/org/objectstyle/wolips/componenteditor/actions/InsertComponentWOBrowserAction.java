package org.objectstyle.wolips.componenteditor.actions;

import java.util.*;

public class InsertComponentWOBrowserAction extends InsertComponentAction {

	public String getComponentInstanceNameSuffix() { return "Browser"; }
	
	public String getComponentName() { return "WOBrowser"; }
	
	public Collection getRequiredBindings() {
		List bindings = new ArrayList();
		bindings.add("list");
		return bindings;
	}
	
	public boolean canHaveComponentContent() { return false; }
}
