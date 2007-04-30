package tk.eclipse.plugin.csseditor.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;

import tk.eclipse.plugin.htmleditor.ColorProvider;
import tk.eclipse.plugin.htmleditor.HTMLPlugin;

/**
 * @author Naoki Takezoe
 */
public class CSSBlockScanner extends RuleBasedScanner {
	
	public CSSBlockScanner(ColorProvider colorProvider){
		List rules = createRules(colorProvider);
		setRules((IRule[])rules.toArray(new IRule[rules.size()]));
	}
	
	/**
	 * Creates the list of <code>IRule</code>.
	 * If you have to customize rules, override this method.
	 * 
	 * @param colorProvider ColorProvider
	 * @return the list of <code>IRule</code>
	 */
	protected List createRules(ColorProvider colorProvider){
		List rules = new ArrayList();
		rules.add(new CSSRule(
				colorProvider.getToken(HTMLPlugin.PREF_COLOR_CSSPROP), 
				colorProvider.getToken(HTMLPlugin.PREF_COLOR_CSSVALUE)));
		return rules;
	}
	
}
