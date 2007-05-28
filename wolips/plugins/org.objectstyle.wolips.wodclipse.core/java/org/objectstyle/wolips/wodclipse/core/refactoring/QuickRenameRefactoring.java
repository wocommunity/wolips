package org.objectstyle.wolips.wodclipse.core.refactoring;

import java.io.IOException;

import jp.aonir.fuzzyxml.FuzzyXMLAttribute;
import jp.aonir.fuzzyxml.FuzzyXMLDocument;
import jp.aonir.fuzzyxml.FuzzyXMLElement;
import jp.aonir.fuzzyxml.FuzzyXMLNode;
import jp.aonir.fuzzyxml.util.NodeSelectUtil;

import org.eclipse.core.runtime.CoreException;
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
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;
import org.objectstyle.wolips.wodclipse.core.model.IWodElement;
import org.objectstyle.wolips.wodclipse.core.model.IWodModel;
import org.objectstyle.wolips.wodclipse.core.util.WodHtmlUtils;

public class QuickRenameRefactoring {
  public static void rename(int offset, ITextViewer htmlViewer, ITextViewer wodViewer, WodParserCache cache) throws BadLocationException, CoreException, IOException {
    FuzzyXMLDocument htmlModel = cache.getHtmlXmlDocument();
    FuzzyXMLElement element = htmlModel.getElementByOffset(offset);
    if (element != null) {
      String tagName = element.getName();
      if (tagName != null && element.getOffset() + element.getNameOffset() + 1 <= offset && element.getOffset() + element.getNameOffset() + element.getNameLength() >= offset) {
        QuickRenameRefactoring.renameTag(element, htmlViewer, cache);
      }
      else if (WodHtmlUtils.isWOTag(tagName)) {
        FuzzyXMLAttribute nameAttribute = element.getAttributeNode("name");
        String woElementName = nameAttribute.getValue();
        if (woElementName != null && element.getOffset() + nameAttribute.getValueDataOffset() + 1 <= offset && element.getOffset() + nameAttribute.getValueDataOffset() + nameAttribute.getValueDataLength() >= offset) {
          QuickRenameRefactoring.renameElement(woElementName, htmlViewer, wodViewer, cache);
        }
      }
    }
  }

  public static void renameTag(FuzzyXMLElement element, ITextViewer htmlViewer, final WodParserCache cache) throws BadLocationException {
    IDocument htmlDocument = htmlViewer.getDocument();
    LinkedModeModel.closeAllModels(htmlDocument);
    LinkedPositionGroup linkedGroup = new LinkedPositionGroup();
    
    linkedGroup.addPosition(new LinkedPosition(htmlDocument, element.getOffset() + element.getNameOffset() + 1, element.getNameLength(), 0));
    if (element.hasCloseTag()) {
      linkedGroup.addPosition(new LinkedPosition(htmlDocument, element.getCloseTagOffset() + element.getCloseNameOffset() + 1, element.getCloseNameLength(), 1));
    }
    
    QuickRenameRefactoring.enterLinkedMode(linkedGroup, cache, htmlViewer);
  }

  public static void renameElement(String woElementName, ITextViewer htmlViewer, ITextViewer wodViewer, final WodParserCache cache) throws BadLocationException, CoreException, IOException {
    FuzzyXMLDocument htmlModel = cache.getHtmlXmlDocument();
    FuzzyXMLNode[] woTags = NodeSelectUtil.getNodeByFilter(htmlModel.getDocumentElement(), new NamedWebobjectTagFilter(woElementName));

    IDocument htmlDocument = htmlViewer.getDocument();
    LinkedModeModel.closeAllModels(htmlDocument);
    LinkedPositionGroup linkedGroup = new LinkedPositionGroup();
    int sequence = 0;
    for (FuzzyXMLNode woTag : woTags) {
      FuzzyXMLElement woElement = (FuzzyXMLElement) woTag;
      FuzzyXMLAttribute woNameAttr = woElement.getAttributeNode("name");
      if (woNameAttr != null) {
        int offset = woElement.getOffset() + woNameAttr.getValueDataOffset() + 1;
        int length = woNameAttr.getValueDataLength();
        linkedGroup.addPosition(new LinkedPosition(htmlDocument, offset, length, sequence++));
      }
    }

    //LinkedPositionGroup wodGroup = new LinkedPositionGroup();
    IDocument wodDocument = wodViewer.getDocument();
    LinkedModeModel.closeAllModels(wodDocument);
    IWodModel wodModel = cache.getWodModel();
    IWodElement wodElement = wodModel.getElementNamed(woElementName);
    if (wodElement != null) {
      Position namePosition = wodElement.getElementNamePosition();
      if (namePosition != null) {
        linkedGroup.addPosition(new LinkedPosition(wodDocument, namePosition.getOffset(), namePosition.getLength(), sequence++));
      }
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
