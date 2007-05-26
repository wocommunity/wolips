package tk.eclipse.plugin.dtdeditor.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.Token;

import tk.eclipse.plugin.htmleditor.editors.HTMLPartitionScanner;

/**
 * 
 * @author Naoki Takezoe
 */
public class DTDPartitionScanner extends RuleBasedPartitionScanner {
	
	public DTDPartitionScanner(){
		IToken htmlComment = new Token(HTMLPartitionScanner.HTML_COMMENT);
		IToken htmlTag = new Token(HTMLPartitionScanner.HTML_TAG);
//		IToken defaultToken = new Token(IDocument.DEFAULT_CONTENT_TYPE);
		
		List<IPredicateRule> rules = new ArrayList<IPredicateRule>();
		
		rules.add(new MultiLineRule("<!--", "-->", htmlComment));
		rules.add(new MultiLineRule("<",">", htmlTag));
		
		setPredicateRules(rules.toArray(new IPredicateRule[rules.size()]));
	}

}
