package org.objectstyle.wolips.wizards.template;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
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
import org.objectstyle.wolips.wizards.WizardsPlugin;

public class NewProjectFromTemplateWizard extends NewWOProjectWizard {
	private SelectTemplateWizardPage _selectTemplatePage;

	private TemplateInputsWizardPage _templateInputsPage;

	private List<File> _templateBaseFolders;

	public NewProjectFromTemplateWizard() {
		_templateBaseFolders = new LinkedList<File>();
		try {
			File projectTemplatesFile = URLUtils.cheatAndTurnIntoFile(TemplateEngine.class.getResource("/ProjectTemplates"));
			if (projectTemplatesFile != null) {
				_templateBaseFolders.add(projectTemplatesFile);
			}
		} catch (IllegalArgumentException e) {
			WizardsPlugin.getDefault().log(e);
		}
		_templateBaseFolders.add(new File("/Library/Application Support/WOLips/Project Templates"));
		_templateBaseFolders.add(new File(System.getProperty("user.home"), "Documents and Settings/Application Data/WOLips/Project Templates"));
		_templateBaseFolders.add(new File(System.getProperty("user.home"), "Documents and Settings/AppData/Local/WOLips/Project Templates"));
		_templateBaseFolders.add(new File(System.getProperty("user.home"), "Library/Application Support/WOLips/Project Templates"));
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
			// Package types get a yourname_folder variable made
			if (input.getType() == ProjectInput.Type.Package) {
				templateEngine.setPropertyForKey(((String) input.getValue()).replace('.', '/'), input.getName() + "_folder");
			}
		}

		Object[] keys = templateEngine.getKeys();
		List<String> templateKeys = new LinkedList<String>();
		for (Object key : keys) {
			if (key instanceof String) {
				templateKeys.add((String) key);
			}
		}
		// MS: Sort inverse by name so "basePackage_folder" evaluates before
		// "basePackage" (so the longer one wins).
		Collections.sort(templateKeys, new ReverseStringLengthComparator());

		_createProject(templateEngine, selectedTemplate.getFolder().getParentFile(), project.getLocation().toFile(), selectedTemplate.getFolder(), templateKeys, progressMonitor);
		templateEngine.run(progressMonitor);
	}

	protected void _createProject(TemplateEngine templateEngine, File baseFolder, File projectFolder, File templateFolder, List<String> templateKeys, IProgressMonitor progressMonitor) throws CoreException {
		for (File templateChild : templateFolder.listFiles()) {
			String templateChildName = templateChild.getName();
			for (String key : templateKeys) {
				Object value = templateEngine.getPropertyForKey(key);
				if (value instanceof String) {
					templateChildName = templateChildName.replaceAll("\\$" + key, (String) value);
				}
			}
			File destinationFile = new File(projectFolder, templateChildName);
			if (templateChild.isDirectory()) {
				destinationFile.mkdirs();
				_createProject(templateEngine, baseFolder, destinationFile, templateChild, templateKeys, progressMonitor);
			} else {
				if (!"template.xml".equals(templateChildName)) {
					String templatePath = templateChild.getAbsolutePath();
					templatePath = templatePath.substring(baseFolder.getAbsolutePath().length());
					templateEngine.addTemplate(new TemplateDefinition(templatePath, destinationFile.getParentFile().getAbsolutePath(), destinationFile.getName(), destinationFile.getName()));
				}
			}
		}

	}

	/**
	 * Sorts Strings in reverse order by length.
	 * 
	 * @author mschrag
	 */
	protected static class ReverseStringLengthComparator implements Comparator<String> {
		public int compare(String s1, String s2) {
			return (s1.length() > s2.length()) ? -1 : (s1.length() < s2.length()) ? 1 : 0;
		}
	}
}
