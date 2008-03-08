package org.objectstyle.wolips.wodclipse.core.refactoring;

import jp.aonir.fuzzyxml.FuzzyXMLAttribute;
import jp.aonir.fuzzyxml.FuzzyXMLDocument;
import jp.aonir.fuzzyxml.FuzzyXMLElement;
import jp.aonir.fuzzyxml.FuzzyXMLNode;
import jp.aonir.fuzzyxml.util.NodeSelectUtil;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.link.ILinkedModeListener;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.link.LinkedModeUI;
import org.eclipse.jface.text.link.LinkedPosition;
import org.eclipse.jface.text.link.LinkedPositionGroup;
import org.eclipse.ui.texteditor.link.EditorLinkedModeUI;
import org.objectstyle.wolips.bindings.wod.IWodElement;
import org.objectstyle.wolips.bindings.wod.IWodModel;
import org.objectstyle.wolips.bindings.wod.IWodUnit;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;
import org.objectstyle.wolips.wodclipse.core.util.WodHtmlUtils;

public class QuickRenameRefactoring {
  public static void renameWodSelection(int offset, ITextViewer htmlViewer, ITextViewer wodViewer, WodParserCache cache) throws Exception {
    IWodModel wodModel = cache.getWodEntry().getModel();
    if (wodModel != null) {
      IWodUnit wodUnit = wodModel.getWodUnitAtIndex(offset);
      if (wodUnit != null && wodUnit instanceof IWodElement) {
        IWodElement wodElement = (IWodElement) wodUnit;
        Position elementNamePosition = wodElement.getElementNamePosition();
        if (elementNamePosition != null && elementNamePosition.includes(offset)) {
          String elementName = wodElement.getElementName();
          if (elementName != null) {
            QuickRenameRefactoring.renameElement(elementName, htmlViewer, wodViewer, cache, false);
          }
        }
      }
    }
  }

  public static void renameHtmlSelection(int offset, ITextViewer htmlViewer, ITextViewer wodViewer, WodParserCache cache) throws Exception {
    FuzzyXMLDocument htmlModel = cache.getHtmlEntry().getModel();
    FuzzyXMLElement element = htmlModel.getElementByOffset(offset);
    if (element != null) {
      String tagName = element.getName();
      if (tagName != null && element.getOffset() + element.getNameOffset() + 1 <= offset && element.getOffset() + element.getNameOffset() + element.getNameLength() >= offset) {
        QuickRenameRefactoring.renameHtmlTag(element, htmlViewer, cache);
      }
      else if (WodHtmlUtils.isWOTag(tagName)) {
        FuzzyXMLAttribute nameAttribute = element.getAttributeNode("name");
        String woElementName = nameAttribute.getValue();
        if (woElementName != null && element.getOffset() + nameAttribute.getValueDataOffset() + 1 <= offset && element.getOffset() + nameAttribute.getValueDataOffset() + nameAttribute.getValueDataLength() >= offset) {
          QuickRenameRefactoring.renameElement(woElementName, htmlViewer, wodViewer, cache, true);
        }
      }
    }
  }

  public static void renameHtmlTag(FuzzyXMLElement element, ITextViewer htmlViewer, final WodParserCache cache) throws BadLocationException {
    IDocument htmlDocument = htmlViewer.getDocument();
    LinkedModeModel.closeAllModels(htmlDocument);
    LinkedPositionGroup linkedGroup = new LinkedPositionGroup();

    linkedGroup.addPosition(new LinkedPosition(htmlDocument, element.getOffset() + element.getNameOffset() + 1, element.getNameLength(), 0));
    if (element.hasCloseTag()) {
      linkedGroup.addPosition(new LinkedPosition(htmlDocument, element.getCloseTagOffset() + element.getCloseNameOffset() + 1, element.getCloseNameLength(), 1));
    }

    QuickRenameRefactoring.enterLinkedMode(linkedGroup, cache, htmlViewer);
  }

  protected static int linkHtml(String woElementName, IDocument htmlDocument, LinkedPositionGroup linkedGroup, WodParserCache cache, int sequence) throws Exception {
    FuzzyXMLDocument htmlModel = cache.getHtmlEntry().getModel();
    FuzzyXMLNode[] woTags = NodeSelectUtil.getNodeByFilter(htmlModel.getDocumentElement(), new NamedWebobjectTagFilter(woElementName));
    LinkedModeModel.closeAllModels(htmlDocument);
    for (FuzzyXMLNode woTag : woTags) {
      FuzzyXMLElement woElement = (FuzzyXMLElement) woTag;
      FuzzyXMLAttribute woNameAttr = woElement.getAttributeNode("name");
      if (woNameAttr != null) {
        int offset = woElement.getOffset() + woNameAttr.getValueDataOffset() + 1;
        int length = woNameAttr.getValueDataLength();
        linkedGroup.addPosition(new LinkedPosition(htmlDocument, offset, length, sequence++));
      }
    }
    return sequence;
  }

  protected static int linkWod(String woElementName, IDocument wodDocument, LinkedPositionGroup linkedGroup, WodParserCache cache, int sequence) throws Exception {
    LinkedModeModel.closeAllModels(wodDocument);
    IWodModel wodModel = cache.getWodEntry().getModel();
    IWodElement wodElement = wodModel.getElementNamed(woElementName);
    if (wodElement != null) {
      Position namePosition = wodElement.getElementNamePosition();
      if (namePosition != null) {
        linkedGroup.addPosition(new LinkedPosition(wodDocument, namePosition.getOffset(), namePosition.getLength(), sequence++));
      }
    }
    return sequence;
  }

  public static void renameElement(String woElementName, ITextViewer htmlViewer, ITextViewer wodViewer, final WodParserCache cache, boolean highlightHtml) throws Exception {
    int sequence = 0;
    LinkedPositionGroup linkedGroup = new LinkedPositionGroup();
    IDocument htmlDocument = htmlViewer.getDocument();
    IDocument wodDocument = wodViewer.getDocument();
    if (highlightHtml) {
      sequence = QuickRenameRefactoring.linkHtml(woElementName, htmlDocument, linkedGroup, cache, sequence);
      sequence = QuickRenameRefactoring.linkWod(woElementName, wodDocument, linkedGroup, cache, sequence);
    }
    else {
      sequence = QuickRenameRefactoring.linkWod(woElementName, wodDocument, linkedGroup, cache, sequence);
      sequence = QuickRenameRefactoring.linkHtml(woElementName, htmlDocument, linkedGroup, cache, sequence);
    }
    QuickRenameRefactoring.enterLinkedMode(linkedGroup, cache, htmlViewer, wodViewer);
  }

  protected static void enterLinkedMode(LinkedPositionGroup linkedGroup, final WodParserCache cache, ITextViewer... textViewers) throws BadLocationException {
    if (!linkedGroup.isEmpty()) {
      LinkedModeModel linkedModeModel = new LinkedModeModel();
      linkedModeModel.addGroup(linkedGroup);
      linkedModeModel.forceInstall();

      //    JavaEditor editor = getJavaEditor();
      //    if (editor != null) {
      //      model.addLinkingListener(new EditorHighlightingSynchronizer(editor));
      //    }

      linkedModeModel.addLinkingListener(new ILinkedModeListener() {
        public void resume(LinkedModeModel model, int flags) {
          // DO NOTHING
        }

        public void suspend(LinkedModeModel model) {
          // DO NOTHING
        }

        public void left(LinkedModeModel model, int flags) {
          try {
            cache.clearCache();
          }
          catch (Exception e) {
            e.printStackTrace();
          }
        }
      });
      LinkedModeUI htmlUI = new EditorLinkedModeUI(linkedModeModel, textViewers);
      //    ui.setInitialOffset(offset);
      htmlUI.setExitPosition(textViewers[0], 0, 0, LinkedPositionGroup.NO_STOP);
      htmlUI.enter();
    }
  }
}
