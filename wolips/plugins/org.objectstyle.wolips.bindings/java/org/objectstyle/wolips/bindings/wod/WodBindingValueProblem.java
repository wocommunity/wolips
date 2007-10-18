package org.objectstyle.wolips.bindings.wod;

import org.eclipse.jface.text.Position;

public class WodBindingValueProblem extends WodBindingProblem {
	public WodBindingValueProblem(String bindingName, String message, Position position, int lineNumber, boolean warning) {
		super(bindingName, message, position, lineNumber, warning);
	}
}
