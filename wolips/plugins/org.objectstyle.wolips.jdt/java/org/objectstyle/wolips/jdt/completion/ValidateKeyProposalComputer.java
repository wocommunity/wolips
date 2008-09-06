package org.objectstyle.wolips.jdt.completion;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.ui.text.java.ContentAssistInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposalComputer;

public class ValidateKeyProposalComputer implements IJavaCompletionProposalComputer {
	private final ValidateKeyProposalProcessor _processor = new ValidateKeyProposalProcessor();

	public ValidateKeyProposalComputer() {
		// DO NOTHING
	}

	public List computeCompletionProposals(ContentAssistInvocationContext context, IProgressMonitor monitor) {
		return Arrays.asList(_processor.computeCompletionProposals(context.getViewer(), context.getInvocationOffset()));
	}

	public List computeContextInformation(ContentAssistInvocationContext context, IProgressMonitor monitor) {
		return Arrays.asList(_processor.computeContextInformation(context.getViewer(), context.getInvocationOffset()));
	}

	public String getErrorMessage() {
		return _processor.getErrorMessage();
	}

	public void sessionStarted() {
		// DO NOTHING
	}

	public void sessionEnded() {
		// DO NOTHING
	}
}
