package org.objectstyle.wolips.htmlpreview.editor;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Stack;

import jp.aonir.fuzzyxml.FuzzyXMLElement;
import jp.aonir.fuzzyxml.FuzzyXMLNode;
import jp.aonir.fuzzyxml.internal.RenderContext;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.objectstyle.wolips.bindings.wod.IWodBinding;
import org.objectstyle.wolips.bindings.wod.IWodElement;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;

public abstract class TagDelegate {
	public abstract void renderNode(IWodElement wodElement, FuzzyXMLElement xmlElement, RenderContext renderContext, StringBuffer htmlBuffer, StringBuffer cssBuffer, Stack<WodParserCache> caches, Stack<FuzzyXMLNode> nodes);

	public void reset() {
		// DO NOTHING
	}

	public static void appendHtmlBindings(StringBuffer htmlBuffer, IWodElement wodElement) {
		IWodBinding idBinding = wodElement.getBindingNamed("id");
		if (idBinding != null && !idBinding.isKeyPath()) {
			htmlBuffer.append(" id = \"" + idBinding.getValue() + "\"");
		}

		IWodBinding classBinding = wodElement.getBindingNamed("class");
		if (classBinding != null && !classBinding.isKeyPath()) {
			htmlBuffer.append(" class = \"" + classBinding.getValue() + "\"");
		}

		IWodBinding styleBinding = wodElement.getBindingNamed("style");
		if (styleBinding != null && !styleBinding.isKeyPath()) {
			htmlBuffer.append(" style = \"" + styleBinding.getValue() + "\"");
		}
	}

	public static String getResourceUrl(String frameworkBindingName, String filenameBindingName, String hrefBindingName, IWodElement wodElement, Stack<WodParserCache> caches) {
		String resourceUrl;
		IWodBinding hrefBinding = wodElement.getBindingNamed(hrefBindingName);
		if (hrefBinding != null && !hrefBinding.isKeyPath()) {
			resourceUrl = hrefBinding.getValue();
		} else {
			resourceUrl = TagDelegate.getResourceUrl(frameworkBindingName, filenameBindingName, wodElement, caches);
		}
		return resourceUrl;
	}

	public static String getResourceUrl(String frameworkBindingName, String filenameBindingName, IWodElement wodElement, Stack<WodParserCache> caches) {
		File webserverResourcesFolder = null;
		IWodBinding frameworkBinding = wodElement.getBindingNamed(frameworkBindingName);
		String framework = null;
		if (frameworkBinding == null) {
			framework = "app";
		}
		else if (!frameworkBinding.isKeyPath()) {
			framework = frameworkBinding.getValue();
		}
		if (framework != null) {
			framework = framework.replaceAll("\"", "");

			IJavaProject javaProject = caches.peek().getJavaProject();
			if ("app".equals(framework)) {
				webserverResourcesFolder = new File(javaProject.getProject().getLocation().toFile(), "WebServerResources");
			}
			else {
				try {
					IClasspathEntry[] classpathEntries = javaProject.getResolvedClasspath(true);
					for (IClasspathEntry classpathEntry : classpathEntries) {
						if ((classpathEntry.getEntryKind() == IClasspathEntry.CPE_PROJECT || classpathEntry.getEntryKind() == IClasspathEntry.CPE_SOURCE) && classpathEntry.getPath().segment(0).equals(framework)) {
							File projectFolder = new File(ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile(), classpathEntry.getPath().segment(0));
							webserverResourcesFolder = new File(projectFolder, "WebServerResources");
						}
					}
				} catch (JavaModelException e) {
					e.printStackTrace();
				}
			}
		}
		
		String resourceUrl = null;
		if (webserverResourcesFolder != null) {
			File webserverResourceFile = null;
			IWodBinding filenameBinding = wodElement.getBindingNamed(filenameBindingName);
			if (filenameBinding != null && !filenameBinding.isKeyPath()) {
				String filename = filenameBinding.getValue().replaceAll("\"", "");
				webserverResourceFile = new File(webserverResourcesFolder, filename);
			}

			if (webserverResourceFile != null) {
				try {
					resourceUrl = webserverResourceFile.getAbsoluteFile().toURL().toExternalForm();
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
		}

		if (resourceUrl == null) {
			resourceUrl = "MISSING";
		}

		return resourceUrl;
	}
}
