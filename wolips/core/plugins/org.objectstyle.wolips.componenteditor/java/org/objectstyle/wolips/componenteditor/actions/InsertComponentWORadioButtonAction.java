package org.objectstyle.wolips.componenteditor.actions;

import java.util.*;

public class InsertComponentWORadioButtonAction extends InsertComponentAction {

	public String getComponentInstanceNameSuffix() { return "RadioButton"; }
	
	public String getComponentName() { return "WORadioButton"; }
	
	public Collection getRequiredBindings() {
		List bindings = new ArrayList();
		bindings.add("checked");
		return bindings;
	}
	
	public boolean canHaveComponentContent() { return false; }

}
