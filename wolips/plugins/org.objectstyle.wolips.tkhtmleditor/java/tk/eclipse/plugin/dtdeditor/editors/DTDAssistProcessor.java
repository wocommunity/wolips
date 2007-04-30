package tk.eclipse.plugin.dtdeditor.editors;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.aonir.fuzzyxml.internal.FuzzyXMLUtil;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

import tk.eclipse.plugin.htmleditor.HTMLPlugin;
import tk.eclipse.plugin.htmleditor.HTMLUtil;
import tk.eclipse.plugin.htmleditor.assist.HTMLAssistProcessor;
import tk.eclipse.plugin.htmleditor.editors.HTMLSourceEditor;

/**
 * 
 * @author Naoki Takezoe
 */
public class DTDAssistProcessor extends HTMLAssistProcessor {
	
	private static final int UNDEF = 0;
	private static final int ELEMENT_ELEMENT  = 1;
	private static final int ELEMENT_TYPE     = 2;
	private static final int ELEMENT_CONTENT  = 3;
	private static final int ATTLIST_ELEMENT  = 4;
	private static final int ATTLIST_ATTRNAME = 5;
	private static final int ATTLIST_ATTRTYPE = 6;
	private static final int ATTLIST_ATTROMIT = 7;
	
	private static String[] DECLS = {"ELEMENT", "ATTLIST","ENTITY","NOTATION"};
	private static String[] ATTR_TYPES = {"CDATA","ID","IDREF","IDREFS","ENTITY","ENTITIES","NMTOKEN","NMTOKENS"};
	private static String[] ATTR_OMITS = {"#REQUIRED","#IMPLIED","#FIXED \"\"","\"\""};
	private static String[] ELEM_TYPES = {"ANY", "EMPTY", "(#PCDATA)", "()"};
	
	private Pattern elementPattern = Pattern.compile("<!ELEMENT\\s+(.*?)\\s");
	private Pattern entityPattern = Pattern.compile("<!ENTITY\\s+%\\s(.*?)\\s");
	
	private List elements = new ArrayList();
	private List entities = new ArrayList();
	
	public void update(HTMLSourceEditor editor, String source){
		elements.clear();
		source = FuzzyXMLUtil.comment2space(source, false);
		Matcher matcher = elementPattern.matcher(source);
		while(matcher.find()){
			elements.add(matcher.group(1).trim());
		}
		
		entities.clear();
		matcher = entityPattern.matcher(source);
		while(matcher.find()){
			entities.add("%" + matcher.group(1).trim());
		}
	}
	
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer,int documentOffset) {
		
		String text = FuzzyXMLUtil.comment2space(viewer.getDocument().get(), false);
		text = text.substring(0, documentOffset);
		
		String word = getWord(text);
		int context = getContext(text);
		
		List list = new ArrayList();
		
		if(word.startsWith("<") || context==UNDEF){
			for(int i=0;i<DECLS.length;i++){
				if(("<!" + DECLS[i]).startsWith(word)){
					list.add(createProposal("<!" + DECLS[i] + " >", "<!" + DECLS[i] + " ... >", word, documentOffset,
							DECLS[i].length() + 3, HTMLPlugin.ICON_TAG));
				}
			}
		} else if(context==ATTLIST_ELEMENT || context==ELEMENT_CONTENT){
			for(int i=0;i<elements.size();i++){
				String element = (String)elements.get(i);
				if(element.startsWith(word)){
					list.add(createProposal(element, element, word, documentOffset, element.length(), HTMLPlugin.ICON_ELEMENT));
				}
			}
		} else if(context==ATTLIST_ATTRTYPE){
			for(int i=0;i<ATTR_TYPES.length;i++){
				if(ATTR_TYPES[i].startsWith(word)){
					list.add(createProposal(ATTR_TYPES[i], ATTR_TYPES[i], word, documentOffset, ATTR_TYPES[i].length(), HTMLPlugin.ICON_VALUE));
				}
			}
			for(int i=0;i<entities.size();i++){
				String value = (String)entities.get(i);
				if(value.startsWith(word)){
					list.add(createProposal(value, value, word, documentOffset, value.length(), HTMLPlugin.ICON_VALUE));
				}
			}
		} else if(context==ATTLIST_ATTROMIT){
			for(int i=0;i<ATTR_OMITS.length;i++){
				if(ATTR_OMITS[i].startsWith(word) && !word.startsWith("\"")){
					int position = ATTR_OMITS[i].indexOf('"');
					if(position < 0){
						position = ATTR_OMITS[i].length();
					} else {
						position++;
					}
					list.add(createProposal(ATTR_OMITS[i], ATTR_OMITS[i], word, documentOffset, position, HTMLPlugin.ICON_VALUE));
				}
			}
		} else if(context==ELEMENT_TYPE){
			for(int i=0;i<ELEM_TYPES.length;i++){
				if(ELEM_TYPES[i].startsWith(word)){
					int position = ELEM_TYPES[i].length();
					if(ELEM_TYPES[i].equals("()")){
						position = 1;
					}
					list.add(createProposal(ELEM_TYPES[i], ELEM_TYPES[i], word, documentOffset, position, HTMLPlugin.ICON_VALUE));
				}
			}
		}
		
		HTMLUtil.sortCompilationProposal(list);
		ICompletionProposal[] prop = (ICompletionProposal[])list.toArray(new ICompletionProposal[list.size()]);
		return prop;
	}
	
	private static CompletionProposal createProposal(String text, String display, String word, int offset, int position, String image){
		return new CompletionProposal(
				text, offset - word.length(), word.length(), position,
				HTMLPlugin.getDefault().getImageRegistry().get(image), display, 
				null, null);
	}
	
	protected String getWord(String text){
		
		StringBuffer sb = new StringBuffer();
		
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if(Character.isWhitespace(c) || c=='(' || c==')' || c=='|' || c==','){
				if(sb.length() > 0){
					sb.setLength(0);
				}
			} else {
				sb.append(c);
			}
		}
		
		return sb.toString();
	}
	
	protected int getContext(String text){
		
		StringBuffer sb = new StringBuffer();
		int context = UNDEF;
		boolean flag = false;
		
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if((Character.isWhitespace(c) || c=='(' || c==')') && !flag){
				if(c=='(' || c==')'){ sb.append(c); }
				if(sb.length() > 0){
					context = judgeContext(sb.toString(), context);
					sb.setLength(0);
				}
			} else {
				if(c=='"'){
					flag = !flag;
				}
				sb.append(c);
			}
		}
		
		return context;
	}
	
	private int judgeContext(String word, int prev){
		if(word.equals("<!ELEMENT")){
			return ELEMENT_ELEMENT;
		} else if(word.equals("<!ATTLIST")){
			return ATTLIST_ELEMENT;
		} else if(word.startsWith("<!")){
			return UNDEF;
		} else if(prev==ELEMENT_ELEMENT){
			return ELEMENT_TYPE;
		} else if(prev==ELEMENT_TYPE && word.equals("(")){
			return ELEMENT_CONTENT;
		} else if(prev==ELEMENT_CONTENT && !word.endsWith(")")){
			return ELEMENT_CONTENT;
		} else if(prev==ATTLIST_ELEMENT){
			return ATTLIST_ATTRNAME;
		} else if(prev==ATTLIST_ATTRNAME){
			return ATTLIST_ATTRTYPE;
		} else if(prev==ATTLIST_ATTRTYPE){
			return ATTLIST_ATTROMIT;
		} else if(prev==ATTLIST_ATTROMIT){
			if(word.startsWith("#FIXED")){
				return ATTLIST_ATTROMIT;
			} else {
				return ATTLIST_ATTRNAME;
			}
		}
		return UNDEF;
	}

	/* (non-Javadoc)
	 * @see tk.eclipse.plugin.htmleditor.assist.HTMLAssistProcessor#enableTemplate()
	 */
	public boolean enableTemplate() {
		return false;
	}
}
