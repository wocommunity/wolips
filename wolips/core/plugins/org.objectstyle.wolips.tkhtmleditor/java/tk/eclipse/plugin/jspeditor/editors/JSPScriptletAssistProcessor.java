package tk.eclipse.plugin.jspeditor.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.core.DefaultWorkingCopyOwner;
import org.eclipse.jdt.internal.ui.text.java.AbstractJavaCompletionProposal;
import org.eclipse.jdt.ui.text.java.CompletionProposalCollector;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ContextInformation;
import org.eclipse.jface.text.contentassist.ContextInformationValidator;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;

import tk.eclipse.plugin.htmleditor.HTMLPlugin;
import tk.eclipse.plugin.htmleditor.HTMLUtil;
import tk.eclipse.plugin.jspeditor.compiler.CompileResult;
import tk.eclipse.plugin.jspeditor.compiler.JSPCompiler;

/**
 * Provides code completion for Java code.
 * 
 * @author Naoki Takezoe
 * @since 2.0.3
 */
public class JSPScriptletAssistProcessor implements IContentAssistProcessor {

	private IFile file;
	
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {
		if(file == null){
			return new ICompletionProposal[0];
		}
		try {
			CompileResult result = JSPCompiler.compile(viewer.getDocument().get());
			IJavaProject project = JavaCore.create(file.getProject());
			if(project != null){
				ICompilationUnit unit = HTMLUtil.getTemporaryCompilationUnit(project);
				HTMLUtil.setContentsToCU(unit, result.toString());
				
				CompletionProposalCollector collector = new CompletionProposalCollector(project);
				int headerLength = result.getHeader().length();
				unit.codeComplete(headerLength + offset, 
						collector, DefaultWorkingCopyOwner.PRIMARY);
				
				IJavaCompletionProposal[] proposals = collector.getJavaCompletionProposals();
				List<ICompletionProposal> list = new ArrayList<ICompletionProposal>();
				for(int i=0;i<proposals.length;i++){
					if(proposals[i] instanceof AbstractJavaCompletionProposal){
						AbstractJavaCompletionProposal proposal = (AbstractJavaCompletionProposal)proposals[i];
						if(proposal.getDisplayString().endsWith("_xxx")){
							continue;
						}
						proposal.setReplacementOffset(proposal.getReplacementOffset() - headerLength);
						list.add(proposal);
					}
				}
				return list.toArray(new ICompletionProposal[list.size()]);
			}			
		} catch(Exception e){
			HTMLPlugin.logException(e);
		}
		return new ICompletionProposal[0];
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
	
	
	public void update(JSPSourceEditor editor){
		IEditorInput input = editor.getEditorInput();
		if(input instanceof IFileEditorInput){
			this.file = ((IFileEditorInput)input).getFile();
		}
	}

}
