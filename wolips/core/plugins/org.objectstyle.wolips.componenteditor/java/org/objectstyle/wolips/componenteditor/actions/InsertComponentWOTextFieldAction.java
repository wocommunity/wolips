package org.objectstyle.wolips.componenteditor.actions;

import java.util.*;

public class InsertComponentWOTextFieldAction extends InsertComponentAction {

	public String getComponentInstanceNameSuffix() { return "textField"; }
	
	public String getComponentName() { return "WOTextField"; }
	
	public Collection getRequiredBindings() {
		List bindings = new ArrayList();
		bindings.add("value");
		return bindings;
	}
	
	public boolean canHaveComponentContent() { return false; }
}
