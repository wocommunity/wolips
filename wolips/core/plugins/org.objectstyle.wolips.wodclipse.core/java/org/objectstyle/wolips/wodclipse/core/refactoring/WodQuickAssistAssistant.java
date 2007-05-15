package org.objectstyle.wolips.wodclipse.core.refactoring;

import org.eclipse.jface.text.quickassist.QuickAssistAssistant;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.texteditor.ITextEditor;

public class WodQuickAssistAssistant extends QuickAssistAssistant {
  private ITextEditor _htmlEditor;
  private ISourceViewer _htmlSourceViewer;

  public WodQuickAssistAssistant(ITextEditor htmlEditor) {
    _htmlEditor = htmlEditor;
  }

  @Override
  public void install(ISourceViewer sourceViewer) {
    super.install(sourceViewer);
    _htmlSourceViewer = sourceViewer;
  }

  @Override
  public void uninstall() {
    super.uninstall();
    _htmlSourceViewer = null;
  }
  
  @Override
  public String showPossibleQuickAssists() {
    // TODO Auto-generated method stub
    return super.showPossibleQuickAssists();
  }

}
