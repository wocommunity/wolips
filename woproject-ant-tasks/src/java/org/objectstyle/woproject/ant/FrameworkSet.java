/* ====================================================================
 *
 * The ObjectStyle Group Software License, Version 1.0
 *
 * Copyright (c) 2002 - 2005 The ObjectStyle Group
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
package org.objectstyle.woproject.ant;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.FileScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.PropertyHelper;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.PatternSet;
import org.apache.tools.ant.types.PatternSet.NameEntry;
import org.objectstyle.woenvironment.env.WOVariables;
import org.objectstyle.woenvironment.frameworks.ExternalFolderFramework;
import org.objectstyle.woenvironment.frameworks.ExternalFolderRoot;
import org.objectstyle.woenvironment.frameworks.ExternalFrameworkModel;
import org.objectstyle.woenvironment.frameworks.FrameworkLibrary;
import org.objectstyle.woenvironment.frameworks.IFramework;
import org.objectstyle.woenvironment.frameworks.Root;
import org.w3c.dom.Node;

/**
 * A subclass of FileSet that with special support for matching WOFrameworks.
 *
 * @author Andrei Adamchik
 */
public class FrameworkSet extends FileSet {
  private Root<IFramework> root;

  private ExternalFrameworkModel frameworkModel;

  private boolean eclipse;

  private boolean embed;

  private boolean hasBundles;

  private boolean frameworkIncludesCreated;

  private File deploymentDir;

  private String ifCondition;

  /**
   * Creates new FrameworkSet.
   */
  public FrameworkSet() {
    ifCondition = "";
  }

  public void setEclipse(boolean eclipse) {
    this.eclipse = eclipse;
  }

  public boolean getEclipse() {
    return this.eclipse;
  }

  public void setFrameworkModel(ExternalFrameworkModel frameworkModel) {
    this.frameworkModel = frameworkModel;
  }

  @SuppressWarnings("unchecked")
  public ExternalFrameworkModel getFrameworkModel() {
    if (this.frameworkModel == null) {
      this.frameworkModel = new ExternalFrameworkModel(getProject().getProperties());
    }
    return this.frameworkModel;
  }

  /**
   * Sets the deployment root directory (can be different from the normal
   * <code>root</code> specified). The idea is that you can specify the
   * compile time path via <code>dir=/some_binary_release_path/</code>, but
   * still end up with <code>LOCALROOT/Library/Frameworks/Bar.framework</code>
   * as the prefix.
   *
   * @param root
   */
  public void setDeploymentDir(File root) {
    this.deploymentDir = root;
  }

  public void setBundles(String value) {
    String bundles[] = value.split("/");
    PatternSet ps = createPatternSet();
    for (int i = 0; i < bundles.length; i++) {
      String framework = bundles[i];
      if (framework.trim().length() > 0) {
        hasBundles = true;
        framework = framework + ".framework";
        ps.createInclude().setName(framework);
      }
    }
  }

  protected File getDeployedFile(File file) {
    File result = file;
    if (this.deploymentDir != null && !getEmbed()) {
      // maps
      // foo/bar/Baz.framework/Resources/Java/xxx.jar ->
      // /System/Library/Frameworks/Baz.framework
      String oldPath = file.getPath();
      String newRoot = deploymentDir.getPath();
      String newPath = oldPath.replaceFirst("(.*?)(/\\w+\\.framework/)", newRoot + "$2");
      result = new File(newPath);
    }
    return result;
  }

  protected void setFrameworkRoot(ExternalFolderRoot root) {
    this.root = root;
  }

  protected Root<IFramework> getFrameworkRoot() {
    if (this.root == null) {
      ExternalFrameworkModel currentFrameworkModel = getFrameworkModel();
      try {
        this.root = currentFrameworkModel.getRootForFolder(getDir());
      }
      catch (IOException e) {
        throw new BuildException("There was no matching framework root found for the folder '" + getDir() + "'.  The known framework roots are " + currentFrameworkModel.getRoots() + ".", e);
      }
    }
    return this.root;
  }

  /**
   * Sets root directory of this FileSet based on a symbolic name, that can be
   * "User", "Local", "System". Throws BuildException if an
   * invalid root is specified.
   */
  public void setRoot(String rootName) throws BuildException {
    ExternalFrameworkModel currentFrameworkModel = getFrameworkModel();
    this.root = currentFrameworkModel.getRootWithShortName(rootName);
    if (this.root == null) {
      throw new BuildException("There is no root named '" + rootName + "' in " + currentFrameworkModel.getRoots() + ".");
    }
    File rootFolder = ((ExternalFolderRoot) this.root).getFrameworksFolder();
    setDir(rootFolder);
  }

  protected List<ExternalFolderFramework> getEclipseFrameworks() {
    try {
      List<ExternalFolderFramework> frameworks = new LinkedList<ExternalFolderFramework>();
      List<Node> conEntries = FileUtil.getClasspathEntriesOfKind(getProject().getBaseDir(), "con");
      for (Node conEntry : conEntries) {
        Node pathAttribute = conEntry.getAttributes().getNamedItem("path");
        String path = pathAttribute.getTextContent();
        if (path != null && path.startsWith("WOFramework/")) {
          int slashIndex = path.indexOf("/");
          String frameworkName = path.substring(slashIndex + 1);
          IFramework framework = this.frameworkModel.getFrameworkWithName(frameworkName);
          if (framework == null) {
            throw new BuildException("The framework name '" + frameworkName + "' does not exist.");
          }
          else if (framework instanceof ExternalFolderFramework && framework.getRoot() == this.root) {
            ExternalFolderFramework externalFolderFramework = (ExternalFolderFramework) framework;
            frameworks.add(externalFolderFramework);
          }
        }
      }
      return frameworks;
    }
    catch(BuildException e) {
    	throw e;
    }
    catch (Throwable t) {
      throw new BuildException("Failed to process eclipse frameworks: "+t.getMessage(), t);
    }
  }

  @Override
  public synchronized void setupDirectoryScanner(FileScanner ds, Project p) {
    if (this.eclipse && !frameworkIncludesCreated) {
      try {
        List<ExternalFolderFramework> frameworks = getEclipseFrameworks();
        for (ExternalFolderFramework framework : frameworks) {
          NameEntry frameworkInclude = createInclude();
          frameworkInclude.setName(framework.getFrameworkFolder().getName());
        }

		// If no frameworks are to be included from this directory, create an
		// empty include.  Setting a name or using an exclude will throw Ant into
		// an infinite loop.
        if (frameworks.isEmpty()) {
          NameEntry frameworkExclude = createInclude();
        }
        frameworkIncludesCreated = true;
      }
      catch (Throwable t) {
        throw new BuildException("Failed to process eclipse frameworks: "+t.getMessage(), t);
      }
    }

    super.setupDirectoryScanner(ds, p);
  }

  public void setEmbed(boolean flag) {
    this.embed = flag;
  }

  public boolean getEmbed() {
    if (isReference() && getProject() != null) {
      return ((FrameworkSet) getRef(getProject())).getEmbed();
    }
    return this.embed;
  }

  public void setIf(String string) {
    ifCondition = string == null ? "" : string;
  }

  protected Path getJarsPath() {
    Path frameworkPath = new Path(getProject());
    
    List<ExternalFolderFramework> frameworks;
    if (getEclipse()) {
      frameworks = getEclipseFrameworks();
    }
    else {
      frameworks = new LinkedList<ExternalFolderFramework>();
      String[] includedFrameworkFolderNames = getDirectoryScanner(getProject()).getIncludedDirectories();
      for (String includedFrameworkFolderName : includedFrameworkFolderNames) {
        String frameworkName = ExternalFolderFramework.frameworkNameForFolder(new File(includedFrameworkFolderName));
        if (frameworkName != null) {
          if (getDir() == null) {
            IFramework framework = getFrameworkModel().getFrameworkWithName(frameworkName);
            if (framework instanceof ExternalFolderFramework) {
              frameworks.add((ExternalFolderFramework) framework);
            }
          }
          else {
            ExternalFolderFramework framework = new ExternalFolderFramework(getFrameworkRoot(), new File(getDir(), includedFrameworkFolderName));
            frameworks.add(framework);
          }
        }
        else {
          System.out.println("FrameworkSet.getJarsPath: ILLEGAL FRAMEWORK NAMED " + frameworkName);
        }
      }
    }

    for (IFramework framework : frameworks) {
      if (framework.getRoot().equals(getFrameworkRoot())) {
        for (FrameworkLibrary frameworkLibrary : framework.getFrameworkLibraries()) {
          File jarFile = frameworkLibrary.getLibraryFile();
          File deployedJarFile = getDeployedFile(jarFile);
          //log(": Framework JAR " + jarFile, Project.MSG_VERBOSE);
          frameworkPath.setLocation(deployedJarFile);
        }
      }
      else {
        //System.out.println("FrameworkSet.getJarsPath:   SKIPPED FRAMEWORK " + framework + " (" + includedFrameworkName + ") for " + getFrameworkRoot());
      }
    }
    return frameworkPath;
  }

  public static Path jarsPathForFrameworkSets(Project project, List<FrameworkSet> frameworkSets, WOVariables variables) {
    List<AntDependency> unorderedDependencies = new LinkedList<AntDependency>();
    for (FrameworkSet frameworkSet : frameworkSets) {
      Path jarsPath = frameworkSet.getJarsPath();
      for (String jarPath : jarsPath.list()) {
        unorderedDependencies.add(new AntDependency(frameworkSet, jarPath, variables));
      }
    }
    List<AntDependency> orderedDependencies = new AntDependencyOrdering().orderDependencies(unorderedDependencies);

    Path path = new Path(project);
    for (AntDependency dependency : orderedDependencies) {
      String jarPath = dependency.getJarPath();
      path.append(new Path(project, jarPath));
    }

    //System.out.println("FrameworkSet.jarsPathForFrameworkSets1: <" + path + ">");
    return path;
  }

  public static String jarsPathForFrameworkSets(Project project, String relativeEmbeddedFrameworksDir, List<FrameworkSet> frameworkSets, WOVariables variables) {
    List<AntDependency> unorderedDependencies = new LinkedList<AntDependency>();
    for (FrameworkSet frameworkSet : frameworkSets) {
      Path jarsPath = frameworkSet.getJarsPath();
      for (String jarPath : jarsPath.list()) {
        unorderedDependencies.add(new AntDependency(frameworkSet, jarPath, variables));
      }
    }

    StringBuffer path = new StringBuffer();
    List<AntDependency> orderedDependencies = new AntDependencyOrdering().orderDependencies(unorderedDependencies);
    for (AntDependency dependency : orderedDependencies) {
      String jarPath = dependency.getJarPath();
      String encodedPath = variables.encodePath(jarPath);
      FrameworkSet frameworkSet = dependency.getFrameworkSet();
      if (frameworkSet.getEmbed()) {
        String prefix = frameworkSet.getDir(project).getAbsolutePath();
        prefix = variables.encodePath(prefix);
        if (frameworkSet.hasBundles()) {
          encodedPath = encodedPath.replaceFirst(".*?(\\w+.framework)", "APPROOT/" + relativeEmbeddedFrameworksDir + "/$1");
        }
        else {
          encodedPath = encodedPath.replaceFirst(prefix, "APPROOT/" + relativeEmbeddedFrameworksDir);
        }
      }

      path.append(encodedPath).append(System.getProperty("line.separator"));
    }

    //System.out.println("FrameworkSet.jarsPathForFrameworkSets2: <" + path + ">");
    return path.toString();
  }

  private boolean hasBundles() {
    return hasBundles;
  }

  @Override
  public DirectoryScanner getDirectoryScanner(Project p) {
    if (getDir() == null || !getDir().exists()) {
      DirectoryScanner scanner = new DirectoryScanner() {
        @Override
        public synchronized String[] getIncludedDirectories() {
          return new String[0];
        }

        @Override
        public synchronized int getIncludedDirsCount() {
          return 0;
        }

        @Override
        public synchronized String[] getIncludedFiles() {
          return new String[0];
        }

        @Override
        public synchronized int getIncludedFilesCount() {
          return 0;
        }
      };
      return scanner;
    }
    return super.getDirectoryScanner(p);
  }

  private static String replaceProperties(Project project, String value, Hashtable keys) throws BuildException {
    PropertyHelper ph = PropertyHelper.getPropertyHelper(project);
    return ph.replaceProperties(null, value, keys);
  }

  private boolean testIfCondition() {
    if ("".equals(ifCondition)) {
      return true;
    }
    String string = FrameworkSet.replaceProperties(getProject(), ifCondition, getProject().getProperties());
    return getProject().getProperty(string) != null;
  }

  @Override
  public String toString() {
    return "[FrameworkSet: root = " + getDir() + "]";
  }

//  /**
//   * Overrides the super method in order to return the right DirectoryScanner
//   * that doesn't sort directories. See svn revision detail:
//   * http://svn.apache.org/viewcvs.cgi?rev=274976&view=rev
//   *
//   * Instead sort by the order defined in the include list.
//   */
//  @Override
//  public DirectoryScanner getDirectoryScanner(Project p) {
//    DirectoryScanner ds = super.getDirectoryScanner(p);
//    if (isReference()) {
//      return ds;
//    }
//    // Setup a new type for the directory scanner to avoid sorting included
//    // directories as set by:
//    // http://svn.apache.org/viewcvs.cgi?rev=274976&view=rev
//    // but rather sort by order of the includes list.
//    ds = new SortedDirectoryScanner();
//    setupDirectoryScanner(ds, p);
//    ds.setFollowSymlinks(isFollowSymlinks());
//    ds.scan();
//    return ds;
//  }

//  protected class SortedDirectoryScanner extends DirectoryScanner implements Comparator {
//    private List<String> includeNonPatternList;
//
//    @SuppressWarnings("unchecked")
//    @Override
//    public synchronized String[] getIncludedDirectories() {
//      if (dirsIncluded == null) {
//        throw new IllegalStateException();
//      }
//      if (includeNonPatternList == null) {
//        includeNonPatternList = new ArrayList<String>();
//        fillNonPatternList(includeNonPatternList, includes);
//      }
//      Collections.sort(dirsIncluded, this);
//      String directories[] = new String[dirsIncluded.size()];
//      dirsIncluded.copyInto(directories);
//      return directories;
//    }
//
//    @Override
//    public synchronized String[] getIncludedFiles() {
//      // The results of calling jarsPaths() on the FrameworkSet is a
//      // fully qualified path.  This creates a problem as FileSet qualifies
//      // these file paths with the directory.  To accomodate this, we
//      // trim off the root dir.  This will be added back after getIncludedFiles()
//      // is called.  This (hack) was done instead of making a parallel
//      // implementation of jarsPath() that returns a partial path.
//      String[] frameworkJars = getJarsPath().list();
//      int dirLength = getDir(getProject()).toString().length();
//      for (int i = 0; i < frameworkJars.length; i++) {
//        frameworkJars[i] = frameworkJars[i].substring(dirLength);
//      }
//
//      // The included files are both the normally included files as well as
//      // the jars in the frameworks
//      String[] files = super.getIncludedFiles();
//      String[] all = new String[files.length + frameworkJars.length];
//      System.arraycopy(files, 0, all, 0, files.length);
//      System.arraycopy(frameworkJars, 0, all, files.length, frameworkJars.length);
//
//      return all;
//    }
//
//    public int compare(Object o1, Object o2) {
//      String frameworkDir1 = (String) o1;
//      String frameworkDir2 = (String) o2;
//      if (isCaseSensitive()) {
//        return includeNonPatternList.indexOf(frameworkDir1) - includeNonPatternList.indexOf(frameworkDir2);
//      }
//      return includeNonPatternList.indexOf(frameworkDir1.toUpperCase()) - includeNonPatternList.indexOf(frameworkDir2.toUpperCase());
//    }
//
//    @SuppressWarnings("unchecked")
//    private String[] fillNonPatternList(List list, String patterns[]) {
//      System.out.println("SortedDirectoryScanner.fillNonPatternList: " + list + ", " + patterns);
//      ArrayList<String> al = new ArrayList<String>(patterns.length);
//      for (int i = 0; i < patterns.length; i++)
//        if (!SelectorUtils.hasWildcards(patterns[i])) {
//          list.add(isCaseSensitive() ? ((Object) (patterns[i])) : ((Object) (patterns[i].toUpperCase())));
//        }
//        else {
//          al.add(patterns[i]);
//        }
//
//      return list.size() != 0 ? (String[]) al.toArray(new String[al.size()]) : patterns;
//    }
//
//  }

}
