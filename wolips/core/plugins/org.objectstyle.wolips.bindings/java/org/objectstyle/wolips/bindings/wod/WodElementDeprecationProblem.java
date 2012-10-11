package org.objectstyle.wolips.bindings.wod;

import org.eclipse.jface.text.Position;

/**
 * Represents a Wod element that points to a deprecated element.
 * 
 * @author jw
 */
public class WodElementDeprecationProblem extends WodElementProblem {
	public WodElementDeprecationProblem(IWodElement element, String message, Position position, int lineNumber, boolean warning) {
		super(element, message, position, lineNumber, warning);
	}
}
