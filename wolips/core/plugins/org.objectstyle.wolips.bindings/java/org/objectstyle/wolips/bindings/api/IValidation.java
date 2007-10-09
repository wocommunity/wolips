package org.objectstyle.wolips.bindings.api;

import java.util.Map;

public interface IValidation {
	public IValidation[] getValidationChildren();
	
	public boolean isAffectedByBindingNamed(String bindingName);

	public boolean evaluate(Map _bindings);
}
