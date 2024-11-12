package tk.eclipse.plugin.htmleditor.assist;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;

import com.helger.css.ECSSVersion;
import com.helger.css.decl.CSSSelectorSimpleMember;
import com.helger.css.decl.CascadingStyleSheet;
import com.helger.css.reader.CSSReader;

import jp.aonir.fuzzyxml.FuzzyXMLAttribute;
import jp.aonir.fuzzyxml.FuzzyXMLDocument;
import jp.aonir.fuzzyxml.FuzzyXMLElement;
import jp.aonir.fuzzyxml.FuzzyXMLNode;
import jp.aonir.fuzzyxml.FuzzyXMLParser;
import tk.eclipse.plugin.htmleditor.HTMLUtil;

/**
 * This provides code completion for class attribute of HTML tags.
 * <p>
 * Completion proposals is got from an internal stylesheet like following:
 * <pre>
 * &lt;style type=&quot;text/css&quot;&gt;
 * ...
 * &lt;/style&gt;
 * </pre>
 * And external stylesheet that is included as following:
 * <pre>
 * &lt;link rel=&quot;stylesheet&quot; type=&quot;text/css&quot; href=&quot;...&quot; /&gt;
 * </pre>
 */
public class CSSAssistProcessor {

  private HashMap<String, ArrayList<String>> _rules = new HashMap<String, ArrayList<String>>();
  private IFile _file;

  /**
   * Reload informations of code completion.
   * 
   * @param source HTML
   */
  public void reload(IFile file, String source) {
    this._file = file;
    _rules.clear();
    source = HTMLUtil.scriptlet2space(source, false);
    FuzzyXMLDocument doc;
    if ("html".equalsIgnoreCase(file.getFileExtension())) {
      try {
        doc = WodParserCache.parser(file).getHtmlEntry().getModel();
      }
      catch (Exception e) {
        e.printStackTrace();
        doc = null;
      }
    }
    else {
      doc = new FuzzyXMLParser(false).parse(source);
    }
    if (doc != null) {
      processElement(doc.getDocumentElement());
    }
  }

  private void processElement(FuzzyXMLElement element) {
    if (element.getName().equalsIgnoreCase("link")) {
      // external CSS cpecified by link tag
      String rel = "";
      String type = "";
      String href = "";
      FuzzyXMLAttribute[] attrs = element.getAttributes();
      for (int i = 0; i < attrs.length; i++) {
        if (attrs[i].getName().equalsIgnoreCase("rel")) {
          rel = attrs[i].getValue();
        }
        else if (attrs[i].getName().equalsIgnoreCase("type")) {
          type = attrs[i].getValue();
        }
        else if (attrs[i].getName().equalsIgnoreCase("href")) {
          href = attrs[i].getValue();
        }
      }
      if (rel.equalsIgnoreCase("stylesheet") && type.equalsIgnoreCase("text/css")) {
        try {
          IFile css = getFile(href);
          if (css != null && css.exists()) {
            String text = new String(HTMLUtil.readStream(css.getContents()));
            processStylesheet(text);
          }
        }
        catch (Exception ex) {
        }
      }
    }
    else if (element.getName().equalsIgnoreCase("style")) {
      // inline CSS defined in style tag
      String type = "";
      FuzzyXMLAttribute[] attrs = element.getAttributes();
      for (int i = 0; i < attrs.length; i++) {
        if (attrs[i].getName().equalsIgnoreCase("type")) {
          type = attrs[i].getValue();
        }
      }
      if (type.equalsIgnoreCase("text/css")) {
        String text = HTMLUtil.getXPathValue(element, "/");
        processStylesheet(text);
      }
    }
    FuzzyXMLNode[] children = element.getChildren();
    for (int i = 0; i < children.length; i++) {
      if (children[i] instanceof FuzzyXMLElement) {
        processElement((FuzzyXMLElement) children[i]);
      }
    }
  }

  private IFile getFile(String path) {
    if (path.startsWith("/")) {
      return null;
      //			try {
      //				HTMLProjectParams params = new HTMLProjectParams(file.getProject());
      //				return file.getProject().getFile(new Path(params.getRoot()).append(path));
      //			} catch(Exception ex){
      //			}
    }
    return _file.getProject().getFile(_file.getParent().getProjectRelativePath().append(path));
  }

  /**
   * Parse CSS and create completion informations.
   * 
   * @param css CSS
   */
  private void processStylesheet(String css) {
	  /*
	   * This function appears to be building a list of class names for elements, 
	   * with a catchall * element for classses declared without an element. So 
	   * something like,
	   * * (class1, class2)
	   * div (class3, class4)
	   * Then it can provide these class names in code assist when editing.
	   */
	  CascadingStyleSheet styles = CSSReader.readFromString(css, ECSSVersion.LATEST);
	  styles.getAllStyleRules().stream().forEach(stylerule ->{
		  stylerule.getAllSelectors().forEach(sel ->{
			  //FIXME add to _rules here
			  sel.getAllMembers().stream().forEach(mem ->{
				  if(mem instanceof CSSSelectorSimpleMember m) {
					  if(m.isElementName()) {
					  }
				  }
			  });
		  });
	  });
  }

  /**
   * Returns completion proposal for class attribute.
   * 
   * @param tagName a tag name
   * @return an array of completion proposals
   */
  public AssistInfo[] getAssistInfo(String tagName, String value) {
    try {
      if (value.indexOf(' ') != -1) {
        value = value.substring(0, value.lastIndexOf(' ') + 1);
      }
      else {
        value = "";
      }

      ArrayList<String> assists = new ArrayList<String>();
      ArrayList<String> all = _rules.get("*");
      if (all != null) {
        assists.addAll(all);
      }
      if (_rules.get(tagName.toLowerCase()) != null) {
        ArrayList<String> list = _rules.get(tagName.toLowerCase());
        assists.addAll(list);
      }
      AssistInfo[] info = new AssistInfo[assists.size()];
      for (int i = 0; i < assists.size(); i++) {
        String keyword = assists.get(i);
        info[i] = new AssistInfo(value + keyword, keyword);
      }
      return info;
    }
    catch (Exception ex) {
    }
    return new AssistInfo[0];
  }
}
