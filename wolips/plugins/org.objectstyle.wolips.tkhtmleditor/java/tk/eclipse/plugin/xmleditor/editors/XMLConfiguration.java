package tk.eclipse.plugin.xmleditor.editors;

import tk.eclipse.plugin.htmleditor.ColorProvider;
import tk.eclipse.plugin.htmleditor.HTMLHyperlinkDetector;
import tk.eclipse.plugin.htmleditor.assist.HTMLAssistProcessor;
import tk.eclipse.plugin.htmleditor.editors.HTMLConfiguration;

/**
 * The editor configuration for the <code>XMLEditor</code>.
 * 
 * @author Naoki Takezoe
 * @see tk.eclipse.plugin.xmleditor.editors.XMLAssistProcessor
 */
public class XMLConfiguration extends HTMLConfiguration {
	
	private ClassNameHyperLinkProvider classNameHyperlinkProvider = null;
	
	public XMLConfiguration(ColorProvider colorProvider) {
		super(colorProvider);
	}
	
	/**
	 * Returns the <code>XMLAssistProcessor</code> as the assist processor.
	 * 
	 * @return the <code>XMLAssistProcessor</code>
	 */
	@Override
  protected HTMLAssistProcessor createAssistProcessor() {
		return new XMLAssistProcessor();
	}
	
	public ClassNameHyperLinkProvider getClassNameHyperlinkProvider(){
		return this.classNameHyperlinkProvider;
	}
	
	/**
	 * Returns the <code>HTMLHyperlinkDetector</code> which has
	 * <code>ClassNameHyperLinkProvider</code>.
	 * <p>
	 * Provides the classname hyperlink for the following attributes.
	 * <ul>
	 *   <li>type</li>
	 *   <li>class</li>
	 *   <li>classname</li>
	 *   <li>bean</li>
	 *   <li>component</li>
	 * </li>
	 */	
	@Override
  protected HTMLHyperlinkDetector createHyperlinkDetector() {
		if(this.classNameHyperlinkProvider == null){
			this.classNameHyperlinkProvider = new ClassNameHyperLinkProvider();
		}
		HTMLHyperlinkDetector detector = super.createHyperlinkDetector();
		detector.addHyperlinkProvider(this.classNameHyperlinkProvider);
		return detector;
	}
	
}
