package org.objectstyle.wolips.componenteditor.actions;

import java.util.*;

/**
 * <P>This will insert a WOString 
 */

public class InsertComponentWOStringAction extends InsertComponentAction
{
	public InsertComponentWOStringAction() { super(); }
	
	public String getComponentInstanceNameSuffix() { return "String"; }
	
	public String getComponentName() { return "WOString"; }
	
	public Collection getRequiredBindings() {
		List bindings = new ArrayList();
		bindings.add("value");
		return bindings;
	}
}
