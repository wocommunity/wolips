package org.objectstyle.wolips.eomodeler.factories;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.objectstyle.woenvironment.plist.PropertyListParserException;
import org.objectstyle.wolips.baseforplugins.util.StringUtils;
import org.objectstyle.wolips.baseforplugins.util.URLUtils;
import org.objectstyle.wolips.eomodeler.core.model.AbstractEOClassLoader;
import org.objectstyle.wolips.eomodeler.core.model.EOModel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class IDEAProjectEOClassLoaderFactory extends AbstractEOClassLoader {
	@Override
	protected void fillInDevelopmentClasspath(Set<URL> classpathUrls) throws Exception {
		// DO NOTHING
	}

	@Override
	protected void fillInModelClasspath(EOModel model, Set<URL> classpathUrls) throws Exception {
		List<File> ideaProjectFiles = new LinkedList<File>();
		try {
			File modelFolder = URLUtils.cheatAndTurnIntoFile(model.getModelURL()).getParentFile();
			IDEAProjectEOModelGroupFactory.findIdeaProjectFilesInFolder(modelFolder, ideaProjectFiles);
		} catch (PropertyListParserException e) {
			e.printStackTrace();
			throw new IOException("Failed to parse '.EntityModeler.plist'. " + StringUtils.getErrorMessage(e));
		}
		if (!ideaProjectFiles.isEmpty()) {
			for (File ideaProjectFile : ideaProjectFiles) {
				System.out.println("IDEAProjectEOClassLoaderFactory.fillInModelClasspath: Project = " + ideaProjectFile);
				String ideaProjectPath = ideaProjectFile.getParentFile().getAbsolutePath();
				try {
					XPathExpression ideaLibrariesExpression = XPathFactory.newInstance().newXPath().compile("project/component[@name='libraryTable']/library");
					XPathExpression ideaClassesRootExpression = XPathFactory.newInstance().newXPath().compile("CLASSES/root");

					Document ideaProjectDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(ideaProjectFile);
					NodeList ideaLibraryNodes = (NodeList) ideaLibrariesExpression.evaluate(ideaProjectDocument, XPathConstants.NODESET);
					for (int ideaLibraryNum = 0; ideaLibraryNum < ideaLibraryNodes.getLength(); ideaLibraryNum++) {
						Element ideaLibraryElement = (Element) ideaLibraryNodes.item(ideaLibraryNum);
						NodeList rootNodes = (NodeList) ideaClassesRootExpression.evaluate(ideaLibraryElement, XPathConstants.NODESET);
						for (int rootNodeNum = 0; rootNodeNum < rootNodes.getLength(); rootNodeNum++) {
							Element rootNodeElement = (Element) rootNodes.item(rootNodeNum);
							URL rootUrl;
							String rootUrlStr = rootNodeElement.getAttribute("url");
							rootUrlStr = rootUrlStr.replaceAll("\\$PROJECT_DIR\\$", ideaProjectPath);
							if (rootUrlStr.startsWith("jar://")) {
								rootUrlStr = rootUrlStr.replaceFirst("^jar://", "");
								rootUrlStr = rootUrlStr.replaceFirst("!/$", "");
								rootUrl = new File(rootUrlStr).getCanonicalFile().toURL();
							}
							else {
								rootUrl = new URL(rootUrlStr);
							}
							System.out.println("IDEAProjectEOClassLoaderFactory.fillInModelClasspath:   " + rootUrl);
							classpathUrls.add(rootUrl);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
