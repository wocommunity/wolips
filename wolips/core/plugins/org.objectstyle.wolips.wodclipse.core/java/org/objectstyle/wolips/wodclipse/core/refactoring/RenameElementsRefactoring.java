package org.objectstyle.wolips.wodclipse.core.refactoring;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

import jp.aonir.fuzzyxml.FuzzyXMLAttribute;
import jp.aonir.fuzzyxml.FuzzyXMLDocument;
import jp.aonir.fuzzyxml.FuzzyXMLElement;
import jp.aonir.fuzzyxml.FuzzyXMLNode;
import jp.aonir.fuzzyxml.util.NodeSelectUtil;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;
import org.objectstyle.wolips.wodclipse.core.model.IWodElement;
import org.objectstyle.wolips.wodclipse.core.model.IWodModel;
import org.objectstyle.wolips.wodclipse.core.util.WodDocumentUtils;

public class RenameElementsRefactoring implements IRunnableWithProgress {
  private List<ElementRename> _renames;
  private WodParserCache _cache;

  public RenameElementsRefactoring(List<ElementRename> renames, WodParserCache cache) {
    _renames = renames;
    _cache = cache;
  }

  public void run(IProgressMonitor monitor) throws InvocationTargetException {
    try {
      IDocument htmlDocument = _cache.getHtmlDocument();
      if (htmlDocument != null) {
        List<TextEdit> htmlEdits = new LinkedList<TextEdit>();
        for (ElementRename rename : _renames) {
          FuzzyXMLDocument htmlModel = _cache.getHtmlXmlDocument();
          FuzzyXMLNode[] woTags = NodeSelectUtil.getNodeByFilter(htmlModel.getDocumentElement(), new NamedWebobjectTagFilter(rename.getOldName()));

          for (FuzzyXMLNode woTag : woTags) {
            FuzzyXMLElement woElement = (FuzzyXMLElement) woTag;
            FuzzyXMLAttribute woNameAttr = woElement.getAttributeNode("name");
            if (woNameAttr != null) {
              int offset = woElement.getOffset() + woNameAttr.getValueDataOffset() + 1;
              int length = woNameAttr.getValueDataLength();
              htmlEdits.add(new ReplaceEdit(offset, length, rename.getNewName()));
            }
          }
        }
        WodDocumentUtils.applyEdits(htmlDocument, htmlEdits);
      }

      IDocument wodDocument = _cache.getWodDocument();
      if (wodDocument != null) {
        IWodModel wodModel = _cache.getWodModel();
        List<TextEdit> wodEdits = new LinkedList<TextEdit>();
        MultiTextEdit multiEdit = new MultiTextEdit();
        for (ElementRename rename : _renames) {
          IWodElement wodElement = wodModel.getElementNamed(rename.getOldName());
          wodEdits.add(new ReplaceEdit(wodElement.getElementNamePosition().getOffset(), wodElement.getElementNamePosition().getLength(), rename.getNewName()));
        }
        WodDocumentUtils.applyEdits(wodDocument, wodEdits);
      }
    }
    catch (Exception e) {
      throw new InvocationTargetException(e, "Failed to refactor.");
    }
  }

  public static void run(String oldName, String newName, WodParserCache cache, IProgressMonitor progressMonitor) throws CoreException, InvocationTargetException, InterruptedException {
    List<ElementRename> renames = new LinkedList<ElementRename>();
    renames.add(new ElementRename(oldName, newName));
    TemplateRefactoring.processHtmlAndWod(new RenameElementsRefactoring(renames, cache), cache, progressMonitor);
  }

  public static void run(final List<ElementRename> renames, final WodParserCache cache, IProgressMonitor progressMonitor) throws InvocationTargetException, InterruptedException, CoreException {
    TemplateRefactoring.processHtmlAndWod(new RenameElementsRefactoring(renames, cache), cache, progressMonitor);
  }

}
