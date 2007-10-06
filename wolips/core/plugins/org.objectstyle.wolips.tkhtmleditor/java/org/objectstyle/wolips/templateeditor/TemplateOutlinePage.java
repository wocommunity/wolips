package org.objectstyle.wolips.templateeditor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.aonir.fuzzyxml.FuzzyXMLDocument;
import jp.aonir.fuzzyxml.FuzzyXMLElement;
import jp.aonir.fuzzyxml.FuzzyXMLNode;
import jp.aonir.fuzzyxml.FuzzyXMLParser;
import jp.aonir.fuzzyxml.FuzzyXMLText;
import jp.aonir.fuzzyxml.internal.RenderContext;

import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.AnnotationModelEvent;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModelListener;
import org.eclipse.jface.text.source.IAnnotationModelListenerExtension;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.StatusTextEvent;
import org.eclipse.swt.browser.StatusTextListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.part.Page;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.objectstyle.wolips.wodclipse.core.Activator;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;
import org.objectstyle.wolips.wodclipse.core.model.IWodBinding;
import org.objectstyle.wolips.wodclipse.core.model.IWodElement;
import org.objectstyle.wolips.wodclipse.core.preferences.PreferenceConstants;
import org.objectstyle.wolips.wodclipse.core.util.WodHtmlUtils;

import tk.eclipse.plugin.htmleditor.HTMLPlugin;
import tk.eclipse.plugin.htmleditor.HTMLUtil;
import tk.eclipse.plugin.htmleditor.editors.IHTMLOutlinePage;

/**
 * An implementaion of IContentOutlinePage for the HTML editor.
 * This shows the outline of HTML document.
 */
public class TemplateOutlinePage extends Page implements IContentOutlinePage, IHTMLOutlinePage, StatusTextListener, IAnnotationModelListener, IAnnotationModelListenerExtension {
  private static final String COLLAPSE_STRING = "&ndash;";
  private static final String EXPAND_STRING = "+";

  private TemplateSourceEditor _editor;
  private FuzzyXMLDocument _doc;

  private Browser _browser;
  private int _counter;
  private Map<String, FuzzyXMLNode> _idToNodeMap;
  private Map<FuzzyXMLNode, String> _nodeToIDMap;

  private List<ISelectionChangedListener> _selectionChangedListeners;
  private ISelection _selection;

  private Set<String> _collapsedIDs;

  public TemplateOutlinePage(TemplateSourceEditor editor) {
    _editor = editor;
    _selectionChangedListeners = new LinkedList<ISelectionChangedListener>();
    _collapsedIDs = new HashSet<String>();
  }

  public FuzzyXMLDocument getDoc() {
    return _doc;
  }

  @Override
  public void createControl(Composite parent) {
    _browser = new Browser(parent, SWT.NONE);
    _browser.addStatusTextListener(this);

    update();
  }

  protected boolean isHTML() {
    return true;
  }

  protected FuzzyXMLParser createParser() {
    FuzzyXMLParser parser = new FuzzyXMLParser(isHTML());
    return parser;
  }

  @Override
  public Control getControl() {
    return _browser;
  }

  @Override
  public void setFocus() {
    _browser.setFocus();
  }

  /**
   * The protocol that we use to communicate between the Browser preview component
   * and WOLips is via the Browser status text.  Changing status text in Javascript
   * fires an SWT event of the form 'command:target' that we parse on the Java side
   * to determine what action the user performed inside the Browser.  This method
   * handles each of the corresponding event types.
   * 
   * @param event the status text event
   */
  @SuppressWarnings("unchecked")
  public void changed(StatusTextEvent event) {
    String text = event.text;
    int colonIndex = text.indexOf(':');
    String command = text.substring(0, colonIndex);
    String target = text.substring(colonIndex + 1);
    if ("select".equals(command)) {
      outlineNodeSelected(target);
    }
    else if ("expand".equals(command)) {
      outlineNodeExpanded(target);
    }
    else if ("collapse".equals(command)) {
      outlineNodeCollapsed(target);
    }
  }

  /**
   * Called when the user collapses an outline node
   * 
   * @param target the id of the target node
   */
  public void outlineNodeCollapsed(String target) {
    if (!_collapsedIDs.contains(target)) {
      _collapsedIDs.add(target);

      FuzzyXMLNode selectedNode = _idToNodeMap.get(target);
      ProjectionAnnotationModel model = ((ProjectionViewer) _editor.getViewer()).getProjectionAnnotationModel();
      ProjectionAnnotation lastAnnotation = getAnnotationForNode(selectedNode, model);
      if (lastAnnotation != null) {
        model.collapse(lastAnnotation);
      }
    }
  }

  /**
   * Called when the user expands an outline node
   * 
   * @param target the id of the target node
   */
  public void outlineNodeExpanded(String target) {
    if (_collapsedIDs.contains(target)) {
      _collapsedIDs.remove(target);

      FuzzyXMLNode selectedNode = _idToNodeMap.get(target);
      ProjectionAnnotationModel model = ((ProjectionViewer) _editor.getViewer()).getProjectionAnnotationModel();
      ProjectionAnnotation lastAnnotation = getAnnotationForNode(selectedNode, model);
      if (lastAnnotation != null) {
        model.expand(lastAnnotation);
      }
    }
  }

  /**
   * Called when the user clicks on an outline node.
   * 
   * @param target the id of the target node
   */
  public void outlineNodeSelected(String target) {
    FuzzyXMLNode selectedNode = _idToNodeMap.get(target);
    _selection = new StructuredSelection(selectedNode);
    SelectionChangedEvent selectionChangedEvent = new SelectionChangedEvent(this, _selection);
    for (ISelectionChangedListener listener : _selectionChangedListeners) {
      listener.selectionChanged(selectionChangedEvent);
    }
    _editor.selectAndReveal(selectedNode.getOffset(), selectedNode.getLength());
    _editor.getViewer().getTextWidget().setFocus();
  }

  /**
   * Returns the annotation whose position is equal the given node's offset.
   * 
   * @param node the node to lookup annotations for
   * @param model the annotation model
   * @return the matching annotation (or null if not found) 
   */
  @SuppressWarnings("unchecked")
  protected ProjectionAnnotation getAnnotationForNode(FuzzyXMLNode node, ProjectionAnnotationModel model) {
    ProjectionAnnotation matchingAnnotation = null;
    if (model != null) {
      int index = node.getOffset();
      Iterator<ProjectionAnnotation> annotationsIter = model.getAnnotationIterator();
      while (annotationsIter.hasNext()) {
        ProjectionAnnotation annotation = annotationsIter.next();
        if (model.getPosition(annotation).getOffset() == index) {
          matchingAnnotation = annotation;
        }
      }
    }
    return matchingAnnotation;
  }

  /**
   * Called when the projection annotation model changes.  This allows
   * us to sync expanding/collapsing of editor folding with expanding/collapsing
   * of the outline view.
   * 
   * @param event the event
   */
  public void modelChanged(AnnotationModelEvent event) {
    IAnnotationModel model = event.getAnnotationModel();
    Annotation[] annotations = event.getChangedAnnotations();
    if (annotations != null) {
      for (Annotation annotation : annotations) {
        if (annotation instanceof ProjectionAnnotation) {
          ProjectionAnnotation projectionAnnotation = (ProjectionAnnotation) annotation;
          Position annotationPosition = model.getPosition(annotation);
          FuzzyXMLNode node = _doc.getElementByOffset(annotationPosition.getOffset());
          String nodeID = _nodeToIDMap.get(node);
          if (nodeID != null) {
            if (projectionAnnotation.isCollapsed()) {
              _browser.execute("collapse('" + nodeID + "');");
            }
            else {
              _browser.execute("expand('" + nodeID + "');");
            }
            _browser.execute("scroll(window.pageXOffset, window.pageYOffset)");
          }
        }
      }
    }
  }

  public void modelChanged(IAnnotationModel model) {
    // DO NOTHING
  }

  public void addSelectionChangedListener(ISelectionChangedListener listener) {
    _selectionChangedListeners.add(listener);
  }

  public ISelection getSelection() {
    return _selection;
  }

  public void removeSelectionChangedListener(ISelectionChangedListener listener) {
    _selectionChangedListeners.remove(listener);
  }

  public void setSelection(ISelection selection) {
    System.out.println("TemplateOutlinePage.setSelection: " + selection);
  }

  @Override
  public void dispose() {
    _nodeToIDMap.clear();
    _idToNodeMap.clear();
    _collapsedIDs.clear();
    _selectionChangedListeners.clear();
    super.dispose();
  }

  /**
   * Called when the document changes.
   */
  public void update() {
    if (getControl() == null || getControl().isDisposed()) {
      return;
    }

    ProjectionAnnotationModel model = ((ProjectionViewer) _editor.getViewer()).getProjectionAnnotationModel();
    model.removeAnnotationModelListener(this);
    model.addAnnotationModelListener(this);

    try {
      _doc = createParser().parse(_editor.getHTMLSource());
    }
    catch (Exception e) {
      e.printStackTrace();
    }

    _counter = 0;
    _idToNodeMap = new HashMap<String, FuzzyXMLNode>();
    _nodeToIDMap = new HashMap<FuzzyXMLNode, String>();

    try {
      WodParserCache cache = _editor.getParserCache();
      RenderContext renderContext = new RenderContext(true);
      FuzzyXMLElement documentElement = _doc.getDocumentElement();
      StringBuffer documentContentsBuffer = new StringBuffer();
      renderHeader(documentContentsBuffer);
      renderElement(documentElement, renderContext, documentContentsBuffer, cache);
      renderFooter(documentContentsBuffer);
      
      String documentContents = documentContentsBuffer.toString();

      boolean rendered = _browser.setText(documentContents);
      if (!rendered) {
        HTMLPlugin.logError("Can't create preview of component HTML.");
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Renders the page header and embedded CSS.
   * 
   * @param renderBuffer the render buffer
   */
  protected void renderHeader(StringBuffer renderBuffer) {
    renderBuffer.append("<html><body>");
    renderBuffer.append("<style>");
    renderBuffer.append("body { font-family: Helvetica; font-size: 8pt; }");

    renderBuffer.append("a { text-decoration: none; }");

    renderBuffer.append("div.element { overflow: hidden; margin-top: 5px; margin-bottom: 10px; margin-right: 0px; padding: 0px; border: 1px solid rgb(230, 230, 230); border-right: none; }");
    renderBuffer.append("div.element.wo { border: 1px solid rgb(200, 200, 255); border-right: none; }");
    renderBuffer.append("div.element.document { margin: 0px; padding: 0px; border: none; }");

    renderBuffer.append("div.element div.expandcollapse { cursor: pointer; float: right; background-color: rgb(255, 255, 255); width: 10px; border: 1px solid rgb(230, 230, 230); border-top: none; padding-left: 3px; padding-right: 3px; text-align: center; }");
    renderBuffer.append("div.element div.expandcollapse:hover { font-weight: bold; border-width: 2px; border-right-width: 1px; }");
    renderBuffer.append("div.element div.expandcollapse:active { font-weight: bold; border-width: 2px; border-right-width: 1px; background-color: rgb(230, 230, 230); }");
    renderBuffer.append("div.element.wo div.expandcollapse { border-color: rgb(200, 200, 255); }");
    renderBuffer.append("div.element.wo div.expandcollapse:active { background-color: rgb(200, 200, 255); }");
    renderBuffer.append("div.element.document > div.expandcollapse { display: none; }");

    renderBuffer.append("div.element div.summary { cursor: pointer; white-space: nowrap; background-color: rgb(240, 240, 240); padding: 3px; }");
    renderBuffer.append("div.element.wo div.summary { background-color: rgb(240, 240, 255); }");
    renderBuffer.append("div.element.document div.summary { margin: 0px; padding: 0px; border: none; display: none; }");

    renderBuffer.append("div.element div.summary div.title { font-weight: bold; }");
    renderBuffer.append("div.element div.summary div.title span.type { font-size: 0.80em; color: rgb(150, 150, 150); }");
    renderBuffer.append("div.element div.summary div.title.nonwo { color: rgb(180, 180, 180); }");
    renderBuffer.append("div.element div.summary div.title.missing { font-style: italic; }");
    renderBuffer.append("div.element div.summary div.title.nonwo span.className { font-weight: bold; color: rgb(120, 120, 200); padding-left: 20px; display: none; }");

    renderBuffer.append("div.element div.summary table.bindings { font-family: Helvetica; font-size: 8pt; margin: 0px; padding: 0px; }");
    renderBuffer.append("div.element div.summary table.bindings th { text-align: right; font-weight: normal; color: rgb(220, 0, 0); padding-right: 3px; }");
    renderBuffer.append("div.element div.summary table.bindings td { color: rgb(150, 0, 0); }");
    renderBuffer.append("div.element div.summary table.bindings td.literal { color: rgb(0, 0, 150); }");

    renderBuffer.append("div.element div.contents { background-color: rgb(255, 255, 255); padding-left: 10px; padding-right: 0px; padding-top: 5px; padding-bottom: 5px; border-top: 1px solid rgb(230, 230, 230); }");
    renderBuffer.append("div.element.document div.contents { margin: 0px; padding: 0px; border: none; }");
    renderBuffer.append("div.element.wo div.contents { background-color: rgb(250, 250, 255); border-color: rgb(200, 200, 255); }");

    renderBuffer.append("div.element div.summary:hover { border-color: rgb(210, 210, 210); }");
    renderBuffer.append("div.element div.summary:hover { background-color: rgb(220, 220, 220); }");
    renderBuffer.append("div.element.wo div.summary:hover { background-color: rgb(220, 220, 245); }");
    renderBuffer.append("div.element.wo div.summary:hover { border-color: rgb(170, 170, 225); }");

    renderBuffer.append("div.text { display: inline; }");

    renderBuffer.append("</style>");
    renderBuffer.append("<script>");
    renderBuffer.append("function expandCollapse(id) { if ('none' == document.getElementById(id + '_contents').style.display) { expand(id); } else { collapse(id); } }");
    renderBuffer.append("function expand(id) { document.getElementById(id + '_contents').style.display = 'block'; document.getElementById(id + '_toggle').innerHTML = '" + TemplateOutlinePage.COLLAPSE_STRING + "'; window.status = 'expand:' + id; }");
    renderBuffer.append("function collapse(id) { document.getElementById(id + '_contents').style.display = 'none'; document.getElementById(id + '_toggle').innerHTML = '" + TemplateOutlinePage.EXPAND_STRING + "'; window.status = 'collapse:' + id; }");
    renderBuffer.append("</script>");
  }

  /**
   * Renders the page footer.
   * 
   * @param renderBuffer the render buffer
   */
  protected void renderFooter(StringBuffer renderBuffer) {
    renderBuffer.append("</body></html>");
  }
  
  /**
   * All the magic happens here.  This method renders a single document node (and recursively calls itself).
   * 
   * @param node the node to render
   * @param renderContext the current render context
   * @param renderBuffer the render buffer
   * @param cache the WodParserCache for the current editor
   */
  protected void renderElement(FuzzyXMLNode node, RenderContext renderContext, StringBuffer renderBuffer, WodParserCache cache) {
    String nodeID = "node" + (_counter++);
    _idToNodeMap.put(nodeID, node);
    _nodeToIDMap.put(node, nodeID);
    if (node instanceof FuzzyXMLElement) {
      FuzzyXMLElement element = (FuzzyXMLElement) node;
      boolean empty = element.isEmpty();
      String nodeName = element.getName();
      String className = "element " + nodeName.replace(':', '_');

      boolean woTag = WodHtmlUtils.isWOTag(nodeName);

      if (woTag) {
        className = className + " wo";
      }

      //renderBuffer.append("<div id = \"" + nodeID + "\" class = \"" + className + "\" onmouseover = \"window.status = 'over:" + nodeID + "';\" onmouseout = \"window.status = 'out:" + nodeID + "';\" >");
      renderBuffer.append("<div id = \"" + nodeID + "\" class = \"" + className + "\">");

      if (!empty) {
        renderBuffer.append("<div id = \"" + nodeID + "_toggle\" class = \"expandcollapse\" onclick = \"expandCollapse('" + nodeID + "')\">");
        if (_collapsedIDs.contains(nodeID)) {
          renderBuffer.append(TemplateOutlinePage.EXPAND_STRING);
        }
        else {
          renderBuffer.append(TemplateOutlinePage.COLLAPSE_STRING);
        }
        renderBuffer.append("</div>");
      }

      renderBuffer.append("<div class = \"summary\" onclick = \"window.status = 'select:" + nodeID + "'\">");

      if (woTag) {
        boolean wo54 = Activator.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.WO54_KEY);
        try {
          IWodElement wodElement = WodHtmlUtils.getOrCreateWodElement(element, wo54, cache);
          if (wodElement != null) {
            if (WodHtmlUtils.isInline(nodeName)) {
              renderBuffer.append("<div class = \"title\"><span class = \"nodeName\">" + wodElement.getElementType() + "</span></div>");
            }
            else {
              renderBuffer.append("<div class = \"title\"><span class = \"nodeName\">" + wodElement.getElementName() + "</span> : <span class = \"type\">" + wodElement.getElementType() + "</span></div>");
            }
            List<IWodBinding> wodBindings = wodElement.getBindings();
            if (wodBindings.size() > 0) {
              renderBuffer.append("<table class = \"bindings\">");
              for (IWodBinding wodBinding : wodBindings) {
                renderBuffer.append("<tr>");
                renderBuffer.append("<th>" + wodBinding.getName() + "</th>");
                String bindingClass;
                if (wodBinding.isKeyPath()) {
                  bindingClass = "keypath";
                }
                else {
                  bindingClass = "literal";
                }
                renderBuffer.append("<td class = \"" + bindingClass + "\">" + wodBinding.getValue() + "</td>");
                renderBuffer.append("</tr>");
              }
              renderBuffer.append("</table>");
            }
          }
          else {
            String missingName = element.getAttributeValue("name");
            if (missingName == null) {
              missingName = nodeName;
            }
            renderBuffer.append("<div class = \"title missing\">" + missingName + "</div>");
          }
        }
        catch (Throwable t) {
          // IGNORE
          t.printStackTrace();
        }
      }
      else {
        renderBuffer.append("<div class = \"title nonwo\"><span class = \"nodeName\">");
        renderBuffer.append(nodeName);
        renderBuffer.append("</span>");
        String elementClass = element.getAttributeValue("class");
        if (elementClass != null) {
          renderBuffer.append("<span class = \"className\">" + elementClass + "</span>");
        }
        renderBuffer.append("</div>");
      }

      renderBuffer.append("</div>");

      if ("script".equalsIgnoreCase(nodeName)) {
        // don't show script
      }
      else if ("style".equalsIgnoreCase(nodeName)) {
        // don't show style
      }
      else {
        if (!empty) {
          renderBuffer.append("<div id = \"" + nodeID + "_contents\" class = \"contents\"");
          if (_collapsedIDs.contains(nodeID)) {
            renderBuffer.append(" style = \"display: none\"");
          }
          renderBuffer.append(">");
          FuzzyXMLNode[] children = element.getChildren();
          for (FuzzyXMLNode child : children) {
            renderElement(child, renderContext, renderBuffer, cache);
          }
          renderBuffer.append("</div>");
        }
      }

      renderBuffer.append("</div>");
    }
    else {
      StringBuffer nodeBuffer = new StringBuffer();
      node.toXMLString(renderContext, nodeBuffer);
      String nodeStr = HTMLUtil.escapeHTML(nodeBuffer.toString());
      boolean isText = (node instanceof FuzzyXMLText);
      if (isText) {
        renderBuffer.append("<div class = \"text\">" + nodeStr + "</div>");
      }
      else {
        renderBuffer.append(nodeStr);
      }
    }
  }

}
