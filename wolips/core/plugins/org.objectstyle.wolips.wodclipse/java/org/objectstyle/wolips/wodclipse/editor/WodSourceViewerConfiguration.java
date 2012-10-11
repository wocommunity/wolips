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
package org.objectstyle.wolips.wodclipse.editor;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.reconciler.MonoReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.objectstyle.wolips.wodclipse.core.parser.WodScanner;

/**
 * @author mike
 */
public class WodSourceViewerConfiguration extends SourceViewerConfiguration {
	private WodScanner myScanner;

	private WodEditor myEditor;

	private MonoReconciler myReconciler;

	private PresentationReconciler myPresentationReconciler;

	private ContentAssistant myContentAssistant;

	public WodSourceViewerConfiguration(WodEditor _editor) {
		myEditor = _editor;
	}

	protected WodScanner getWODScanner() {
		if (myScanner == null) {
			myScanner = WodScanner.newWODScanner();
		}
		return myScanner;
	}

	public IAnnotationHover getAnnotationHover(ISourceViewer _sourceViewer) {
		return new WodAnnotationHover(_sourceViewer.getAnnotationModel());
	}

	public IReconciler getReconciler(ISourceViewer _sourceViewer) {
		if (myReconciler == null) {
			// WodReconcilingStrategy reconcilingStrategy = new
			// WodReconcilingStrategy(myEditor);
			// myReconciler = new MonoReconciler(reconcilingStrategy, false);
		}
		return myReconciler;
	}

	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceView) {
		if (myPresentationReconciler == null) {
			myPresentationReconciler = new PresentationReconciler();
			DefaultDamagerRepairer dr = new DefaultDamagerRepairer(getWODScanner());
			myPresentationReconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
			myPresentationReconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);
		}
		return myPresentationReconciler;
	}

	public IContentAssistant getContentAssistant(ISourceViewer _sourceViewer) {
		if (myContentAssistant == null) {
			WodCompletionProcessor completionProcessor = new WodCompletionProcessor(myEditor);
			myContentAssistant = new ContentAssistant();
			myContentAssistant.setContentAssistProcessor(completionProcessor, IDocument.DEFAULT_CONTENT_TYPE);
			myContentAssistant.enableAutoActivation(true);
			myContentAssistant.setAutoActivationDelay(500);
			myContentAssistant.enableAutoInsert(true);
			myContentAssistant.setProposalPopupOrientation(IContentAssistant.PROPOSAL_OVERLAY);
			myContentAssistant.enableColoredLabels(true);
		}
		return myContentAssistant;
	}

	public IHyperlinkDetector[] getHyperlinkDetectors(ISourceViewer sourceViewer) {
		IHyperlinkDetector[] detectors = super.getHyperlinkDetectors(sourceViewer);
		IHyperlinkDetector[] result = new IHyperlinkDetector[detectors.length + 1];
		System.arraycopy(detectors, 0, result, 0, detectors.length);
		result[result.length - 1] = new WodElementHyperlinkDetector(myEditor);
		return result;
	}
}