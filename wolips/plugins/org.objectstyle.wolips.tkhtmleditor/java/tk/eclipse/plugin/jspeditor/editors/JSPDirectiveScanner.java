package tk.eclipse.plugin.jspeditor.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;

import tk.eclipse.plugin.htmleditor.ColorProvider;
import tk.eclipse.plugin.htmleditor.HTMLPlugin;
import tk.eclipse.plugin.htmleditor.editors.HTMLWhitespaceDetector;

/**
 * @author Naoki Takezoe
 */
public class JSPDirectiveScanner extends RuleBasedScanner {

	public JSPDirectiveScanner(ColorProvider provider){
		IToken string = provider.getToken(HTMLPlugin.PREF_COLOR_STRING);
		IToken script = provider.getToken(HTMLPlugin.PREF_COLOR_SCRIPT);
		List rules = new ArrayList();
		
		rules.add(new SingleLineRule("\"", "\"", string, '\\'));
		rules.add(new SingleLineRule("\'", "\'", string, '\\'));
		rules.add(new WhitespaceRule(new HTMLWhitespaceDetector()));
		
		WordRule delimitor = new WordRule(new IWordDetector(){
			public boolean isWordStart(char c){
				if(c=='<' || c=='%' || c=='@'){
					return true;
				}
				return false;
			}
			public boolean isWordPart(char c){
				if(c=='<' || c=='%' || c=='=' || c=='>' || c=='@'){
					return true;
				}
				return false;
			}
		});
		delimitor.addWord("<%@", script);
		delimitor.addWord("%>", script);
		rules.add(delimitor);
		
		
		setRules((IRule[])rules.toArray(new IRule[rules.size()]));
	}
}
