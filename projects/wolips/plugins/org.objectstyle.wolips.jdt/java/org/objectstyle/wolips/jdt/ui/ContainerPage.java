/* ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0 
 *
 * Copyright (c) 2004 The ObjectStyle Group 
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

package org.objectstyle.wolips.jdt.ui;

// http://www.eclipse.org/articles/Understanding%20Layouts/Understanding%20Layouts.htm

import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.ui.wizards.IClasspathContainerPage;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * Insert the type's description here.
 * @see WizardPage
 */
public class ContainerPage
	extends WizardPage
	implements IClasspathContainerPage {
	private ContainerContentProvider containerContentProvider;
	private CheckboxTreeViewer checkboxTreeViewer;
	
	/**
	 * The constructor.
	 */
	public ContainerPage() {
		super("non-localized WOClassPathContainerPage");
	}

	public void createControl(Composite parent) {
		Composite thisPage = new Composite(parent, SWT.NONE);

		thisPage.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		thisPage.setLayout(new GridLayout());
		//thisPage.setLayout(new RowLayout(SWT.VERTICAL));

		this.checkboxTreeViewer = new CheckboxTreeViewer(thisPage, SWT.MULTI);
		//_uiList = new CheckboxTreeViewer(thisPage, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		GridData gd =
			new GridData(
				GridData.HORIZONTAL_ALIGN_FILL | GridData.FILL_HORIZONTAL);
		//|GridData.VERTICAL_ALIGN_FILL
		Rectangle trim =
			this.checkboxTreeViewer.getTree().computeTrim(
				0,
				0,
				0,
				12 * this.checkboxTreeViewer.getTree().getItemHeight());
		gd.heightHint = trim.height;
		this.checkboxTreeViewer.getTree().setLayoutData(gd);
		this.checkboxTreeViewer.setContentProvider(this.containerContentProvider);
		this.checkboxTreeViewer.setLabelProvider(this.containerContentProvider);
		this.checkboxTreeViewer.setInput(this.containerContentProvider);
		Label lbl = new Label(thisPage, SWT.SINGLE);
		lbl.setText("Hint: use Ctrl-click or Shift-click");

		thisPage.layout();

		setControl(thisPage);
		this.containerContentProvider.setCheckboxTreeViewer(this.checkboxTreeViewer);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.ui.wizards.IClasspathContainerPage#finish()
	 */
	public boolean finish() {
		return true;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.ui.wizards.IClasspathContainerPage#getSelection()
	 */
	public IClasspathEntry getSelection() {
		return this.containerContentProvider.getClasspathEntry();
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.ui.wizards.IClasspathContainerPage#setSelection(org.eclipse.jdt.core.IClasspathEntry)
	 */
	public void setSelection(IClasspathEntry containerEntry) {
		if(containerEntry == null) {
			this.containerContentProvider = new ContainerContentProvider();
		}
		else {
			this.containerContentProvider = new ContainerContentProvider(containerEntry);
		}
	}

}
