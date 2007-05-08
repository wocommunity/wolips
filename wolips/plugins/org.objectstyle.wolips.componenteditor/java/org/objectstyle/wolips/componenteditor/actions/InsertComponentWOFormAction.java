package org.objectstyle.wolips.componenteditor.actions;

import java.util.*;

public class InsertComponentWOFormAction extends InsertComponentAction
{
	public String getComponentInstanceNameSuffix() { return "Form"; }
	
	public String getComponentName() { return "WOForm"; }
	
	public Collection getRequiredBindings() { return null; }

}
