package org.objectstyle.wolips.eomodeler.factories;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.objectstyle.woenvironment.env.WOEnvironment;
import org.objectstyle.wolips.eomodeler.core.model.AbstractManifestEOModelGroupFactory;
import org.objectstyle.wolips.eomodeler.core.model.ManifestSearchFolder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class EclipseProjectEOModelGroupFactory extends AbstractManifestEOModelGroupFactory {
	@Override
	public List<ManifestSearchFolder> getSearchFolders(File selectedModelFolder) throws IOException {
		List<ManifestSearchFolder> searchFolders = null;
		File eclipseProjectFolder = findEclipseProjectFolder(selectedModelFolder);
		if (eclipseProjectFolder != null) {
			searchFolders = new LinkedList<ManifestSearchFolder>();
			Set<File> visitedProjectFolders = new HashSet<File>();
			try {
				processEclipseProject(eclipseProjectFolder, searchFolders, visitedProjectFolders, new WOEnvironment());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return searchFolders;
	}

	protected void processEclipseProject(File eclipseProjectFolder, List<ManifestSearchFolder> searchFolders, Set<File> visitedProjects, WOEnvironment env) throws IOException, XPathExpressionException, SAXException, ParserConfigurationException {
		if (eclipseProjectFolder == null || !eclipseProjectFolder.exists() || visitedProjects.contains(eclipseProjectFolder)) {
			return;
		}

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

	protected File findEclipseProjectFolder(File folder) throws IOException {
		File eclipseProjectFolder = null;
		if (folder != null) {
			if (folder.isDirectory()) {
				File possibleEclipseProjectFile = new File(folder, ".classpath");
				if (possibleEclipseProjectFile.exists()) {
					eclipseProjectFolder = folder.getCanonicalFile();
				}
			}
			if (eclipseProjectFolder == null) {
				File parentFolder = folder.getParentFile();
				eclipseProjectFolder = findEclipseProjectFolder(parentFolder);
			}
		}
		return eclipseProjectFolder;
	}

}
