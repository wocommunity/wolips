package org.objectstyle.wolips.wodclipse.core.util;

import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentRewriteSession;
import org.eclipse.jface.text.DocumentRewriteSessionType;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension4;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.TextEdit;

public class WodDocumentUtils {
  public static void applyEdits(IDocument document, List<TextEdit> edits) throws MalformedTreeException, BadLocationException {
    IDocumentExtension4 doc4 = (IDocumentExtension4) document;
    DocumentRewriteSession rewriteSession = doc4.startRewriteSession(DocumentRewriteSessionType.UNRESTRICTED);
    try {
      MultiTextEdit multiEdit = new MultiTextEdit();
      for (TextEdit edit : edits) {
        multiEdit.addChild(edit);
      }
      multiEdit.apply(document);
    }
    finally {
      doc4.stopRewriteSession(rewriteSession);
    }

  }
}
