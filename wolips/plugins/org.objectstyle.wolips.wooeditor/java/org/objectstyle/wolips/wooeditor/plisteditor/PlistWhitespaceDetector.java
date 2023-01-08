package org.objectstyle.wolips.wooeditor.plisteditor;

import org.eclipse.jface.text.rules.IWhitespaceDetector;

public class PlistWhitespaceDetector implements IWhitespaceDetector {

	@Override
	public boolean isWhitespace(char c) {
		return (c == ' ' || c == '\t' || c == '\n' || c == '\r');
	}
}
