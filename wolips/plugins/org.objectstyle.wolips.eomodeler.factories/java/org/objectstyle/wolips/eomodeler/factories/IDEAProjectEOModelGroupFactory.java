package org.objectstyle.wolips.eomodeler.factories;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.objectstyle.wolips.eomodeler.core.model.AbstractManifestEOModelGroupFactory;
import org.objectstyle.wolips.eomodeler.core.model.ManifestSearchFolder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class IDEAProjectEOModelGroupFactory extends AbstractManifestEOModelGroupFactory {
	@Override
	public List<ManifestSearchFolder> getSearchFolders(File selectedModelFolder) throws IOException {
		List<ManifestSearchFolder> searchFolders = null;
		File ideaProjectFile = findIdeaProjectFileInFolder(selectedModelFolder);
		if (ideaProjectFile != null) {
			searchFolders = new LinkedList<ManifestSearchFolder>();
			Map<String, File> ideaLibraries = new HashMap<String, File>();
			String ideaProjectPath = ideaProjectFile.getParentFile().getAbsolutePath();
			Set<File> visitedModulePaths = new HashSet<File>();
			try {
				XPathExpression ideaLibrariesExpression = XPathFactory.newInstance().newXPath().compile("//project/component[@name='libraryTable']/library");
				XPathExpression ideaModulesExpression = XPathFactory.newInstance().newXPath().compile("//project/component[@name='ProjectModuleManager']/modules/module");
				XPathExpression ideaClassesRootExpression = XPathFactory.newInstance().newXPath().compile("//CLASSES/root");

				Document ideaProjectDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(ideaProjectFile);
				NodeList ideaLibraryNodes = (NodeList) ideaLibrariesExpression.evaluate(ideaProjectDocument, XPathConstants.NODESET);
				for (int ideaLibraryNum = 0; ideaLibraryNum < ideaLibraryNodes.getLength(); ideaLibraryNum++) {
					Element ideaLibraryElement = (Element) ideaLibraryNodes.item(ideaLibraryNum);
					String libraryName = ideaLibraryElement.getAttribute("name");
					NodeList rootNodes = (NodeList) ideaClassesRootExpression.evaluate(ideaLibraryElement, XPathConstants.NODESET);
					for (int rootNodeNum = 0; rootNodeNum < rootNodes.getLength(); rootNodeNum++) {
						Element rootNodeElement = (Element) rootNodes.item(rootNodeNum);
						String rootPath = rootNodeElement.getAttribute("url");
						if (rootPath.contains(".framework/Resources")) {
							rootPath = rootPath.replaceAll("^[^:]+://", "");
							rootPath = rootPath.replaceAll("\\$PROJECT_DIR\\$", ideaProjectPath);
							rootPath = rootPath.replaceAll("(.*\\.framework/Resources).*", "$1");
							ideaLibraries.put(libraryName, new File(rootPath).getAbsoluteFile());
							break;
						}
					}
				}

				NodeList ideaModuleNodes = (NodeList) ideaModulesExpression.evaluate(ideaProjectDocument, XPathConstants.NODESET);
				for (int ideaModuleNum = 0; ideaModuleNum < ideaModuleNodes.getLength(); ideaModuleNum++) {
					Element ideaModuleElement = (Element) ideaModuleNodes.item(ideaModuleNum);
					String ideaModulePath = ideaModuleElement.getAttribute("filepath");
					ideaModulePath = ideaModulePath.replaceAll("\\$PROJECT_DIR\\$", ideaProjectPath);
					File ideaModuleFile = new File(ideaModulePath).getAbsoluteFile();
					processIdeaModuleFile(ideaModuleFile, searchFolders, ideaLibraries, visitedModulePaths);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return searchFolders;
	}

	protected void processIdeaModuleFile(File ideaModuleFile, List<ManifestSearchFolder> searchFolders, Map<String, File> ideaLibraries, Set<File> visitedModulePaths) throws IOException, XPathExpressionException, SAXException, ParserConfigurationException {
		if (!ideaModuleFile.exists()) {
			return;
		} else if (visitedModulePaths.contains(ideaModuleFile)) {
			return;
		}
		visitedModulePaths.add(ideaModuleFile);

		String ideaProjectPath = ideaModuleFile.getParentFile().getAbsolutePath();
		Document ideaModuleDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(ideaModuleFile);
		XPathExpression sourceFoldersExpression = XPathFactory.newInstance().newXPath().compile("//module/component/content/sourceFolder");
		NodeList sourceFolderNodes = (NodeList) sourceFoldersExpression.evaluate(ideaModuleDocument, XPathConstants.NODESET);
		for (int sourceFolderNum = 0; sourceFolderNum < sourceFolderNodes.getLength(); sourceFolderNum++) {
			Element sourceFolderElement = (Element) sourceFolderNodes.item(sourceFolderNum);
			String sourceFolderPath = sourceFolderElement.getAttribute("url");
			sourceFolderPath = sourceFolderPath.replaceAll("^file://", "");
			sourceFolderPath = sourceFolderPath.replaceAll("\\$MODULE_DIR\\$", ideaProjectPath);
			File sourceFolder = new File(sourceFolderPath).getAbsoluteFile();
			// MS: Only add folders named "Resources"?
			if (sourceFolderPath.contains("/Resources")) {
				searchFolders.add(new ManifestSearchFolder(sourceFolder));
			}
		}

		XPathExpression ideaModulesExpression = XPathFactory.newInstance().newXPath().compile("//module/component/orderEntry");
		NodeList ideaModuleNodes = (NodeList) ideaModulesExpression.evaluate(ideaModuleDocument, XPathConstants.NODESET);
		for (int ideaModuleNum = 0; ideaModuleNum < ideaModuleNodes.getLength(); ideaModuleNum++) {
			Element ideaModuleElement = (Element) ideaModuleNodes.item(ideaModuleNum);
			String ideaModuleType = ideaModuleElement.getAttribute("type");
			if ("module".equals(ideaModuleType)) {
				String ideaModuleName = ideaModuleElement.getAttribute("module-name");
				File dependentModuleFile = new File(ideaModuleFile.getParentFile(), ideaModuleName + ".iml").getAbsoluteFile();
				processIdeaModuleFile(dependentModuleFile, searchFolders, ideaLibraries, visitedModulePaths);
			} else if ("library".equals(ideaModuleType)) {
				String ideaLibraryName = ideaModuleElement.getAttribute("name");
				File ideaLibraryFolder = ideaLibraries.get(ideaLibraryName);
				if (ideaLibraryFolder != null) {
					searchFolders.add(new ManifestSearchFolder(ideaLibraryFolder));
				}
			}
		}
	}

	protected File findIdeaProjectFileInFolder(File folder) {
		File ideaProjectFile = null;
		if (folder != null) {
			if (folder.isDirectory()) {
				File[] files = folder.listFiles();
				if (files != null) {
					for (File file : files) {
						if (file.getName().endsWith(".ipr")) {
							ideaProjectFile = file;
							break;
						}
					}
				}
			}
			if (ideaProjectFile == null) {
				File parentFolder = folder.getParentFile();
				ideaProjectFile = findIdeaProjectFileInFolder(parentFolder);
			}
		}
		return ideaProjectFile;
	}

}
