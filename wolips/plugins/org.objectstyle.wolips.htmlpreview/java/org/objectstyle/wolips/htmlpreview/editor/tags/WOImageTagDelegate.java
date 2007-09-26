package org.objectstyle.wolips.htmlpreview.editor.tags;

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
import org.objectstyle.wolips.htmlpreview.editor.TagDelegate;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;
import org.objectstyle.wolips.wodclipse.core.model.IWodBinding;
import org.objectstyle.wolips.wodclipse.core.model.IWodElement;

public class WOImageTagDelegate extends TagDelegate {

	@Override
	public void renderNode(IWodElement wodElement, FuzzyXMLElement xmlElement, RenderContext renderContext, StringBuffer htmlBuffer, StringBuffer cssBuffer, Stack<WodParserCache> caches, Stack<FuzzyXMLNode> nodes) {
		File webserverResourcesFolder = null;
		IWodBinding frameworkBinding = wodElement.getBindingNamed("framework");
		if (frameworkBinding != null && !frameworkBinding.isKeyPath()) {
			String framework = frameworkBinding.getValue().replaceAll("\"", "");

			IJavaProject javaProject = caches.peek().getJavaProject();
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

		String imageUrl = null;
		if (webserverResourcesFolder != null) {
			File webserverResourceFile = null;
			IWodBinding filenameBinding = wodElement.getBindingNamed("filename");
			if (filenameBinding != null && !filenameBinding.isKeyPath()) {
				String filename = filenameBinding.getValue().replaceAll("\"", "");
				webserverResourceFile = new File(webserverResourcesFolder, filename);
			}

			if (webserverResourceFile != null) {
				try {
					imageUrl = webserverResourceFile.getAbsoluteFile().toURL().toExternalForm();
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
		}
		
		if (imageUrl == null) {
			imageUrl = "MISSING";
		}

		htmlBuffer.append("<img src = \"" + imageUrl + "\"/>");
	}

}
