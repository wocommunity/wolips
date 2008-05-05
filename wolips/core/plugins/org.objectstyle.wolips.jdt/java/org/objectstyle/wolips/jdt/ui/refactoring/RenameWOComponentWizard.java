package org.objectstyle.wolips.jdt.ui.refactoring;

import org.eclipse.jdt.internal.ui.IJavaHelpContextIds;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jdt.internal.ui.refactoring.RefactoringMessages;
import org.eclipse.jdt.internal.ui.refactoring.reorg.RenameRefactoringWizard;
import org.eclipse.ltk.core.refactoring.Refactoring;

public class RenameWOComponentWizard extends RenameRefactoringWizard {
	public RenameWOComponentWizard(Refactoring refactoring) {
		//TODO Replace RenameResouce messages with local strings
		super(refactoring,
			RefactoringMessages.RenameResourceWizard_defaultPageTitle, 
			RefactoringMessages.RenameResourceWizard_inputPage_description, 
			JavaPluginImages.DESC_WIZBAN_REFACTOR,
			IJavaHelpContextIds.RENAME_RESOURCE_WIZARD_PAGE);
	}
}
