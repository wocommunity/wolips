package org.objectstyle.wolips.componenteditor.actions;

import java.util.*;

/**
 * <P>This class contains the basic specifications for a new
 * instance of a component which is being inserted into
 * another component's template.  This object is used to
 * pass between say the action and the dialog box to collect
 * extra information about the insertion.  An example might
 * be that the user would like to edit the name of the
 * component instance.</P> 
 * @author apl
 *
 */

public class InsertComponentSpecification {

	protected String componentName = null;
	protected String componentInstanceName = null;
	protected String componentInstanceNameSuffix = null;
	protected Collection requiredBindings = null;
	
	public InsertComponentSpecification(String componentName)
	{
		super();
		this.componentName = componentName;
	}
	
	public String getComponentName() {
		return componentName;
	}
	
	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}
	
	public String getComponentInstanceName() {
		return componentInstanceName;
	}
	
	public void setComponentInstanceName(String componentInstanceName) {
		this.componentInstanceName = componentInstanceName;
	}

	public Collection getRequiredBindings() {
		return requiredBindings;
	}

	public void setRequiredBindings(Collection value) {
		this.requiredBindings = value;
	}

	public String getComponentInstanceNameSuffix() {
		return componentInstanceNameSuffix;
	}

	public void setComponentInstanceNameSuffix(String componentInstanceNameSuffix) {
		this.componentInstanceNameSuffix = componentInstanceNameSuffix;
	}
	
}
