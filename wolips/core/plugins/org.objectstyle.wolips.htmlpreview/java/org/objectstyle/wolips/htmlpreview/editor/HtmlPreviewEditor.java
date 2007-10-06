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
package org.objectstyle.wolips.htmlpreview.editor;

import jp.aonir.fuzzyxml.FuzzyXMLDocument;
import jp.aonir.fuzzyxml.FuzzyXMLElement;
import jp.aonir.fuzzyxml.internal.RenderContext;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;
import org.objectstyle.wolips.components.editor.EditorInteraction;
import org.objectstyle.wolips.components.editor.IEmbeddedEditor;
import org.objectstyle.wolips.components.editor.IEmbeddedEditorSelected;
import org.objectstyle.wolips.htmlpreview.HtmlPreviewPlugin;
import org.objectstyle.wolips.templateeditor.TemplateEditor;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;

/**
 * based on an eclipse.org example
 * 
 * @author uli
 */
public class HtmlPreviewEditor implements IEmbeddedEditor, IEmbeddedEditorSelected, IEditorPart {

	private EditorInteraction _editorInteraction;

	private IEditorSite _site;

	private IEditorInput _input;

	private Browser _browser;

	/**
	 * Update the contents of the Preview page
	 */
	private void updatePreviewContent() {
		if (_editorInteraction == null) {
			return;
		}

		IDocument editDocument = _editorInteraction.getHtmlDocumentProvider().getHtmlEditDocument();
		String documentContents = editDocument.get();

		try {
			WodParserCache cache = ((TemplateEditor) _editorInteraction.getHtmlDocumentProvider()).getSourceEditor().getParserCache();
			FuzzyXMLDocument htmlDocument = cache.getHtmlXmlDocument();
			RenderContext renderContext = new RenderContext(true);
			renderContext.setDelegate(new PreviewRenderDelegate(cache));
			FuzzyXMLElement documentElement = htmlDocument.getDocumentElement();
			documentContents = documentElement.toXMLString(renderContext);
		} catch (Exception e) {
			e.printStackTrace();
		}

		boolean rendered = _browser.setText(documentContents);
		if (!rendered) {
			HtmlPreviewPlugin.getDefault().log("Can't create preview of component HTML.");
		}
	}

	public void initEditorInteraction(EditorInteraction editorInteraction) {
		this._editorInteraction = editorInteraction;
	}

	public IEditorInput getEditorInput() {
		return _input;
	}

	public IEditorSite getEditorSite() {
		return _site;
	}

	public void init(IEditorSite initSite, IEditorInput initInput) throws PartInitException {
		this._site = initSite;
		this._input = initInput;
	}

	public void addPropertyListener(IPropertyListener listener) {
		// do nothing
	}

	public void createPartControl(Composite parent) {
		_browser = new Browser(parent, SWT.READ_ONLY);

	}

	public IWorkbenchPartSite getSite() {
		return _site;
	}

	public String getTitle() {
		return null;
	}

	public Image getTitleImage() {
		return null;
	}

	public String getTitleToolTip() {
		return null;
	}

	public void removePropertyListener(IPropertyListener listener) {
		// do nothing
	}

	public Object getAdapter(Class adapter) {
		return null;
	}

	public void doSave(IProgressMonitor monitor) {
		// do nothing
	}

	public void doSaveAs() {
		// do nothing
	}

	public boolean isDirty() {
		return false;
	}

	public boolean isSaveAsAllowed() {
		return false;
	}

	public boolean isSaveOnCloseNeeded() {
		return false;
	}

	public void dispose() {
		_browser.dispose();
	}

	public void setFocus() {
		_browser.setFocus();
	}

	public void editorSelected() {
		updatePreviewContent();
	}

	public EditorInteraction getEditorInteraction() {
		return _editorInteraction;
	}
	
	
}