package tk.eclipse.plugin.jspeditor.converters;

import java.util.Iterator;
import java.util.Map;

import jp.aonir.fuzzyxml.FuzzyXMLNode;
import tk.eclipse.plugin.jspeditor.editors.JSPInfo;

public class HTMLConverter extends AbstractCustomTagConverter {
	
	private String tagName = null;
	
	public HTMLConverter(String tagName){
		this.tagName = tagName;
	}
	
	protected String convertStartTag(Map attributes) {
		return "<" + createTag(tagName,attributes) + ">";
	}
	
	protected String convertEndTag() {
		if(tagName.indexOf(" ")!=-1){
			return "</" + tagName.substring(0,tagName.indexOf(" ")) + ">";
		} else {
			return "</" + tagName + ">";
		}
	}
	
	protected String createTag(String tagName,Map attributes){
		StringBuffer sb = new StringBuffer();
		sb.append(tagName);
		
		Iterator ite = attributes.keySet().iterator();
		while(ite.hasNext()){
			Object key = ite.next();
			sb.append(" " + key + "=\"" + attributes.get(key) + "\"");
		}
		
		return sb.toString();
	}
	
	public String process(Map attrs, FuzzyXMLNode[] children,JSPInfo info) {
		StringBuffer sb = new StringBuffer();
		sb.append(convertStartTag(attrs));
		sb.append(evalBody(children,info));
		sb.append(convertEndTag());
		return sb.toString();
	}
}
