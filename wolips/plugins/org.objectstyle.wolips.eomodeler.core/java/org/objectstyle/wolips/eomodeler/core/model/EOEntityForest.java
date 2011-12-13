package org.objectstyle.wolips.eomodeler.core.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * EOEntityForest represents a collection of EOEntityTreeNodes. An arbitrary
 * collection of EOEntityTreeNodes constitutes a forest as you can have multiple
 * root nodes.
 * 
 * @author mschrag
 */
public class EOEntityForest {
	private Map<EOEntity, EOEntityTreeNode> _nodes;

	public EOEntityForest() {
		_nodes = new TreeMap<EOEntity, EOEntityTreeNode>();
	}

	public Set<EOEntityTreeNode> getRootNodes() {
		Set<EOEntityTreeNode> rootNodes = new TreeSet<EOEntityTreeNode>();
		for (EOEntityTreeNode node : _nodes.values()) {
			if (node.isRoot()) {
				rootNodes.add(node);
			}
		}
		return rootNodes;
	}

	public void add(Collection<EOEntity> entities) {
		for (EOEntity entity : entities) {
			add(entity);
		}
	}

	public EOEntityTreeNode add(EOEntity entity) {
		EOEntityTreeNode node = _nodes.get(entity);
		if (node == null) {
			node = new EOEntityTreeNode(entity);
			_nodes.put(entity, node);

			EOEntity parentEntity = entity.getParent();
			if (parentEntity != null) {
				EOEntityTreeNode parentNode = _nodes.get(parentEntity);
				if (parentNode != null) {
					parentNode.addChild(node);
				}
			}

			Set<EOEntity> childrenEntities = entity.getChildrenEntities();
			for (EOEntity childEntity : childrenEntities) {
				EOEntityTreeNode childNode = _nodes.get(childEntity);
				if (childNode != null) {
					node.addChild(childNode);
				}
			}
		}
		return node;
	}

	public List<EOEntity> breadthFirst() {
		List<EOEntity> entities = new LinkedList<EOEntity>();
		Set<EOEntityTreeNode> rootNodes = getRootNodes();
		for (EOEntityTreeNode rootNode : rootNodes) {
			entities.add(rootNode.getEntity());
		}
		for (EOEntityTreeNode rootNode : rootNodes) {
			rootNode._breadthFirst(entities);
		}
		return entities;
	}
}
