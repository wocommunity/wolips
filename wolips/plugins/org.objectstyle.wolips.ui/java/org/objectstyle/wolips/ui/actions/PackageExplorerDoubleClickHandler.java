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
package org.objectstyle.wolips.ui.actions;

import java.util.Iterator;

import org.eclipse.core.resources.IFolder;
import org.eclipse.jdt.internal.core.util.WeakHashSet;
import org.eclipse.jdt.internal.ui.packageview.PackageExplorerPart;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.internal.UIPlugin;

/**
 * Eclipse does not understand how to open a bundle folder. This handler sneaks
 * in and listens to double-click events on Package Explorer to try and provide
 * that missing ability for EOModel folders and WO component folders.
 * 
 * @author mschrag
 */
public class PackageExplorerDoubleClickHandler implements IPageListener, IPartListener2, IDoubleClickListener {
	private WeakHashSet _listeningPackageExplorers;

	public PackageExplorerDoubleClickHandler() {
		_listeningPackageExplorers = new WeakHashSet();
		IWorkbenchPage[] pages = UIPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getPages();
		for (int i = 0; i < pages.length; i++) {
			IWorkbenchPage page = pages[i];
			findAndAttachToPackageExplorerInPage(page);
		}
	}

	public void pageActivated(IWorkbenchPage _page) {
		findAndAttachToPackageExplorerInPage(_page);
		_page.addPartListener(this);
	}

	public void pageClosed(IWorkbenchPage _page) {
		_page.removePartListener(this);
		// do nothing
	}

	public void pageOpened(IWorkbenchPage _page) {
		// do nothing
	}

	public void partActivated(IWorkbenchPartReference _partRef) {
		// do nothing
	}

	public void partBroughtToTop(IWorkbenchPartReference _partRef) {
		// do nothing
	}

	public void partClosed(IWorkbenchPartReference _partRef) {
		// do nothing
	}

	public void partDeactivated(IWorkbenchPartReference _partRef) {
		// do nothing
	}

	public void partHidden(IWorkbenchPartReference _partRef) {
		// do nothing
	}

	public void partInputChanged(IWorkbenchPartReference _partRef) {
		// do nothing
	}

	public void partOpened(IWorkbenchPartReference _partRef) {
		// do nothing
	}

	public void partVisible(IWorkbenchPartReference partRef) {
		attachToPartIfNecessary(partRef);
	}

	protected void findAndAttachToPackageExplorerInPage(IWorkbenchPage page) {
		IWorkbenchPartReference packageExplorerPartRef = page.findViewReference(JavaUI.ID_PACKAGES);
		attachToPartIfNecessary(packageExplorerPartRef);
	}

	protected synchronized void attachToPartIfNecessary(IWorkbenchPartReference partReference) {
		if (partReference != null && JavaUI.ID_PACKAGES.equals(partReference.getId())) {
			IWorkbenchPart part = partReference.getPart(false);
			if (part instanceof PackageExplorerPart) {
				PackageExplorerPart packageExplorerPart = (PackageExplorerPart) part;
				if (!_listeningPackageExplorers.contains(packageExplorerPart)) {
					TreeViewer packageExplorerTreeViewer = packageExplorerPart.getTreeViewer();
					if (packageExplorerTreeViewer != null) {
						packageExplorerTreeViewer.addDoubleClickListener(this);
						_listeningPackageExplorers.add(packageExplorerPart);
					}
				}
			}
		}
	}

	public void doubleClick(DoubleClickEvent _event) {
		ISelection selection = _event.getSelection();
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			Iterator selectedObjectsIter = structuredSelection.iterator();
			while (selectedObjectsIter.hasNext()) {
				Object selectedObj = selectedObjectsIter.next();
				if (selectedObj instanceof IFolder) {
					IFolder selectedFolder = (IFolder) selectedObj;
					if (!OpenEntityModelerAction.openResourceIfPossible(selectedFolder)) {
						OpenWOAction.openResourceIfPossible(selectedFolder);
					}
				}
			}
		}
	}
}