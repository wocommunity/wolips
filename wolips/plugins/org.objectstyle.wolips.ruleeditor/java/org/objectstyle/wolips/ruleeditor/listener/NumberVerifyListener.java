package org.objectstyle.wolips.ruleeditor.listener;

import org.eclipse.swt.events.*;

/**
 * @author <a href="mailto:georg@moleque.com.br">Georg von Bülow</a>
 */
public class NumberVerifyListener implements VerifyListener {

	public void verifyText(final VerifyEvent event) {
		String text = event.text;

		char[] chars = new char[text.length()];

		text.getChars(0, chars.length, chars, 0);

		// TODO: Use regular expression instead
		for (int index = 0; index < chars.length; index++) {
			if (!((('0' <= chars[index]) && (chars[index] <= '9')))) {
				event.doit = false;

				return;
			}
		}
	}

}
