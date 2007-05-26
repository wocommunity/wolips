package tk.eclipse.plugin.jspeditor.editors;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.aonir.fuzzyxml.FuzzyXMLAttribute;
import jp.aonir.fuzzyxml.FuzzyXMLDocument;
import jp.aonir.fuzzyxml.FuzzyXMLElement;
import jp.aonir.fuzzyxml.FuzzyXMLNode;
import jp.aonir.fuzzyxml.FuzzyXMLParser;

import org.eclipse.ui.IFileEditorInput;

import tk.eclipse.plugin.htmleditor.HTMLPlugin;
import tk.eclipse.plugin.htmleditor.HTMLUtil;
import tk.eclipse.plugin.htmleditor.ICustomTagConverter;
import tk.eclipse.plugin.htmleditor.ICustomTagConverterContributer;

/**
 * This class provides some utility methods to convert JSP to HTML for preview.
 */
public class JSPPreviewConverter {
	
	private static Pattern petternScript   = Pattern.compile("<%(.*?)%>",Pattern.DOTALL);
	private static Pattern petternTagBegin = Pattern.compile("<(|/)((\\w+?):(.*?))>");
	
	/**
	 * Converts JSP to HTML for preview.
	 * 
	 * @param jsp JSP
	 * @return converted HTML
	 */
	public static String convertJSP(IFileEditorInput input,String jsp){
		// get JSPInfo
		JSPInfo info = JSPInfo.getJSPInfo(input.getFile(),jsp);
		
		// remove JSP comments
		//jsp = jsp.replaceAll("<%--(.|\n|\r)*?--%>","");
		jsp = HTMLUtil.comment2space(jsp,false);
		
		// split by <body>
		String lower = jsp.toLowerCase();
		int index = lower.indexOf("<body");
		int end   = lower.indexOf("</body>");
		
		// if <body> doesn't exist, remove all
		if(index==-1){
			jsp = petternScript.matcher(jsp).replaceAll("");
			jsp = petternTagBegin.matcher(jsp).replaceAll("");
			return jsp;
		}
		if(end==-1){
			end = jsp.length();
		} else {
			end = end + 7;
		}
		
		String head = jsp.substring(0,index);
		String body = jsp.substring(index, end);
		
		// before <body>
		head = petternScript.matcher(head).replaceAll("");
		head = petternTagBegin.matcher(head).replaceAll("");
		
		// after <body>
		body = processScript(body);
		body = processTag(body,info);
		body = body.replaceAll("&amp;nbsp;","&nbsp;");
		body = body.replaceAll("&apos;"    ,"'");
		
		return "<html>" + head + body + "</html>";
	}
	
	/**
	 * Process scriptlet.
	 * 
	 * @param jsp JSP
	 * @return converted JSP
	 */
	private static String processScript(String jsp){
		StringBuffer sb = new StringBuffer();
		Matcher matcher = petternScript.matcher(jsp);
		int index = 0;
		while(matcher.find()){
			sb.append(jsp.substring(index,matcher.start()));
			sb.append(HTMLUtil.escapeHTML(matcher.group(0)));
			index = matcher.end();
		}
		if(index < jsp.length()-1){
			sb.append(jsp.substring(index));
		}
		return sb.toString();
	}
	
	/**
	 * This method should be private, but it's required to be public...
	 * 
	 * @param element
	 * @param info
	 * @return
	 */
	public static String processElement(FuzzyXMLElement element,JSPInfo info){
		StringBuffer sb = new StringBuffer();
		
		if(element.getName().indexOf(":")!=-1){
			String tagName = element.getName();
			String[] dim = tagName.split(":");
			
			// get URI from prefix
			String uri = info.getTaglibUri(dim[0]);
			// get converter
			ICustomTagConverter converter = null;
			if(uri!=null){
				ICustomTagConverterContributer contributer = HTMLPlugin.getDefault().getCustomTagContributer(uri);
				if(contributer!=null){
					converter = contributer.getConverter(dim[1]);
				}
			}
			if(converter!=null){
				HashMap<String, String> attrMap = new HashMap<String, String>();
				FuzzyXMLAttribute[] attrs = element.getAttributes();
				for(int i=0;i<attrs.length;i++){
					attrMap.put(attrs[i].getName(),attrs[i].getValue());
				}
				sb.append(converter.process(attrMap,element.getChildren(),info));
				return sb.toString();
			}
		}
		
		if(element.getChildren().length==0){
			sb.append(element.toXMLString());
		} else {
			sb.append(element2startTag(element));
			FuzzyXMLNode[] node = element.getChildren();
			for(int i=0;i<node.length;i++){
				if(node[i] instanceof FuzzyXMLElement){
					sb.append(processElement((FuzzyXMLElement)node[i],info));
				} else {
					sb.append(node[i].toXMLString());
				}
			}
			sb.append(element2closeTag(element));
		}
		
		return sb.toString();
	}
	
	private static String element2startTag(FuzzyXMLElement e){
		StringBuffer sb = new StringBuffer();
		sb.append("<" + e.getName());
		FuzzyXMLAttribute[] attr = e.getAttributes();
		for(int i=0;i<attr.length;i++){
			sb.append(" " + attr[i].getName() + "=\"" + attr[i].getValue() + "\"");
		}
		sb.append(">");
		return sb.toString();
	}
	
	private static String element2closeTag(FuzzyXMLElement e){
		return "</" + e.getName() + ">";
	}
	
	/**
	 * Process taglibs.
	 * 
	 * @param jsp  JSP
	 * @param info JSPInfo
	 * @return processed JSP
	 */
	private static String processTag(String jsp,JSPInfo info){
		FuzzyXMLDocument doc = new FuzzyXMLParser().parse(jsp);
		FuzzyXMLNode[] nodes = doc.getDocumentElement().getChildren();
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<nodes.length;i++){
			if(nodes[i] instanceof FuzzyXMLElement){
				sb.append(processElement((FuzzyXMLElement)nodes[i],info));
			}
		}
		return sb.toString();
	}

}
