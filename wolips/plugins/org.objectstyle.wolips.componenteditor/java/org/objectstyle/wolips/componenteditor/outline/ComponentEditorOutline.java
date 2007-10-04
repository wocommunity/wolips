/*
 * ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0
 * 
 * Copyright (c) 2005 The ObjectStyle Group and individual authors of the
 * software. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 1.
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. The end-user documentation
 * included with the redistribution, if any, must include the following
 * acknowlegement: "This product includes software developed by the ObjectStyle
 * Group (http://objectstyle.org/)." Alternately, this acknowlegement may
 * appear in the software itself, if and wherever such third-party
 * acknowlegements normally appear. 4. The names "ObjectStyle Group" and
 * "Cayenne" must not be used to endorse or promote products derived from this
 * software without prior written permission. For written permission, please
 * contact andrus@objectstyle.org. 5. Products derived from this software may
 * not be called "ObjectStyle" nor may "ObjectStyle" appear in their names
 * without prior written permission of the ObjectStyle Group.
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
 * This software consists of voluntary contributions made by many individuals
 * on behalf of the ObjectStyle Group. For more information on the ObjectStyle
 * Group, please see <http://objectstyle.org/> .
 *  
 */
package org.objectstyle.wolips.componenteditor.outline;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.Page;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

/**
 * @author uli
 */
public class ComponentEditorOutline extends Page implements IContentOutlinePage, ISelectionProvider, ISelectionChangedListener {
	private PageBook pagebook;

	private IContentOutlinePage currentPage;

	private IContentOutlinePage emptyPage;

	private IActionBars actionBars;

	private IMenuManager menuManager;

	private IToolBarManager toolBarManager;

	private IStatusLineManager statusLineManager;

	private ISelection selection;

	private List<ISelectionChangedListener> listeners;

	public ComponentEditorOutline() {
		super();
		listeners = new ArrayList<ISelectionChangedListener>();
	}

	public void createControl(Composite parent) {
		pagebook = new PageBook(parent, SWT.NONE);
	}

	public void dispose() {
		if (pagebook != null && !pagebook.isDisposed())
			pagebook.dispose();
		pagebook = null;
		listeners = null;
	}

	public boolean isDisposed() {
		return listeners == null;
	}

	public Control getControl() {
		return pagebook;
	}

	public PageBook getPagebook() {
		return pagebook;
	}

	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		if (listeners != null) {
			listeners.add(listener);
		}
	}

	public ISelection getSelection() {
		return selection;
	}

	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		if (listeners != null) {
			listeners.remove(listener);
		}
	}

	public void selectionChanged(SelectionChangedEvent event) {
		setSelection(event.getSelection());
	}

	public void setSelection(ISelection selection) {
		this.selection = selection;
		if (listeners == null)
			return;
		SelectionChangedEvent e = new SelectionChangedEvent(this, selection);
		for (int i = 0; i < listeners.size(); i++) {
			listeners.get(i).selectionChanged(e);
		}
	}

	public void makeContributions(IMenuManager newMenuManager, IToolBarManager newToolBarManager, IStatusLineManager newStatusLineManager) {
		this.menuManager = newMenuManager;
		this.toolBarManager = newToolBarManager;
		this.statusLineManager = newStatusLineManager;
	}

	public void setActionBars(IActionBars actionBars) {
		this.actionBars = actionBars;
		if (currentPage != null)
			setPageActive(currentPage);

	}

	public IActionBars getActionBars() {
		return actionBars;
	}

	public void setFocus() {
		if (currentPage != null)
			currentPage.setFocus();
	}

	private IContentOutlinePage getEmptyPage() {
		if (emptyPage == null)
			emptyPage = new EmptyOutlinePage();
		return emptyPage;
	}

	public void setPageActive(IContentOutlinePage contentOutlinePage) {
		IContentOutlinePage page = contentOutlinePage;
		if (page == null) {
			page = getEmptyPage();
		}
		if (currentPage != null) {
			currentPage.removeSelectionChangedListener(this);
		}
		page.addSelectionChangedListener(this);
		this.currentPage = page;
		if (pagebook == null) {
			// still not being made
			return;
		}
		Control control = page.getControl();
		if (control == null || control.isDisposed()) {
			if (page instanceof Page) {
				((Page) page).init(this.getSite());
			}
			page.setActionBars(getActionBars());
			page.createControl(pagebook);
			control = page.getControl();
			if (page instanceof Page) {
				((Page) page).makeContributions(menuManager, toolBarManager, statusLineManager);
			}
		}
		pagebook.showPage(control);
		this.currentPage = page;
	}

}
