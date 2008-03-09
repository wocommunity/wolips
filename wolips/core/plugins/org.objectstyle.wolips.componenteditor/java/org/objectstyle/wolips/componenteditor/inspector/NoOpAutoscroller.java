package org.objectstyle.wolips.componenteditor.inspector;

import org.eclipse.swt.widgets.Control;

public class NoOpAutoscroller extends AbstractAutoscroller<Control> {
	public NoOpAutoscroller() {
		super(null);
	}

	@Override
	protected int getHorizontalPosition() {
		return 0;
	}

	@Override
	protected int getVerticalPosition() {
		return 0;
	}

	@Override
	protected void scrollDown(int speed) {
		// DO NOTHING
	}

	@Override
	protected void scrollLeft(int speed) {
		// DO NOTHING
	}

	@Override
	protected void scrollRight(int speed) {
		// DO NOTHING
	}

	@Override
	protected void scrollUp(int speed) {
		// DO NOTHING
	}
}
