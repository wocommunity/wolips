package org.objectstyle.wolips.eomodeler.core.model;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * EOEntityTreeNode is a data structure for representing a subtree of a set of
 * entities. As an example, when you are generating SQ, you need to be able to
 * order an arbitrary set of entities in order of their inheritance.
 * 
 * @author mschrag
 */
public class EOEntityTreeNode implements Comparable<EOEntityTreeNode> {
	private EOEntity _entity;

	private EOEntityTreeNode _parent;

	private Set<EOEntityTreeNode> _children;

	public EOEntityTreeNode(EOEntity entity) {
		_entity = entity;
		_children = new TreeSet<EOEntityTreeNode>();
	}

	public EOEntity getEntity() {
		return _entity;
	}

	public boolean isRoot() {
		return _parent == null;
	}

	public EOEntityTreeNode getParent() {
		return _parent;
	}

	public Set<EOEntityTreeNode> getChildren() {
		return _children;
	}

	public void addChild(EOEntityTreeNode entity) {
		entity._parent = this;
		_children.add(entity);
	}

	public void removeChild(EOEntityTreeNode entity) {
		entity._parent = null;
		_children.remove(entity);
	}

	public void _breadthFirst(List<EOEntity> entities) {
		for (EOEntityTreeNode childNode : getChildren()) {
			entities.add(childNode.getEntity());
		}
		for (EOEntityTreeNode childNode : getChildren()) {
			childNode._breadthFirst(entities);
		}
	}

	public String toString() {
		return "[EOEntityNode: entity=" + _entity.getName() + "]";
	}

	public int compareTo(EOEntityTreeNode otherNode) {
		return _entity.getName().compareTo(otherNode.getEntity().getName());
	}
}
