package tk.eclipse.plugin.csseditor.editors;

import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.Token;

/**
 * @author Naoki Takezoe
 */
public class CSSPartitionScanner extends RuleBasedPartitionScanner {
	
	public final static String CSS_COMMENT = "__css_comment";
	
	public CSSPartitionScanner(){
		IToken comment  = new Token(CSS_COMMENT);
		
		IPredicateRule[] rules = new IPredicateRule[1];
		rules[0] = new MultiLineRule("/*" , "*/" ,comment);
		
		setPredicateRules(rules);
	}
}
