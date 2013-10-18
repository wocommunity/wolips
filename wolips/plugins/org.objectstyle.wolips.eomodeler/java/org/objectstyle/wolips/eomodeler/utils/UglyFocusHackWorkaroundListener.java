package org.objectstyle.wolips.eomodeler.utils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

public class UglyFocusHackWorkaroundListener implements Listener {

	/* This is only needed on OSX with SWT 4.x when it screws up the contents of Text
	 * fields that are not the first child and are focused using the mouse. */
	private static final Listener listener = new UglyFocusHackWorkaroundListener();
	public void handleEvent(Event e) {
		Text t = (Text) e.widget;
		switch (e.type) {
		case SWT.FocusIn: {
			if (t.getData() == null) {
				t.setData(t.getText());
			} 
			else if (!t.getData().equals(t.getText())) {
				t.setText((String) t.getData());
			}
			break;
		}
		default: {
			t.setData(t.getText());
		}
		}
	}
	
	public static void addListener(Text textField) {
		textField.addListener(SWT.FocusIn, listener);
		textField.addListener(SWT.FocusOut, listener);
		textField.addListener(SWT.Modify, listener);}
}
