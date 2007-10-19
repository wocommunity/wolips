package org.objectstyle.wolips.eomodeler.factories;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
		System.out.println("IDEAProjectEOModelGroupFactory.getSearchFolders: Looking for IDEA projects ...");
		List<ManifestSearchFolder> searchFolders = null;
		List<File> ideaProjectFiles = new LinkedList<File>();
		findIdeaProjectFilesInFolder(selectedModelFolder, ideaProjectFiles);
		if (!ideaProjectFiles.isEmpty()) {
			searchFolders = new LinkedList<ManifestSearchFolder>();
			for (File ideaProjectFile : ideaProjectFiles) {
				System.out.println("IDEAProjectEOModelGroupFactory.getSearchFolders: Project = " + ideaProjectFile);
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
								ideaLibraries.put(libraryName, new File(rootPath).getCanonicalFile());
								System.out.println("IDEAProjectEOModelGroupFactory.getSearchFolders: Library " + libraryName + "=" + ideaLibraries.get(libraryName));
								break;
							}
						}
					}

					NodeList ideaModuleNodes = (NodeList) ideaModulesExpression.evaluate(ideaProjectDocument, XPathConstants.NODESET);
					for (int ideaModuleNum = 0; ideaModuleNum < ideaModuleNodes.getLength(); ideaModuleNum++) {
						Element ideaModuleElement = (Element) ideaModuleNodes.item(ideaModuleNum);
						String ideaModulePath = ideaModuleElement.getAttribute("filepath");
						ideaModulePath = ideaModulePath.replaceAll("\\$PROJECT_DIR\\$", ideaProjectPath);
						File ideaModuleFile = new File(ideaModulePath).getCanonicalFile();
						processIdeaModuleFile(ideaModuleFile, searchFolders, ideaLibraries, visitedModulePaths);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println("IDEAProjectEOModelGroupFactory.getSearchFolders: Search folders = " + searchFolders);
		return searchFolders;
	}

	protected void processIdeaModuleFile(File ideaModuleFile, List<ManifestSearchFolder> searchFolders, Map<String, File> ideaLibraries, Set<File> visitedModulePaths) throws IOException, XPathExpressionException, SAXException, ParserConfigurationException {
		if (!ideaModuleFile.exists()) {
			return;
		} else if (visitedModulePaths.contains(ideaModuleFile)) {
			return;
		}
		visitedModulePaths.add(ideaModuleFile);
		System.out.println("IDEAProjectEOModelGroupFactory.processIdeaModuleFile: Module file '" + ideaModuleFile + "' ...");

		String ideaProjectPath = ideaModuleFile.getParentFile().getAbsolutePath();
		Document ideaModuleDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(ideaModuleFile);
		XPathExpression sourceFoldersExpression = XPathFactory.newInstance().newXPath().compile("//module/component/content");
		NodeList contentNodes = (NodeList) sourceFoldersExpression.evaluate(ideaModuleDocument, XPathConstants.NODESET);
		for (int contentNum = 0; contentNum < contentNodes.getLength(); contentNum++) {
			Element contentElement = (Element) contentNodes.item(contentNum);
			String contentPath = contentElement.getAttribute("url");
			contentPath = contentPath.replaceAll("^file://", "");
			contentPath = contentPath.replaceAll("\\$MODULE_DIR\\$", ideaProjectPath);
			File contentFolder = new File(contentPath).getCanonicalFile();
			searchFolders.add(new ManifestSearchFolder(contentFolder));
		}

		XPathExpression ideaModulesExpression = XPathFactory.newInstance().newXPath().compile("//module/component/orderEntry");
		NodeList ideaModuleNodes = (NodeList) ideaModulesExpression.evaluate(ideaModuleDocument, XPathConstants.NODESET);
		for (int ideaModuleNum = 0; ideaModuleNum < ideaModuleNodes.getLength(); ideaModuleNum++) {
			Element ideaModuleElement = (Element) ideaModuleNodes.item(ideaModuleNum);
			String ideaModuleType = ideaModuleElement.getAttribute("type");
			if ("module".equals(ideaModuleType)) {
				String ideaModuleName = ideaModuleElement.getAttribute("module-name");
				File dependentModuleFile = new File(ideaModuleFile.getParentFile(), ideaModuleName + ".iml").getCanonicalFile();
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

	protected void findIdeaProjectFilesInFolder(File folder, List<File> ideaProjectFiles) throws IOException {
		if (folder != null) {
			boolean foundProjectFiles = false;
			if (folder.isDirectory()) {
				System.out.println("IDEAProjectEOModelGroupFactory.findIdeaProjectFilesInFolder: Looking in " + folder + " ...");
				File projectLocator = new File(folder, ".EntityModeler.idea");
				if (projectLocator.exists()) {
					System.out.println("IDEAProjectEOModelGroupFactory.findIdeaProjectFilesInFolder:   Found project locator '" + projectLocator + '.');
					BufferedReader projectLocatorReader = new BufferedReader(new FileReader(projectLocator));
					try {
						String projectFileStr;
						while ((projectFileStr = projectLocatorReader.readLine()) != null) {
							File projectFile = new File(projectFileStr);
							if (!projectFile.exists()) {
								projectFile = new File(folder, projectFileStr);
							}
							if (projectFile.exists()) {
								projectFile = projectFile.getCanonicalFile();
								if (isIdeaProjectFile(projectFile)) {
									System.out.println("IDEAProjectEOModelGroupFactory.findIdeaProjectFilesInFolder:     Found project '" + projectFile + "'.");
									ideaProjectFiles.add(projectFile.getCanonicalFile());
									foundProjectFiles = true;
								}
							}
						}
					} finally {
						projectLocatorReader.close();
					}
				} else {
					File[] files = folder.listFiles();
					if (files != null) {
						for (File file : files) {
							if (isIdeaProjectFile(file)) {
								System.out.println("IDEAProjectEOModelGroupFactory.findIdeaProjectFilesInFolder:   Found project '" + file + "'.");
								ideaProjectFiles.add(file.getCanonicalFile());
								foundProjectFiles = true;
							}
						}
					}
				}
			}
			if (!foundProjectFiles) {
				File parentFolder = folder.getParentFile();
				findIdeaProjectFilesInFolder(parentFolder, ideaProjectFiles);
			}
		}
	}

	protected boolean isIdeaProjectFile(File file) {
		return file.getName().endsWith(".ipr");
	}

}
