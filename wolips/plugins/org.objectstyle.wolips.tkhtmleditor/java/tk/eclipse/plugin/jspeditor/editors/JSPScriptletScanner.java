package tk.eclipse.plugin.jspeditor.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;

import tk.eclipse.plugin.htmleditor.ColorProvider;
import tk.eclipse.plugin.htmleditor.HTMLPlugin;
import tk.eclipse.plugin.htmleditor.editors.JavaWhitespaceDetector;
import tk.eclipse.plugin.htmleditor.editors.JavaWordDetector;

/**
 * @author Naoki Takezoe
 */
public class JSPScriptletScanner extends RuleBasedScanner {
	
	private static String[] KEYWORDS = {
			"abstract",
			"break",
			"case", "catch", "class", "const", "continue",
			"default", "do",
			"else", "extends",
			"final", "finally", "for",
			"goto",
			"if", "implements", "import", "instanceof", "interface",
			"native", "new",
			"package", "private", "protected", "public",
			"static", "super", "switch", "synchronized",
			"this", "throw", "throws", "transient", "try",
			"volatile",
			"while",
			"void", "boolean", "char", "byte", "short", "strictfp", "int", "long", "float", "double",
			"return",
			"true", "false"
	};
	
	public JSPScriptletScanner(ColorProvider provider){
		IToken normal  = provider.getToken(HTMLPlugin.PREF_COLOR_FG);
		IToken comment = provider.getToken(HTMLPlugin.PREF_JSP_COMMENT);
		IToken string  = provider.getToken(HTMLPlugin.PREF_JSP_STRING);
		IToken keyword = provider.getToken(HTMLPlugin.PREF_JSP_KEYWORD);
		IToken script  = provider.getToken(HTMLPlugin.PREF_COLOR_SCRIPT);
		
		List rules = new ArrayList();
		
		rules.add(new MultiLineRule("/*" , "*/" , comment));
		rules.add(new EndOfLineRule("//", comment));
		rules.add(new SingleLineRule("\"", "\"", string, '\\'));
		rules.add(new SingleLineRule("\'", "\'", string, '\\'));
		
		WordRule wordRule = new WordRule(new JavaWordDetector(), normal);
		for(int i=0;i<KEYWORDS.length;i++){
			wordRule.addWord(KEYWORDS[i], keyword);
		}
		rules.add(wordRule);
		
		WordRule delimitor = new WordRule(new IWordDetector(){
			public boolean isWordStart(char c){
				if(c=='<' || c=='%'){
					return true;
				}
				return false;
			}
			public boolean isWordPart(char c){
				if(c=='<' || c=='%' || c=='=' || c=='>'){
					return true;
				}
				return false;
			}
		}, normal);
		delimitor.addWord("<%=", script);
		delimitor.addWord("<%", script);
		delimitor.addWord("%>", script);
		rules.add(delimitor);
		
		rules.add(new WhitespaceRule(new JavaWhitespaceDetector()));
		setRules((IRule[])rules.toArray(new IRule[rules.size()]));
	}
}
