package org.objectstyle.wolips.wodclipse.core.refactoring;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

import jp.aonir.fuzzyxml.FuzzyXMLDocument;
import jp.aonir.fuzzyxml.FuzzyXMLElement;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;
import org.objectstyle.wolips.bindings.wod.IWodElement;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;
import org.objectstyle.wolips.wodclipse.core.util.WodDocumentUtils;

public class ChangeElementTypeRefactoring implements IRunnableWithProgress {
  private IWodElement _element;
  private String _newType;
  private WodParserCache _cache;

  public ChangeElementTypeRefactoring(String newType, IWodElement element, WodParserCache cache) {
    _newType = newType;
    _element = element;
    _cache = cache;
  }

  public void run(IProgressMonitor monitor) throws InvocationTargetException {
    try {
      Position typePosition = _element.getElementTypePosition();
      if (_element.isTemporary()) {
        IDocument htmlDocument = _cache.getHtmlDocument();
        if (htmlDocument != null) {
          List<TextEdit> htmlEdits = new LinkedList<TextEdit>();
          FuzzyXMLDocument xmlDocument = _cache.getHtmlXmlDocument();
          if (xmlDocument != null) {
            FuzzyXMLElement xmlElement = xmlDocument.getElementByOffset(typePosition.getOffset());
            if (xmlElement != null && xmlElement.hasCloseTag()) {
              htmlEdits.add(new ReplaceEdit(xmlElement.getCloseTagOffset() + xmlElement.getCloseNameOffset() + 1, xmlElement.getCloseNameLength(), "wo:" + _newType));
            }
          }
          htmlEdits.add(new ReplaceEdit(typePosition.getOffset(), typePosition.getLength(), _newType));
          WodDocumentUtils.applyEdits(htmlDocument, htmlEdits);
        }
      }
      else {
        IDocument wodDocument = _cache.getWodDocument();
        if (wodDocument != null) {
          List<TextEdit> wodEdits = new LinkedList<TextEdit>();
          wodEdits.add(new ReplaceEdit(typePosition.getOffset(), typePosition.getLength(), _newType));
          WodDocumentUtils.applyEdits(wodDocument, wodEdits);
        }
      }
    }
    catch (Exception e) {
      throw new InvocationTargetException(e, "Failed to refactor.");
    }
  }

  public static void run(String newType, IWodElement element, WodParserCache cache, IProgressMonitor progressMonitor) throws CoreException, InvocationTargetException, InterruptedException {
    TemplateRefactoring.processHtmlAndWod(new ChangeElementTypeRefactoring(newType, element, cache), cache, progressMonitor);
  }
}
