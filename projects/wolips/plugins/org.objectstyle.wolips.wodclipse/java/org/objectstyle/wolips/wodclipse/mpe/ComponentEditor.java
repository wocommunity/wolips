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
package org.objectstyle.wolips.wodclipse.mpe;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.MultiEditor;
import org.eclipse.ui.part.MultiEditorInput;

/**
 * @author uli
 */
public class ComponentEditor extends MultiEditor {

	/*
	 * @see IWorkbenchPart#createPartControl(Composite)
	 */
	public void createPartControl(Composite parent) {
		final CTabFolder folder = new CTabFolder(parent, SWT.BOTTOM);
		Composite javaEditorParent = new Composite(folder, SWT.NONE);
		javaEditorParent.setLayout(new FillLayout());
		CTabItem javaTab = new CTabItem(folder, SWT.NONE);
		javaTab.setControl(javaEditorParent);
		javaTab.setText("Java");

		Composite componentEditorParent = new Composite(folder, SWT.NONE);
		componentEditorParent.setLayout(new FillLayout());
		CTabItem componentTab = new CTabItem(folder, SWT.NONE);
		componentTab.setControl(componentEditorParent);
		componentTab.setText("Component");

		Composite apiEditorParent = new Composite(folder, SWT.NONE);
		apiEditorParent.setLayout(new FillLayout());
		CTabItem apiTab = new CTabItem(folder, SWT.NONE);
		apiTab.setControl(apiEditorParent);
		apiTab.setText("Api");

		SashForm componentEditorSashParent = new SashForm(
				componentEditorParent, SWT.VERTICAL | SWT.SMOOTH);

		IEditorPart[] innerEditors = getInnerEditors();

		for (int i = 0; i < innerEditors.length; i++) {
			final IEditorPart e = innerEditors[i];
			switch (i) {
			case 0:
				createInnerPartControl(javaEditorParent, e);
				addPropertyListener(e);
				break;

			case 1:
				SashForm htmlSashform = new SashForm(componentEditorSashParent,
						SWT.VERTICAL);
				createInnerPartControl(htmlSashform, e);
				addPropertyListener(e);
				break;

			case 2:
				SashForm wodSashform = new SashForm(componentEditorSashParent,
						SWT.VERTICAL);
				createInnerPartControl(wodSashform, e);
				addPropertyListener(e);
				break;
			case 3:
				createInnerPartControl(apiEditorParent, e);
				addPropertyListener(e);
				break;

			default:
				break;
			}
		}
		folder.setSelection(javaTab);
	}

	private void addPropertyListener(IEditorPart editorPart) {
		editorPart.addPropertyListener(new IPropertyListener() {
			public void propertyChanged(Object source, int property) {
				if (property == IEditorPart.PROP_DIRTY
						|| property == IWorkbenchPart.PROP_TITLE)
					if (source instanceof IEditorPart)
						fireDirtyProperty();
			}
		});

	}

	void fireDirtyProperty() {
		this.firePropertyChange(IEditorPart.PROP_DIRTY);
	}

	/**
	 * Draw the gradient for the specified editor.
	 */
	protected void drawGradient(IEditorPart innerEditor, Gradient g) {
		return;
	}

	protected int getIndex(IEditorPart editor) {
		IEditorPart innerEditors[] = getInnerEditors();
		for (int i = 0; i < innerEditors.length; i++) {
			if (innerEditors[i] == editor)
				return i;
		}
		return -1;
	}

	/*
	 * @see IEditorPart#init(IEditorSite, IEditorInput)
	 */
	public void init(IEditorSite site, MultiEditorInput input)
			throws PartInitException {
		super.init(site, input);
		String javaInputName = input.getInput()[0].getName();
		String partName = javaInputName.substring(0, javaInputName.length() - 5) + " WOComponent";
		setPartName(partName);
	}

}