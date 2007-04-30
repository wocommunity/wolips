package tk.eclipse.plugin.jspeditor.editors;

import java.util.ArrayList;

import jp.aonir.fuzzyxml.FuzzyXMLComment;
import jp.aonir.fuzzyxml.FuzzyXMLDocType;
import jp.aonir.fuzzyxml.FuzzyXMLElement;
import jp.aonir.fuzzyxml.FuzzyXMLNode;
import jp.aonir.fuzzyxml.FuzzyXMLText;

import org.eclipse.swt.graphics.Image;

import tk.eclipse.plugin.htmleditor.HTMLPlugin;
import tk.eclipse.plugin.htmleditor.editors.HTMLOutlinePage;

public class JSPOutlinePage extends HTMLOutlinePage {
	
	public JSPOutlinePage(JSPSourceEditor editor) {
		super(editor);
	}
	
	protected Image getNodeImage(FuzzyXMLNode element){
		if(element instanceof FuzzyXMLElement){
			return super.getNodeImage(element);
		} else if(element instanceof FuzzyXMLText){
			FuzzyXMLText t = (FuzzyXMLText)element;
			if(t.getValue().startsWith("<%--")){
				return HTMLPlugin.getDefault().getImageRegistry().get(HTMLPlugin.ICON_COMMENT);
			}
			return HTMLPlugin.getDefault().getImageRegistry().get(HTMLPlugin.ICON_TAG);
		}
		return super.getNodeImage(element);
	}
	
	protected String getNodeText(FuzzyXMLNode node){
		if(node instanceof FuzzyXMLText){
			String text = ((FuzzyXMLText)node).getValue();
			if(text.startsWith("<%--")){
				return "#comment";
			} else if(text.startsWith("<%@")){
				return "jsp:directive";
			} else if(text.startsWith("<%=")){
				return "jsp:expression";
			} else if(text.startsWith("<%")){
				return "jsp:scriptlet";
			}
		}
		return super.getNodeText(node);
	}
	
	protected Object[] getNodeChildren(FuzzyXMLElement element){
		ArrayList children = new ArrayList();
		FuzzyXMLNode[] nodes = element.getChildren();
		for(int i=0;i<nodes.length;i++){
			if(nodes[i] instanceof FuzzyXMLElement){
				children.add(nodes[i]);
			} else if(nodes[i] instanceof FuzzyXMLText && ((FuzzyXMLText)nodes[i]).getValue().startsWith("<%")){
				children.add(nodes[i]);
			} else if(nodes[i] instanceof FuzzyXMLDocType){
				children.add(nodes[i]);
			} else if(nodes[i] instanceof FuzzyXMLComment){
				children.add(nodes[i]);
			}
		}
		return (FuzzyXMLNode[])children.toArray(new FuzzyXMLNode[children.size()]);
	}
	

}
