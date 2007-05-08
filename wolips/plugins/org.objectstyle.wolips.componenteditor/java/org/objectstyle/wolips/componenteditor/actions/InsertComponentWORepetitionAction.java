package org.objectstyle.wolips.componenteditor.actions;

import java.util.*;

public class InsertComponentWORepetitionAction extends InsertComponentAction {

	public String getComponentInstanceNameSuffix() { return "Repetition"; }
	
	public String getComponentName() { return "WORepetition"; }
	
	public Collection getRequiredBindings() {
		List bindings = new ArrayList();
		bindings.add("list");
		bindings.add("item");
		return bindings;
	}
	
	public boolean canHaveComponentContent() { return true; }
}
