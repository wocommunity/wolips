package org.objectstyle.wolips.componenteditor.inspector;

import org.eclipse.swt.custom.StyledText;

public class TextAutoscroller extends AbstractAutoscroller<StyledText> {
	public TextAutoscroller(StyledText st) {
		super(st);
	}

	@Override
	protected int getHorizontalPosition() {
		return getControl().getHorizontalIndex();
	}

	@Override
	protected int getVerticalPosition() {
		return getControl().getTopIndex();
	}

	@Override
	protected void scrollUp(int speed) {
		getControl().setTopIndex(getVerticalPosition() - 1);
	}

	@Override
	protected void scrollDown(int speed) {
		getControl().setTopIndex(getVerticalPosition() + 1);
	}

	@Override
	protected void scrollLeft(int speed) {
		getControl().setHorizontalIndex(getHorizontalPosition() - 1);
	}

	@Override
	protected void scrollRight(int speed) {
		getControl().setHorizontalIndex(getHorizontalPosition() + 1);
	}
}
