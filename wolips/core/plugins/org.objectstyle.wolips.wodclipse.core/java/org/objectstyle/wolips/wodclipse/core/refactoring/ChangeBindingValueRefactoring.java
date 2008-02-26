package org.objectstyle.wolips.wodclipse.core.refactoring;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;
import org.objectstyle.wolips.bindings.wod.IWodBinding;
import org.objectstyle.wolips.bindings.wod.IWodElement;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;
import org.objectstyle.wolips.wodclipse.core.util.WodDocumentUtils;

public class ChangeBindingValueRefactoring implements IRunnableWithProgress {
  private IWodElement _element;
  private IWodBinding _binding;
  private String _newValue;
  private WodParserCache _cache;

  public ChangeBindingValueRefactoring(String newValue, IWodElement element, IWodBinding binding, WodParserCache cache) {
    _newValue = newValue;
    _element = element;
    _binding = binding;
    _cache = cache;
  }

  public void run(IProgressMonitor monitor) throws InvocationTargetException {
    try {
      _cache.clearCache();

      Position valuePosition = _binding.getValuePosition();
      if (valuePosition == null) {
        if (_element.isTemporary()) {
          IDocument htmlDocument = _cache.getHtmlEntry().getDocument();
          if (htmlDocument != null) {
            List<TextEdit> htmlEdits = new LinkedList<TextEdit>();
            htmlEdits.add(new InsertEdit(_element.getEndOffset() + 1, " " + _binding.getName() + "=\"" + _newValue + "\""));
            WodDocumentUtils.applyEdits(htmlDocument, htmlEdits);
          }
        }
        else {
          IDocument wodDocument = _cache.getWodEntry().getDocument();
          if (wodDocument != null) {
            List<TextEdit> wodEdits = new LinkedList<TextEdit>();
            wodEdits.add(new InsertEdit(_element.getEndOffset(), "  " + _binding.getName() + " = " + _newValue +";\n"));
            WodDocumentUtils.applyEdits(wodDocument, wodEdits);
          }
        }
      }
      else {
        if (_element.isTemporary()) {
          IDocument htmlDocument = _cache.getHtmlEntry().getDocument();
          if (htmlDocument != null) {
            List<TextEdit> htmlEdits = new LinkedList<TextEdit>();
            htmlEdits.add(new ReplaceEdit(valuePosition.getOffset(), valuePosition.getLength(), _newValue));
            WodDocumentUtils.applyEdits(htmlDocument, htmlEdits);
          }
        }
        else {
          IDocument wodDocument = _cache.getWodEntry().getDocument();
          if (wodDocument != null) {
            List<TextEdit> wodEdits = new LinkedList<TextEdit>();
            wodEdits.add(new ReplaceEdit(valuePosition.getOffset(), valuePosition.getLength(), _newValue));
            WodDocumentUtils.applyEdits(wodDocument, wodEdits);
          }
        }
      }
    }
    catch (Exception e) {
      throw new InvocationTargetException(e, "Failed to refactor.");
    }
  }

  public static void run(String newValue, IWodElement element, IWodBinding binding, WodParserCache cache, IProgressMonitor progressMonitor) throws CoreException, InvocationTargetException, InterruptedException {
    TemplateRefactoring.processHtmlAndWod(new ChangeBindingValueRefactoring(newValue, element, binding, cache), cache, progressMonitor);
  }
}
