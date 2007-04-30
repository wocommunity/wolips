package tk.eclipse.plugin.dtdeditor.editors;

import tk.eclipse.plugin.htmleditor.ColorProvider;
import tk.eclipse.plugin.htmleditor.HTMLPlugin;
import tk.eclipse.plugin.htmleditor.assist.HTMLAssistProcessor;
import tk.eclipse.plugin.htmleditor.editors.HTMLConfiguration;
import tk.eclipse.plugin.htmleditor.editors.HTMLTagScanner;

/**
 * @author Naoki Takezoe
 */
public class DTDConfiguration extends HTMLConfiguration {
	
	private HTMLTagScanner tagScanner;
	
	public DTDConfiguration(ColorProvider colorProvider) {
		super(colorProvider);
	}
	
	protected HTMLTagScanner getTagScanner() {
		if (tagScanner == null) {
			tagScanner = new DTDTagScanner(getColorProvider());
			tagScanner.setDefaultReturnToken(
					getColorProvider().getToken(HTMLPlugin.PREF_COLOR_TAG));
		}
		return tagScanner;
	}
	
	protected HTMLAssistProcessor createAssistProcessor() {
		return new DTDAssistProcessor();
	}
}
