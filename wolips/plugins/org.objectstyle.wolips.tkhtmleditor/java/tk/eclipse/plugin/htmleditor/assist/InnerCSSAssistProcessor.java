package tk.eclipse.plugin.htmleditor.assist;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

import tk.eclipse.plugin.csseditor.editors.CSSAssistProcessor;

/**
 * The <code>IContentAssistProcessor</code> implementation
 * for the embedded CSS in the HTML.
 * 
 * @author Naoki Takezoe
 * @see 2.0.3
 */
public class InnerCSSAssistProcessor extends CSSAssistProcessor {
	
	private HTMLAssistProcessor processor;
	private static final String START_SEQ = "<style";
	private static final String END_SEQ= "</style>";

	/**
	 * Constructor.
	 * 
	 * @param processor the parent <code>HTMLAssistProcessor</code>
	 */
	public InnerCSSAssistProcessor(HTMLAssistProcessor processor){
		this.processor = processor;
	}
	
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {
		
		String source = viewer.getDocument().get();
		String subSource = source.substring(0, offset);
		
		int lastIndex = subSource.lastIndexOf(START_SEQ);
		if(lastIndex >= 0){
			int endIndex = source.indexOf('>', lastIndex);
			if(lastIndex <= offset && offset <= endIndex){
				return processor.computeCompletionProposals(viewer, offset);
			}
		}
		lastIndex = subSource.lastIndexOf("</");
		if(lastIndex >= 0){
			int endIndex = source.indexOf('>', lastIndex);
			if(lastIndex <= offset && offset <= endIndex){
				return new ICompletionProposal[0];
			}
		}
		
		return super.computeCompletionProposals(viewer, offset);
	}
	
	protected String getSource(ITextViewer viewer) {
		StringBuffer sb = new StringBuffer();
		String source = viewer.getDocument().get();
		int lastIndex = 0;
		int index = 0;
		while((index = source.indexOf(START_SEQ, lastIndex))>=0){
			int tagEnd = source.indexOf('>', index);
			if(tagEnd >= 0){
				sb.append(source.substring(lastIndex, tagEnd));
				int end = source.indexOf(END_SEQ, index);
				if(end >= 0){
					sb.append(source.substring(tagEnd + 1, end));
					sb.append(makeSpace(END_SEQ.length()));
					lastIndex = end;
					continue;
				}
			}
			sb.append(makeSpace(index - lastIndex + START_SEQ.length()));
			lastIndex = index + START_SEQ.length();
		}
		
		return sb.toString();
	}
	
	/**
	 * Makes whitespaces which has a given length.
	 * 
	 * @param length the length
	 * @return whitespaces
	 */
	private String makeSpace(int length){
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<length;i++){
			sb.append(' ');
		}
		return sb.toString();
	}
	
}
