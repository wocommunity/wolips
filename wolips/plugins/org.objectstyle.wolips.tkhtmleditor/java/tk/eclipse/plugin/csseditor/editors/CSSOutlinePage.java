package tk.eclipse.plugin.csseditor.editors;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;
import org.w3c.css.sac.InputSource;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.css.CSSStyleRule;
import org.w3c.dom.css.CSSStyleSheet;

import tk.eclipse.plugin.htmleditor.HTMLPlugin;
import tk.eclipse.plugin.htmleditor.HTMLUtil;

import com.steadystate.css.parser.CSSOMParser;

/**
 * @author Naoki Takezoe
 */
public class CSSOutlinePage extends ContentOutlinePage {

  private CSSEditor editor;
  private List<String> selectors = new ArrayList<String>();

  public CSSOutlinePage(CSSEditor editor) {
    super();
    this.editor = editor;
  }

  @Override
  public void createControl(Composite parent) {
    super.createControl(parent);
    TreeViewer viewer = getTreeViewer();
    viewer.setContentProvider(new CSSContentProvider());
    viewer.setLabelProvider(new CSSLabelProvider());
    viewer.addSelectionChangedListener(new CSSSelectionChangedListener());
    viewer.setInput(this.selectors);
    update();
  }

  public void update() {
    try {
      CSSOMParser parser = new CSSOMParser();
      InputSource is = new InputSource(new StringReader(editor.getDocumentProvider().getDocument(editor.getEditorInput()).get()));
      CSSStyleSheet stylesheet = parser.parseStyleSheet(is);
      this.selectors.clear();
      CSSRuleList list = stylesheet.getCssRules();
      for (int i = 0; i < list.getLength(); i++) {
        CSSRule rule = list.item(i);
        if (rule instanceof CSSStyleRule) {
          CSSStyleRule styleRule = (CSSStyleRule) rule;
          String selector = styleRule.getSelectorText();
          this.selectors.add(selector);
        }
      }
      getTreeViewer().refresh();
    }
    catch (Throwable t) {
    }
  }

  private class CSSContentProvider implements ITreeContentProvider {
    public Object[] getChildren(Object parentElement) {
      if (parentElement instanceof ArrayList) {
        return ((ArrayList) parentElement).toArray();
      }
      return new Object[0];
    }

    public Object getParent(Object element) {
      if (element instanceof String) {
        return selectors;
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

  private class CSSSelectionChangedListener implements ISelectionChangedListener {
    public void selectionChanged(SelectionChangedEvent event) {
      IStructuredSelection sel = (IStructuredSelection) event.getSelection();
      String element = (String) sel.getFirstElement();

      element = element.replaceAll("\\*", "");

      String text = editor.getDocumentProvider().getDocument(editor.getEditorInput()).get();
      text = HTMLUtil.cssComment2space(text);
      int offset = text.indexOf(element);
      if (offset >= 0) {
        editor.selectAndReveal(offset, 0);
      }
    }
  }

  private class CSSLabelProvider extends LabelProvider {
    @Override
    public Image getImage(Object element) {
      return HTMLPlugin.getDefault().getImageRegistry().get(HTMLPlugin.ICON_CSS_RULE);
    }
  }
}
