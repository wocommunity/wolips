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

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

/**
 * @author uli
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 */
public class SupportView extends ViewPart {

	/** JFace's tree component used to present resource details. */
	private AbstractTreeViewer viewer;

	/** The content provider for this view's TreeViewer. */
	private SupportContentProvider supportContentProvider;

	/**
	 * Constructs a resource view object, registering a resource change
	 * listener.
	 */
	public SupportView() {
		super();
	}

	/**
	 * Creates the SWT controls for the resource view.
	 * 
	 * @param parent
	 *            the parent control
	 * @see IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent) {
		this.viewer = new TreeViewer(parent);
		this.supportContentProvider = new SupportContentProvider();
		this.viewer.setContentProvider(this.supportContentProvider);
		this.start(this.supportContentProvider);
		createActions();
	}

	private void start(SupportContentProvider contentProvider) {
		if (this.viewer.getControl().isDisposed()) {
			return;
		}

		// turn redraw off so the UI will reflect changes only after we are done
		this.viewer.getControl().setRedraw(false);

		// fires viewer update
		this.viewer.setInput(contentProvider);

		// shows all nodes in the resource tree
		this.viewer.expandAll();

		// we are done, turn redraw on
		this.viewer.getControl().setRedraw(true);
	}

	/**
	 * Creates and publishes this view's actions.
	 */
	private void createActions() {
		IActionBars actionBars = this.getViewSite().getActionBars();

		final GlobalAction copyAction = new CopyStructuredSelectionAction(
				new TreeSelectionProviderDecorator(this.viewer));
		copyAction.registerAsGlobalAction(actionBars);

		final GlobalAction selectAllAction = new SelectAllAction(
				new TreeTextOperationTarget((Tree) this.viewer.getControl()));
		selectAllAction.registerAsGlobalAction(actionBars);

		actionBars.updateActionBars();

		final MenuManager menuMgr = new MenuManager();
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				manager.add(copyAction);
			}
		});
		Menu menu = menuMgr.createContextMenu(this.viewer.getControl());
		this.viewer.getControl().setMenu(menu);
	}

	/**
	 * dispose.
	 * 
	 * @see org.eclipse.ui.IWorkbenchPart#dispose()
	 */
	public void dispose() {
		super.dispose();
	}

	/**
	 * Asks this part to take focus within the workbench. Does nothing.
	 */
	public void setFocus() {
		return;
	}
}