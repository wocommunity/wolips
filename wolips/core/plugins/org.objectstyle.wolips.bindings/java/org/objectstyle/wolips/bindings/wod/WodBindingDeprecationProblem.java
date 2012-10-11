package org.objectstyle.wolips.bindings.wod;

import org.eclipse.jface.text.Position;
import org.objectstyle.wolips.bindings.api.IApiBinding;

/**
 * Class representing a deprecated binding.
 * 
 * @author jw
 */
public class WodBindingDeprecationProblem extends WodBindingProblem {
	public WodBindingDeprecationProblem(IWodElement element, IApiBinding binding, String bindingName, String message, Position position, int lineNumber, boolean warning) {
		super(element, binding, bindingName, message, position, lineNumber, warning);
	}
}
