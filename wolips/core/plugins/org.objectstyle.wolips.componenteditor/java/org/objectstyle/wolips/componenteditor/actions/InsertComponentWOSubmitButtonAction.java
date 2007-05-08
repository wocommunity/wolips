package org.objectstyle.wolips.componenteditor.actions;

import java.util.*;

public class InsertComponentWOSubmitButtonAction extends InsertComponentAction
{
	public InsertComponentWOSubmitButtonAction() { super(); }
	
	public String getComponentInstanceNameSuffix() { return "SubmitButton"; }
	
	public String getComponentName() { return "WOSubmitButton"; }
	
	public Collection getRequiredBindings() { return null; }
	
	public boolean canHaveComponentContent() { return true; }

}