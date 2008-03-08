package org.objectstyle.wolips.wodclipse.core.document;

import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.custom.StyledText;

public interface ITextWOEditor extends IWOEditor {
  public ISourceViewer getWOSourceViewer();

  public StyledText getWOEditorControl();
}
