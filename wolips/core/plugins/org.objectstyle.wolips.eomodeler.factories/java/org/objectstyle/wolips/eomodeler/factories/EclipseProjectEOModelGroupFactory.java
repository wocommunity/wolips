package org.objectstyle.wolips.eomodeler.factories;

import java.io.File;
import java.io.IOException;
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

import org.objectstyle.woenvironment.env.WOEnvironment;
import org.objectstyle.wolips.baseforplugins.util.StringUtils;
import org.objectstyle.wolips.eomodeler.core.model.AbstractManifestEOModelGroupFactory;
import org.objectstyle.wolips.eomodeler.core.model.ManifestSearchFolder;
import org.objectstyle.wolips.eomodeler.core.wocompat.EMPropertyListSerialization;
import org.objectstyle.wolips.eomodeler.core.wocompat.PropertyListParserException;
import org.objectstyle.wolips.eomodeler.core.wocompat.SimpleParserDataStructureFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class EclipseProjectEOModelGroupFactory extends AbstractManifestEOModelGroupFactory {
	@Override
	public List<ManifestSearchFolder> getSearchFolders(File selectedModelFolder) throws IOException {
		System.out.println("EclipseProjectEOModelGroupFactory.getSearchFolders: Looking for Eclipse projects ...");
		List<ManifestSearchFolder> searchFolders = null;
		List<File> eclipseProjectFolders = new LinkedList<File>();
		try {
			findEclipseProjectFolders(selectedModelFolder, eclipseProjectFolders);
		} catch (PropertyListParserException e) {
			e.printStackTrace();
			throw new IOException("Failed to parse '.EntityModeler.plist'. " + StringUtils.getErrorMessage(e));
		}
		if (!eclipseProjectFolders.isEmpty()) {
			searchFolders = new LinkedList<ManifestSearchFolder>();
			for (File eclipseProjectFolder : eclipseProjectFolders) {
				System.out.println("EclipseProjectEOModelGroupFactory.getSearchFolders: Project = " + eclipseProjectFolder);
				Set<File> visitedProjectFolders = new HashSet<File>();
				try {
					processEclipseProject(eclipseProjectFolder, searchFolders, visitedProjectFolders, new WOEnvironment());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println("EclipseProjectEOModelGroupFactory.getSearchFolders: " + searchFolders);
		return searchFolders;
	}

	protected void processEclipseProject(File eclipseProjectFolder, List<ManifestSearchFolder> searchFolders, Set<File> visitedProjects, WOEnvironment env) throws IOException, XPathExpressionException, SAXException, ParserConfigurationException {
		if (eclipseProjectFolder == null || !eclipseProjectFolder.exists() || visitedProjects.contains(eclipseProjectFolder)) {
			return;
		}
		System.out.println("EclipseProjectEOModelGroupFactory.processEclipseProject: Project folder '" + eclipseProjectFolder + "' ...");

		File buildFolder = new File(eclipseProjectFolder, "build");
		if (buildFolder.exists()) {
			File buildResourcesFolder;
			File frameworkResourcesFolder = new File(buildFolder, eclipseProjectFolder.getName() + ".framework" + File.separator + "Resources");
			if (frameworkResourcesFolder.exists()) {
				buildResourcesFolder = frameworkResourcesFolder;
			} else {
				File woaResourcesFolder = new File(buildFolder, eclipseProjectFolder.getName() + ".woa" + File.separator + "Contents" + File.separator + "Resources");
				if (woaResourcesFolder.exists()) {
					buildResourcesFolder = woaResourcesFolder;
				} else {
					buildResourcesFolder = buildFolder;
				}
			}
			// We're cheating some here and forcing project build resources
			// folders to the front of the line ...
			searchFolders.add(0, new ManifestSearchFolder(buildResourcesFolder));
		}
		visitedProjects.add(eclipseProjectFolder);

		File eclipseClasspathFile = new File(eclipseProjectFolder, ".classpath");
		XPathExpression eclipseClasspathEntryExpression = XPathFactory.newInstance().newXPath().compile("//classpath/classpathentry");
		Document eclipseClasspathDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(eclipseClasspathFile);
		NodeList eclipseClasspathEntryNodes = (NodeList) eclipseClasspathEntryExpression.evaluate(eclipseClasspathDocument, XPathConstants.NODESET);
		for (int eclipseClasspathEntryNum = 0; eclipseClasspathEntryNum < eclipseClasspathEntryNodes.getLength(); eclipseClasspathEntryNum++) {
			Element eclipseClasspathEntryElement = (Element) eclipseClasspathEntryNodes.item(eclipseClasspathEntryNum);
			String kind = eclipseClasspathEntryElement.getAttribute("kind");
			String path = eclipseClasspathEntryElement.getAttribute("path");
			if ("src".equals(kind) && path != null && path.startsWith("/")) {
				File referencedProjectFolder = new File(eclipseProjectFolder.getParentFile(), path).getCanonicalFile();
				processEclipseProject(referencedProjectFolder, searchFolders, visitedProjects, env);
			} else if ("con".equals(kind) && path != null && path.startsWith("org.objectstyle.wolips.WO_CLASSPATH/")) {
				String[] frameworkNames = path.split("/");
				File userFrameworksFolder = new File(env.getWOVariables().userHome(), "Library" + File.separator + "Frameworks");
				File localFrameworksFolder = new File(env.getWOVariables().localRoot(), "Library" + File.separator + "Frameworks");
				File systemFrameworksFolder = new File(env.getWOVariables().systemRoot(), "Library" + File.separator + "Frameworks");
				for (int frameworkNum = 1; frameworkNum < frameworkNames.length; frameworkNum++) {
					String frameworkFolderName = frameworkNames[frameworkNum] + ".framework";
					File matchingFrameworkFolder = null;
					File userFrameworkFolder = new File(userFrameworksFolder, frameworkFolderName);
					if (userFrameworkFolder.exists()) {
						matchingFrameworkFolder = userFrameworkFolder.getCanonicalFile();
					} else {
						File localFrameworkFolder = new File(localFrameworksFolder, frameworkFolderName);
						if (localFrameworkFolder.exists()) {
							matchingFrameworkFolder = localFrameworkFolder.getCanonicalFile();
						} else {
							File systemFrameworkFolder = new File(systemFrameworksFolder, frameworkFolderName);
							if (systemFrameworkFolder.exists()) {
								matchingFrameworkFolder = systemFrameworkFolder.getCanonicalFile();
							}
						}
					}
					if (matchingFrameworkFolder != null && !visitedProjects.contains(matchingFrameworkFolder)) {
						searchFolders.add(new ManifestSearchFolder(new File(matchingFrameworkFolder, "Resources")));
						visitedProjects.add(matchingFrameworkFolder);
					}
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected void findEclipseProjectFolders(File folder, List<File> eclipseProjectFolders) throws IOException, PropertyListParserException {
		if (folder != null) {
			boolean foundProjectFolders = false;
			if (folder.isDirectory()) {
				System.out.println("EclipseProjectEOModelGroupFactory.findEclipseProjectFolders: Looking for '" + folder + "' ...");
				File projectLocator = new File(folder, ".EntityModeler.plist");
				if (projectLocator.exists()) {
					System.out.println("EclipseProjectEOModelGroupFactory.findEclipseProjectFolders:   Found project locator '" + projectLocator + "' ...");
					Map<String, Object> projectProperties = (Map<String, Object>) EMPropertyListSerialization.propertyListWithContentsOfFile(projectLocator.getCanonicalPath(), new SimpleParserDataStructureFactory());
					Map<String, List<String>> dependencies = (Map<String, List<String>>) projectProperties.get("Dependencies");
					if (dependencies != null) {
						List<String> projectFilePaths = dependencies.get("Eclipse");
						if (projectFilePaths != null) {
							for (String projectFilePath : projectFilePaths) {
								File projectFolder = new File(projectFilePath);
								if (!projectFolder.exists()) {
									projectFolder = new File(folder, projectFilePath);
								}
								if (projectFolder.exists()) {
									projectFolder = projectFolder.getCanonicalFile();
									if (isEclipseProjectFolder(projectFolder)) {
										System.out.println("EclipseProjectEOModelGroupFactory.findEclipseProjectFolders:     Found project '" + projectFolder + "'");
										eclipseProjectFolders.add(projectFolder);
										foundProjectFolders = true;
									}
								}
							}
						}
					}
				} else if (isEclipseProjectFolder(folder)) {
					System.out.println("EclipseProjectEOModelGroupFactory.findEclipseProjectFolders:   Found project '" + folder + "'");
					File eclipseProjectFolder = folder.getCanonicalFile();
					eclipseProjectFolders.add(eclipseProjectFolder);
					foundProjectFolders = true;
				}
			}
			if (!foundProjectFolders) {
				File parentFolder = folder.getParentFile();
				findEclipseProjectFolders(parentFolder, eclipseProjectFolders);
			}
		}
	}

	public boolean isEclipseProjectFolder(File folder) {
		return new File(folder, ".project").exists() && new File(folder, ".classpath").exists();
	}

}
