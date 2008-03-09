package org.objectstyle.wolips.bindings.wod;

import org.eclipse.jface.text.Position;
import org.objectstyle.wolips.bindings.api.IApiBinding;

public class WodBindingValueProblem extends WodBindingProblem {
	public WodBindingValueProblem(IWodElement element, IApiBinding binding, String bindingName, String message, Position position, int lineNumber, boolean warning) {
		super(element, binding, bindingName, message, position, lineNumber, warning);
	}
}
