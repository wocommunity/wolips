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
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.ContentAssistAction;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.objectstyle.wolips.baseforplugins.util.Throttle;
import org.objectstyle.wolips.components.editor.EditorInteraction;
import org.objectstyle.wolips.components.editor.IEmbeddedEditor;
import org.objectstyle.wolips.components.editor.IWebobjectTagListener;
import org.objectstyle.wolips.components.editor.IWodDocumentProvider;
import org.objectstyle.wolips.locate.LocateException;
import org.objectstyle.wolips.locate.LocatePlugin;
import org.objectstyle.wolips.locate.result.LocalizedComponentsLocateResult;
import org.objectstyle.wolips.wodclipse.WodclipsePlugin;
import org.objectstyle.wolips.wodclipse.core.Activator;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;
import org.objectstyle.wolips.wodclipse.core.parser.ElementNameRule;
import org.objectstyle.wolips.wodclipse.core.parser.RulePosition;
import org.objectstyle.wolips.wodclipse.core.parser.WodScanner;

/**
 * @author mike
 * @author uli
 */
public class WodEditor extends TextEditor implements IEmbeddedEditor, IWebobjectTagListener, IWodDocumentProvider {
	private WodContentOutlinePage _contentOutlinePage;

	private IEditorInput _input;

	private LocalizedComponentsLocateResult _componentsLocateResults;

	private EditorInteraction _editorInteraction;

	private Throttle _wodOutlineUpdateThrottle;

	public WodEditor() {
		_wodOutlineUpdateThrottle = new Throttle(1000, new WodOutlineUpdater());
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
	protected void performRevert() {
		super.performRevert();
		updateValidation();
	}

	@Override
	protected void performSaveAs(IProgressMonitor progressMonitor) {
		super.performSaveAs(progressMonitor);
		updateValidation();
	}
	
	@Override
	protected void performSave(boolean overwrite, IProgressMonitor progressMonitor) {
		super.performSave(overwrite, progressMonitor);
		updateValidation();
	}

	protected void updateValidation() {
		try {
			ResourcesPlugin.getWorkspace().run(new IWorkspaceRunnable() {
				public void run(IProgressMonitor monitor) {
					try {
						IFileEditorInput input = (IFileEditorInput) getEditorInput();
						IFile inputFile = input.getFile();
						WodParserCache cache = WodParserCache.parser(inputFile);
						cache.parseHtmlAndWodIfNecessary();
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
		_wodOutlineUpdateThrottle.start();
	}

	public void dispose() {
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

	public void initEditorInteraction(EditorInteraction editorInteraction) {
		_editorInteraction = editorInteraction;
		_editorInteraction.setWebObjectTagListener(this);
		_editorInteraction.setWodDocumentProvider(this);
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

	public EditorInteraction getEditorInteraction() {
		return _editorInteraction;
	}

}
