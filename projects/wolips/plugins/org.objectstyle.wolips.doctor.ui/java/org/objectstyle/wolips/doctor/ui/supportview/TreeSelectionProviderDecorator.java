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


import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;

/**
 * @author uli
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class TreeSelectionProviderDecorator implements ISelectionProvider {

	/** The decorated selection provider. */
	private ISelectionProvider selectionProvider;

	/** 
	 * Constructs a <code>TreeSelectionProviderDecorator</code> having the given 
	 * selection provider as its decorated object.
	 * 
	 * @param selectionProvider the selection provider to be decorated
	 */
	public TreeSelectionProviderDecorator(ISelectionProvider selectionProvider) {
		this.selectionProvider = selectionProvider;
	}

	/**
	 * @see org.eclipse.jface.viewers.ISelectionProvider#addSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		this.selectionProvider.addSelectionChangedListener(listener);
	}

	/**
	 * Returns the current selection for this provider. If the selection is a
	 * structured selection made of <code>TreeContentProviderNode</code>
	 * elements, this method will return a structured selection where the order of
	 * elements is the same order the elements appear in the tree (only for tree
	 * elements that are instances of <code>TreeContentProviderNode</code>).
	 * 
	 * @return the current selection, ordered in the same sequence they appear in
	 * the tree
	 * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
	 */
	public ISelection getSelection() {
		// gets the original selection object 
		ISelection selection = this.selectionProvider.getSelection();

		// in these cases the original selection will be returned
		if (selection == null || selection.isEmpty() || !(selection instanceof IStructuredSelection))
			return selection;

		// constructs a list with the selected elements 
		IStructuredSelection structuredSelection = (IStructuredSelection) selection;
		final List selectedElements = new ArrayList(structuredSelection.toList());

		// tries to find a TreeContentProviderNode between the selected elements
		TreeContentProviderNode anyNode = findNodeElement(selectedElements);

		// if there is no TreeContentProviderNodes, there is nothing to do
		if (anyNode == null)
			return selection;

		// otherwise, we will move the elements to a new list in the same order
		// we find them in the tree.
		final List orderedElements = new LinkedList();

		// uses a visitor to traverse the whole tree
		// when a visited node is the selected list, it is moved to the ordered list  
		anyNode.getRoot().accept(new ITreeNodeVisitor() {
			public boolean visit(TreeContentProviderNode node) {
				int elementIndex = selectedElements.indexOf(node);

				if (selectedElements.contains(node))
					orderedElements.add(selectedElements.remove(elementIndex));

				return true;
			}
		});

		// any remaining elements in the list (probably they are not tree nodes)
		// are copied to the end of the ordered list    
		orderedElements.addAll(selectedElements);
		return new StructuredSelection(orderedElements);
	}

	/** 
	 * Returns the first element in the list that is instance of 
	 * <code>TreeContentProviderNode</code>.
	 * @param elements
	 * 
	 * @return the first element that is a tree node or null, if none is found.
	 */
	private TreeContentProviderNode findNodeElement(List elements) {
		for (Iterator iter = elements.iterator(); iter.hasNext();) {
			Object element = iter.next();
			if (element instanceof TreeContentProviderNode)
				return (TreeContentProviderNode) element;
		}

		return null;
	}

	/**
	 * @see org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		this.selectionProvider.removeSelectionChangedListener(listener);
	}

	/**
	 * @see org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)
	 */
	public void setSelection(ISelection selection) {
		this.selectionProvider.setSelection(selection);
	}

}