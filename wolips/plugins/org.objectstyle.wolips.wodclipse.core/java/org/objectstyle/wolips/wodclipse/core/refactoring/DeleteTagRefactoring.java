package org.objectstyle.wolips.wodclipse.core.refactoring;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

import jp.aonir.fuzzyxml.FuzzyXMLElement;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.TextEdit;
import org.objectstyle.wolips.bindings.wod.HtmlElementName;
import org.objectstyle.wolips.bindings.wod.IWodElement;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;
import org.objectstyle.wolips.wodclipse.core.util.WodDocumentUtils;
import org.objectstyle.wolips.wodclipse.core.util.WodHtmlUtils;

public class DeleteTagRefactoring implements IRunnableWithProgress {
  private boolean _wo54;
  private FuzzyXMLElement _element;
  private WodParserCache _cache;

  public DeleteTagRefactoring(FuzzyXMLElement element, boolean wo54, WodParserCache cache) {
    _wo54 = wo54;
    _element = element;
    _cache = cache;
  }

  public void run(IProgressMonitor monitor) throws InvocationTargetException {
    try {
      int referenceCount = 0;

      if (WodHtmlUtils.isWOTag(_element) && !WodHtmlUtils.isInline(_element)) {
        IWodElement wodElement = WodHtmlUtils.getWodElement(_element, _wo54, true, _cache);
        String elementName = wodElement.getElementName();
        List<HtmlElementName> htmlElementNames = _cache.getHtmlEntry().getHtmlElementCache().getHtmlElementNames(elementName);
        if (htmlElementNames != null) {
          referenceCount = htmlElementNames.size();
        }
        
        if (referenceCount == 1) {
          IDocument wodDocument = _cache.getWodEntry().getDocument();
          if (wodDocument != null) {
            List<TextEdit> wodEdits = new LinkedList<TextEdit>();
            wodEdits.add(new DeleteEdit(wodElement.getStartOffset(), wodElement.getEndOffset() - wodElement.getStartOffset() + 1));
            WodDocumentUtils.applyEdits(wodDocument, wodEdits);
          }
        }
      }

      IDocument htmlDocument = _cache.getHtmlEntry().getDocument();
      if (htmlDocument != null) {
        List<TextEdit> htmlEdits = new LinkedList<TextEdit>();
        htmlEdits.add(new DeleteEdit(_element.getOffset(), _element.getLength()));
        WodDocumentUtils.applyEdits(htmlDocument, htmlEdits);
      }
    }
    catch (Exception e) {
      throw new InvocationTargetException(e, "Failed to refactor.");
    }
  }

  public static void run(FuzzyXMLElement element, boolean wo54, WodParserCache cache, IProgressMonitor progressMonitor) throws CoreException, InvocationTargetException, InterruptedException {
    TemplateRefactoring.processHtmlAndWod(new DeleteTagRefactoring(element, wo54, cache), cache, progressMonitor);
  }
}
