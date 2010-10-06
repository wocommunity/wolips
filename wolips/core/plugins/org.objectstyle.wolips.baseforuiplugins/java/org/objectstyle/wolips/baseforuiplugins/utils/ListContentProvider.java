package org.objectstyle.wolips.baseforuiplugins.utils;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class ListContentProvider implements IStructuredContentProvider {
	private List _contents;

	public Object[] getElements(Object input) {
		if (_contents != null && _contents == input) {
			return _contents.toArray();
		}
		return new Object[0];
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (newInput instanceof List) {
			_contents = (List) newInput;
		} else {
			_contents = null;
		}
	}

	public void dispose() {
		// DO NOTHING
	}

	public boolean isDeleted(Object o) {
		return _contents != null && !_contents.contains(o);
	}
}
