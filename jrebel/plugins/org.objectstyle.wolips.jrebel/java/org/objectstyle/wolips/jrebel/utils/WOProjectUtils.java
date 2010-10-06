package org.objectstyle.wolips.jrebel.utils;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.w3c.dom.Document;

public class WOProjectUtils {
  private static final XPath xpath = XPathFactory.newInstance().newXPath();
  private static final XPathExpression xp_bundleName, xp_bundleVersion;
  private static final DocumentBuilderFactory _builderFactory = DocumentBuilderFactory.newInstance();

  private WOProjectUtils() {
    //Utility class
  }
  
  static {
    _builderFactory.setValidating(false);
    _builderFactory.setNamespaceAware(false);
    _builderFactory.setAttribute("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
    try {
      xp_bundleName = xpath.compile("//plist/dict/string[preceding-sibling::key/text()=\"CFBundleName\"]");
      xp_bundleVersion = xpath.compile("//plist/dict/string[preceding-sibling::key/text()=\"CFBundleShortVersionString\"]");
    } catch (XPathExpressionException e) {
      throw new RuntimeException(e);
    } 
  }

  public static boolean isWOApplication(IJavaProject project) {
    try {
      return (project.findType("com.webobjects.appserver.WOApplication", (IProgressMonitor)null) != null);
    } catch (JavaModelException e) {
      e.printStackTrace();
    }
    return false;
  }
  
  public static String woVersion(IJavaProject project) {
    URLClassLoader classLoader = new WOProjectClassLoader(null, project);
    try {
      Enumeration<URL> urls = classLoader.getResources("Resources/Info.plist");
      DocumentBuilder docBuilder = _builderFactory.newDocumentBuilder();

      while (urls.hasMoreElements()) {
        URL url = urls.nextElement();
        try {
          docBuilder.reset();
          Document doc = docBuilder.parse(url.toString());
          String frameworkName = xp_bundleName.evaluate(doc);
          if ("WebObjects Framework".equals(frameworkName)) {
            return xp_bundleVersion.evaluate(doc);
          }
        } catch (Throwable e) {
          // Skip and move on
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ParserConfigurationException e) {
      e.printStackTrace();
    }
    
    return null;
  }
}
