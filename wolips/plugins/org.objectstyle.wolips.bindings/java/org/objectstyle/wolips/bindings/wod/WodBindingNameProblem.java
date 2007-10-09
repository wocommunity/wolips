package org.objectstyle.wolips.bindings.wod;

import org.eclipse.jface.text.Position;

public class WodBindingNameProblem extends WodBindingProblem {
	public WodBindingNameProblem(String bindingName, String message, Position position, int lineNumber, boolean warning, String relatedToFileNames) {
		super(bindingName, message, position, lineNumber, warning, relatedToFileNames);
	}

	public WodBindingNameProblem(String bindingName, String message, Position position, int lineNumber, boolean warning, String[] relatedToFileNames) {
		super(bindingName, message, position, lineNumber, warning, relatedToFileNames);
	}
}
