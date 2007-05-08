package org.objectstyle.wolips.componenteditor.actions;

import java.util.*;

public class InsertComponentWOHyperlinkAction extends InsertComponentAction {

	public String getComponentInstanceNameSuffix() { return "Hyperlink"; }
	
	public String getComponentName() { return "WOHyperlink"; }
	
	public Collection getRequiredBindings() {
		List bindings = new ArrayList();
		bindings.add("action");
		return bindings;
	}
	
	public boolean canHaveComponentContent() { return true; }
}
