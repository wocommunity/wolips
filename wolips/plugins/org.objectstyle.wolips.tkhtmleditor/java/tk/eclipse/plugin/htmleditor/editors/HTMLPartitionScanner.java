package tk.eclipse.plugin.htmleditor.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.Token;

/**
 * The partition scanner for the <code>HTMLSourceEditor</code>.
 * And defines static variables for the parted tokens.
 * 
 * @author Naoki Takezoe
 */
public class HTMLPartitionScanner extends RuleBasedPartitionScanner {
	
	public final static String HTML_DEFAULT   = "__html_default";
	public final static String HTML_COMMENT   = "__html_comment";
	public final static String HTML_TAG       = "__html_tag";
	public final static String HTML_SCRIPT    = "__html_script";
	public final static String HTML_DOCTYPE   = "__html_doctype";
	public final static String HTML_DIRECTIVE = "__html_directive";
	public final static String JAVASCRIPT     = "__html_javascript";
	public final static String HTML_CSS       = "__html_css";
	
	/**
	 * The constructor.
	 */
	public HTMLPartitionScanner() {

		IToken htmlComment   = new Token(HTML_COMMENT);
		IToken htmlTag       = new Token(HTML_TAG);
		IToken htmlScript    = new Token(HTML_SCRIPT);
		IToken htmlDoctype   = new Token(HTML_DOCTYPE);
		IToken htmlDirective = new Token(HTML_DIRECTIVE);
		IToken javaScript    = new Token(JAVASCRIPT);
		IToken htmlCss       = new Token(HTML_CSS);

		List rules = new ArrayList();

		rules.add(new MultiLineRule("<!--", "-->", htmlComment));
		rules.add(new MultiLineRule("<%--", "--%>", htmlComment));
		rules.add(new DocTypeRule(htmlDoctype));
		rules.add(new MultiLineRule("<%@", "%>", htmlDirective));
		rules.add(new MultiLineRule("<%", "%>", htmlScript));
		rules.add(new MultiLineRule("<![CDATA[", "]]>", htmlDoctype));
		rules.add(new MultiLineRule("<?xml", "?>", htmlDoctype));
		rules.add(new MultiLineRule("<script", "</script>", javaScript));
		rules.add(new MultiLineRule("<style", "</style>", htmlCss));
		rules.add(new TagRule(htmlTag));
		
		setPredicateRules((IPredicateRule[])rules.toArray(new IPredicateRule[rules.size()]));
	}
}
