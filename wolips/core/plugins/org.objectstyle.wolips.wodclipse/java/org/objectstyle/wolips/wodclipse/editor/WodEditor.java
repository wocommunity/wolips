/*
 * ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0
 * 
 * Copyright (c) 2005 - 2006 The ObjectStyle Group and individual authors of the
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
package org.objectstyle.wolips.wodclipse.editor;

import java.util.ResourceBundle;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.internal.ui.IJavaHelpContextIds;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.ContentAssistAction;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.objectstyle.wolips.baseforplugins.util.Throttle;
import org.objectstyle.wolips.bindings.wod.IWodElement;
import org.objectstyle.wolips.bindings.wod.IWodModel;
import org.objectstyle.wolips.components.editor.ComponentEditorInteraction;
import org.objectstyle.wolips.components.editor.IEmbeddedEditor;
import org.objectstyle.wolips.components.editor.IWebobjectTagListener;
import org.objectstyle.wolips.components.editor.IWodDocumentProvider;
import org.objectstyle.wolips.locate.LocateException;
import org.objectstyle.wolips.locate.LocatePlugin;
import org.objectstyle.wolips.locate.result.LocalizedComponentsLocateResult;
import org.objectstyle.wolips.wodclipse.WodclipsePlugin;
import org.objectstyle.wolips.wodclipse.core.Activator;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;
import org.objectstyle.wolips.wodclipse.core.document.ITextWOEditor;
import org.objectstyle.wolips.wodclipse.core.parser.ElementNameRule;
import org.objectstyle.wolips.wodclipse.core.parser.RulePosition;
import org.objectstyle.wolips.wodclipse.core.parser.WodScanner;
import org.objectstyle.wolips.wodclipse.core.util.CursorPositionSupport;
import org.objectstyle.wolips.wodclipse.core.util.ICursorPositionListener;
import org.objectstyle.wolips.wodclipse.core.util.WodModelUtils;

/**
 * @author mike
 * @author uli
 */
public class WodEditor extends TextEditor implements IEmbeddedEditor, IWebobjectTagListener, IWodDocumentProvider, ITextWOEditor {
	private WodParserCache _cache;

	private CursorPositionSupport _cursorPositionSupport;

	private WodContentOutlinePage _contentOutlinePage;

	private IEditorInput _input;

	private LocalizedComponentsLocateResult _componentsLocateResults;

	private ComponentEditorInteraction _editorInteraction;

	private Throttle _wodOutlineUpdateThrottle;

	public WodEditor() {
		_wodOutlineUpdateThrottle = new Throttle("WodOutline", 1000, new WodOutlineUpdater());
		_cursorPositionSupport = new CursorPositionSupport(this);
		setSourceViewerConfiguration(new WodSourceViewerConfiguration(this));
	}

	@Override
	public void doSave(IProgressMonitor progressMonitor) {
		super.doSave(progressMonitor);
	}

	@Override
	public void doSaveAs() {
		super.doSaveAs();
	}

	@Override
	protected void doSetInput(IEditorInput input) throws CoreException {
		super.doSetInput(input);
		_cache = null;
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		getSourceViewer().getTextWidget().getParent().setBackground(parent.getBackground());
	}

	@Override
	protected void performRevert() {
		super.performRevert();
		updateValidation();
		_editorInteraction.fireWebObjectChanged();
	}

	@Override
	protected void performSaveAs(IProgressMonitor progressMonitor) {
		super.performSaveAs(progressMonitor);
		updateValidation();
		_editorInteraction.fireWebObjectChanged();
	}

	@Override
	protected void performSave(boolean overwrite, IProgressMonitor progressMonitor) {
		super.performSave(overwrite, progressMonitor);
		updateValidation();
		_editorInteraction.fireWebObjectChanged();
	}

	public WodParserCache getParserCache() throws CoreException, LocateException {
		if (_cache == null) {
			IFileEditorInput input = (IFileEditorInput) getEditorInput();
			IFile inputFile = input.getFile();
			_cache = WodParserCache.parser(inputFile);
		}
		return _cache;
	}

	protected void updateValidation() {
		try {
			// resource.getWorkspace().run(r, null,IWorkspace.AVOID_UPDATE,
			// null);
			ResourcesPlugin.getWorkspace().run(new IWorkspaceRunnable() {
				public void run(IProgressMonitor monitor) {
					try {
						WodParserCache cache = getParserCache();
						cache.parse();
						cache.validate();
					} catch (Exception ex) {
						Activator.getDefault().log(ex);
					}
				}
			}, null);
		} catch (CoreException e) {
			Activator.getDefault().log(e);
		}
	}

	public synchronized void addCursorPositionListener(ICursorPositionListener listener) {
		_cursorPositionSupport.addCursorPositionListener(listener);
	}

	public synchronized void removeCursorPositionListener(ICursorPositionListener listener) {
		_cursorPositionSupport.removeCursorPositionListener(listener);
	}

	@Override
	protected void handleCursorPositionChanged() {
		super.handleCursorPositionChanged();
		_cursorPositionSupport.cursorPositionChanged(getViewer().getSelectedRange());
	}

	public ISourceViewer getViewer() {
		return getSourceViewer();
	}

	protected void initializeKeyBindingScopes() {
		setKeyBindingScopes(new String[] { "org.objectstyle.wolips.componenteditor.componentEditorScope" });
	}

	protected ISourceViewer createSourceViewer(Composite parent, IVerticalRuler verticalRuler, int styles) {
		return super.createSourceViewer(parent, verticalRuler, styles);
	}

	protected void createActions() {
		super.createActions();

		String BUNDLE_FOR_CONSTRUCTED_KEYS = "org.eclipse.jdt.internal.ui.javaeditor.ConstructedJavaEditorMessages";//$NON-NLS-1$
		ResourceBundle fgBundleForConstructedKeys = ResourceBundle.getBundle(BUNDLE_FOR_CONSTRUCTED_KEYS);
		// WodclipsePlugin.getDefault().getResourceBundle()
		ContentAssistAction action = new ContentAssistAction(fgBundleForConstructedKeys, "ContentAssistProposal.", this); //$NON-NLS-1$
		action.setActionDefinitionId(ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS);
		setAction("ContentAssistProposal", action);
		markAsStateDependentAction("ContentAssistProposal", true);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(action, IJavaHelpContextIds.CONTENT_ASSIST_ACTION);
	}

	public void updateWebObjectsTagNames() {
		// MS: Come back to this
		// try {
		// IDocument document =
		// getDocumentProvider().getDocument(getEditorInput());
		// Set elementNamesSet = WodScanner.getTextForRulesOfType(document,
		// ElementNameRule.class);
		// String[] elementNames = (String[]) elementNamesSet.toArray(new
		// String[elementNamesSet.size()]);
		// HtmleditorPlugin.getDefault().setWebObjectsTagNames(elementNames);
		// } catch (BadLocationException t) {
		// // null means no tags
		// // the user has to enter the name manually
		// HtmleditorPlugin.getDefault().setWebObjectsTagNames(null);
		// }
	}

	public Object getAdapter(Class adapter) {
		if (IContentOutlinePage.class.equals(adapter)) {
			if (_contentOutlinePage == null) {
				_contentOutlinePage = new WodContentOutlinePage(getDocumentProvider(), this);
				_contentOutlinePage.setInput(_input);
			}
			return _contentOutlinePage;
		}
		return super.getAdapter(adapter);
	}

	protected Throttle getWodOutlineUpdateThrottle() {
		return _wodOutlineUpdateThrottle;
	}

	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		super.init(site, input);
		_wodOutlineUpdateThrottle.stop();
		_input = input;
		IDocument document = getDocumentProvider().getDocument(getEditorInput());
		document.addDocumentListener(new IDocumentListener() {
			public void documentAboutToBeChanged(DocumentEvent event) {
				// Do nothing
			}

			public void documentChanged(DocumentEvent event) {
				getWodOutlineUpdateThrottle().ping();
			}
		});

		try {
			getParserCache().getWodEntry().setDocument(document);
		} catch (Exception e) {
			e.printStackTrace();
		}

		_wodOutlineUpdateThrottle.start();
	}

	public void dispose() {
		try {
			getParserCache().getWodEntry().setDocument(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		_wodOutlineUpdateThrottle.stop();
		super.dispose();
	}

	protected class WodOutlineUpdater implements Runnable {
		public void run() {
			IContentOutlinePage contentOutlinePage = (IContentOutlinePage) WodEditor.this.getAdapter(IContentOutlinePage.class);
			if (contentOutlinePage instanceof WodContentOutlinePage) {
				final WodContentOutlinePage wodContentOutlinePage = (WodContentOutlinePage) contentOutlinePage;
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						wodContentOutlinePage.update();
					}
				});
			}
		}
	}

	public LocalizedComponentsLocateResult getComponentsLocateResults() throws CoreException, LocateException {
		if (_componentsLocateResults == null) {
			_componentsLocateResults = LocatePlugin.getDefault().getLocalizedComponentsLocateResult(((IFileEditorInput) _input).getFile());
		}
		return _componentsLocateResults;
	}

	public void initEditorInteraction(ComponentEditorInteraction editorInteraction) {
		_editorInteraction = editorInteraction;
		_editorInteraction.addWebObjectTagListener(this);
		_editorInteraction.setWodDocumentProvider(this);
	}

	public void webObjectChanged() {
		// DO NOTHING
	}

	public void webObjectTagSelected(String name) {
		try {
			IDocument document = getDocumentProvider().getDocument(getEditorInput());
			WodScanner wodScanner = WodScanner.wodScannerForDocument(document);
			RulePosition elementNameRulePosition = wodScanner.firstRulePositionOfTypeWithText(ElementNameRule.class, name);
			if (elementNameRulePosition != null) {
				IRegion region = document.getLineInformationOfOffset(elementNameRulePosition.getTokenOffset());
				setHighlightRange(region.getOffset(), region.getLength(), true);
			}
		} catch (BadLocationException e) {
			WodclipsePlugin.getDefault().log(e);
		}
	}

	public IDocument getWodEditDocument() {
		IDocument document = getDocumentProvider().getDocument(getEditorInput());
		return document;
	}

	public ComponentEditorInteraction getEditorInteraction() {
		return _editorInteraction;
	}
	
	public ISourceViewer getWOSourceViewer() {
		return getViewer();
	}

	public StyledText getWOEditorControl() {
		return getViewer().getTextWidget();
	}

	public int getOffsetAtPoint(Point point) {
		StyledText st = getViewer().getTextWidget();
		int modelOffset;
		if (!st.getBounds().contains(point)) {
			modelOffset = -1;
		} else {
			try {
				int offset = st.getOffsetAtLocation(point);
				modelOffset = AbstractTextEditor.widgetOffset2ModelOffset(getSourceViewer(), offset);
			} catch (IllegalArgumentException e) {
				modelOffset = -1;
			}
		}
		return modelOffset;
	}

	public IWodModel getWodModel(boolean refreshModel) throws Exception {
		WodParserCache cache = getParserCache();
		IWodModel model;
		if (refreshModel || isDirty()) {
			model = WodModelUtils.createWodModel(cache.getWodEntry().getFile(), cache.getWodEntry().getDocument());
			cache.getWodEntry().setModel(model);
		} else {
			model = cache.getWodEntry().getModel();
		}
		return model;
	}

	public IWodElement getWodElementAtPoint(Point point, boolean resolveWodElement, boolean refreshModel) throws Exception {
		int offset = getOffsetAtPoint(point);
		IWodElement element = getWodModel(refreshModel).getWodElementAtIndex(offset);
		return element;
	}

	public IWodElement getSelectedElement(boolean resolveWodElement, boolean refreshModel) throws Exception {
		IWodElement element = null;
		ISelectionProvider selectionProvider = getSelectionProvider();
		if (selectionProvider != null) {
			ISelection selection = selectionProvider.getSelection();
			if (selection instanceof ITextSelection) {
				int offset = ((ITextSelection) selection).getOffset();
				element = getWodModel(refreshModel).getWodElementAtIndex(offset);
			}
		}
		return element;
	}
}
