package org.objectstyle.wolips.baseforuiplugins.utils;

import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

public class WOTextCellEditor extends TextCellEditor {
	public WOTextCellEditor() {
		// DO NOTHING
	}

	public WOTextCellEditor(Composite parent) {
		super(parent, "carbon".equals(SWT.getPlatform()) ? SWT.BORDER | SWT.SINGLE : SWT.SINGLE);
	}

	public WOTextCellEditor(Composite parent, int style) {
		super(parent, "carbon".equals(SWT.getPlatform()) ? SWT.BORDER | SWT.SINGLE : style);
	}

	@Override
	protected Control createControl(Composite parent) {
		Text textControl = (Text) super.createControl(parent);
		if ("carbon".equals(SWT.getPlatform())) {
			textControl.addControlListener(new ControlListener() {
				private boolean _moving;

				private boolean _resizing;

				public void controlMoved(ControlEvent e) {
					if (!_moving) {
						_moving = true;
						try {
							Text resizedText = (Text) e.widget;
							Point location = resizedText.getLocation();
							Composite controlParent = resizedText.getParent();
							if (controlParent instanceof Tree) {
								TreeColumn firstColumn = ((Tree) controlParent).getColumn(0);
								// The first column of a tree lines up differently for some reason -- maybe
								// the open/close triangle causes it to move.  I don't see any API to 
								// determine what column this editor is editing, so this lame hack is
								// checking to see if our initial x position is less than the width of the
								// first column.
								if (resizedText.getBounds().x < firstColumn.getWidth()) {
									if (((Tree)controlParent).getColumnCount() == 1) {
										resizedText.setLocation(location.x - 3, location.y - 5);
									}
									else {
										resizedText.setLocation(location.x - 7, location.y - 5);
									}
								}
								else {
									resizedText.setLocation(location.x - 7, location.y - 6);
								}
							} else {
								resizedText.setLocation(location.x - 7, location.y - 6);
							}
						} finally {
							_moving = false;
						}
					}
				}

				public void controlResized(ControlEvent e) {
					if (!_resizing) {
						_resizing = true;
						try {
							Text resizedText = (Text) e.widget;
							Point size = resizedText.getSize();
							Composite controlParent = resizedText.getParent();
							if (controlParent instanceof Tree) {
								TreeColumn firstColumn = ((Tree) controlParent).getColumn(0);
								// The first column of a tree lines up differently for some reason -- maybe
								// the open/close triangle causes it to move.  I don't see any API to 
								// determine what column this editor is editing, so this lame hack is
								// checking to see if our initial x position is less than the width of the
								// first column.
								if (resizedText.getBounds().x < firstColumn.getWidth()) {
									if (((Tree)controlParent).getColumnCount() == 1) {
										resizedText.setSize(size.x + 5, size.y + 10);
									}
									else {
										resizedText.setSize(size.x + 11, size.y + 9);
									}
								}
								else {
									resizedText.setSize(size.x + 9, size.y + 12);
								}
							} else {
								resizedText.setSize(size.x + 8, size.y + 12);
							}
						} finally {
							_resizing = false;
						}
					}
				}
			});
		}
		return textControl;
	}

	public Text getText() {
		return text;
	}
}
