package tk.eclipse.plugin.htmleditor.assist;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;

import jp.aonir.fuzzyxml.FuzzyXMLAttribute;
import jp.aonir.fuzzyxml.FuzzyXMLDocument;
import jp.aonir.fuzzyxml.FuzzyXMLElement;
import jp.aonir.fuzzyxml.FuzzyXMLNode;
import jp.aonir.fuzzyxml.FuzzyXMLParser;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.objectstyle.wolips.locate.LocateException;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;
import org.w3c.css.sac.AttributeCondition;
import org.w3c.css.sac.Condition;
import org.w3c.css.sac.ConditionalSelector;
import org.w3c.css.sac.ElementSelector;
import org.w3c.css.sac.InputSource;
import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SelectorList;
import org.w3c.css.sac.SimpleSelector;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.css.CSSStyleRule;
import org.w3c.dom.css.CSSStyleSheet;

import tk.eclipse.plugin.htmleditor.HTMLUtil;

import com.steadystate.css.parser.CSSOMParser;

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

  private HashMap rules = new HashMap();
  private IFile file;

  /**
   * Reload informations of code completion.
   * 
   * @param source HTML
   */
  public void reload(IFile file, String source) {
    this.file = file;
    rules.clear();
    source = HTMLUtil.scriptlet2space(source, false);
    FuzzyXMLDocument doc;
    if ("html".equalsIgnoreCase(file.getFileExtension())) {
      try {
        doc = WodParserCache.parser(file).getHtmlDocument();
      }
      catch (Exception e) {
        e.printStackTrace();
        doc = null;
      }
    }
    else {
      doc = new FuzzyXMLParser().parse(source);
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
    // ‘Š‘ÎƒpƒX
    return file.getProject().getFile(file.getParent().getProjectRelativePath().append(path));
  }

  /**
   * Parse CSS and create completion informations.
   * 
   * @param css CSS
   */
  private void processStylesheet(String css) {
    try {
      CSSOMParser parser = new CSSOMParser();
      InputSource is = new InputSource(new StringReader(css));
      CSSStyleSheet stylesheet = parser.parseStyleSheet(is);
      CSSRuleList list = stylesheet.getCssRules();
      //			ArrayList assists = new ArrayList();
      for (int i = 0; i < list.getLength(); i++) {
        CSSRule rule = list.item(i);
        if (rule instanceof CSSStyleRule) {
          CSSStyleRule styleRule = (CSSStyleRule) rule;
          String selector = styleRule.getSelectorText();
          SelectorList selectors = parser.parseSelectors(new InputSource(new StringReader(selector)));
          for (int j = 0; j < selectors.getLength(); j++) {
            Selector sel = selectors.item(j);
            if (sel instanceof ConditionalSelector) {
              Condition cond = ((ConditionalSelector) sel).getCondition();
              SimpleSelector simple = ((ConditionalSelector) sel).getSimpleSelector();

              if (simple instanceof ElementSelector) {
                String tagName = ((ElementSelector) simple).getLocalName();
                if (tagName == null) {
                  tagName = "*";
                }
                else {
                  tagName = tagName.toLowerCase();
                }
                if (cond instanceof AttributeCondition) {
                  AttributeCondition attrCond = (AttributeCondition) cond;
                  if (rules.get(tagName) == null) {
                    ArrayList classes = new ArrayList();
                    //										classes.add(new AssistInfo(attrCond.getValue()));
                    classes.add(attrCond.getValue());
                    rules.put(tagName, classes);
                  }
                  else {
                    ArrayList classes = (ArrayList) rules.get(tagName);
                    //										classes.add(new AssistInfo(attrCond.getValue()));
                    classes.add(attrCond.getValue());
                  }
                }
              }
            }
          }
        }
      }
    }
    catch (Throwable ex) {
      // java.lang.Error: Missing return statement in function
    }
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

      ArrayList assists = new ArrayList();
      ArrayList all = (ArrayList) rules.get("*");
      if (all != null) {
        assists.addAll(all);
      }
      if (rules.get(tagName.toLowerCase()) != null) {
        ArrayList list = (ArrayList) rules.get(tagName.toLowerCase());
        assists.addAll(list);
      }
      AssistInfo[] info = new AssistInfo[assists.size()];
      for (int i = 0; i < assists.size(); i++) {
        String keyword = (String) assists.get(i);
        info[i] = new AssistInfo(value + keyword, keyword);
      }
      return info;
    }
    catch (Exception ex) {
    }
    return new AssistInfo[0];
  }
}
