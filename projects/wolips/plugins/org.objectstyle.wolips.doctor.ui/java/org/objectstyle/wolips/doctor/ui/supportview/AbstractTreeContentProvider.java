/* ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0 
 *
 * Copyright (c) 2002 - 2004 The ObjectStyle Group 
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

package org.objectstyle.wolips.doctor.ui.supportview;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * @author uli
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 */
public abstract class AbstractTreeContentProvider
		implements
			ITreeContentProvider {

	/**
	 * Flag for omitting the root or not when providing the contents.
	 */
	private boolean omitRoot;

	/**
	 * The root node.
	 */
	private TreeContentProviderNode rootNode;

	/**
	 * Constructs a AbstractTreeContentProvider.
	 * 
	 * @param omitRoot
	 *            if true, the root node will be omitted when providing
	 *            contents.
	 */
	protected AbstractTreeContentProvider(boolean omitRoot) {
		this.omitRoot = omitRoot;
	}

	/**
	 * Constructs a AbstractTreeContentProvider that will omit the root node
	 * when providing contents.
	 * 
	 * @see #AbstractTreeContentProvider(boolean)
	 */
	protected AbstractTreeContentProvider() {
		this(true);
	}

	/**
	 * Returns the child elements of the given parent element.
	 * 
	 * @return an array containing <code>parentElement</code>'s children.
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(
	 *      java.lang.Object)
	 */
	public Object[] getChildren(Object parentElement) {
		if (!(parentElement instanceof TreeContentProviderNode))
			return null;

		TreeContentProviderNode treeNode = (TreeContentProviderNode) parentElement;
		return treeNode.getChildren();
	}

	/**
	 * Returns the parent for the given element, or <code>null</code>
	 * indicating that the parent can't be computed.
	 * 
	 * @return <coded>element</code>'s parent node or null, if it is a root
	 *         node
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(
	 *      java.lang.Object)
	 */
	public Object getParent(Object element) {
		if (!(element instanceof TreeContentProviderNode))
			return null;

		TreeContentProviderNode treeNode = (TreeContentProviderNode) element;
		return treeNode.getParent();
	}

	/**
	 * Returns whether the given element has children.
	 * 
	 * @return true, if <code>element</code> has children, false otherwise
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(
	 *      java.lang.Object)
	 */
	public boolean hasChildren(Object element) {
		return element instanceof TreeContentProviderNode
				&& ((TreeContentProviderNode) element).hasChildren();
	}

	/**
	 * Returns the elements to display in the viewer when its input is set to
	 * the given element.
	 * 
	 * @return this content provider root element's children
	 */
	public Object[] getElements(Object inputElement) {
		if (this.rootNode == null) {
			return new Object[0];
		}

		return this.omitRoot
				? this.rootNode.getChildren()
				: new Object[]{this.rootNode};
	}

	/**
	 * Disposes of this content provider. This is called by the viewer when it
	 * is disposed.
	 *  
	 */
	public void dispose() {
		this.rootNode = null;
	}

	/**
	 * Helper method that creates a root node given a node name and value.
	 * 
	 * @param name
	 *            the name of the node
	 * @param value
	 *            the value of the node. May be null.
	 * @return the tree node created
	 */
	protected TreeContentProviderNode createNode(String name, Object value) {
		return new TreeContentProviderNode(name, value);
	}

	/**
	 * Notifies this content provider that the given viewer's input has been
	 * switched to a different element. Rebuilds this content provider's state
	 * from a given resource.
	 * 
	 * @param viewer
	 *            ignored
	 * @param oldInput
	 *            ignored
	 * @param input
	 *            the new input. If null, clears this content provider. If not,
	 *            is passed in a call to <code>rebuild(Object)</code>.
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(
	 *      org.eclipse.jface.viewers.Viewer, java.lang.Object,
	 *      java.lang.Object)
	 * @see #rebuild(Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, final Object input) {
		if (input == null) {
			this.rootNode = createNode("root"); //$NON-NLS-1$
			return;
		}

		if (!acceptInput(input))
			return;

		this.rootNode = createNode("root"); //$NON-NLS-1$	    
		rebuild(input);
	}

	/**
	 * Helper method that creates a root node given a node name and no value.
	 * 
	 * @param name
	 *            the name of the node
	 * @return the tree node created
	 * @see TreeContentProviderNode#TreeContentProviderNode(String)
	 */
	protected TreeContentProviderNode createNode(String name) {
		return new TreeContentProviderNode(name);
	}

	/**
	 * Reconstructs this content provider data model upon the provided input
	 * object.
	 * 
	 * @param input
	 *            the new input object - must not be null
	 */
	protected abstract void rebuild(Object input);

	/**
	 * Returns true if the provided input is accepted by this content provider.
	 * 
	 * @param input
	 *            an input object
	 * @return boolean true if the provided object is accepted, false otherwise
	 */
	protected abstract boolean acceptInput(Object input);

	/**
	 * Returns the rootNode.
	 * 
	 * @return this content provider root node
	 */
	protected TreeContentProviderNode getRootNode() {
		return this.rootNode;
	}

}