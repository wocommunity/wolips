package tk.eclipse.plugin.jseditor.editors;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ContextInformation;
import org.eclipse.jface.text.contentassist.ContextInformationValidator;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.swt.graphics.Image;

import tk.eclipse.plugin.htmleditor.HTMLPlugin;
import tk.eclipse.plugin.htmleditor.HTMLProjectParams;
import tk.eclipse.plugin.htmleditor.HTMLUtil;
import tk.eclipse.plugin.htmleditor.template.HTMLTemplateAssistProcessor;
import tk.eclipse.plugin.htmleditor.template.HTMLTemplateManager;
import tk.eclipse.plugin.htmleditor.template.JavaScriptContextType;
import tk.eclipse.plugin.jseditor.launch.JavaScriptLibraryTable;

/**
 * IContentAssistProcessor implementation for JavaScriptEditor.
 * 
 * @author Naoki Takezoe
 */
public class JavaScriptAssistProcessor extends HTMLTemplateAssistProcessor { /* implements IContentAssistProcessor {*/
	
	private static List<AssistInfo> staticAssistInfo = new ArrayList<AssistInfo>();
	
	// assist proposal types
	private static final int VARIABLE =  0;
	private static final int FUNCTION =  1;
	private static final int KEYWORD  =  2;
	private static final int OBJECT   =  3;
	private static final int UNDEF    = 99;
	
	private static final String[] GLOBAL_PROPERTIES = {
			"Infinity", "NaN", "undefined"
	};
	
	private static final String[] GLOBAL_FUNCTIONS = {
			"decodeURI()", "decodeURIComponent()",
			"encodeURI()", "encodeURIComponent()",
			"escape()", "unescape()", "eval()", "isNaN()",
			"parseFloat()", "parseInt()", "taint()", "untaint()",
	};
	
	private static final String[] CLASSES = {
			"Arguments", "Array", "Boolean", "Date", "Error",
			"Function", "Math", "NativeError", "Number", "Object", "RegExp", "String"
	};
	
	private static Map<String, AssistInfo[]> STATIC_MEMBERS = new HashMap<String, AssistInfo[]>();
	
	static {
//		// add keyword to static assist informations
//		for(int i=0;i<JavaScriptScanner.KEYWORDS.length;i++){
//			staticAssistInfo.add(new AssistInfo(KEYWORD, JavaScriptScanner.KEYWORDS[i]));
//		}
		for(int i=0;i<GLOBAL_PROPERTIES.length;i++){
			staticAssistInfo.add(new AssistInfo(VARIABLE, GLOBAL_PROPERTIES[i], GLOBAL_PROPERTIES[i].length()));
		}
		for(int i=0;i<GLOBAL_FUNCTIONS.length;i++){
			staticAssistInfo.add(new AssistInfo(FUNCTION, GLOBAL_FUNCTIONS[i], GLOBAL_FUNCTIONS[i].length()-1));
		}
		for(int i=0;i<CLASSES.length;i++){
			staticAssistInfo.add(new AssistInfo(OBJECT, CLASSES[i], CLASSES[i], CLASSES[i].length()));
		}
		
		AssistInfo[] math = {
				new AssistInfo(VARIABLE, "E - Math", "E"),
				new AssistInfo(VARIABLE, "LN10 - Math", "LN10"),
				new AssistInfo(VARIABLE, "LN2 - Math", "LN2"),
				new AssistInfo(VARIABLE, "LOG10E - Math", "LOG10E"),
				new AssistInfo(VARIABLE, "LOG2E - Math", "LOG2E"),
				new AssistInfo(VARIABLE, "PI - Math", "PI"),
				new AssistInfo(VARIABLE, "SQRT1_2 - Math", "SQRT1_2"),
				new AssistInfo(VARIABLE, "SQRT2 - Math", "SQRT2"),
				new AssistInfo(FUNCTION, "abs() - Math", "abs()"),
				new AssistInfo(FUNCTION, "acos() - Math", "acos()"),
				new AssistInfo(FUNCTION, "asin() - Math", "asin()"),
				new AssistInfo(FUNCTION, "atan() - Math", "atan()"),
				new AssistInfo(FUNCTION, "atan2() - Math", "atan2()"),
				new AssistInfo(FUNCTION, "ceil() - Math", "ceil()"),
				new AssistInfo(FUNCTION, "cos() - Math", "cos()"),
				new AssistInfo(FUNCTION, "exp() - Math", "exp()"),
				new AssistInfo(FUNCTION, "floor() - Math", "floor()"),
				new AssistInfo(FUNCTION, "log() - Math", "log()"),
				new AssistInfo(FUNCTION, "max() - Math", "max()"),
				new AssistInfo(FUNCTION, "min() - Math", "min()"),
				new AssistInfo(FUNCTION, "pow() - Math", "pow()"),
				new AssistInfo(FUNCTION, "random() - Math", "random()"),
				new AssistInfo(FUNCTION, "round() - Math", "round()"),
				new AssistInfo(FUNCTION, "sin() - Math", "sin()"),
				new AssistInfo(FUNCTION, "sqrt() - Math", "sqrt()"),
				new AssistInfo(FUNCTION, "tan() - Math", "tan()"),
		};
		STATIC_MEMBERS.put("Math", math);
		
		AssistInfo[] object = {
				new AssistInfo(VARIABLE, "constructor - Object", "constructor"),
				new AssistInfo(VARIABLE, "prototype - Object", "prototype"),
				new AssistInfo(FUNCTION, "hasOwnProperty() - Object", "hasOwnProperty()"),
//				new AssistInfo(FUNCTION, "eval() - Object", "eval()"),
				new AssistInfo(FUNCTION, "hasOwnProperty() - Object", "hasOwnProperty()"),
				new AssistInfo(FUNCTION, "isPrototyprOf() - Object", "isPrototyprOf()"),
				new AssistInfo(FUNCTION, "propertyIsEnumerable() - Object", "propertyIsEnumerable()"),
				new AssistInfo(FUNCTION, "toLocaleString() - Object", "toLocaleString()"),
				new AssistInfo(FUNCTION, "toSource() - Object", "toSource()"),
				new AssistInfo(FUNCTION, "toString() - Object", "toString()"),
				new AssistInfo(FUNCTION, "unwatch() - Object", "unwatch()"),
				new AssistInfo(FUNCTION, "valueOf() - Object", "valueOf()"),
				new AssistInfo(FUNCTION, "watch() - Object","watch()"),
		};
		STATIC_MEMBERS.put("Object", object);
	}
	
	private List<AssistInfo> functions = new ArrayList<AssistInfo>();
	
	/**
	 * Returns source code to parse from <code>ITextViewer</code>.
	 * <p>
	 * If you want to use this class with the your own editor 
	 * which supports the document contains JavaScript such as HTML/JSP,
	 * override this method as returns only JavaScript code.
	 * 
	 * @param viewer <code>ITextViewer</code>
	 * @return JavaScript source code
	 */
	protected String getSource(ITextViewer viewer){
		return viewer.getDocument().get();
	}
	
	@Override
  public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {
		List<ICompletionProposal> proposal = new ArrayList<ICompletionProposal>();
		String source = getSource(viewer);
		
		String[] words = getLastWord(viewer, offset);
		String last = words[0];
		String word = words[1];
		
		List<String> addedStrings = new ArrayList<String>();
		
		if(last.endsWith(".")){
			String objName = last.substring(0, last.length()-1);
			AssistInfo[] info = STATIC_MEMBERS.get(objName);
			if(info==null && !isNumeric(objName)){
				info = STATIC_MEMBERS.get("Object");
			}
			if(info!=null){
				for(int i=0;i<info.length;i++){
					if(info[i].replaceString.startsWith(word)){
						proposal.add(info[i].createCompletionProposal(offset, word));
					}
				}
			}
		} else {
			for(int i=0;i<staticAssistInfo.size();i++){
				AssistInfo info = staticAssistInfo.get(i);
				if(info.replaceString.startsWith(word)){
					proposal.add(info.createCompletionProposal(offset, word));
				}
			}
			
			JavaScriptModel model = new JavaScriptModel(source);
			JavaScriptContext context = model.getContextFromOffset(offset);
			if(context!=null){
				JavaScriptElement[] children = context.getVisibleElements();
				for(int i=0;i<children.length;i++){
					if(children[i].getName().startsWith(word)){
						int type = UNDEF;
						int position = 0;
						String replace = null;
						
						if(children[i] instanceof JavaScriptFunction){
							type = FUNCTION;
							replace = children[i].getName() + "()";
							if(((JavaScriptFunction)children[i]).getArguments().length()==0){
								position = replace.length();
							} else{
								position = replace.length() - 1;
							}
						} else if(children[i] instanceof JavaScriptVariable){
							type = VARIABLE;
							replace = children[i].getName();
							position = replace.length();
						}
						if(!addedStrings.contains(replace)){
							proposal.add(new CompletionProposal(
									replace, offset - word.length(), word.length(),
									position, getImageFromType(type),
									children[i].toString(), null, null));
							addedStrings.add(replace);
						}
					}
				}
			}
		}
		
		for(int i=0;i<functions.size();i++){
			AssistInfo info = functions.get(i);
			if(info.replaceString.startsWith(word) && !addedStrings.contains(info.replaceString)){
				proposal.add(info.createCompletionProposal(offset, word));
				addedStrings.add(info.replaceString);
			}
		}
		
		ICompletionProposal[] templates = super.computeCompletionProposals(viewer, offset);
		for(int i=0;i<templates.length;i++){
			proposal.add(templates[i]);
		}
		
		// sort
		HTMLUtil.sortCompilationProposal(proposal);
		ICompletionProposal[] prop = proposal.toArray(new ICompletionProposal[proposal.size()]);
		
		return prop;
	}
	
	/**
	 * Returns <code>Image</code> from the assist proposal type.
	 * 
	 * @param type KEYWORD, VARIABLE or FUNCTION
	 * @return Image from the type
	 */
	private static Image getImageFromType(int type){
		switch(type){
			case KEYWORD:
				return HTMLPlugin.getDefault().getImageRegistry().get(HTMLPlugin.ICON_VALUE);
			case VARIABLE:
				return HTMLPlugin.getDefault().getImageRegistry().get(HTMLPlugin.ICON_VARIABLE);
			case FUNCTION:
				return HTMLPlugin.getDefault().getImageRegistry().get(HTMLPlugin.ICON_FUNCTION);
			case OBJECT:
				return HTMLPlugin.getDefault().getImageRegistry().get(HTMLPlugin.ICON_CLASS);
			default:
				return null;
		}
	}
	
	private boolean isNumeric(String value){
		for(int i=0;i<value.length();i++){
			char c = value.charAt(i);
			if(c < '0' || c > '9'){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Cuts out the last word of caret position.
	 * 
	 * @param viewer ITextViewer
	 * @param offset the caret offset
	 * @return the last word of caret position
	 */
	protected String[] getLastWord(ITextViewer viewer, int offset){
		String source = viewer.getDocument().get();
		source = source.substring(0, offset);
		String last = "";
		
		int start = source.lastIndexOf('\n');
		if(start==-1){
			start = source.lastIndexOf('\r');
		}
		if(start==-1){
			start = 0;
		}
		
		StringBuffer sb = new StringBuffer();
		for(int i=start;i < offset;i++){
			char c = source.charAt(i);
			if(Character.isWhitespace(c) || !Character.isJavaIdentifierPart(c)){
				if(sb.length()!=0){
					if(c=='.'){
						sb.append(c);
					}
					last = sb.toString();
					sb.setLength(0);
				}
			} else {
				sb.append(c);
			}
		}
		return new String[]{last, sb.toString()};
	}
	
	/**
	 * The structure for assist informations.
	 */
	private static class AssistInfo {
		
		private int type;
		private int position;
		private String displayString;
		private String replaceString;
		
		public AssistInfo(int type, String string){
			this(type, string, string);
		}
		
		public AssistInfo(int type, String string, int position){
			this(type, string, string, position);
		}
		
		public AssistInfo(int type, String displayString, String replaceString){
			this(type, displayString, replaceString, 
					replaceString.endsWith("()") ? replaceString.length()-1 : replaceString.length());
		}
		
		public AssistInfo(int type, String displayString, String replaceString, int position){
			this.type = type;
			this.displayString = displayString;
			this.replaceString = replaceString;
			this.position = position;
		}
		
		public CompletionProposal createCompletionProposal(int offset, String word){
			return new CompletionProposal(
					replaceString, offset - word.length(), word.length(),
					position, getImageFromType(type),
					displayString, null, null);
		}
	}
	
	@Override
  public IContextInformation[] computeContextInformation(ITextViewer viewer, int offset) {
		return new ContextInformation[0];
	}

	@Override
  public char[] getCompletionProposalAutoActivationCharacters() {
		return new char[0];
	}

	@Override
  public char[] getContextInformationAutoActivationCharacters() {
		return new char[0];
	}

	@Override
  public String getErrorMessage() {
		return "error";
	}

	@Override
  public IContextInformationValidator getContextInformationValidator() {
		return new ContextInformationValidator(this);
	}
	
	@Override
  protected TemplateContextType getContextType(ITextViewer viewer, IRegion region) {
		HTMLTemplateManager manager = HTMLTemplateManager.getInstance();
		return manager.getContextTypeRegistry().getContextType(JavaScriptContextType.CONTEXT_TYPE);
	}
	
	/**
	 * Updates internal informations.
	 * 
	 * @param file the editing file
	 */
	public void update(IFile file){
		try {
			
			HTMLProjectParams params = new HTMLProjectParams(file.getProject());
			String[] javaScripts = params.getJavaScripts();
			IWorkspaceRoot wsroot = ResourcesPlugin.getWorkspace().getRoot();
			functions.clear();
			
			for(int i=0;i<javaScripts.length;i++){
				InputStream in = null;
				if(javaScripts[i].startsWith(JavaScriptLibraryTable.PREFIX)){
					IResource resource = wsroot.findMember(javaScripts[i].substring(JavaScriptLibraryTable.PREFIX.length()));
					if(resource!=null && resource instanceof IFile && resource.exists()){
						in = ((IFile)resource).getContents();
					}
				} else {
					in = new FileInputStream(javaScripts[i]);
				}
				String source = new String(HTMLUtil.readStream(in));
				JavaScriptModel model = new JavaScriptModel(source);
				JavaScriptElement[] elements = model.getChildren();
				
				for(int j=0;j<elements.length;j++){
					if(elements[j] instanceof JavaScriptFunction){
						String replace = elements[j].getName() + "()";
						int position = 0;
						if(((JavaScriptFunction)elements[j]).getArguments().length()==0){
							position = replace.length();
						} else{
							position = replace.length() - 1;
						}
						functions.add(new AssistInfo(
								FUNCTION, elements[j].toString(), replace, position));
					}
				}
			}
		} catch(Exception ex){
			HTMLPlugin.logException(ex);
		}
	}
	
}
