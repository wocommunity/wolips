package org.objectstyle.wolips.componenteditor.actions;

import java.util.*;

public class InsertComponentWOCheckBoxAction extends InsertComponentAction {

	public String getComponentInstanceNameSuffix() { return "CheckBox"; }
	
	public String getComponentName() { return "WOHCheckBox"; }
	
	public Collection getRequiredBindings() {
		List bindings = new ArrayList();
		bindings.add("checked");
		return bindings;
	}
	
	public boolean canHaveComponentContent() { return false; }

}
