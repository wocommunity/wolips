package tk.eclipse.plugin.htmleditor.assist;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.core.DefaultWorkingCopyOwner;
import org.eclipse.jdt.internal.ui.text.java.JavaCompletionProposal;
import org.eclipse.jdt.internal.ui.text.java.LazyJavaTypeCompletionProposal;
import org.eclipse.jdt.ui.text.java.CompletionProposalCollector;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;

import tk.eclipse.plugin.htmleditor.assist.FieldAssistUtils.ContentProposalImpl;

/**
 * Provides type name completion in the Java project.
 * 
 * @author Naoki Takezoe
 */
public class TypeNameContentProposalProvider implements IContentProposalProvider {
	
	private IJavaProject project;
	
	/**
	 * Constructor.
	 * 
	 * @param project the Java project
	 */
	public TypeNameContentProposalProvider(IJavaProject project){
		this.project = project;
	}
	
	public void setJavaProject(IJavaProject project){
		this.project = project;
	}
	
	public IContentProposal[] getProposals(String contents, int position) {
		try {
			CompletionProposalCollector collector = new CompletionProposalCollector(project);
			ICompilationUnit unit = FieldAssistUtils.getTemporaryCompilationUnit(project);
			contents = contents.substring(0, position);
			String source = "public class _xxx { public static void hoge(){ " + contents + "}}";
			FieldAssistUtils.setContentsToCU(unit, source);
			unit.codeComplete(source.length() - 2, collector, DefaultWorkingCopyOwner.PRIMARY);
			IJavaCompletionProposal[] proposals = collector.getJavaCompletionProposals();
			List<ContentProposalImpl> result = new ArrayList<ContentProposalImpl>();
			
			for(int j=0;j<proposals.length;j++){
				if(proposals[j].getImage()!=null){
					String replaceString = null;
					if(proposals[j] instanceof LazyJavaTypeCompletionProposal){
						LazyJavaTypeCompletionProposal p = (LazyJavaTypeCompletionProposal)proposals[j];
						replaceString = p.getReplacementString();
					} else if(proposals[j] instanceof JavaCompletionProposal){
						JavaCompletionProposal p = (JavaCompletionProposal)proposals[j];
						replaceString = p.getReplacementString();
					}
					if(replaceString!=null && replaceString.startsWith(contents)){
						result.add(new FieldAssistUtils.ContentProposalImpl(replaceString, position));
					}
				}
			}
			
			return result.toArray(new IContentProposal[result.size()]);
			
		} catch(Exception ex){
			ex.printStackTrace();
		}
		
		return null;
	}

}
