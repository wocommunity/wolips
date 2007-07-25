package tk.eclipse.plugin.htmleditor;

import java.util.ArrayList;
import java.util.List;

import jp.aonir.fuzzyxml.FuzzyXMLAttribute;
import jp.aonir.fuzzyxml.FuzzyXMLDocument;
import jp.aonir.fuzzyxml.FuzzyXMLElement;
import jp.aonir.fuzzyxml.FuzzyXMLParser;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import tk.eclipse.plugin.htmleditor.editors.HTMLHyperlinkInfo;
import tk.eclipse.plugin.htmleditor.editors.HTMLSourceEditor;

/**
 * The <code>IHyperlinkDetector</code> implementation for 
 * the <code>HTMLSourceEditor</code>.
 * <p>
 * This class detects the <strong>href</string> attribute
 * as the hyperlink in default. And it's possible to add
 * additional rules by <code>addHyperlinkProvider()</code>.
 * 
 * @author Naoki Takezoe
 * @see tk.eclipse.plugin.htmleditor.editors.HTMLSourceEditor
 * @see tk.eclipse.plugin.htmleditor.IHyperlinkProvider
 */
public class HTMLHyperlinkDetector implements IHyperlinkDetector {

  private HTMLSourceEditor editor;
  private List<IHyperlinkProvider> providers = new ArrayList<IHyperlinkProvider>();

  /**
   * @param editor the <code>HTMLSourceEditor</code> instance
   */
  public void setEditor(HTMLSourceEditor editor) {
    this.editor = editor;
  }

  /**
   * Adds the additional hyperlink provider.
   * 
   * @param provider the additional hyperlink provider
   */
  public void addHyperlinkProvider(IHyperlinkProvider provider) {
    this.providers.add(provider);
  }

  /**
   * Returns the <code>IProject</code> of the editing file.
   * <p>
   * If the editor input isn't <code>IFileEditorInput</code>,
   * this method returns <code>null</code>.
   * 
   * @return the <code>IProject</code>
   */
  private IProject getProject() {
    IEditorInput input = editor.getEditorInput();
    if (input instanceof IFileEditorInput) {
      return ((IFileEditorInput) input).getFile().getProject();
    }
    return null;
  }

  public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {
    IHyperlink hyperlink = detectHyperlink(textViewer.getDocument(), region.getOffset());
    if (hyperlink != null) {
      return new IHyperlink[] { hyperlink };
    }
    return null;
  }

  private IHyperlink detectHyperlink(IDocument doc, int offset) {
    FuzzyXMLDocument document = new FuzzyXMLParser(false).parse(editor.getHTMLSource());
    FuzzyXMLElement element = document.getElementByOffset(offset);
    if (element == null) {
      return null;
    }
    FuzzyXMLAttribute[] attrs = element.getAttributes();
    HTMLHyperlinkInfo info = getOpenFileInfo(document, element, null, null, offset);
    if (info != null) {
      return (IHyperlink) info.getObject();
    }
    else {
      for (int i = 0; i < attrs.length; i++) {
        if (attrs[i].getOffset() < offset && offset < attrs[i].getOffset() + attrs[i].getLength()) {
          int attrOffset = getAttributeValueOffset(doc.get(), attrs[i]);
          int attrLength = attrs[i].getValue().length();
          if (attrOffset >= 0 && attrLength >= 0 && attrOffset <= offset) {
            info = getOpenFileInfo(document, element, attrs[i].getName(), attrs[i].getValue(), offset - attrOffset);
            IHyperlink hyperlink = null;
            if (info != null && info.getObject() != null) {
              if (info.getObject() instanceof IHyperlink) {
                hyperlink = (IHyperlink) info.getObject();
              }
              else {
                hyperlink = new HTMLHyperlink(new Region(attrOffset + info.getOffset(), info.getLength()), info.getObject());
              }
            }
            return hyperlink;
          }
        }
      }
    }
    return null;
  }

  /**
   * Returns a target of hyperlink.
   */
  private HTMLHyperlinkInfo getOpenFileInfo(FuzzyXMLDocument doc, FuzzyXMLElement element, String attrName, String attrValue, int offset) {
    try {
      IProject project = getProject();
      if (project == null) {
        return null;
      }
      IFile file = ((IFileEditorInput) editor.getEditorInput()).getFile();
      for (int i = 0; i < providers.size(); i++) {
        IHyperlinkProvider provider = this.providers.get(i);
        HTMLHyperlinkInfo info = provider.getHyperlinkInfo(file, doc, element, attrName, attrValue, offset);
        if (info != null && info.getObject() != null) {
          return info;
        }
      }
      if (attrName != null && attrName.equalsIgnoreCase("href")) {
        String href = attrValue;
        if (href.indexOf("#") > 0) {
          href = href.substring(0, href.indexOf("#"));
        }
        IPath path = file.getParent().getProjectRelativePath();
        IResource resource = project.findMember(path.append(href));
        if (resource != null && resource.exists() && resource instanceof IFile) {
          HTMLHyperlinkInfo info = new HTMLHyperlinkInfo();
          info.setObject(resource);
          info.setOffset(0);
          info.setLength(attrValue.length());
          return info;
        }
      }
    }
    catch (Exception ex) {
      HTMLPlugin.logException(ex);
    }
    return null;
  }

  /**
   * Returns an attribute value offset.
   * 
   * @param source the source code
   * @param attr the attribute
   * @return the offset of the attribute
   */
  private int getAttributeValueOffset(String source, FuzzyXMLAttribute attr) {
    int offset = source.indexOf('=', attr.getOffset());
    if (offset == -1) {
      return -1;
    }
    char c = ' ';
    while (c == ' ' || c == '\t' || c == '\r' || c == '\n' || c == '"' || c == '\'') {
      offset++;
      if (source.length() == offset + 1) {
        break;
      }
      c = source.charAt(offset);
    }
    return offset;
  }

  private class HTMLHyperlink implements IHyperlink {

    private IRegion region;
    private Object openObject;

    public HTMLHyperlink(IRegion region, Object openObject) {
      this.region = region;
      this.openObject = openObject;
    }

    public IRegion getHyperlinkRegion() {
      return region;
    }

    public String getTypeLabel() {
      return null;
    }

    public String getHyperlinkText() {
      return null;
    }

    public void open() {
      try {
        if (openObject instanceof IFile) {
          IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
          IDE.openEditor(window.getActivePage(), (IFile) openObject, true);
        }
        else if (openObject instanceof IJavaElement) {
          JavaUI.revealInEditor(JavaUI.openInEditor((IJavaElement) openObject), (IJavaElement) openObject);
        }
      }
      catch (Exception ex) {
        HTMLPlugin.logException(ex);
      }
    }

  }

}
