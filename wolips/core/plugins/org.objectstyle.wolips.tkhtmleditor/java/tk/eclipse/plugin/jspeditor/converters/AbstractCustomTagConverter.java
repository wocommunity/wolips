package tk.eclipse.plugin.jspeditor.converters;

import java.util.Iterator;
import java.util.Map;

import jp.aonir.fuzzyxml.FuzzyXMLElement;
import jp.aonir.fuzzyxml.FuzzyXMLNode;
import jp.aonir.fuzzyxml.internal.RenderContext;
import tk.eclipse.plugin.htmleditor.ICustomTagConverter;
import tk.eclipse.plugin.jspeditor.editors.JSPInfo;
import tk.eclipse.plugin.jspeditor.editors.JSPPreviewConverter;

public abstract class AbstractCustomTagConverter implements ICustomTagConverter {

  protected String evalBody(FuzzyXMLNode child, JSPInfo info) {
    return evalBody(new FuzzyXMLNode[] { child }, info);
  }

  protected String evalBody(FuzzyXMLNode[] children, JSPInfo info) {
    if (children == null) {
      return "";
    }
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < children.length; i++) {
      if (children[i] == null) {
        continue;
      }
      else if (children[i] instanceof FuzzyXMLElement) {
        sb.append(JSPPreviewConverter.processElement((FuzzyXMLElement) children[i], info));
      }
      else {
        sb.append(children[i].toXMLString(new RenderContext(true)));
      }
    }
    return sb.toString();
  }

  protected String getAttribute(Map attrs) {
    StringBuffer sb = new StringBuffer();
    Iterator ite = attrs.keySet().iterator();
    while (ite.hasNext()) {
      String key = (String) ite.next();
      if (key.equals("styleClass")) {
        sb.append(" class = \"" + attrs.get(key) + "\"");
      }
      else {
        sb.append(" " + key + " = \"" + attrs.get(key) + "\"");
      }
    }
    return sb.toString();
  }

}
