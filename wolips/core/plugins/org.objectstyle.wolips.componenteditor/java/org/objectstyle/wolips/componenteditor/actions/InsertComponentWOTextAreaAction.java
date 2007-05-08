package org.objectstyle.wolips.componenteditor.actions;

import java.util.*;

public class InsertComponentWOTextAreaAction extends InsertComponentAction {

	public String getComponentInstanceNameSuffix() { return "TextArea"; }
	
	public String getComponentName() { return "WOTextArea"; }
	
	public Collection getRequiredBindings() {
		List bindings = new ArrayList();
		bindings.add("value");
		return bindings;
	}
	
	public boolean canHaveComponentContent() { return false; }
}
