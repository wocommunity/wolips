/* ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0 
 *
 * Copyright (c) 2004 The ObjectStyle Group 
 * and individual authors of the software.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:  
 *       "This product includes software developed by the 
 *        ObjectStyle Group (http://objectstyle.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "ObjectStyle Group" and "Cayenne" 
 *    must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written 
 *    permission, please contact andrus@objectstyle.org.
 *
 * 5. Products derived from this software may not be called "ObjectStyle"
 *    nor may "ObjectStyle" appear in their names without prior written
 *    permission of the ObjectStyle Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE OBJECTSTYLE GROUP OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the ObjectStyle Group.  For more
 * information on the ObjectStyle Group, please see
 * <http://objectstyle.org/>.
 *
 */
package org.objectstyle.wolips.jdt.classpath.model;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.objectstyle.woenvironment.frameworks.AbstractFolderFramework;
import org.objectstyle.woenvironment.frameworks.Framework;
import org.objectstyle.woenvironment.frameworks.FrameworkLibrary;
import org.objectstyle.woenvironment.frameworks.IFramework;
import org.objectstyle.woenvironment.frameworks.Root;
import org.objectstyle.woenvironment.frameworks.Version;
import org.objectstyle.woenvironment.plist.SimpleParserDataStructureFactory;
import org.objectstyle.woenvironment.plist.WOLXMLPropertyListSerialization;
import org.objectstyle.wolips.core.resources.types.project.ProjectAdapter;
import org.objectstyle.wolips.jdt.JdtPlugin;
import org.objectstyle.wolips.variables.BuildProperties;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class EclipseProjectFramework extends Framework implements IEclipseFramework {
	private IProject project;
	private IClasspathEntry[] cachedClasspathEntries;

	public EclipseProjectFramework(Root root, IProject project) {
		super(root, EclipseProjectFramework.frameworkNameForProject(project));
		this.project = project;
	}

	public IProject getProject() {
		return project;
	}

	public List<FrameworkLibrary> getFrameworkLibraries() {
		return new LinkedList<FrameworkLibrary>();
	}

	public IFramework resolveFramework() {
		IPath buildPath = this.project.getLocation().append("build");
		AbstractFolderFramework pathFramework = new EclipsePathFramework(new EclipseFolderRoot(Root.PROJECT_ROOT, "Built Project Framework", buildPath.toFile(), buildPath.toFile()), buildPath.append(getName() + ".framework").toFile());
		return pathFramework;
	}

	public boolean isResolved() {
		return true;
	}

	public synchronized IClasspathEntry[] getClasspathEntries() {
		List<IClasspathEntry> classpathEntries;
		if (cachedClasspathEntries == null) {
			classpathEntries = new LinkedList<IClasspathEntry>();
			classpathEntries.add(JavaCore.newProjectEntry(this.project.getFullPath()));
			cachedClasspathEntries = classpathEntries.toArray(new IClasspathEntry[classpathEntries.size()]);
		}
		return cachedClasspathEntries.clone();
	}
	
	public Version getVersion() {
		Version version = null;
		File pomFile = project.getLocation().append("pom.xml").toFile();
		if (pomFile.exists()) {
			try {
				Document pomDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(pomFile);
				pomDocument.normalize();
				NodeList versionNodes = (NodeList) XPathFactory.newInstance().newXPath().compile("//project/version").evaluate(pomDocument, XPathConstants.NODESET);
				if (versionNodes.getLength() == 0) {
					versionNodes = (NodeList) XPathFactory.newInstance().newXPath().compile("//parent/version").evaluate(pomDocument, XPathConstants.NODESET);
				}
				if (versionNodes.getLength() > 0) {
					String versionStr = versionNodes.item(0).getFirstChild().getNodeValue();
					if (versionStr != null) {
						version = new Version(versionStr);
					}
				}
			} catch (Throwable t) {
				JdtPlugin.getDefault().getPluginLogger().log(t);
			}
		}
		else {
			BuildProperties buildProperties = (BuildProperties)this.project.getAdapter(BuildProperties.class);
			if (buildProperties != null) {
				version = buildProperties.getVersion();
			}
			if (version == null) {
				version = super.getVersion();
			}
		}
		return version;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> getInfoPlist() {
		ProjectAdapter projectAdapter = (ProjectAdapter) this.project.getAdapter(ProjectAdapter.class);
		Map<String, Object> propertyList = null;
		if (projectAdapter != null) {
			try {
				File infoPlist;
				BuildProperties buildProperties = (BuildProperties)project.getAdapter(BuildProperties.class);
				if (buildProperties.getWOVersion().isAtLeastVersion(5, 6)) {
					infoPlist = this.project.getLocation().append("Info.plist").toFile();
				}
				else {
					infoPlist = projectAdapter.getWOJavaArchive().removeLastSegments(1).append("Info.plist").toFile();
				}
				if (infoPlist.exists()) {
					propertyList = (Map<String, Object>) WOLXMLPropertyListSerialization.propertyListWithContentsOfFile(infoPlist, new SimpleParserDataStructureFactory());
				}
			} catch (Throwable t) {
				JdtPlugin.getDefault().getPluginLogger().log(t);
				propertyList = null;
			}
		}
		return propertyList;
	}

	public static String frameworkNameForProject(IProject project) {
		return project.getName();
	}
	
	public static void main(String[] args) throws SAXException, IOException, ParserConfigurationException, XPathExpressionException {
		File pomFile = new File("/Volumes/WebObjects56/JavaFoundation/pom.xml");
		Document pomDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(pomFile);
		pomDocument.normalize();
		NodeList versionNodes = (NodeList) XPathFactory.newInstance().newXPath().compile("//parent/version").evaluate(pomDocument, XPathConstants.NODESET);
		if (versionNodes.getLength() > 0) {
			String version = versionNodes.item(0).getFirstChild().getNodeValue();
			System.out.println("EclipseProjectFramework.main: " + version);
		}
		System.out.println("EclipseProjectFramework.main: " + versionNodes.getLength());

	}
}