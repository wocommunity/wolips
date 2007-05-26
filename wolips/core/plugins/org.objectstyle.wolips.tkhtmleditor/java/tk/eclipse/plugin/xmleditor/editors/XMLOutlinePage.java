package tk.eclipse.plugin.xmleditor.editors;

import jp.aonir.fuzzyxml.FuzzyXMLElement;
import jp.aonir.fuzzyxml.FuzzyXMLNode;

import org.eclipse.swt.graphics.Image;

import tk.eclipse.plugin.htmleditor.HTMLPlugin;
import tk.eclipse.plugin.htmleditor.editors.HTMLOutlinePage;

/**
 * The content outline page implementation for the <code>XMLEditor</code>.
 * 
 * @author Naoki Takezoe
 */
public class XMLOutlinePage extends HTMLOutlinePage {
	
	public XMLOutlinePage(XMLEditor editor) {
		super(editor);
	}
	
	@Override
  protected Image getNodeImage(FuzzyXMLNode element){
		if(element instanceof FuzzyXMLElement){
			return HTMLPlugin.getDefault().getImageRegistry().get(HTMLPlugin.ICON_TAG);
		}
		return super.getNodeImage(element);
	}
	
	@Override
  protected boolean isHTML(){
		return false;
	}
}
