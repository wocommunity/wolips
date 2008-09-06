package org.objectstyle.wolips.eomodeler.factories;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
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
import org.objectstyle.woenvironment.plist.PropertyListParserException;
import org.objectstyle.woenvironment.plist.SimpleParserDataStructureFactory;
import org.objectstyle.woenvironment.plist.WOLPropertyListSerialization;
import org.objectstyle.wolips.baseforplugins.util.StringUtils;
import org.objectstyle.wolips.baseforplugins.util.URLUtils;
import org.objectstyle.wolips.eomodeler.core.model.AbstractManifestEOModelGroupFactory;
import org.objectstyle.wolips.eomodeler.core.model.ManifestSearchFolder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class EclipseProjectEOModelGroupFactory extends AbstractManifestEOModelGroupFactory {
	@Override
	public List<ManifestSearchFolder> getSearchFolders(File selectedModelFolder) throws IOException {
		// System.out.println(
		// "EclipseProjectEOModelGroupFactory.getSearchFolders: Looking for Eclipse projects ..."
		// );
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
				// System.out.println(
				// "EclipseProjectEOModelGroupFactory.getSearchFolders: Project = "
				// + eclipseProjectFolder);
				Set<File> visitedProjectFolders = new HashSet<File>();
				try {
					processEclipseProject(eclipseProjectFolder, searchFolders, visitedProjectFolders, new WOEnvironment(null));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		// System.out.println(
		// "EclipseProjectEOModelGroupFactory.getSearchFolders: " +
		// searchFolders);
		return searchFolders;
	}

	protected File getWorkspaceFolder(File projectFolder) {
		File workspaceFolder;
		String workspaceFolderPath = System.getProperty("workspaceFolder");
		if (workspaceFolderPath != null) {
			workspaceFolder = new File(workspaceFolderPath);
		} else {
			workspaceFolder = null;
			File searchingFolder = projectFolder;
			while (searchingFolder != null && workspaceFolder == null) {
				File tempFolder = new File(searchingFolder, ".metadata");
				if (tempFolder.exists() && tempFolder.isDirectory()) {
					workspaceFolder = searchingFolder;
				} else {
					searchingFolder = searchingFolder.getParentFile();
				}
			}
		}

		if (workspaceFolder == null) {
			workspaceFolder = projectFolder.getParentFile();
		}

		return workspaceFolder;
	}

	protected File getProjectFolder(File workspaceFolder, String projectName) {
		File projectFolder = null;
		File projectLocationFile = new File(workspaceFolder, ".metadata/.plugins/org.eclipse.core.resources/.projects/" + projectName + "/.location");
		if (projectLocationFile.exists()) {
			// MS: This is pretty crazy, but the .location file is a binary file (of "unknown" layout) that contains
			// a URL that is the location of the project folder.  We're going to load that binary file and look for
			// something that looks like a file: URL ... Crazy but works :)
			try {
				FileInputStream fis = new FileInputStream(projectLocationFile);
				try {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					byte[] buffer = new byte[2048];
					while (fis.available() > 0) {
						int bytesRead = fis.read(buffer);
						baos.write(buffer, 0, bytesRead);
					}
					String locationContents = baos.toString();
					int fileIndex = locationContents.indexOf("file:");
					if (fileIndex != -1) {
						int zeroIndex = locationContents.indexOf(0, fileIndex);
						String projectPath = locationContents.substring(fileIndex, zeroIndex);
						projectFolder = URLUtils.cheatAndTurnIntoFile(new URL(projectPath));
					}
				} finally {
					fis.close();
				}
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}

		if (projectFolder == null) {
			projectFolder = new File(workspaceFolder, projectName);
		}
		return projectFolder;
	}

	protected void processEclipseProject(File eclipseProjectFolder, List<ManifestSearchFolder> searchFolders, Set<File> visitedProjects, WOEnvironment env) throws IOException, XPathExpressionException, SAXException, ParserConfigurationException {
		if (eclipseProjectFolder == null || !eclipseProjectFolder.exists() || visitedProjects.contains(eclipseProjectFolder)) {
			return;
		}
		// System.out.println(
		// "EclipseProjectEOModelGroupFactory.processEclipseProject: Project folder '"
		// + eclipseProjectFolder + "' ...");

		String[] buildFolderNames = new String[] { "build", "dist" };
		for (String buildFolderName : buildFolderNames) {
			File buildFolder = new File(eclipseProjectFolder, buildFolderName);
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
				break;
			}
		}
		visitedProjects.add(eclipseProjectFolder);

		File workspaceFolder = null;
		File eclipseClasspathFile = new File(eclipseProjectFolder, ".classpath");
		XPathExpression eclipseClasspathEntryExpression = XPathFactory.newInstance().newXPath().compile("//classpath/classpathentry");
		Document eclipseClasspathDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(eclipseClasspathFile);
		NodeList eclipseClasspathEntryNodes = (NodeList) eclipseClasspathEntryExpression.evaluate(eclipseClasspathDocument, XPathConstants.NODESET);
		for (int eclipseClasspathEntryNum = 0; eclipseClasspathEntryNum < eclipseClasspathEntryNodes.getLength(); eclipseClasspathEntryNum++) {
			Element eclipseClasspathEntryElement = (Element) eclipseClasspathEntryNodes.item(eclipseClasspathEntryNum);
			String kind = eclipseClasspathEntryElement.getAttribute("kind");
			String path = eclipseClasspathEntryElement.getAttribute("path");
			if ("src".equals(kind) && path != null && path.startsWith("/")) {
				if (workspaceFolder == null) {
					workspaceFolder = getWorkspaceFolder(eclipseProjectFolder);
				}
				File referencedProjectFolder = getProjectFolder(workspaceFolder, path).getCanonicalFile();
					//new File(eclipseProjectFolder.getParentFile(), path).getCanonicalFile();
				processEclipseProject(referencedProjectFolder, searchFolders, visitedProjects, env);
			} else if ("con".equals(kind) && path != null && path.startsWith("org.objectstyle.wolips.ContainerInitializer/")) {
				String[] strs = path.split("/");
				List<String> frameworkNames = new LinkedList<String>();
				for (int i = 3; i < strs.length; i += 11) {
					frameworkNames.add(strs[i]);
				}
				loadFrameworks(frameworkNames.toArray(new String[frameworkNames.size()]), searchFolders, visitedProjects, env);
			} else if ("con".equals(kind) && path != null && path.startsWith("org.objectstyle.wolips.WO_CLASSPATH/")) {
				String[] frameworkNames = path.split("/");
				loadFrameworks(frameworkNames, searchFolders, visitedProjects, env);
			}
		}
	}

	protected void loadFrameworks(String[] frameworkNames, List<ManifestSearchFolder> searchFolders, Set<File> visitedProjects, WOEnvironment env) throws IOException {
		File userFrameworksFolder = new File(env.getWOVariables().userFrameworkPath());
		File localFrameworksFolder = new File(env.getWOVariables().localFrameworkPath());
		File systemFrameworksFolder = new File(env.getWOVariables().systemFrameworkPath());
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
				// System.out.println(
				// "EclipseProjectEOModelGroupFactory.processEclipseProject: framework = "
				// + matchingFrameworkFolder);
				searchFolders.add(new ManifestSearchFolder(new File(matchingFrameworkFolder, "Resources")));
				visitedProjects.add(matchingFrameworkFolder);
			}
		}

	}

	@SuppressWarnings("unchecked")
	protected void findEclipseProjectFolders(File folder, List<File> eclipseProjectFolders) throws IOException, PropertyListParserException {
		if (folder != null) {
			boolean foundProjectFolders = false;
			if (folder.isDirectory()) {
				// System.out.println(
				// "EclipseProjectEOModelGroupFactory.findEclipseProjectFolders: Looking for '"
				// + folder + "' ...");
				File projectLocator = new File(folder, ".EntityModeler.plist");
				if (projectLocator.exists()) {
					// System.out.println(
					// "EclipseProjectEOModelGroupFactory.findEclipseProjectFolders:   Found project locator '"
					// + projectLocator + "' ...");
					Map<String, Object> projectProperties = (Map<String, Object>) WOLPropertyListSerialization.propertyListWithContentsOfFile(projectLocator.getCanonicalPath(), new SimpleParserDataStructureFactory());
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
										// System.out.println(
										// "EclipseProjectEOModelGroupFactory.findEclipseProjectFolders:     Found project '"
										// + projectFolder + "'");
										eclipseProjectFolders.add(projectFolder);
										foundProjectFolders = true;
									}
								}
							}
						}
					}
				} else if (isEclipseProjectFolder(folder)) {
					// System.out.println(
					// "EclipseProjectEOModelGroupFactory.findEclipseProjectFolders:   Found project '"
					// + folder + "'");
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
