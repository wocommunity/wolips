package org.objectstyle.wolips.componenteditor.actions;

import java.util.*;

public class InsertComponentWOImageAction extends InsertComponentAction {

	public String getComponentInstanceNameSuffix() { return "Image"; }
	
	public String getComponentName() { return "WOImage"; }
	
	public Collection getRequiredBindings() {
		List bindings = new ArrayList();
		bindings.add("filename");
		bindings.add("framework");
		return bindings;
	}
	
	public boolean canHaveComponentContent() { return false; }

}
