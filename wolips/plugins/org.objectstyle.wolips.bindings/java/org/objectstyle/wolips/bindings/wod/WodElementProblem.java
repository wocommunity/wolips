package org.objectstyle.wolips.bindings.wod;

import org.eclipse.jface.text.Position;

public class WodElementProblem extends WodProblem {
	public WodElementProblem(String message, Position position, int lineNumber, boolean warning, String relatedToFileNames) {
		super(message, position, lineNumber, warning, relatedToFileNames);
	}

	public WodElementProblem(String message, Position position, int lineNumber, boolean warning, String[] relatedToFileNames) {
		super(message, position, lineNumber, warning, relatedToFileNames);
	}
}
