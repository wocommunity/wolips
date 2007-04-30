package tk.eclipse.plugin.htmleditor.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.SingleLineRule;

import tk.eclipse.plugin.csseditor.editors.CSSBlockScanner;
import tk.eclipse.plugin.htmleditor.ColorProvider;
import tk.eclipse.plugin.htmleditor.HTMLPlugin;

/**
 * <code>RuleBasedScanner</code> for the inner CSS in the HTML.
 * 
 * @author Naoki Takezoe
 * @see 2.0.3
 */
public class InnerCSSScanner extends CSSBlockScanner {

	public InnerCSSScanner(ColorProvider colorProvider) {
		super(colorProvider);
	}
	
	protected List createRules(ColorProvider colorProvider) {
		IToken tag = colorProvider.getToken(HTMLPlugin.PREF_COLOR_TAG);
		IToken comment = colorProvider.getToken(HTMLPlugin.PREF_COLOR_CSSCOMMENT);
		
		List rules = new ArrayList();
		rules.add(new SingleLineRule("<style", ">", tag));
		rules.add(new SingleLineRule("</style", ">", tag));
		rules.add(new MultiLineRule("/*", "*/", comment));
		rules.addAll(super.createRules(colorProvider));
		
		return rules;
	}
}
