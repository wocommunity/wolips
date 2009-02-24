package org.objectstyle.wolips.wizards.template;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.widgets.Composite;
import org.objectstyle.wolips.templateengine.ProjectTemplate;
import org.objectstyle.wolips.wizards.NewWOProjectWizard;

public class NewProjectFromTemplateWizard extends NewWOProjectWizard {
	private SelectTemplateWizardPage _selectTemplatePage;

	private TemplateInputsWizardPage _templateInputsPage;

	public NewProjectFromTemplateWizard() {
		// DO NOTHING
	}

	public NewProjectFromTemplateWizard(String projectTemplateName) {
		setProjectTemplate(ProjectTemplate.loadProjectTemplateNamed(ProjectTemplate.PROJECT_TEMPLATES, projectTemplateName));
	}

	@Override
	protected WizardType getWizardType() {
		return WizardType.NEWPROJ_TEMPLATE_WIZARD;
	}

	@Override
	public void createPageControls(Composite pageContainer) {
		super.createPageControls(pageContainer);
	}

	@Override
	public void addPages() {
		ProjectTemplate projectTemplate = getProjectTemplate();
		if (projectTemplate == null) {
			_selectTemplatePage = new SelectTemplateWizardPage();
			addPage(_selectTemplatePage);
		}

		_templateInputsPage = new TemplateInputsWizardPage();
		if (projectTemplate != null) {
			_templateInputsPage.setProjectTemplate(projectTemplate);
		}
		if (projectTemplate == null || projectTemplate.getInputs().size() > 0) {
			addPage(_templateInputsPage);
		}

		super.addPages();
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		IWizardPage currentPage = page;
		if (currentPage == _selectTemplatePage) {
			ProjectTemplate projectTemplate = _selectTemplatePage.getSelectedProjectTemplate();
			setProjectTemplate(projectTemplate);
			if (projectTemplate != null && projectTemplate.getInputs().size() == 0) {
				currentPage = _templateInputsPage;
			}
		}
		IWizardPage nextPage = super.getNextPage(currentPage);
		if (nextPage == _templateInputsPage) {
			_templateInputsPage.setProjectTemplate(getProjectTemplate());
		}
		return nextPage;
	}

	@Override
	protected void postInstallTemplate(IProject project, IProgressMonitor progressMonitor) throws Exception {
		// DO NOTHING
	}
}
