package org.objectstyle.wolips.wizards.template;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.widgets.Composite;
import org.objectstyle.wolips.baseforplugins.util.URLUtils;
import org.objectstyle.wolips.core.resources.internal.types.project.ProjectPatternsets;
import org.objectstyle.wolips.templateengine.TemplateDefinition;
import org.objectstyle.wolips.templateengine.TemplateEngine;
import org.objectstyle.wolips.wizards.NewWOProjectWizard;

public class NewProjectFromTemplateWizard extends NewWOProjectWizard {
	private SelectTemplateWizardPage _selectTemplatePage;

	private TemplateInputsWizardPage _templateInputsPage;

	private List<File> _templateBaseFolders;

	public NewProjectFromTemplateWizard() {
		_templateBaseFolders = new LinkedList<File>();
		_templateBaseFolders.add(URLUtils.cheatAndTurnIntoFile(TemplateEngine.class.getResource("/ProjectTemplates")));
		_templateBaseFolders.add(new File("/Library/Application Support/WOLips/Project Templates"));
		_templateBaseFolders.add(new File(System.getProperty("user.home"), "Documents and Settings/Application Data/WOLips/Project Templates"));
		_templateBaseFolders.add(new File(System.getProperty("user.home"), "Documents and Settings/AppData/Local/WOLips/Project Templates"));
		_templateBaseFolders.add(new File(System.getProperty("user.home"), "Library/Application Support/WOLips/Project Templates"));
	}

	@Override
	protected WizardType wizardType() {
		return WizardType.NEWPROJ_TEMPLATE_WIZARD;
	}
	
	@Override
	public void createPageControls(Composite pageContainer) {
		super.createPageControls(pageContainer);
	}

	@Override
	public void addPages() {
		_selectTemplatePage = new SelectTemplateWizardPage(_templateBaseFolders);
		addPage(_selectTemplatePage);

		_templateInputsPage = new TemplateInputsWizardPage();
		addPage(_templateInputsPage);
		
		super.addPages();
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		IWizardPage currentPage = page;
		if (currentPage == _selectTemplatePage) {
			ProjectTemplate selectedTemplate = _selectTemplatePage.getSelectedProjectTemplate();
			if (selectedTemplate != null && selectedTemplate.getInputs().size() == 0) {
				currentPage = _templateInputsPage;
			}
		}
		IWizardPage nextPage = super.getNextPage(currentPage);
		if (nextPage == _templateInputsPage) {
			_templateInputsPage.setProjectTemplate(_selectTemplatePage.getSelectedProjectTemplate());
		}
		return nextPage;
	}

	protected void _createProject(IProject project, IProgressMonitor progressMonitor) throws Exception {
		ProjectTemplate selectedTemplate = _selectTemplatePage.getSelectedProjectTemplate();

		TemplateEngine templateEngine = new TemplateEngine();
		templateEngine.setTemplatePath(selectedTemplate.getFolder().getParentFile().getAbsolutePath());
		templateEngine.init();

		templateEngine.getWolipsContext().setProjectName(project.getName());
		templateEngine.getWolipsContext().setAntFolderName(ProjectPatternsets.ANT_FOLDER_NAME);
		ProjectTemplate projectTemplate = _selectTemplatePage.getSelectedProjectTemplate();
		for (ProjectInput input : projectTemplate.getInputs()) {
			templateEngine.setPropertyForKey(input.getValue(), input.getName());
		}

		_createProject(templateEngine, selectedTemplate.getFolder().getParentFile(), project.getLocation().toFile(), selectedTemplate.getFolder(), progressMonitor);
		templateEngine.run(progressMonitor);
	}

	protected void _createProject(TemplateEngine templateEngine, File baseFolder, File projectFolder, File templateFolder, IProgressMonitor progressMonitor) throws CoreException {
		for (File templateChild : templateFolder.listFiles()) {
			if (templateChild.isDirectory()) {
				File childFolder = new File(projectFolder, templateChild.getName());
				childFolder.mkdir();
				_createProject(templateEngine, baseFolder, childFolder, templateChild, progressMonitor);
			} else {
				if (!"template.xml".equals(templateChild.getName())) {
					String templatePath = templateChild.getAbsolutePath();
					templatePath = templatePath.substring(baseFolder.getAbsolutePath().length());
					templateEngine.addTemplate(new TemplateDefinition(templatePath, projectFolder.getAbsolutePath(), templateChild.getName(), templateChild.getName()));
				}
			}
		}

	}

}
