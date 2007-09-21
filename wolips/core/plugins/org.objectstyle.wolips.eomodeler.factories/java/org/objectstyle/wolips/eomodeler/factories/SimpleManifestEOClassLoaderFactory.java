package org.objectstyle.wolips.eomodeler.factories;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Set;

import org.objectstyle.wolips.eomodeler.core.model.AbstractEOClassLoader;
import org.objectstyle.wolips.eomodeler.core.model.EOModel;
import org.objectstyle.wolips.eomodeler.core.utils.URLUtils;

public class SimpleManifestEOClassLoaderFactory extends AbstractEOClassLoader {
  @Override
  protected void fillInDevelopmentClasspath(Set<URL> classpathUrls) throws Exception {
  }

  @Override
  protected void fillInModelClasspath(EOModel model, Set<URL> classpathUrls) throws Exception {
    File modelFolder = URLUtils.cheatAndTurnIntoFile(model.getModelURL()).getParentFile();
    fillInClasspathURLs(new File(modelFolder, "EntityModeler.classpath"), classpathUrls);
    fillInClasspathURLs(new File(modelFolder, ".EntityModeler.classpath"), classpathUrls);
    fillInClasspathURLs(new File(modelFolder.getParent(), "EntityModeler.classpath"), classpathUrls);
    fillInClasspathURLs(new File(modelFolder.getParent(), ".EntityModeler.classpath"), classpathUrls);
    fillInClasspathURLs(new File(System.getProperty("user.home"), "EntityModeler.classpath"), classpathUrls);
    fillInClasspathURLs(new File(System.getProperty("user.home"), ".EntityModeler.classpath"), classpathUrls);
    fillInClasspathURLs(new File(System.getProperty("user.home") + "/Library", "EntityModeler.classpath"), classpathUrls);
    fillInClasspathURLs(new File(System.getProperty("user.home") + "/Library", ".EntityModeler.classpath"), classpathUrls);
    fillInClasspathURLs(new File(System.getProperty("user.home") + "/Library/Preferences", "EntityModeler.classpath"), classpathUrls);
    fillInClasspathURLs(new File(System.getProperty("user.home") + "/Library/Preferences", ".EntityModeler.classpath"), classpathUrls);
  }

  protected void fillInClasspathURLs(File manifestFile, Set<URL> classpathUrls) throws IOException {
    if (manifestFile.exists()) {
      BufferedReader manifestReader = new BufferedReader(new FileReader(manifestFile));
      try {
        String searchFolderPath;
        while ((searchFolderPath = manifestReader.readLine()) != null) {
          File searchFolder = new File(searchFolderPath).getAbsoluteFile();
          if (searchFolder != null && searchFolder.exists()) {
            if (searchFolder.getName().endsWith(".framework")) {
              File javaFolder = new File(searchFolder, "Resources/Java");
              if (javaFolder.exists()) {
                classpathUrls.add(javaFolder.toURL());
                File[] jarFiles = javaFolder.listFiles();
                for (File jarFile : jarFiles) {
                  if (jarFile.getName().toLowerCase().endsWith(".jar")) {
                    classpathUrls.add(jarFile.toURL());
                  }
                }
              }
            }
            else {
              classpathUrls.add(searchFolder.toURL());
            }
          }
        }
      }
      finally {
        manifestReader.close();
      }
    }
  }
}
