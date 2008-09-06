package org.objectstyle.wolips.baseforuiplugins.plist;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.objectstyle.woenvironment.plist.ParserDataStructureFactory;

public class PropertyListContentProvider implements ITreeContentProvider {
	private ParserDataStructureFactory _factory;

	private boolean _rootVisible;

	private Set<String> _filteredKeyPaths;

	public PropertyListContentProvider(ParserDataStructureFactory factory, boolean rootVisible, Set<String> filteredKeyPaths) {
		_factory = factory;
		_rootVisible = rootVisible;
		_filteredKeyPaths = filteredKeyPaths;
	}

	public void setFactory(ParserDataStructureFactory factory) {
		_factory = factory;
	}

	public ParserDataStructureFactory getFactory() {
		return _factory;
	}

	public Object[] getChildren(Object parentElement) {
		return getElements(parentElement);
	}

	public Object getParent(Object element) {
		Object parent;
		if (element instanceof PropertyListPath) {
			PropertyListPath path = (PropertyListPath) element;
			parent = path.getParent();
			if (parent == null) {
				parent = path.getRawObject();
			}
		} else {
			parent = null;
		}
		return parent;
	}

	public boolean hasChildren(Object element) {
		boolean hasChildren = false;
		if (element instanceof PropertyListPath) {
			PropertyListPath path = (PropertyListPath) element;
			hasChildren = path.isCollectionValue();
		} else {
			hasChildren = true;
		}
		return hasChildren;
	}

	public Object[] getElements(Object inputElement) {
		List<PropertyListPath> children;

		boolean filter = false;
		if (inputElement instanceof PropertyListPath) {
			PropertyListPath path = (PropertyListPath) inputElement;
			children = path.getChildren();
			if (path.getParent() == null) {
				filter = true;
			}
		} else if (_rootVisible) {
			children = new LinkedList<PropertyListPath>();
			children.add(new PropertyListPath(inputElement, _factory));
		} else {
			children = new PropertyListPath(inputElement, _factory).getChildren();
		}

		if (_filteredKeyPaths != null && !_filteredKeyPaths.isEmpty() && filter) {
			Iterator<PropertyListPath> childrenIter = children.iterator();
			while (childrenIter.hasNext()) {
				PropertyListPath child = childrenIter.next();
				if (_filteredKeyPaths.contains(child.getKeyPath())) {
					childrenIter.remove();
				}
			}
		}

		return children.toArray();
	}

	public void dispose() {
		// DO NOTHING
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// DO NOTHING
	}

}
