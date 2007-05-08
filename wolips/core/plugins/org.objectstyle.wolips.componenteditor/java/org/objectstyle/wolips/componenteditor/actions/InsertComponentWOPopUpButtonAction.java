package org.objectstyle.wolips.componenteditor.actions;

import java.util.*;

public class InsertComponentWOPopUpButtonAction extends InsertComponentAction {

	public String getComponentInstanceNameSuffix() { return "PopUpButton"; }
	
	public String getComponentName() { return "WOPopUpButton"; }
	
	public Collection getRequiredBindings() {
		List bindings = new ArrayList();
		bindings.add("list");
		bindings.add("item");
		bindings.add("selection");
		return bindings;
	}
	
	public boolean canHaveComponentContent() { return false; }

}
