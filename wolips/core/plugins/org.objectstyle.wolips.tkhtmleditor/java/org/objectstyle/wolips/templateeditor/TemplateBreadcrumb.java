package org.objectstyle.wolips.templateeditor;

import java.util.LinkedList;
import java.util.List;

import jp.aonir.fuzzyxml.FuzzyXMLDocument;
import jp.aonir.fuzzyxml.FuzzyXMLElement;
import jp.aonir.fuzzyxml.FuzzyXMLNode;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.editors.text.TextEditor;
import org.objectstyle.wolips.bindings.wod.IWodElement;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;
import org.objectstyle.wolips.wodclipse.core.document.WodElementTypeHyperlink;
import org.objectstyle.wolips.wodclipse.core.util.ICursorPositionListener;
import org.objectstyle.wolips.wodclipse.core.util.WodHtmlUtils;

public class TemplateBreadcrumb extends Composite implements ICursorPositionListener, MouseListener {
  private TemplateSourceEditor _editor;

  public TemplateBreadcrumb(TemplateSourceEditor editor, Composite parent, int style) {
    super(parent, style);

    setBackground(parent.getBackground());
    RowLayout breadcrumbLayout = new RowLayout();
    breadcrumbLayout.wrap = false;
    breadcrumbLayout.pack = true;
    breadcrumbLayout.justify = false;
    breadcrumbLayout.type = SWT.HORIZONTAL;
    breadcrumbLayout.marginLeft = 5;
    breadcrumbLayout.marginTop = 3;
    breadcrumbLayout.marginRight = 5;
    breadcrumbLayout.marginBottom = 3;
    breadcrumbLayout.spacing = 5;
    setLayout(breadcrumbLayout);

    _editor = editor;
    _editor.addCursorPositionListener(this);

    updateBreadcrumb();
  }

  public void cursorPositionChanged(TextEditor editor, Point selectionRange) {
    updateBreadcrumb();
  }

  public void updateBreadcrumb() {
    try {
      WodParserCache cache = _editor.getParserCache();
      Point selectionRange = _editor.getSelectedRange();

      FuzzyXMLElement element = null;
      FuzzyXMLDocument document = cache.getHtmlXmlDocument();
      if (document != null) {
        element = document.getElementByOffset(selectionRange.x);
      }

      Control[] children = getChildren();
      for (int i = children.length - 1; i >= 0; i--) {
        children[i].dispose();
      }

      if (element != null) {
        List<FuzzyXMLNode> elementStack = new LinkedList<FuzzyXMLNode>();
        FuzzyXMLNode currentNode = element;
        do {
          elementStack.add(currentNode);
          currentNode = currentNode.getParentNode();
        } while (currentNode != null);

        for (int i = elementStack.size() - 2; i >= 0; i--) {
          FuzzyXMLNode stackNode = elementStack.get(i);
          if (stackNode instanceof FuzzyXMLElement) {
            FuzzyXMLElement stackElement = (FuzzyXMLElement) stackNode;

            FuzzyXMLElementWithWodElement data;
            String tagName = stackElement.getName();
            boolean isWOTag = WodHtmlUtils.isWOTag(tagName);
            String displayName = null;
            if (isWOTag) {
              IWodElement wodElement = WodHtmlUtils.getWodElement(stackElement, false, cache);
              if (wodElement != null) {
                displayName = wodElement.getElementType();
              }
              data = new FuzzyXMLElementWithWodElement(stackElement, wodElement);
            }
            else {
              displayName = tagName;
              data = new FuzzyXMLElementWithWodElement(stackElement, null);
            }

            if (displayName == null) {
              displayName = "<unknown>";
            }

            Label nodeButton = new Label(this, SWT.NONE);
            nodeButton.setData(data);
            nodeButton.setBackground(getBackground());
            nodeButton.setText(displayName);
            nodeButton.addMouseListener(this);

            if (!isWOTag) {
              nodeButton.setForeground(getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
            }
          }

          if (i > 0) {
            Label arrowLabel = new Label(this, SWT.NONE);
            arrowLabel.setBackground(getBackground());
            arrowLabel.setForeground(getDisplay().getSystemColor(SWT.COLOR_GRAY));
            arrowLabel.setText(">");
          }
        }
        layout();
        getParent().layout();
      }
    }
    catch (Throwable t) {
      t.printStackTrace();
    }
  }

  public void mouseDoubleClick(MouseEvent event) {
    Label label = (Label) event.getSource();
    FuzzyXMLElementWithWodElement data = (FuzzyXMLElementWithWodElement) label.getData();
    if (data != null && data.getWodElement() != null) {
      try {
        WodElementTypeHyperlink.toElementTypeHyperlink(data.getWodElement(), _editor.getParserCache()).open();
      }
      catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public void mouseDown(MouseEvent event) {
    Label label = (Label) event.getSource();
    FuzzyXMLElementWithWodElement data = (FuzzyXMLElementWithWodElement) label.getData();
    if (data != null && data.getElement() != null) {
      FuzzyXMLElement element = data.getElement();
      int offset = element.getOffset();
      int length = element.getLength();
      _editor.selectAndReveal(offset, length);
    }
  }

  public void mouseUp(MouseEvent event) {
    // DO NOTHING
  }

  public static class FuzzyXMLElementWithWodElement {
    private FuzzyXMLElement _element;
    private IWodElement _wodElement;

    public FuzzyXMLElementWithWodElement(FuzzyXMLElement element, IWodElement wodElement) {
      _element = element;
      _wodElement = wodElement;
    }

    public FuzzyXMLElement getElement() {
      return _element;
    }

    public IWodElement getWodElement() {
      return _wodElement;
    }
  }
}
