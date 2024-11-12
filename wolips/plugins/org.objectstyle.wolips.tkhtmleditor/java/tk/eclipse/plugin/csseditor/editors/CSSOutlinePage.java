package tk.eclipse.plugin.csseditor.editors;

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

import com.helger.css.ECSSVersion;
import com.helger.css.decl.CascadingStyleSheet;
import com.helger.css.reader.CSSReader;

import tk.eclipse.plugin.htmleditor.HTMLPlugin;
import tk.eclipse.plugin.htmleditor.HTMLUtil;

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
        String css = editor.getDocumentProvider().getDocument(editor.getEditorInput()).get();
        this.selectors.clear();
  	  CascadingStyleSheet styles = CSSReader.readFromString(css, ECSSVersion.LATEST);
	  styles.getAllStyleRules().stream().forEach(stylerule ->{
		  stylerule.getAllSelectors().forEach(sel ->{
			  this.selectors.add(sel.getAsCSSString());
		});
	  });
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
