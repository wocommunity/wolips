package org.objectstyle.wolips.wodclipse.core.refactoring;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.objectstyle.wolips.bindings.wod.IWodElement;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;

public class DeleteComponentRefactoring implements IRunnableWithProgress {
  private IWodElement _element;
  private WodParserCache _cache;

  public DeleteComponentRefactoring(IWodElement element, WodParserCache cache) {
    _element = element;
    _cache = cache;
  }

  public void run(IProgressMonitor monitor) throws InvocationTargetException {
    try {
//      Position namePosition = _binding.getNamePosition();
//      if (_element.isInline()) {
//        IDocument htmlDocument = _cache.getHtmlEntry().getDocument();
//        if (htmlDocument != null) {
//          List<TextEdit> htmlEdits = new LinkedList<TextEdit>();
//          htmlEdits.add(new ReplaceEdit(namePosition.getOffset(), namePosition.getLength(), _newName));
//          WodDocumentUtils.applyEdits(htmlDocument, htmlEdits);
//        }
//      }
//      else {
//        IDocument wodDocument = _cache.getWodEntry().getDocument();
//        if (wodDocument != null) {
//          List<TextEdit> wodEdits = new LinkedList<TextEdit>();
//          wodEdits.add(new ReplaceEdit(namePosition.getOffset(), namePosition.getLength(), _newName));
//          WodDocumentUtils.applyEdits(wodDocument, wodEdits);
//        }
//      }
    }
    catch (Exception e) {
      throw new InvocationTargetException(e, "Failed to refactor.");
    }
  }

  public static void run(IWodElement element, WodParserCache cache, IProgressMonitor progressMonitor) throws CoreException, InvocationTargetException, InterruptedException {
    TemplateRefactoring.processHtmlAndWod(new DeleteComponentRefactoring(element, cache), cache, progressMonitor);
  }
}
