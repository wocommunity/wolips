package org.objectstyle.wolips.componenteditor.actions;

import java.util.*;

public class InsertComponentWOConditionalAction extends InsertComponentAction {

	public String getComponentInstanceNameSuffix() { return "Conditional"; }
	
	public String getComponentName() { return "WOConditional"; }
	
	public Collection getRequiredBindings() {
		List bindings = new ArrayList();
		bindings.add("condition");
		return bindings;
	}
	
	public boolean canHaveComponentContent() { return true; }

}
