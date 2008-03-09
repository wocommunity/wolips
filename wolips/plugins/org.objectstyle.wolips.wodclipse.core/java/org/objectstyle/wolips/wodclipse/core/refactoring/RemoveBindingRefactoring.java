package org.objectstyle.wolips.wodclipse.core.refactoring;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.TextEdit;
import org.objectstyle.wolips.bindings.wod.IWodBinding;
import org.objectstyle.wolips.bindings.wod.IWodElement;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;
import org.objectstyle.wolips.wodclipse.core.util.WodDocumentUtils;

public class RemoveBindingRefactoring implements IRunnableWithProgress {
  private IWodElement _element;
  private IWodBinding _binding;
  private WodParserCache _cache;

  public RemoveBindingRefactoring(IWodElement element, IWodBinding binding, WodParserCache cache) {
    _element = element;
    _binding = binding;
    _cache = cache;
  }

  public void run(IProgressMonitor monitor) throws InvocationTargetException {
    try {
      _cache.clearCache();

      Position valuePosition = _binding.getValuePosition();
      if (valuePosition != null) {
        int startOffset = _binding.getStartOffset();
        int endOffset = _binding.getEndOffset();
        if (_element.isInline()) {
          IDocument htmlDocument = _cache.getHtmlEntry().getDocument();
          if (htmlDocument != null) {
            List<TextEdit> htmlEdits = new LinkedList<TextEdit>();
            htmlEdits.add(new DeleteEdit(startOffset - 1, endOffset - startOffset + 2));
            WodDocumentUtils.applyEdits(htmlDocument, htmlEdits);
          }
        }
        else {
          IDocument wodDocument = _cache.getWodEntry().getDocument();
          if (wodDocument != null) {
            List<TextEdit> wodEdits = new LinkedList<TextEdit>();
            wodEdits.add(new DeleteEdit(startOffset, endOffset - startOffset + 1));
            WodDocumentUtils.applyEdits(wodDocument, wodEdits);
          }
        }
      }
    }
    catch (Exception e) {
      throw new InvocationTargetException(e, "Failed to refactor.");
    }
  }

  public static void run(IWodElement element, IWodBinding binding, WodParserCache cache, IProgressMonitor progressMonitor) throws CoreException, InvocationTargetException, InterruptedException {
    TemplateRefactoring.processHtmlAndWod(new RemoveBindingRefactoring(element, binding, cache), cache, progressMonitor);
  }
}
