/*
 * ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0
 * 
 * Copyright (c) 2006 The ObjectStyle Group and individual authors of the
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
package org.objectstyle.wolips.componenteditor.part;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.objectstyle.wolips.components.editor.IEmbeddedEditorSelected;

public abstract class ComponentEditorTab extends Composite {

	private ComponentEditorPart componentEditorPart;

	private SashForm parentSashForm;

	private int tabIndex;
	
	private Color _sashColor;

	public ComponentEditorTab(ComponentEditorPart componentEditorPart, int tabIndex) {
		super(componentEditorPart.publicGetContainer(), SWT.NONE);
		this.componentEditorPart = componentEditorPart;
		parentSashForm = new SashForm(this, SWT.VERTICAL | SWT.SMOOTH);
		parentSashForm.setSashWidth(4);
		_sashColor = new Color(getDisplay(), 205, 205, 205);
		parentSashForm.setBackground(_sashColor);

		this.setLayout(new FillLayout());
		this.tabIndex = tabIndex;
	}

	public ComponentEditorPart getComponentEditorPart() {
		return componentEditorPart;
	}

	protected Composite createInnerPartControl(Composite parent, final IEditorPart e) {
		Composite content = new Composite(parent, SWT.NONE);
		content.setLayout(new FillLayout(SWT.VERTICAL));
		e.createPartControl(content);
		return content;
	}

	public abstract IEditorPart getActiveEmbeddedEditor();

	public SashForm getParentSashForm() {
		return parentSashForm;
	}

	public abstract boolean isDirty();

	public abstract void doSave(IProgressMonitor monitor);

	public abstract void close(boolean save);
	
	public void dispose() {
		_sashColor.dispose();
	}

	public void editorSelected() {
		if (this.getActiveEmbeddedEditor() instanceof IEmbeddedEditorSelected) {
			{
				IEmbeddedEditorSelected embeddedEditorSelected = (IEmbeddedEditorSelected) this.getActiveEmbeddedEditor();
				embeddedEditorSelected.editorSelected();
			}
		}
	}

	public abstract IEditorInput getActiveEditorInput();

	public int getTabIndex() {
		return tabIndex;
	}
}
