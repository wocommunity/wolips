/*
 * ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0
 * 
 * Copyright (c) 2006 The ObjectStyle Group and individual authors of the
 * software. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * 3. The end-user documentation included with the redistribution, if any, must
 * include the following acknowlegement: "This product includes software
 * developed by the ObjectStyle Group (http://objectstyle.org/)." Alternately,
 * this acknowlegement may appear in the software itself, if and wherever such
 * third-party acknowlegements normally appear.
 * 
 * 4. The names "ObjectStyle Group" and "Cayenne" must not be used to endorse or
 * promote products derived from this software without prior written permission.
 * For written permission, please contact andrus@objectstyle.org.
 * 
 * 5. Products derived from this software may not be called "ObjectStyle" nor
 * may "ObjectStyle" appear in their names without prior written permission of
 * the ObjectStyle Group.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * OBJECTSTYLE GROUP OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 * 
 * This software consists of voluntary contributions made by many individuals on
 * behalf of the ObjectStyle Group. For more information on the ObjectStyle
 * Group, please see <http://objectstyle.org/>.
 *  
 */
package org.objectstyle.wolips.eomodeler.outline;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;
import org.objectstyle.wolips.eomodeler.editors.EOModelClipboardHandler;
import org.objectstyle.wolips.eomodeler.editors.EOModelEditor;

public class EOModelContentOutlinePage extends ContentOutlinePage {
	private EOModelTreeViewUpdater myUpdater;

	private EOModelEditor myEditor;

	private EOModelClipboardHandler myClipboardHandler;

	public EOModelContentOutlinePage(EOModelEditor _editor) {
		myClipboardHandler = new EOModelClipboardHandler();
		myEditor = _editor;
	}

	protected void updateClipboardHandler() {
		IPageSite site = getSite();
		if (site != null && myEditor != null) {
			IActionBars actionBars = site.getActionBars();
			myClipboardHandler.attach(actionBars, myEditor);
		}
	}

	public void createControl(Composite _parent) {
		super.createControl(_parent);
		TreeViewer treeViewer = getTreeViewer();
		myUpdater = new EOModelTreeViewUpdater(treeViewer, new EOModelOutlineContentProvider(true, true, true, true, true, true, true));
		myUpdater.setModel(myEditor.getModel());
		updateClipboardHandler();
		// AK: commenting prevents an error in swt
		// setFocus();
	}

	public void init(IPageSite _pageSite) {
		super.init(_pageSite);
		updateClipboardHandler();
	}

	public void selectionChanged(SelectionChangedEvent _event) {
		super.selectionChanged(_event);
		myClipboardHandler.selectionChanged(_event);
	}

	public void setSelection(ISelection _selection) {
		super.setSelection(_selection);
	}
}
