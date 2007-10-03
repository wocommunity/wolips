package org.objectstyle.wolips.wizards.template;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.objectstyle.wolips.wizards.WizardsPlugin;
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
						Element optionsElement = (Element)optionsNodes.item(0);
						NodeList optionNodes = optionsElement.getElementsByTagName("option");
						for (int optionNum = 0; optionNum < optionNodes.getLength(); optionNum ++) {
							Element optionElement = (Element)optionNodes.item(optionNum);
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
		return (obj instanceof ProjectTemplate && compareTo((ProjectTemplate)obj) == 0);
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
	 * Loads the project templates and metadata from the given template folders.
	 * 
	 * @param templateBaseFolders
	 *            the template base folders
	 * @return the project templates
	 */
	public static List<ProjectTemplate> loadProjectTemplates(List<File> templateBaseFolders) {
		List<ProjectTemplate> templates = new LinkedList<ProjectTemplate>();
		for (File templateBaseFolder : templateBaseFolders) {
			List<ProjectTemplate> folderTemplates = ProjectTemplate.loadProjectTemplates(templateBaseFolder);
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
	public static List<ProjectTemplate> loadProjectTemplates(File templateBaseFolder) {
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
							WizardsPlugin.getDefault().log(e);
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
}