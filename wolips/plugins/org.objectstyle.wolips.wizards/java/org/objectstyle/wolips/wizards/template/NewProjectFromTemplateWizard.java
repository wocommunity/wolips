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

	private ProjectTemplate _selectedProjectTemplate;

	public NewProjectFromTemplateWizard() {
		// DO NOTHING
	}

	public NewProjectFromTemplateWizard(String projectTemplateName) {
		_selectedProjectTemplate = ProjectTemplate.loadProjectTemplateNamed(projectTemplateName);
	}

	@Override
	protected WizardType getWizardType() {
		return WizardType.NEWPROJ_TEMPLATE_WIZARD;
	}

	public void setSelectedProjectTemplate(ProjectTemplate selectedProjectTemplate) {
		_selectedProjectTemplate = selectedProjectTemplate;
	}

	public ProjectTemplate getSelectedProjectTemplate() {
		return _selectedProjectTemplate;
	}

	@Override
	public void createPageControls(Composite pageContainer) {
		super.createPageControls(pageContainer);
	}

	@Override
	public void addPages() {
		if (_selectedProjectTemplate == null) {
			_selectTemplatePage = new SelectTemplateWizardPage();
			addPage(_selectTemplatePage);
		}

		_templateInputsPage = new TemplateInputsWizardPage();
		if (_selectedProjectTemplate != null) {
			_templateInputsPage.setProjectTemplate(_selectedProjectTemplate);
		}
		if (_selectedProjectTemplate == null || _selectedProjectTemplate.getInputs().size() > 0) {
			addPage(_templateInputsPage);
		}

		super.addPages();
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		IWizardPage currentPage = page;
		if (currentPage == _selectTemplatePage) {
			_selectedProjectTemplate = _selectTemplatePage.getSelectedProjectTemplate();
			if (_selectedProjectTemplate != null && _selectedProjectTemplate.getInputs().size() == 0) {
				currentPage = _templateInputsPage;
			}
		}
		IWizardPage nextPage = super.getNextPage(currentPage);
		if (nextPage == _templateInputsPage) {
			_templateInputsPage.setProjectTemplate(_selectedProjectTemplate);
		}
		return nextPage;
	}

	@Override
	protected void _createProject(IProject project, IProgressMonitor progressMonitor) throws Exception {
		_selectedProjectTemplate.createProjectContents(project, progressMonitor);
	}
}
