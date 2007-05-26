package tk.eclipse.plugin.jspeditor.editors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.core.DefaultWorkingCopyOwner;
import org.eclipse.jdt.internal.ui.text.java.AbstractJavaCompletionProposal;
import org.eclipse.jdt.ui.text.java.CompletionProposalCollector;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ContextInformation;
import org.eclipse.jface.text.contentassist.ContextInformationValidator;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;

import tk.eclipse.plugin.htmleditor.HTMLPlugin;
import tk.eclipse.plugin.htmleditor.HTMLUtil;

/**
 * Provides code completion for JSP directives.
 * 
 * @author Naoki Takezoe
 */
public class JSPDirectiveAssistProcessor implements IContentAssistProcessor {

	private Image tagImage;
	private Image attrImage;
	private Image valueImage;
	private IFile file;
	
	private static final String DIRECTIVES[] = {
			"include", "page", "taglib"
	};
	
	private static final Map<String, String[]> ATTRIBUTES = new HashMap<String, String[]>();
	static {
		ATTRIBUTES.put("include", new String[]{"file"});
		ATTRIBUTES.put("page", new String[]{"language","extends","import","session","buffer","autoFlush","isThreadSafe",
				"info","errorPage","isErrorPage","contentType","pageEncoding"});
		ATTRIBUTES.put("taglib", new String[]{"prefix","tagdir","uri"});
	}
	
	public JSPDirectiveAssistProcessor(){
		tagImage   = HTMLPlugin.getDefault().getImageRegistry().get(HTMLPlugin.ICON_TAG);
		attrImage  = HTMLPlugin.getDefault().getImageRegistry().get(HTMLPlugin.ICON_ATTR);
		valueImage = HTMLPlugin.getDefault().getImageRegistry().get(HTMLPlugin.ICON_VALUE);
	}
	
	public void update(JSPSourceEditor editor){
		IEditorInput input = editor.getEditorInput();
		if(input instanceof IFileEditorInput){
			this.file = ((IFileEditorInput)input).getFile();
		}
	}
	
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {
		String text = viewer.getDocument().get().substring(0, offset);
		int flag = 0;
		StringBuffer sb = new StringBuffer();
		String directive = "";
		String attrName  = "";
		//String attrValue = "";
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if(c=='<'){
				directive = "";
				flag = 1;
			} else if(flag==1 && c=='%'){
				flag = 2;
			} else if(flag==2 && c=='@'){
				flag = 3;
			} else if(flag==3 && c=='%'){
				sb.setLength(0);
				flag = 0;
			} else if(flag==3 && (c==' ' || c=='\t' || c=='\r' || c=='\n' || c=='=')){
				if(directive.equals("")){
					directive = sb.toString().trim();
				} else {
					attrName = sb.toString().trim();
					if(!attrName.equals("")){
						flag = 4;
					}
				}
				sb.setLength(0);
			} else if(flag==4 && c=='"'){
				sb.append(c);
				flag = 5;
			} else if(flag==5 && c=='"'){
				sb.setLength(0);
				flag = 3;
			} else if(flag==3 || flag==5){
				sb.append(c);
			}
		}
		
		String lastWord = sb.toString().trim();
		
		if(directive.equals("")){
			return getDirectives(lastWord, offset);
		} else if(directive.equals("page") && attrName.equals("import") && lastWord.startsWith("\"")){
			return getImportValues(lastWord, offset);
		} else if(lastWord.startsWith("\"")){
			return getAttributeValues(directive,attrName,lastWord,offset);
		} else {
			return getAttributes(directive, lastWord, offset);
		}
	}
	
	/**
	 * Returns import completion proposals.
	 */
	private ICompletionProposal[] getImportValues(String lastWord, int offset){
		try {
			if(this.file != null){
				IJavaProject project = JavaCore.create(file.getProject());
				CompletionProposalCollector collector = new CompletionProposalCollector(project);
				ICompilationUnit unit = HTMLUtil.getTemporaryCompilationUnit(project);
				String matchString = lastWord.replaceFirst("^\"", "");
				int lastIndex = matchString.lastIndexOf(',');
				if(lastIndex >= 0){
					//offset = offset + lastIndex;
					matchString = matchString.substring(lastIndex + 1);
				}
				String value = "import " + matchString;
				System.out.println(value); // TODO Debug
				HTMLUtil.setContentsToCU(unit, value);
				unit.codeComplete(value.length(), collector, DefaultWorkingCopyOwner.PRIMARY);
				IJavaCompletionProposal[] proposals = collector.getJavaCompletionProposals();
				List<ICompletionProposal> result = new ArrayList<ICompletionProposal>();
				for(int i=0;i<proposals.length;i++){
					if(proposals[i] instanceof AbstractJavaCompletionProposal){
						AbstractJavaCompletionProposal proposal = (AbstractJavaCompletionProposal)proposals[i];
						proposal.setReplacementOffset(offset - matchString.trim().length());
						proposal.setReplacementLength(matchString.length());
						proposal.setReplacementString(proposal.getReplacementString().replaceFirst(";$", ""));
						proposal.setCursorPosition(proposal.getReplacementString().length());
						result.add(proposal);
					}
				}
				return result.toArray(new ICompletionProposal[result.size()]);
			}
		} catch(Exception ex){
			HTMLPlugin.logException(ex);
		}
		return new ICompletionProposal[0];
	}
	
	/**
	 * Returns attribute value completion prosposals.
	 */
	private ICompletionProposal[] getAttributeValues(String directive,String attr,String lastWord,int offset){
		List<String> values = new ArrayList<String>();
		
		if(directive.equals("page")){
			if(attr.equals("autoFlush") || attr.equals("session") || 
					attr.equals("isThreadSafe") || attr.equals("isErrorPage")){
				values.add("true");
				values.add("false");
			}
		}
		
		if(directive.equals("taglib")){
			if(attr.equals("uri")){
				Map<String, String> innerTLD = HTMLPlugin.getInnerTLD();
				Iterator<String> ite = innerTLD.keySet().iterator();
				while(ite.hasNext()){
					values.add(ite.next());
				}
				IPreferenceStore store = HTMLPlugin.getDefault().getPreferenceStore();
				String[] uri = store.getString(HTMLPlugin.PREF_TLD_URI).split("\n");
				for(int i=0;i<uri.length;i++){
					values.add(uri[i]);
				}
			}
		}
		
		lastWord = lastWord.substring(1);
		List<ICompletionProposal> assistInfos = new ArrayList<ICompletionProposal>();
		
		for(int i=0;i<values.size();i++){
			String value = values.get(i);
			if(value.startsWith(lastWord)){
				assistInfos.add(new CompletionProposal(
						value,
						offset-lastWord.length(),
						lastWord.length(),
						value.length(),
						valueImage,
						value,
						null,null));
			}
		}
		
		return assistInfos.toArray(new ICompletionProposal[assistInfos.size()]);
	}
	
	/**
	 * Returns JSP direcive completion proposals.
	 */
	private ICompletionProposal[] getDirectives(String lastWord,int offset){
		List<ICompletionProposal> assistInfos = new ArrayList<ICompletionProposal>();
		
		for(int i=0;i<DIRECTIVES.length;i++){
			String directive = DIRECTIVES[i];
			if(directive.startsWith(lastWord)){
				assistInfos.add(new CompletionProposal(
						directive,
						offset-lastWord.length(),
						lastWord.length(),
						directive.length(),
						tagImage,
						directive,
						null,null));
			}
		}
		
		return assistInfos.toArray(new ICompletionProposal[assistInfos.size()]);
	}

	/**
	 * Returns directive attribute completion proposals.
	 */
	private ICompletionProposal[] getAttributes(String directive,String lastWord,int offset){
		String attrs[] = ATTRIBUTES.get(directive);
		if(attrs==null){
			return new ICompletionProposal[0];
		}
		
		List<ICompletionProposal> assistInfos = new ArrayList<ICompletionProposal>();
		
		for(int i=0;i<attrs.length;i++){
			String attrName = attrs[i];
			String replace = attrName + "=\"\"";
			if(attrName.startsWith(lastWord)){
				assistInfos.add(new CompletionProposal(
						replace,
						offset-lastWord.length(),
						lastWord.length(),
						replace.length() - 1,
						attrImage,
						attrName,
						null,null));
			}
		}
		
		return assistInfos.toArray(new ICompletionProposal[assistInfos.size()]);
	}
	
	public IContextInformation[] computeContextInformation(ITextViewer viewer, int offset) {
		ContextInformation[] info = new ContextInformation[0];
		return info;
	}

	public char[] getCompletionProposalAutoActivationCharacters() {
		return new char[0];
	}

	public char[] getContextInformationAutoActivationCharacters() {
		return new char[0];
	}

	public IContextInformationValidator getContextInformationValidator() {
		return new ContextInformationValidator(this);
	}

	public String getErrorMessage() {
		return "Error";
	}
}
