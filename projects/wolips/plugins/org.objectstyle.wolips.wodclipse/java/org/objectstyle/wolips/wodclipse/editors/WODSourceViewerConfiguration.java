package org.objectstyle.wolips.wodclipse.editors;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.ui.texteditor.ITextEditor;

public class WODSourceViewerConfiguration extends SourceViewerConfiguration {
  private WODScanner myScanner;
  private ITextEditor myEditor;

  public WODSourceViewerConfiguration(ITextEditor _editor) {
    myEditor = _editor;
  }

  protected WODScanner getWODScanner() {
    if (myScanner == null) {
      myScanner = WODScanner.newWODScanner();
    }
    return myScanner;
  }

  public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceView) {
    PresentationReconciler reconciler = new PresentationReconciler();
    DefaultDamagerRepairer dr = new DefaultDamagerRepairer(getWODScanner());
    reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
    reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);
    return reconciler;
  }

  public IContentAssistant getContentAssistant(ISourceViewer _sourceViewer) {
    ContentAssistant assistance = new ContentAssistant();
    assistance.setContentAssistProcessor(new WODCompletionProcessor(myEditor), IDocument.DEFAULT_CONTENT_TYPE);
    assistance.enableAutoActivation(true);
    assistance.setAutoActivationDelay(500);
    assistance.setProposalPopupOrientation(IContentAssistant.PROPOSAL_OVERLAY);
    return assistance;
  }
}