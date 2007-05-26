package tk.eclipse.plugin.jseditor.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.WordRule;

import tk.eclipse.plugin.htmleditor.ColorProvider;
import tk.eclipse.plugin.htmleditor.HTMLPlugin;
import tk.eclipse.plugin.htmleditor.editors.JavaWordDetector;

/**
 * 
 * @author Naoki Takezoe
 */
public class JavaScriptScanner extends RuleBasedScanner {
	
	public static final String KEYWORDS[] = {
			"abstract",
			"boolean", "break", "byte",
			"case", "catch", "char", "class", "const", "continue",
			"default", "do", "double",
			"else", "extends",
			"false", "final", "finally", "float", "for", "function",
			"goto", "if", "implements", "import", "in", "instanceof", "int", "interface",
			"long",
			"native", "new", "null",
			"package", "private", "protected", "prototype", "public",
			"return", "short", "static", "super", "switch", "synchronized",
			"this", "throw", "throws", "transient", "true", "try",
			"var", "void", "while", "with"
	};
	
	public JavaScriptScanner(ColorProvider colorProvider){
		List<IRule> rules = createRules(colorProvider);
		setRules(rules.toArray(new IRule[rules.size()]));
	}
	
	/**
	 * Creates the list of <code>IRule</code>.
	 * If you have to customize rules, override this method.
	 * 
	 * @param colorProvider ColorProvider
	 * @return the list of <code>IRule</code>
	 */
	protected List<IRule> createRules(ColorProvider colorProvider){
		IToken normal  = colorProvider.getToken(HTMLPlugin.PREF_COLOR_FG);
		IToken string  = colorProvider.getToken(HTMLPlugin.PREF_COLOR_JSSTRING);
		IToken comment = colorProvider.getToken(HTMLPlugin.PREF_COLOR_JSCOMMENT);
		IToken keyword = colorProvider.getToken(HTMLPlugin.PREF_COLOR_JSKEYWORD);
		
		List<IRule> rules = new ArrayList<IRule>();
		rules.add(new SingleLineRule("\"", "\"", string, '\\'));
		rules.add(new SingleLineRule("'", "'", string, '\\'));
		rules.add(new EndOfLineRule("//", comment));
		
		WordRule wordRule = new WordRule(new JavaWordDetector(), normal);
		for(int i=0;i<KEYWORDS.length;i++){
			wordRule.addWord(KEYWORDS[i], keyword);
		}
		rules.add(wordRule);
		return rules;
	}
	
}
