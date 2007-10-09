package org.objectstyle.wolips.bindings.api;

import java.util.List;
import java.util.Map;

public interface IValidation {
	public List<IValidation> getValidationChildren();
	
	public boolean isAffectedByBindingNamed(String bindingName);

	public boolean evaluate(Map<String, String> _bindings);
}
