package tk.eclipse.plugin.htmleditor.editors;

import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.WhitespaceRule;

import tk.eclipse.plugin.htmleditor.ColorProvider;
import tk.eclipse.plugin.htmleditor.HTMLPlugin;

public class HTMLTagScanner extends RuleBasedScanner {
  public HTMLTagScanner(ColorProvider colorProvider) {
    IToken literal = colorProvider.getToken(HTMLPlugin.PREF_COLOR_STRING);
    IToken tagName = colorProvider.getToken(HTMLPlugin.PREF_COLOR_TAG);
    IToken attributeName = colorProvider.getToken(HTMLPlugin.PREF_COLOR_ATTRIBUTE);
    IToken ognlBinding = colorProvider.getToken(HTMLPlugin.PREF_COLOR_OGNL);
    IToken dynamicBinding = colorProvider.getToken(HTMLPlugin.PREF_COLOR_DYNAMIC);
    
    IRule[] rules = new IRule[] {
      new MultiLineRule("\"~" , "\"" , ognlBinding, '\\'),
      new MultiLineRule("\'~" , "\'" , ognlBinding, '\\'),
      new MultiLineRule("\"$" , "\"" , dynamicBinding, '\\'),
      new MultiLineRule("\'$" , "\'" , dynamicBinding, '\\'),
      new MultiLineRule("\"" , "\"" , literal, '\\'),
      new MultiLineRule("'"  , "'"  , literal, '\\'),
      new WhitespaceRule(new HTMLWhitespaceDetector()),
      new HTMLTagNameRule(tagName),
      new HTMLAttributeNameRule(attributeName)
    };
    
    setRules(rules);
  }
}
