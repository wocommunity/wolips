/* ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0 
 *
 * Copyright (c) 2002 The ObjectStyle Group 
 * and individual authors of the software.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:  
 *       "This product includes software developed by the 
 *        ObjectStyle Group (http://objectstyle.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "ObjectStyle Group" and "Cayenne" 
 *    must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written 
 *    permission, please contact andrus@objectstyle.org.
 *
 * 5. Products derived from this software may not be called "ObjectStyle"
 *    nor may "ObjectStyle" appear in their names without prior written
 *    permission of the ObjectStyle Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE OBJECTSTYLE GROUP OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the ObjectStyle Group.  For more
 * information on the ObjectStyle Group, please see
 * <http://objectstyle.org/>.
 *
 */
 
package org.objectstyle.wolips.ui.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author uli
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class TreeContentProviderNode implements Comparable {

	/**
	 * A list containing this node's children. 
	 */
	private List children;

	/**
	 * This node's name.
	 */
	private String name;

	/** 
	 * This node's value (may be null).
	 */
	private Object value;

	/**
	 * This node's parent node.
	 */
	private TreeContentProviderNode parent;

	/**
	 * Constructs a TreeContentProviderNode with the given name and value.
	 * 
	 * @param name this node's name
	 * @param value this node's value (may be null)
	 */
	public TreeContentProviderNode(String name, Object value) {
		this.name = name;
		this.value = value;
	}
	/**
	 * Constructs a TreeContentProviderNode with the given name.
	 * 
	 * @param name this node's name. 
	 */
	public TreeContentProviderNode(String name) {
		this(name, null);
	}

	/**
	 * Sets this node's parent.
	 * 
	 * @param parent this node's new parent
	 */
	private void setParent(TreeContentProviderNode parent) {
		this.parent = parent;
	}

	/**
	 * Adds a new child. If the child is a TreeContentProviderNode, sets its parent
	 * to this object.
	 * 
	 * @param child a new child to be added.
	 */
	public void addChild(Object child) {
		// lazilly instantiates the children's list
		if (this.children == null) {
			this.children = new ArrayList();
		}
		this.children.add(child);
		if (child instanceof TreeContentProviderNode) {
			TreeContentProviderNode childNode = (TreeContentProviderNode) child;
			childNode.setParent(this);
		}
	}

	/**
	 * Returns an array containing all children this node has. If this node 
	 * has no children, returns an empty array.
	 * 
	 * @return an array containing this node's children.
	 */
	public Object[] getChildren() {
		return children == null ? new Object[0] : children.toArray();
	}

	/**
	 * Returns a boolean indicating if this node has any children.
	 * 
	 * @return true, if this node has children, false otherwise
	 */
	public boolean hasChildren() {
		return children != null && !children.isEmpty();
	}

	/**
	 * Returns a string representation of the object.
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return name + (value == null ? "" : (" = " + value.toString()));
	}

	/**
	 * Returns this node's parent node.
	 * 
	 * @return this node's parent node or null, if this node is a root
	 */
	public TreeContentProviderNode getParent() {
		return parent;
	}

	/**
	 * Returns this node's value (may be null).
	 * 
	 * @return this node's value
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * Returns a boolean indicating if this node is root or not.
	 * 
	 * @return true if this node is root, false otherwise
	 */
	public boolean isRoot() {
		return parent == null;
	}
	/**
	 * Removes all child nodes (if any) from this node. This operation affects
	 * only this node. No changes are made to the child nodes. 
	 */
	public void removeAllChildren() {
		if (children == null)
			return;

		children.clear();
	}

	/**
	 * Sorts this node's children list in ascending order. The children are 
	 * ordered by name. Any changes in the children list will potentially 
	 * invalidate the ordering. All children must be instances of 
	 * <code>TreeContentProviderNode</code>. 
	 */
	public void sort() {
		if (children == null)
			return;
		Collections.sort(children);
	}

	/**
	 * Compares this node with another node. 
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object other) {
		TreeContentProviderNode otherNode = (TreeContentProviderNode) other;
		return this.name.compareTo(otherNode.name);
	}

	/**
	 * Accepts the given visitor. The visitor's <code>visit</code> method is called
	 * with this node. If the visitor returns <code>true</code>, this method visits
	 * this node's child nodes.
	 *
	 * @param visitor the visitor
	 * @see ITreeNodeVisitor#visit
	 */
	public void accept(ITreeNodeVisitor visitor) {
		if (!visitor.visit(this))
			return;
		if (children == null)
			return;
		for (Iterator childrenIter = children.iterator(); childrenIter.hasNext();) {
			Object child = childrenIter.next();
			// child nodes don't need to be TreeContentProviderNodes
			if (child instanceof TreeContentProviderNode)
				 ((TreeContentProviderNode) child).accept(visitor);
		}
	}

	/**
	 * Returns this node's tree root node. If this node is a root node, returns itself.
	 * 
	 * @return this node's tree root node
	 */
	public TreeContentProviderNode getRoot() {
		return this.getParent() == null ? this : this.getParent().getRoot();
	}

}