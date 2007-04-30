package tk.eclipse.plugin.htmleditor.editors;

import java.io.IOException;
import java.util.ArrayList;

import jp.aonir.fuzzyxml.FuzzyXMLAttribute;
import jp.aonir.fuzzyxml.FuzzyXMLComment;
import jp.aonir.fuzzyxml.FuzzyXMLDocType;
import jp.aonir.fuzzyxml.FuzzyXMLDocument;
import jp.aonir.fuzzyxml.FuzzyXMLElement;
import jp.aonir.fuzzyxml.FuzzyXMLNode;
import jp.aonir.fuzzyxml.FuzzyXMLParser;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;
import org.objectstyle.wolips.locate.LocateException;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;

import tk.eclipse.plugin.htmleditor.HTMLPlugin;

/**
 * An implementaion of IContentOutlinePage for the HTML editor.
 * This shows the outline of HTML document.
 */
public class HTMLOutlinePage extends ContentOutlinePage implements IHTMLOutlinePage {

  private RootNode root;
  private HTMLSourceEditor editor;
  private FuzzyXMLDocument doc;

  public HTMLOutlinePage(HTMLSourceEditor editor) {
    super();
    this.editor = editor;
  }

  public FuzzyXMLDocument getDoc() {
    return doc;
  }

  public void createControl(Composite parent) {
    super.createControl(parent);
    TreeViewer viewer = getTreeViewer();
    root = new RootNode();
    viewer.setContentProvider(new HTMLContentProvider());
    viewer.setLabelProvider(new HTMLLabelProvider());
    viewer.setInput(root);
    viewer.addSelectionChangedListener(new HTMLSelectionChangedListener());

    try {
      viewer.setExpandedState(root.getChildren()[0], true);
    }
    catch (Exception ex) {
      // ignore
    }

    update();
  }

  protected boolean isHTML() {
    return true;
  }

  protected FuzzyXMLParser createParser() {
    FuzzyXMLParser parser = new FuzzyXMLParser(isHTML());
    return parser;
  }

  public void update() {
    if (getControl() == null || getControl().isDisposed()) {
      return;
    }
    IFile file = ((FileEditorInput) editor.getEditorInput()).getFile();
    try {
      //this.doc = WodParserCache.parser(file).getHtmlDocument();
      //if (this.doc == null) {
      this.doc = createParser().parse(editor.getHTMLSource());
      //}
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    TreeViewer viewer = getTreeViewer();
    if (viewer != null) {
      viewer.refresh();
    }
  }

  protected Image getNodeImage(FuzzyXMLNode element) {
    if (element instanceof FuzzyXMLElement) {
      FuzzyXMLElement e = (FuzzyXMLElement) element;
      if (e.getName().equalsIgnoreCase("html")) {
        return HTMLPlugin.getDefault().getImageRegistry().get(HTMLPlugin.ICON_TAG_HTML);
      }
      else if (e.getName().equalsIgnoreCase("title")) {
        return HTMLPlugin.getDefault().getImageRegistry().get(HTMLPlugin.ICON_TITLE);
      }
      else if (e.getName().equalsIgnoreCase("body")) {
        return HTMLPlugin.getDefault().getImageRegistry().get(HTMLPlugin.ICON_BODY);
      }
      else if (e.getName().equalsIgnoreCase("form")) {
        return HTMLPlugin.getDefault().getImageRegistry().get(HTMLPlugin.ICON_FORM);
      }
      else if (e.getName().equalsIgnoreCase("img")) {
        return HTMLPlugin.getDefault().getImageRegistry().get(HTMLPlugin.ICON_IMAGE);
      }
      else if (e.getName().equalsIgnoreCase("a")) {
        return HTMLPlugin.getDefault().getImageRegistry().get(HTMLPlugin.ICON_LINK);
      }
      else if (e.getName().equalsIgnoreCase("table")) {
        return HTMLPlugin.getDefault().getImageRegistry().get(HTMLPlugin.ICON_TABLE);
      }
      else if (e.getName().equalsIgnoreCase("input")) {
        String type = e.getAttributeValue("type");
        if (type != null) {
          if (type.equalsIgnoreCase("button") || type.equalsIgnoreCase("reset") || type.equalsIgnoreCase("submit")) {
            return HTMLPlugin.getDefault().getImageRegistry().get(HTMLPlugin.ICON_BUTTON);
          }
          else if (type.equalsIgnoreCase("radio")) {
            return HTMLPlugin.getDefault().getImageRegistry().get(HTMLPlugin.ICON_RADIO);
          }
          else if (type.equalsIgnoreCase("checkbox")) {
            return HTMLPlugin.getDefault().getImageRegistry().get(HTMLPlugin.ICON_CHECK);
          }
          else if (type.equalsIgnoreCase("text")) {
            return HTMLPlugin.getDefault().getImageRegistry().get(HTMLPlugin.ICON_TEXT);
          }
          else if (type.equalsIgnoreCase("password")) {
            return HTMLPlugin.getDefault().getImageRegistry().get(HTMLPlugin.ICON_PASS);
          }
          else if (type.equalsIgnoreCase("hidden")) {
            return HTMLPlugin.getDefault().getImageRegistry().get(HTMLPlugin.ICON_HIDDEN);
          }
        }
      }
      else if (e.getName().equalsIgnoreCase("select")) {
        return HTMLPlugin.getDefault().getImageRegistry().get(HTMLPlugin.ICON_SELECT);
      }
      else if (e.getName().equalsIgnoreCase("textarea")) {
        return HTMLPlugin.getDefault().getImageRegistry().get(HTMLPlugin.ICON_TEXTAREA);
      }
      return HTMLPlugin.getDefault().getImageRegistry().get(HTMLPlugin.ICON_TAG);
    }
    else if (element instanceof FuzzyXMLDocType) {
      return HTMLPlugin.getDefault().getImageRegistry().get(HTMLPlugin.ICON_DOCTYPE);
    }
    else if (element instanceof FuzzyXMLComment) {
      return HTMLPlugin.getDefault().getImageRegistry().get(HTMLPlugin.ICON_COMMENT);
    }
    return null;
  }

  protected Object[] getNodeChildren(FuzzyXMLElement element) {
    ArrayList children = new ArrayList();
    FuzzyXMLNode[] nodes = element.getChildren();
    for (int i = 0; i < nodes.length; i++) {
      if (nodes[i] instanceof FuzzyXMLElement) {
        children.add(nodes[i]);
      }
      else if (nodes[i] instanceof FuzzyXMLDocType) {
        children.add(nodes[i]);
      }
      else if (nodes[i] instanceof FuzzyXMLComment) {
        children.add(nodes[i]);
      }
    }
    return (FuzzyXMLNode[]) children.toArray(new FuzzyXMLNode[children.size()]);
  }

  protected String getNodeText(FuzzyXMLNode node) {
    if (node instanceof FuzzyXMLElement) {
      StringBuffer sb = new StringBuffer();
      FuzzyXMLAttribute[] attrs = ((FuzzyXMLElement) node).getAttributes();
      for (int i = 0; i < attrs.length; i++) {
        if (sb.length() != 0) {
          sb.append(", ");
        }
        sb.append(attrs[i].getName() + "=" + attrs[i].getValue());
      }
      if (sb.length() == 0) {
        return ((FuzzyXMLElement) node).getName();
      }
      else {
        return ((FuzzyXMLElement) node).getName() + "(" + sb.toString() + ")";
      }
    }
    if (node instanceof FuzzyXMLDocType) {
      return "DOCTYPE";
    }
    if (node instanceof FuzzyXMLComment) {
      return "#comment";
    }
    return node.toString();
  }

  /** root element. */
  private class RootNode {

    public RootNode() {
      super();
    }

    public Object[] getChildren() {
      //			IFile  file = ((FileEditorInput)editor.getEditorInput()).getFile();
      //			return new FileNode[]{
      //				new FileNode(file.getName())
      //			};
      ArrayList children = new ArrayList();
      if (doc == null) {
        update();
      }
      if (doc.getDocumentType() != null) {
        children.add(doc.getDocumentType());
      }
      FuzzyXMLNode[] nodes = doc.getDocumentElement().getChildren();
      for (int i = 0; i < nodes.length; i++) {
        if (nodes[i] instanceof FuzzyXMLElement) {
          children.add(nodes[i]);
          //				} else if(nodes[i] instanceof FuzzyXMLText && ((FuzzyXMLText)nodes[i]).getValue().startsWith("<%")){
          //					children.add(nodes[i]);
        }
      }
      return (FuzzyXMLNode[]) children.toArray(new FuzzyXMLNode[children.size()]);
    }

    public boolean equals(Object obj) {
      if (obj instanceof RootNode) {
        return true;
      }
      return false;
    }
  }

  /** ContentProvider of HTMLOutlinePage. */
  private class HTMLContentProvider implements ITreeContentProvider {

    public Object[] getChildren(Object parentElement) {
      if (parentElement instanceof RootNode) {
        return ((RootNode) parentElement).getChildren();
      }
      else if (parentElement instanceof FuzzyXMLElement) {
        return getNodeChildren((FuzzyXMLElement) parentElement);
      }
      return new Object[0];
    }

    public Object getParent(Object element) {
      if (element instanceof FuzzyXMLNode) {
        FuzzyXMLNode parent = ((FuzzyXMLNode) element).getParentNode();
        if (parent == null) {
          return root.getChildren()[0];
        }
        return parent;
      }
      return null;
    }

    public boolean hasChildren(Object element) {
      if (getChildren(element).length == 0) {
        return false;
      }
      else {
        return true;
      }
    }

    public Object[] getElements(Object inputElement) {
      return getChildren(inputElement);
    }

    public void dispose() {
    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    }
  }

  /** LabelProvider of HTMLOutlinePage */
  private class HTMLLabelProvider extends LabelProvider {

    public Image getImage(Object element) {
      if (element instanceof FuzzyXMLNode) {
        return getNodeImage((FuzzyXMLNode) element);
      }
      return null;
    }

    public String getText(Object element) {
      if (element instanceof FuzzyXMLNode) {
        return getNodeText((FuzzyXMLNode) element);
      }
      return super.getText(element);
    }
  }

  /** This listener is called when selection of TreeViewer is changed. */
  private class HTMLSelectionChangedListener implements ISelectionChangedListener {
    public void selectionChanged(SelectionChangedEvent event) {
      IStructuredSelection sel = (IStructuredSelection) event.getSelection();
      Object element = sel.getFirstElement();
      if (element instanceof FuzzyXMLNode) {
        int offset = ((FuzzyXMLNode) element).getOffset();
        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        IEditorPart editorPart = page.getActiveEditor();
        if (editorPart instanceof HTMLEditor) {
          ((HTMLEditor) editorPart).setOffset(offset);
        }
        else if (editorPart instanceof HTMLSourceEditor) {
          ((HTMLSourceEditor) editorPart).selectAndReveal(offset, 0);
        }
        else {
          HTMLSourceEditor editor = (HTMLSourceEditor) editorPart.getAdapter(HTMLSourceEditor.class);
          if (editor != null) {
            editor.selectAndReveal(offset, 0);
          }
        }
      }
    }
  }
}
