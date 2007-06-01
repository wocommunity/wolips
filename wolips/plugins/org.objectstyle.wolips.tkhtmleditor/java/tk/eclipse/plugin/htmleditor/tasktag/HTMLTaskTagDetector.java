package tk.eclipse.plugin.htmleditor.tasktag;

import jp.aonir.fuzzyxml.FuzzyXMLComment;
import jp.aonir.fuzzyxml.FuzzyXMLDocument;
import jp.aonir.fuzzyxml.FuzzyXMLElement;
import jp.aonir.fuzzyxml.FuzzyXMLNode;
import jp.aonir.fuzzyxml.FuzzyXMLParser;
import jp.aonir.fuzzyxml.FuzzyXMLText;

/**
 * {@link ITaskTagDetector} implementation for HTML / XML / JSP.
 * This detector supports following extensions:
 * 
 * <ul>
 *   <li>.html</li>
 *   <li>.htm</li>
 *   <li>.xml</li>
 *   <li>.tld</li>
 *   <li>.xsd</li>
 *   <li>.jsp</li>
 *   <li>.jspf</li>
 *   <li>.jspx</li>
 * </ul>
 * 
 * @author Naoki Takezoe
 */
public class HTMLTaskTagDetector extends AbstractTaskTagDetector {
	
	public HTMLTaskTagDetector(){
		addSupportedExtension("html");
		addSupportedExtension("htm");
		addSupportedExtension("xml");
		addSupportedExtension("tld");
		addSupportedExtension("xsd");
		addSupportedExtension("jsp");
		addSupportedExtension("jspf");
		addSupportedExtension("jspx");
	}
	
	@Override
  public void doDetect() throws Exception {
		FuzzyXMLDocument doc = new FuzzyXMLParser(false).parse(this._contents);
		processElement(doc.getDocumentElement());
	}
	
	private void processElement(FuzzyXMLElement element){
		FuzzyXMLNode[] children = element.getChildren();
		for(int i=0;i<children.length;i++){
			if(children[i] instanceof FuzzyXMLElement){
				processElement((FuzzyXMLElement)children[i]);
				
			} else if(children[i] instanceof FuzzyXMLComment){
				// for HTML/XML comment
				detectTaskTag(((FuzzyXMLComment)children[i]).getValue(), 
						children[i].getOffset());
				
			} else if(children[i] instanceof FuzzyXMLText){
				// for JSP comment
				String value = ((FuzzyXMLText)children[i]).getValue();
				if(value.startsWith("<%--")){
					if(value.endsWith("--%>")){
						value = value.substring(0, value.length()-4);
					}
					detectTaskTag(value, children[i].getOffset());
				}
			}
			// TODO Should support tags in Java comment
		}
	}
	
}
