package tk.eclipse.plugin.htmleditor.editors;

import java.util.List;

import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.SingleLineRule;

import tk.eclipse.plugin.htmleditor.ColorProvider;
import tk.eclipse.plugin.htmleditor.HTMLPlugin;
import tk.eclipse.plugin.jseditor.editors.JavaScriptScanner;

/**
 * <code>RuleBasedScanner</code> for the inner JavaScript in the HTML.
 * 
 * @author Naoki Takezoe
 * @see 2.0.3
 */
public class InnerJavaScriptScanner extends JavaScriptScanner {

	public InnerJavaScriptScanner(ColorProvider colorProvider) {
		super(colorProvider);
	}

	protected List createRules(ColorProvider colorProvider) {
		IToken tag = colorProvider.getToken(HTMLPlugin.PREF_COLOR_TAG);
		IToken comment = colorProvider.getToken(HTMLPlugin.PREF_COLOR_JSCOMMENT);
		
		List rules = super.createRules(colorProvider);
		rules.add(new SingleLineRule("<script", ">", tag));
		rules.add(new SingleLineRule("</script", ">", tag));
		rules.add(new MultiLineRule("/*", "*/", comment));
		
		return rules;
	}
	
	
}
