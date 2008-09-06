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
package org.objectstyle.woenvironment.frameworks;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.objectstyle.woenvironment.util.Parser;

public abstract class AbstractFolderFramework extends Framework {
  private File frameworkFolder;

  private List<FrameworkLibrary> libraries;

  private File javaFolder;

  public AbstractFolderFramework(Root root, File frameworkFolder) {
    super(root, AbstractFolderFramework.frameworkNameForFolder(frameworkFolder));
    this.frameworkFolder = frameworkFolder;
    reloadLibraries();
  }
  
  public File getFrameworkFolder() {
    return frameworkFolder;
  }

  @SuppressWarnings("unchecked")
  public Map<String, Object> getInfoPlist() {
    Map<String, Object> propertyList;
    File infoPlist = new File(frameworkFolder, "Resources/Info.plist");
    if (infoPlist.exists()) {
      Parser propertyListParser;
      try {
        propertyListParser = new Parser(infoPlist);
        Object propertyListObj = propertyListParser.propertyList();
        if (propertyListObj instanceof Map) {
          propertyList = (Map<String, Object>) propertyListObj;
        }
        else if (propertyListObj instanceof String) {
          //Document infoPlist = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(infoPlist);
          // XML Plist
          propertyList = null;
        }
        else {
          propertyList = null;
        }
      }
      catch (FileNotFoundException e) {
        throw new RuntimeException("Failed to load info.plist for " + getName() + ".", e);
      }
    }
    else {
      propertyList = null;
    }
    return propertyList;
  }

  protected void guessJars(File folder, List<File> jarFiles) {
    File[] guessedJarFiles = folder.listFiles();
    if (guessedJarFiles != null && guessedJarFiles.length > 0) {
      for (File guessedJarFile : guessedJarFiles) {
        if (guessedJarFile.isDirectory()) {
          guessJars(guessedJarFile, jarFiles);
        }
        else {
          String guessedJarFileName = guessedJarFile.getName();
          if ((guessedJarFileName.toLowerCase().endsWith(".jar") || guessedJarFileName.toLowerCase().endsWith(".zip")) && !isSourceJar(guessedJarFileName)) {
            jarFiles.add(guessedJarFile);
          }
        }
      }
    }
  }
  
  @SuppressWarnings("unchecked")
  public synchronized void reloadLibraries() {
    this.libraries = new LinkedList<FrameworkLibrary>();
    List<File> jarFiles = new LinkedList<File>();

    boolean guessJars = true;
    javaFolder = new File(frameworkFolder, "Resources/Java");
    Map infoPlist = getInfoPlist();
    if (infoPlist != null) {
      String javaRoot = (String) infoPlist.get("NSJavaRoot");
      if (javaRoot != null) {
        javaFolder = new File(frameworkFolder, javaRoot);
      }
      List<String> javaPaths = (List<String>) infoPlist.get("NSJavaPath");
      if (javaPaths != null) {
        for (String javaPath : javaPaths) {
          File jarFile = new File(javaFolder, javaPath);
          String jarFileName = jarFile.getName();
          if (jarFile.exists() && !isSourceJar(jarFileName)) {
            jarFiles.add(jarFile);
          }
        }
        guessJars = false;
      }
    }

    if (guessJars && javaFolder.exists()) {
      guessJars(javaFolder, jarFiles);
    }

    for (File jarFile : jarFiles) {
      String jarFileName = jarFile.getName();
      String sourceJar = getSourceJarNameForJarNamed(jarFileName);
      File sourceJarFile = new File(jarFile.getParentFile(), sourceJar);
      if (!sourceJarFile.exists()) {
        sourceJarFile = jarFile;
      }
      FrameworkLibrary library = new FrameworkLibrary(jarFile, sourceJarFile, null, null, null);
      this.libraries.add(library);
    }
  }

  protected String getSourceJarNameForJarNamed(String jarName) {
    String sourceJarName;
    if (jarName.equalsIgnoreCase(getName() + ".jar")) {
      sourceJarName = "src.jar";
    }
    else {
      sourceJarName = jarName.replaceFirst("\\.jar", "-src.jar");
    }
    return sourceJarName;
  }

  protected boolean isSourceJar(String jarName) {
    boolean isSourceJar = false;
    if (jarName.equals("src.jar") || jarName.endsWith("-src.jar")) {
      isSourceJar = true;
    }
    return isSourceJar;
  }

  public List<FrameworkLibrary> getFrameworkLibraries() {
    return this.libraries;
  }

  public IFramework resolveFramework() {
    return this;
  }

  public boolean isResolved() {
    return true;
  }
  
  @Override
  public String toString() {
    return "[Framework: name = " + getName() + "; folder = " + getFrameworkFolder() + "]";
  }

  public static String frameworkNameForFolder(File frameworkFolder) {
    String name = frameworkFolder.getName();
    name = name.substring(0, name.lastIndexOf(".framework"));
    return name;
  }
}