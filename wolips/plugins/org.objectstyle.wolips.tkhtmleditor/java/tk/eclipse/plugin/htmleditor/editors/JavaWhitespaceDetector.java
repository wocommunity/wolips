package tk.eclipse.plugin.htmleditor.editors;

import org.eclipse.jface.text.rules.IWhitespaceDetector;

public class JavaWhitespaceDetector implements IWhitespaceDetector {

	/**
	 * @see IWhitespaceDetector#isWhitespace
	 */
	public boolean isWhitespace(char c) {
		return Character.isWhitespace(c);
	}

}
