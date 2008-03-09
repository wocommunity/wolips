package org.objectstyle.wolips.bindings.wod;

import org.eclipse.jface.text.Position;
import org.objectstyle.wolips.bindings.api.IApiBinding;

public class WodBindingNameProblem extends WodBindingProblem {
	public WodBindingNameProblem(IWodElement element, IApiBinding binding, String bindingName, String message, Position position, int lineNumber, boolean warning) {
		super(element, binding, bindingName, message, position, lineNumber, warning);
	}
}
