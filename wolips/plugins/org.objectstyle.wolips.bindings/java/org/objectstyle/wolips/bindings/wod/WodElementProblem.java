package org.objectstyle.wolips.bindings.wod;

import org.eclipse.jface.text.Position;

public class WodElementProblem extends WodProblem {
	public WodElementProblem(String message, Position position, int lineNumber, boolean warning) {
		super(message, position, lineNumber, warning);
	}
}
