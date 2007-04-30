package tk.eclipse.plugin.dtdeditor.editors;

import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.WhitespaceRule;

import tk.eclipse.plugin.htmleditor.ColorProvider;
import tk.eclipse.plugin.htmleditor.HTMLPlugin;
import tk.eclipse.plugin.htmleditor.editors.HTMLTagScanner;
import tk.eclipse.plugin.htmleditor.editors.HTMLWhitespaceDetector;

/**
 * @author Naoki Takezoe
 */
public class DTDTagScanner extends HTMLTagScanner {

	public DTDTagScanner(ColorProvider colorProvider) {
		super(colorProvider);
		
		IToken string = colorProvider.getToken(HTMLPlugin.PREF_COLOR_STRING);
		
		IRule[] rules = new IRule[3];
		rules[0] = new MultiLineRule("\"" , "\"" , string, '\\');
		rules[1] = new MultiLineRule("(" , ")" , string);
		rules[2] = new WhitespaceRule(new HTMLWhitespaceDetector());
		
		setRules(rules);
	}
	
}
