package org.objectstyle.wolips.templateengine;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.objectstyle.wolips.baseforplugins.util.URLUtils;
import org.objectstyle.wolips.datasets.adaptable.ProjectPatternsets;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * ProjectTemplate models a single external project template folder along with
 * its template metadata.
 * 
 * @author mschrag
 */
public class ProjectTemplate implements Comparable<ProjectTemplate> {
	private File _folder;

	private Document _metadata;

	private String _name;

	private List<ProjectInput> _inputs;

	/**
	 * Constructs a new ProjectTemplate
	 * 
	 * @param folder
	 *            the location of the template foldere
	 * @param metadata
	 *            the metadata XML document for the template
	 */
	public ProjectTemplate(File folder, Document metadata) {
		_folder = folder;
		_metadata = metadata;
	}

	/**
	 * Returns the template folder for this template.
	 * 
	 * @return the template folder for this template
	 */
	public File getFolder() {
		return _folder;
	}

	/**
	 * Returns the metadata for this template.
	 * 
	 * @return the metadata for this template
	 */
	public Document getMetadata() {
		return _metadata;
	}

	/**
	 * Returns the name of this template.
	 * 
	 * @return the name of this template
	 */
	public synchronized String getName() {
		if (_name == null) {
			if (_metadata != null) {
				_name = _metadata.getDocumentElement().getAttribute("name");
			}
			if (_name == null) {
				_name = _folder.getName();
			}
		}
		return _name;
	}

	/**
	 * Returns the inputs for this template.
	 * 
	 * @return the inputs for this template
	 */
	public synchronized List<ProjectInput> getInputs() {
		if (_inputs == null) {
			_inputs = new LinkedList<ProjectInput>();
			if (_metadata != null) {
				NodeList inputsNodeList = _metadata.getDocumentElement().getElementsByTagName("input");
				for (int nodeNum = 0; nodeNum < inputsNodeList.getLength(); nodeNum++) {
					Element inputNode = (Element) inputsNodeList.item(nodeNum);
					String name = inputNode.getAttribute("name");
					String type = inputNode.getAttribute("type");
					ProjectInput input = new ProjectInput(name, ProjectInput.Type.valueOf(type));

					NodeList optionsNodes = inputNode.getElementsByTagName("options");
					if (optionsNodes.getLength() > 0) {
						Element optionsElement = (Element) optionsNodes.item(0);
						NodeList optionNodes = optionsElement.getElementsByTagName("option");
						for (int optionNum = 0; optionNum < optionNodes.getLength(); optionNum++) {
							Element optionElement = (Element) optionNodes.item(optionNum);
							String optionName = optionElement.getAttribute("name");
							String optionValue = optionElement.getAttribute("value");
							input.addOption(optionName, optionValue);
						}
					}

					NodeList defaultValueNodes = inputNode.getElementsByTagName("default");
					if (defaultValueNodes.getLength() > 0) {
						Node defaultValueNode = defaultValueNodes.item(0);
						String defaultValue = defaultValueNode.getTextContent();
						input.setDefaultText(defaultValue);
					}

					NodeList questionNodes = inputNode.getElementsByTagName("question");
					if (questionNodes.getLength() > 0) {
						Node questionNode = questionNodes.item(0);
						String question = questionNode.getTextContent();
						input.setQuestion(question);
					}

					_inputs.add(input);
				}
			}
		}
		return _inputs;
	}

	@Override
	public int hashCode() {
		return _name == null ? super.hashCode() : _name.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof ProjectTemplate && compareTo((ProjectTemplate) obj) == 0);
	}

	public int compareTo(ProjectTemplate o) {
		int comparison;
		if (o == null) {
			comparison = -1;
		} else {
			String name = getName();
			String oName = o.getName();
			if (name == null) {
				if (oName == null) {
					comparison = 0;
				} else {
					comparison = 1;
				}
			} else {
				comparison = name.compareToIgnoreCase(oName);
			}
		}
		return comparison;
	}

	public String toString() {
		return "[ProjectTemplate: name = " + _name + "]";
	}

	/**
	 * Creates the contents of the given project from the definition and inputs
	 * of this template.
	 * 
	 * @param project
	 *            the project to fill in
	 * @param progressMonitor
	 *            the progress tracker
	 * @throws Exception
	 *             if the project cannot be created
	 */
	public void createProjectContents(IProject project, IProgressMonitor progressMonitor) throws Exception {
		TemplateEngine templateEngine = new TemplateEngine();
		templateEngine.setTemplatePath(getFolder().getParentFile().getAbsolutePath());
		templateEngine.init();

		templateEngine.getWolipsContext().setProjectName(project.getName());
		templateEngine.getWolipsContext().setAntFolderName(ProjectPatternsets.ANT_FOLDER_NAME);
		templateEngine.setPropertyForKey(project.getName(), "projectName");
		templateEngine.setPropertyForKey(project.getName().toLowerCase(), "projectName_lowercase");
		for (ProjectInput input : getInputs()) {
			Object value = input.getValue();
			templateEngine.setPropertyForKey(value, input.getName());
			if (input.getValue() instanceof String) {
				templateEngine.setPropertyForKey(((String) value).toLowerCase(), input.getName() + "_lowercase");
			}
			// Package types get a yourname_folder variable made
			if (input.getType() == ProjectInput.Type.Package) {
				templateEngine.setPropertyForKey(((String) value).replace('.', '/'), input.getName() + "_folder");
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

		_createProjectFolder(templateEngine, getFolder().getParentFile(), project.getLocation().toFile(), getFolder(), templateKeys, progressMonitor);
		templateEngine.run(progressMonitor);
	}

	protected void _createProjectFolder(TemplateEngine templateEngine, File baseFolder, File projectFolder, File templateFolder, List<String> templateKeys, IProgressMonitor progressMonitor) throws CoreException {
		for (File templateChild : templateFolder.listFiles()) {
			String templateChildName = templateChild.getName();
			// Skip over files named "__placeholder__".  These exist only so
			// empty folders make it into the plugin.
			if ("__placeholder__".equals(templateChildName) || "__placeholder".equals(templateChildName)) {
				continue;
			}
			
			for (String key : templateKeys) {
				Object value = templateEngine.getPropertyForKey(key);
				if (value instanceof String) {
					templateChildName = templateChildName.replaceAll("__" + key + "__", (String) value);
				}
			}
			File destinationFile = new File(projectFolder, templateChildName);
			if (templateChild.isDirectory()) {
				destinationFile.mkdirs();
				_createProjectFolder(templateEngine, baseFolder, destinationFile, templateChild, templateKeys, progressMonitor);
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
	 * Returns the list of template folder locations.
	 * 
	 * @return the list of template folder locations
	 */
	public static List<File> templateBaseFolders() {
		LinkedList<File> templateBaseFolders = new LinkedList<File>();
		try {
			File projectTemplatesFile = URLUtils.cheatAndTurnIntoFile(TemplateEngine.class.getResource("/ProjectTemplates"));
			if (projectTemplatesFile != null) {
				templateBaseFolders.add(projectTemplatesFile);
			}
		} catch (IllegalArgumentException e) {
			TemplateEnginePlugin.getDefault().log(e);
		}
		templateBaseFolders.add(new File("/Library/Application Support/WOLips/Project Templates"));
		templateBaseFolders.add(new File(System.getProperty("user.home"), "Documents and Settings/Application Data/WOLips/Project Templates"));
		templateBaseFolders.add(new File(System.getProperty("user.home"), "Documents and Settings/AppData/Local/WOLips/Project Templates"));
		templateBaseFolders.add(new File(System.getProperty("user.home"), "Library/Application Support/WOLips/Project Templates"));
		return templateBaseFolders;
	}

	/**
	 * Loads the project templates from the predefined project template folder
	 * locations.
	 * 
	 * @return the available project templates;
	 */
	public static List<ProjectTemplate> loadProjectTemplates() {
		return ProjectTemplate.loadProjectTemplatesFromFolders(ProjectTemplate.templateBaseFolders());
	}

	/**
	 * Loads the project templates and metadata from the given template folders.
	 * 
	 * @param templateBaseFolders
	 *            the template base folders
	 * @return the project templates
	 */
	public static List<ProjectTemplate> loadProjectTemplatesFromFolders(List<File> templateBaseFolders) {
		List<ProjectTemplate> templates = new LinkedList<ProjectTemplate>();
		for (File templateBaseFolder : templateBaseFolders) {
			List<ProjectTemplate> folderTemplates = ProjectTemplate.loadProjectTemplatesFromFolder(templateBaseFolder);
			for (ProjectTemplate projectTemplate : folderTemplates) {
				// last template for that name wins
				templates.remove(projectTemplate);
				templates.add(projectTemplate);
			}
		}
		Collections.sort(templates);
		return templates;
	}

	/**
	 * Loads the project templates and metadata from the given template folder.
	 * 
	 * @param templateBaseFolder
	 *            the template base folder
	 * @return the project templates
	 */
	public static List<ProjectTemplate> loadProjectTemplatesFromFolder(File templateBaseFolder) {
		List<ProjectTemplate> templates = new LinkedList<ProjectTemplate>();
		if (templateBaseFolder.exists()) {
			for (File templateFolder : templateBaseFolder.listFiles()) {
				if (templateFolder.isDirectory() && !templateFolder.isHidden()) {
					File templateXML = new File(templateFolder, "template.xml");
					if (templateXML.exists()) {
						try {
							Document templateDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(templateXML);
							templateDocument.normalize();
							ProjectTemplate template = new ProjectTemplate(templateFolder, templateDocument);
							templates.add(template);
						} catch (Exception e) {
							TemplateEnginePlugin.getDefault().log(e);
						}
					} else {
						ProjectTemplate template = new ProjectTemplate(templateFolder, null);
						templates.add(template);
					}
				}
			}
		}
		return templates;
	}

	/**
	 * Returns the ProjectTemplate with the given name.
	 * 
	 * @param name
	 *            the name of the template to load
	 * @return the requested Project Template or null if there is no template
	 *         with the given name
	 */
	public static ProjectTemplate loadProjectTemplateNamed(String name) {
		List<ProjectTemplate> projectTemplates = ProjectTemplate.loadProjectTemplates();
		for (ProjectTemplate projectTemplate : projectTemplates) {
			if (name.equals(projectTemplate.getName())) {
				return projectTemplate;
			}
		}
		return null;
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