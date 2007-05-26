package tk.eclipse.plugin.xmleditor.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.core.DefaultWorkingCopyOwner;
import org.eclipse.jdt.internal.ui.text.java.AbstractJavaCompletionProposal;
import org.eclipse.jdt.internal.ui.text.java.JavaCompletionProposal;
import org.eclipse.jdt.internal.ui.text.java.LazyJavaTypeCompletionProposal;
import org.eclipse.jdt.ui.text.java.CompletionProposalCollector;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.swt.graphics.Image;

import tk.eclipse.plugin.htmleditor.HTMLUtil;
import tk.eclipse.plugin.htmleditor.assist.AssistInfo;

/**
 * Provides code completion at the attribute value.
 * 
 * @author Naoki Takezoe
 * @since 2.0.3
 */
public class ClassNameAssistProcessor {
	
	/**
	 * Provides classname completion.
	 * 
	 * @param value the inputed value
	 * @return the array of attribute value proposals
	 */
	public AssistInfo[] getClassAttributeValues(IFile file, String value) {
		if(value.length()==0){
			return new AssistInfo[0];
		}
		try {
			IJavaProject project = JavaCore.create(file.getProject());
			CompletionProposalCollector collector = new CompletionProposalCollector(project);
			ICompilationUnit unit = HTMLUtil.getTemporaryCompilationUnit(project);
			String source = "public class _xxx { public static void hoge(){ " + value + "}}";
			HTMLUtil.setContentsToCU(unit, source);
			unit.codeComplete(source.length() - 2, collector, DefaultWorkingCopyOwner.PRIMARY);
			IJavaCompletionProposal[] proposals = collector.getJavaCompletionProposals();
			List<AssistInfo> result = new ArrayList<AssistInfo>();
			
			for(int j=0;j<proposals.length;j++){
				if(proposals[j].getImage()!=null){
					if(proposals[j] instanceof LazyJavaTypeCompletionProposal){
						LazyJavaTypeCompletionProposal p = (LazyJavaTypeCompletionProposal)proposals[j];
						if(p.getReplacementString().startsWith(value)){
							result.add(new JavaClassAssistInfo(p));
						}
					} else if(proposals[j] instanceof JavaCompletionProposal){
						JavaCompletionProposal p = (JavaCompletionProposal)proposals[j];
						if(p.getReplacementString().startsWith(value)){
							result.add(new JavaClassAssistInfo(p));
						}
					}
				}
			}
			
			return result.toArray(new AssistInfo[result.size()]);
			
		} catch(Exception ex){
			ex.printStackTrace();
		}
		
		return new AssistInfo[0];
	}
	
	/**
	 * The wrapper of <code>AbstractJavaCompletionProposal</code>.
	 * <p>
	 * This class provides the <code>AssistInfo</code> interface and 
	 * <code>toCompletionProposal()</code> returns the wrapped 
	 * <code>AbstractJavaCompletionProposal</code> instance.
	 */
	private class JavaClassAssistInfo extends AssistInfo {
		
		private AbstractJavaCompletionProposal proposal;
		
		public JavaClassAssistInfo(AbstractJavaCompletionProposal proposal){
			super("");
			this.proposal = proposal;
		}
		
		@Override
    public String getDisplayString() {
			return proposal.getDisplayString();
		}

		@Override
    public Image getImage() {
			return proposal.getImage();
		}

		@Override
    public String getReplaceString() {
			return proposal.getReplacementString();
		}

		@Override
    public ICompletionProposal toCompletionProposal(int offset, String matchString, Image defaultImage) {
			proposal.setReplacementOffset(offset - matchString.length());
			proposal.setReplacementLength(matchString.length());
			proposal.setCursorPosition(proposal.getReplacementString().length());
			return proposal;
		}
	}
	
}
