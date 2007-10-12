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
import org.objectstyle.wolips.bindings.Activator;
import org.objectstyle.wolips.bindings.wod.IWodBinding;
import org.objectstyle.wolips.bindings.wod.IWodElement;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;
import org.objectstyle.wolips.wodclipse.core.util.WodHtmlUtils;

import tk.eclipse.plugin.htmleditor.HTMLPlugin;
import tk.eclipse.plugin.htmleditor.editors.IHTMLOutlinePage;

/**
 * An implementaion of IContentOutlinePage for the HTML editor.
 * This shows the outline of HTML document.
 */
public class TemplateOutlinePage extends Page implements IContentOutlinePage, IHTMLOutlinePage, StatusTextListener, IAnnotationModelListener, IAnnotationModelListenerExtension {
  private static final String COMPACT_VIEW_PREFERENCE_KEY = "org.objectstyle.wolips.templateEditor.compactView";
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
  private boolean _compactView;
  private int _lastPageOffset;

  public TemplateOutlinePage(TemplateSourceEditor editor) {
    _editor = editor;
    _selectionChangedListeners = new LinkedList<ISelectionChangedListener>();
    _collapsedIDs = new HashSet<String>();
    _compactView = HTMLPlugin.getDefault().getPreferenceStore().getBoolean(HTMLPlugin.PREF_TEMPLATE_COMPACT_VIEW);
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
    FuzzyXMLParser parser = new FuzzyXMLParser(Activator.getDefault().isWO54(), isHTML());
    return parser;
  }

  @Override
  public Control getControl() {
    return _browser;
  }

  @Override
  public void setFocus() {
    // MS: This might be a little bit weird on the interaction side.  We refresh
    // the view when it gets focus, to prevent you from clicking on something that
    // is now out of date.
    if (_editor.isDirty()) {
      update();
    }
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
    else if ("toggleCompact".equals(command)) {
      _compactView = !_compactView;
      HTMLPlugin.getDefault().getPreferenceStore().setValue(HTMLPlugin.PREF_TEMPLATE_COMPACT_VIEW, _compactView);
    }
    else if ("pageYOffset".equals(command)) {
      try {
        _lastPageOffset = Integer.parseInt(target);
      }
      catch (NumberFormatException e) {
        e.printStackTrace();
        _lastPageOffset = 0;
      }
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

      _browser.execute("updatePageYOffset()");
      if (_lastPageOffset > 0) {
        documentContentsBuffer.append("<script>scroll(100, " + _lastPageOffset + ");</script>");
      }

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
    renderBuffer.append("<html>\n");
    if (_compactView) {
      renderBuffer.append("<body id = \"outline\" class = \"compact\">\n");
    }
    else {
      renderBuffer.append("<body id = \"outline\" class = \"verbose\">\n");
    }
    renderBuffer.append("<style>\n");
    renderBuffer.append("body { font-family: Helvetica; font-size: 8pt; margin: 5px; margin-top: 2px; }\n");

    renderBuffer.append("a { text-decoration: none; }\n");

    renderBuffer.append("div.viewControls { right: 8px; margin: 0px; margin-bottom: 2px; text-align: right; }\n");
    renderBuffer.append("div.viewControls a { color: rgb(0, 0, 200); }\n");
    renderBuffer.append("div.elements { margin-top: 0px; }\n");

    renderBuffer.append("div.element { overflow: hidden; margin-top: 5px; margin-bottom: 5px; margin-right: 0px; padding: 0px; border: 1px solid rgb(230, 230, 230); border-right: none; }\n");
    renderBuffer.append("div.element div.summary { cursor: pointer; white-space: nowrap; background-color: rgb(240, 240, 240); padding: 3px; }\n");
    renderBuffer.append("div.element div.summary:hover { background-color: rgb(220, 220, 220); border-color: rgb(210, 210, 210); }\n");
    renderBuffer.append("div.element div.expandcollapse { cursor: pointer; float: right; background-color: rgb(255, 255, 255); width: 10px; border: 1px solid rgb(230, 230, 230); border-top: none; padding-left: 3px; padding-right: 3px; text-align: center; }\n");
    renderBuffer.append("div.element div.expandcollapse:hover { font-weight: bold; border-width: 2px; border-right-width: 1px; background-color: rgb(245, 245, 245); }\n");
    renderBuffer.append("div.element div.expandcollapse:active { font-weight: bold; border-width: 2px; border-right-width: 1px; background-color: rgb(230, 230, 230); }\n");
    renderBuffer.append("div.element div.contents { background-color: rgb(255, 255, 255); padding-left: 10px; padding-right: 0px; padding-top: 5px; padding-bottom: 5px; border-top: 1px solid rgb(230, 230, 230); }\n");

    renderBuffer.append("div.element.wo { border-color: rgb(200, 200, 255); }\n");
    renderBuffer.append("div.element.wo div.summary { background-color: rgb(240, 240, 255); }\n");
    renderBuffer.append("div.element.wo div.summary:hover { background-color: rgb(210, 210, 255); border-color: rgb(210, 210, 255); }\n");
    renderBuffer.append("div.element.wo div.summary div.title span.type { font-weight: normal; font-size: 0.80em; color: rgb(150, 150, 150); }\n");
    renderBuffer.append("div.element.wo > div.expandcollapse { border-color: rgb(200, 200, 255); }\n");
    renderBuffer.append("div.element.wo > div.expandcollapse:hover { background-color: rgb(245, 245, 255); }\n");
    renderBuffer.append("div.element.wo > div.expandcollapse:active { background-color: rgb(200, 200, 255); }\n");
    renderBuffer.append("div.element.wo div.contents { background-color: rgb(250, 250, 255); border-color: rgb(200, 200, 255); }\n");

    renderBuffer.append("div.element div.summary div.title { font-weight: bold; }\n");
    renderBuffer.append("div.element div.summary div.title.nonwo { color: rgb(180, 180, 180); }\n");
    renderBuffer.append("div.element div.summary div.title.missing { font-style: italic; }\n");
    renderBuffer.append("div.element div.summary div.title.nonwo span.idName { font-weight: bold; color: rgb(180, 180, 180); padding-left: 10px; }\n");
    renderBuffer.append("div.element div.summary div.title.nonwo span.className { font-weight: bold; color: rgb(180, 180, 180); padding-left: 10px; }\n");

    renderBuffer.append("div.element div.summary table.bindings { font-family: Helvetica; font-size: 8pt; margin: 0px; padding: 0px; }\n");
    renderBuffer.append("div.element div.summary table.bindings th { text-align: right; font-weight: normal; color: rgb(220, 0, 0); padding-right: 3px; }\n");
    renderBuffer.append("div.element div.summary table.bindings td.literal { color: rgb(0, 0, 200); }\n");
    renderBuffer.append("div.element div.summary table.bindings td.ognl { color: rgb(180, 0, 0); }\n");
    renderBuffer.append("div.element div.summary table.bindings td.keypath { color: rgb(180, 0, 0); }\n");

    renderBuffer.append("div.text { display: inline; }\n");

    renderBuffer.append("span.negate { font-weight: bold; }\n");

    renderBuffer.append("div.element.wo.WOString.simple { display: inline; border: none; }\n");
    renderBuffer.append("div.element.wo.WOString.simple div.summary { display: inline; border: 1px solid rgb(200, 200, 255); border: none; background-color: transparent; padding: 0px; }\n");
    renderBuffer.append("div.element.wo.WOString.simple div.summary div.title { display: inline; }\n");
    renderBuffer.append("div.element.wo.WOString.simple div.text.literal { color: rgb(0, 0, 200); }\n");
    renderBuffer.append("div.element.wo.WOString.simple div.text.ognl { color: rgb(180, 0, 0); }\n");
    renderBuffer.append("div.element.wo.WOString.simple div.text.keypath { color: rgb(180, 0, 0); }\n");

//    renderBuffer.append("div.element.wo.WOString div.summary { background-color: rgb(240, 240, 255); }\n");
//    renderBuffer.append("div.element.wo.WOString div.summary:hover { background-color: rgb(240, 240, 255); border-color: rgb(170, 225, 170); }\n");
//    renderBuffer.append("div.element.wo.WOString div.contents { background-color: rgb(250, 255, 250); border-color: rgb(200, 255, 200); }\n");
//    renderBuffer.append("div.element.wo.WOString:hover div.contents { display: block; }\n");

    renderBuffer.append("div.element.wo.WOConditional { border-color: rgb(190, 250, 190); }\n");
    renderBuffer.append("div.element.wo.WOConditional div.summary { background-color: rgb(230, 250, 230); }\n");
    renderBuffer.append("div.element.wo.WOConditional div.summary:hover { background-color: rgb(200, 250, 200); border-color: rgb(200, 255, 200); }\n");
    renderBuffer.append("div.element.wo.WOConditional div.expandcollapse { border-color: rgb(200, 255, 200); }\n");
    renderBuffer.append("div.element.wo.WOConditional div.expandcollapse:hover { background-color: rgb(245, 255, 245); }\n");
    renderBuffer.append("div.element.wo.WOConditional div.expandcollapse:active { background-color: rgb(200, 255, 200); }\n");
    renderBuffer.append("div.element.wo.WOConditional div.contents { background-color: rgb(250, 255, 250); border-color: rgb(190, 250, 190); }\n");
    renderBuffer.append("div.element.wo.WOConditional div.summary div.title span.type { display: none; }\n");
    renderBuffer.append("div.element.wo.WOConditional div.summary table.bindings th { text-align: right; font-weight: normal; color: rgb(220, 0, 0); padding-right: 3px; }\n");
    renderBuffer.append("div.element.wo.WOConditional div.summary table.bindings td.literal { color: rgb(0, 0, 200); }\n");
    renderBuffer.append("div.element.wo.WOConditional div.summary table.bindings td.ognl { color: rgb(180, 0, 0); }\n");
    renderBuffer.append("div.element.wo.WOConditional div.summary table.bindings td.keypath { color: rgb(180, 0, 0); }\n");

//    renderBuffer.append("div.element.wo.WORepetition { border-color: rgb(255, 200, 200); }\n");
//    renderBuffer.append("div.element.wo.WORepetition div.summary { background-color: rgb(255, 230, 230); }\n");
//    renderBuffer.append("div.element.wo.WORepetition div.summary:hover { background-color: rgb(255, 210, 210); border-color: rgb(255, 200, 200); }\n");
//    renderBuffer.append("div.element.wo.WORepetition div.expandcollapse { border-color: rgb(255, 200, 200); }\n");
//    renderBuffer.append("div.element.wo.WORepetition div.expandcollapse:hover { background-color: rgb(255, 250, 250); }\n");
//    renderBuffer.append("div.element.wo.WORepetition div.expandcollapse:active { background-color: rgb(255, 200, 200); }\n");
//    renderBuffer.append("div.element.wo.WORepetition div.contents { background-color: rgb(255, 250, 250); border-color: rgb(255, 200, 200); }\n");
//    renderBuffer.append("div.element.wo.WORepetition div.summary div.title span.type { display: none; }\n");
//    renderBuffer.append("div.element.wo.WORepetition div.summary table.bindings th { color: rgb(220, 0, 0); }\n");
//    renderBuffer.append("div.element.wo.WORepetition div.summary table.bindings td { color: rgb(150, 0, 0); }\n");

    renderBuffer.append("body.verbose div.element div.summary div.title span.nodeName.verbose { display: block; }\n");
    renderBuffer.append("body.verbose div.element div.summary div.title span.nodeName.compact { display: none; }\n");

    renderBuffer.append("body.compact { font-size: 7.5pt; }\n");
    renderBuffer.append("body.compact div.element { margin-top: 2px; margin-bottom: 3px; }\n");
    renderBuffer.append("body.compact div.element div.expandcollapse { width: 5px; padding-left: 4px; padding-right: 4px; padding-top: 1px; padding-bottom: 1px; }\n");
    renderBuffer.append("body.compact div.element div.expandcollapse:hover { border-width: 1px; }\n");
    renderBuffer.append("body.compact div.element div.summary { padding: 1px; padding-left: 2px; }\n");
    renderBuffer.append("body.compact div.element div.summary table.bindings { display: none; }\n");
    renderBuffer.append("body.compact div.element div.summary div.title { font-weight: normal; }\n");
    renderBuffer.append("body.compact div.element div.summary div.title span.nodeName.verbose { display: none; }\n");
    renderBuffer.append("body.compact div.element div.summary div.title span.nodeName.compact { display: block; }\n");
    renderBuffer.append("body.compact div.element div.summary div.title span.type { display: none; }\n");
    renderBuffer.append("body.compact div.element div.contents { padding-left: 8px; padding-top: 2px; padding-bottom: 2px; }\n");

    renderBuffer.append("body.compact div.element.wo.WOString { display: inline; border: none; }\n");
    renderBuffer.append("body.compact div.element.wo.WOString div.summary { display: inline; border: 1px solid rgb(200, 200, 255); }\n");
    renderBuffer.append("body.compact div.element.wo.WOString div.summary div.title { display: inline; }\n");
    renderBuffer.append("body.compact div.element.wo.WOString.simple { border: none; }\n");
    renderBuffer.append("body.compact div.element.wo.WOString.simple div.summary { border: none; background-color: transparent; padding: 0px; }\n");
    renderBuffer.append("body.compact div.element.wo.WOString.simple div.summary div.title { display: inline; }\n");

    renderBuffer.append("body div.element.document { margin: 0px; padding: 0px; border: none; }\n");
    renderBuffer.append("body div.element.document div.summary { margin: 0px; padding: 0px; border: none; display: none; }\n");
    renderBuffer.append("body div.element.document > div.expandcollapse { display: none; }\n");
    renderBuffer.append("body div.element.document div.contents { margin: 0px; padding: 0px; border: none; }\n");

    renderBuffer.append("</style>\n");
    renderBuffer.append("<script>\n");
    renderBuffer.append("function expandCollapse(id) { if ('none' == document.getElementById(id + '_contents').style.display) { expand(id); } else { collapse(id); } }\n");
    renderBuffer.append("function expand(id) { document.getElementById(id + '_contents').style.display = 'block'; document.getElementById(id + '_toggle').innerHTML = '" + TemplateOutlinePage.COLLAPSE_STRING + "'; window.status = 'expand:' + id; }\n");
    renderBuffer.append("function collapse(id) { document.getElementById(id + '_contents').style.display = 'none'; document.getElementById(id + '_toggle').innerHTML = '" + TemplateOutlinePage.EXPAND_STRING + "'; window.status = 'collapse:' + id; }\n");
    renderBuffer.append("function toggleCompact() { if ('compact' == document.getElementById('outline').className) { document.getElementById('outline').className = 'verbose'; } else { document.getElementById('outline').className = 'compact'; } window.status = 'toggleCompact:'; }\n");
    renderBuffer.append("function updatePageYOffset() { window.status = 'pageYOffset:' + window.pageYOffset; }\n");
    renderBuffer.append("</script>\n");

    renderBuffer.append("<div class = \"viewControls\"><a href = \"#\" onclick = \"toggleCompact()\">toggle compact view</a></div>\n");
    renderBuffer.append("<div class = \"elements\">\n");
  }

  /**
   * Renders the page footer.
   * 
   * @param renderBuffer the render buffer
   */
  protected void renderFooter(StringBuffer renderBuffer) {
    renderBuffer.append("</div></body></html>");
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
      String className = "element";

      boolean woTag = WodHtmlUtils.isWOTag(nodeName);
      boolean woSimpleString = false;

      IWodElement wodElement = null;
      if (woTag) {
        className = className + " wo";
        boolean wo54 = Activator.getDefault().isWO54();
        try {
          wodElement = WodHtmlUtils.getOrCreateWodElement(element, wo54, cache);
        }
        catch (Throwable t) {
          // IGNORE
          t.printStackTrace();
        }
        if (wodElement != null) {
          className = className + " " + wodElement.getElementType();

          if ("WOString".equals(wodElement.getElementType())) {
            if (wodElement.getBindingNamed("value") != null) {
              if (wodElement.getBindingNamed("escapeHTML") != null && wodElement.getBindings().size() == 2) {
                woSimpleString = true;
              }
              else if (wodElement.getBindings().size() == 1) {
                woSimpleString = true;
              }
            }

            // MS: FORCE OFF FOR NOW
            //woSimpleString = false;

            if (woSimpleString) {
              className = className + " simple";
            }
          }
        }
      }
      else {
        className = className + " " + nodeName.toLowerCase();
      }

      boolean showExpandCollapse = !empty;
      if ("script".equalsIgnoreCase(nodeName)) {
        // don't show script
        showExpandCollapse = false;
      }
      else if ("style".equalsIgnoreCase(nodeName)) {
        // don't show style
        showExpandCollapse = false;
      }

      //renderBuffer.append("<div id = \"" + nodeID + "\" class = \"" + className + "\" onmouseover = \"window.status = 'over:" + nodeID + "';\" onmouseout = \"window.status = 'out:" + nodeID + "';\" >");
      renderBuffer.append("<div id = \"" + nodeID + "\" class = \"" + className + "\">");

      if (showExpandCollapse) {
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

      // ... a simple string tag
      if (woSimpleString) {
        IWodBinding valueBinding = wodElement.getBindingNamed("value");
        String text = valueBinding.getValue();
        String textClassName;
        if (valueBinding.isLiteral()) {
          //text = text.replaceAll("^\"([^\"]+)\"", "$1");
          textClassName = "text literal";
        }
        else if (valueBinding.isOGNL()) {
          textClassName = "text ognl";
        }
        else {
          textClassName = "text keypath";
        }
        renderBuffer.append("<div class = \"title\"></div> <div class = \"" + textClassName + "\">[" + text + "]</div>");
      }
      // ... a WO tag
      else if (woTag) {
        if (wodElement != null) {
          if (WodHtmlUtils.isInline(nodeName)) {

            String summaryName = wodElement.getElementType();
            renderBuffer.append("<div class = \"title\"><span class = \"nodeName verbose\">" + summaryName + "</span></div>");

            // Special case for the compact display of a conditional
            if ("WOConditional".equals(summaryName)) {
              String conditionValue = wodElement.getBindingValue("condition");
              if (conditionValue != null) {
                summaryName = conditionValue;
                boolean negated = false;
                IWodBinding negateBinding = wodElement.getBindingNamed("negate");
                if (negateBinding != null) {
                  if (negateBinding.isTrueValue()) {
                    negated = true;
                  }
                }
                else if (nodeName.equals("wo:not")) {
                  negated = true;
                }

                if (negated) {
                  summaryName = "<span class = \"negate\">not</span> " + summaryName;
                }
                else if (negateBinding != null) {
                  summaryName = summaryName + " <span class = \"negate\">not = " + negateBinding.getValue() + "</span>";
                }
              }
            }
            // Special case for the compact display of a string
            else if ("WOString".equals(summaryName)) {
              String value = wodElement.getBindingValue("value");
              if (value != null) {
                summaryName = "WOString: " + value;
              }
            }
            // Special case for the compact display of links, buttons, etc
            else if (wodElement.getBindingNamed("action") != null) {
              String action = wodElement.getBindingValue("action");
              summaryName = wodElement.getElementType() + ": " + action;
            }
            // Special case for the compact display of form fields
            else if (wodElement.getBindingNamed("value") != null) {
              String value = wodElement.getBindingValue("value");
              summaryName = wodElement.getElementType() + ": " + value;
            }
            // Special case for the compact display of checkboxes
            else if (wodElement.getBindingNamed("checked") != null) {
              String checked = wodElement.getBindingValue("checked");
              summaryName = wodElement.getElementType() + ": " + checked;
            }
            // Special case for the compact display of repetitions and popup buttons
            else if (wodElement.getBindingNamed("list") != null) {
              String list = wodElement.getBindingValue("list");
              summaryName = wodElement.getElementType() + ": " + list;
            }
            renderBuffer.append("<div class = \"title\"><span class = \"nodeName compact\">" + summaryName + "</span></div>");
          }
          else {
            renderBuffer.append("<div class = \"title\"><span class = \"nodeName\">" + wodElement.getElementName() + "</span> <span class = \"type\">: " + wodElement.getElementType() + "</span></div>");
          }

          // WO bindings
          List<IWodBinding> wodBindings = wodElement.getBindings();
          if (wodBindings.size() > 0) {
            renderBuffer.append("<table class = \"bindings\">");
            for (IWodBinding wodBinding : wodBindings) {
              renderBuffer.append("<tr>");
              renderBuffer.append("<th>" + wodBinding.getName() + "</th>");
              String bindingClass;
              if (wodBinding.isLiteral()) {
                bindingClass = "literal";
              }
              else if (wodBinding.isOGNL()) {
                bindingClass = "ognl";
              }
              else {
                bindingClass = "keypath";
              }
              renderBuffer.append("<td class = \"" + bindingClass + "\">" + wodBinding.getValue() + "</td>");
              renderBuffer.append("</tr>");
            }
            renderBuffer.append("</table>");
          }
        }
        // ... if the WO tag doesn't have a WOD entry, show it as "missing"
        else {
          String missingName = element.getAttributeValue("name");
          if (missingName == null) {
            missingName = nodeName;
          }
          renderBuffer.append("<div class = \"title missing\">" + missingName + "</div>");
        }
      }
      // ... an html node
      else {
        renderBuffer.append("<div class = \"title nonwo\"><span class = \"nodeName\">");
        renderBuffer.append(nodeName);
        renderBuffer.append("</span>");

        String elementID = element.getAttributeValue("id");
        if (elementID != null) {
          renderBuffer.append("<span class = \"idName\">#" + elementID + "</span>");
        }

        String elementClass = element.getAttributeValue("class");
        if (elementClass != null) {
          elementClass = elementClass.replace(' ', '.');
          renderBuffer.append("<span class = \"className\">." + elementClass + "</span>");
        }
        renderBuffer.append("</div>");
      }

      renderBuffer.append("</div>");

      // ... if there are children, show the contents
      if (showExpandCollapse) {
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

      renderBuffer.append("</div>");
    }
    else {
      StringBuffer nodeBuffer = new StringBuffer();
      node.toXMLString(renderContext, nodeBuffer);
      String nodeStr = nodeBuffer.toString();
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
