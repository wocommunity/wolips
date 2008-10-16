/* ====================================================================
 *
 * The ObjectStyle Group Software License, Version 1.0
 *
 * Copyright (c) 2002 - 2004 The ObjectStyle Group
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.FilterSet;
import org.apache.tools.ant.types.FilterSetCollection;
import org.apache.tools.ant.types.PatternSet;
import org.objectstyle.woenvironment.env.WOVariables;
import org.objectstyle.woenvironment.util.FileStringScanner;

/**
 * Subclass of ProjectFormat that defines file copying strategy for
 * WOApplications.
 *
 * @author Andrei Adamchik
 */
public class AppFormat extends ProjectFormat {
  protected HashMap<String, String> templateMap = new HashMap<String, String>();

  protected HashMap<String, FilterSetCollection> filterMap = new HashMap<String, FilterSetCollection>();

  protected String appPaths;

  protected String frameworkPaths;

  protected String otherClasspaths;

  /**
   * Creates new AppFormat and initializes it with the name of the project
   * being built.
   */
  public AppFormat(WOTask task) {
    super(task);
    prepare();
  }

  /**
   * Builds a list of files for the application, maps them to templates and
   * filters.
   */
  private void prepare() {
    log("AppFormat prepare", Project.MSG_VERBOSE);
    preparePaths();

    prepare52();
    prepareWindows();
    prepareUnix();
    prepareMac();

    // add Info.plist
    String infoFile = new File(getApplicatonTask().contentsDir(), "Info.plist").getPath();
    createMappings(infoFile, woappPlusVersion() + "/Info.plist", infoFilter(null));
    // add web.xml
    if (((WOApplication) this.task).webXML) {
      String webXMLFile = new File(getApplicatonTask().contentsDir(), "web.xml").getPath();
      createMappings(webXMLFile, woappPlusVersion() + "/web.xml", webXMLFilter());
    }
  }

  /**
   * Prepares all path values needed for substitutions.
   */
  private void preparePaths() {
    appPaths = buildAppPaths();
    frameworkPaths = buildFrameworkPaths();
    otherClasspaths = buildOtherClassPaths();
  }

  /**
   * Prepares all path values needed for substitutions.
   */
  private void prepare52() {
    if (this.getApplicatonTask().getWOEnvironment().wo52()) {
      Copy cp = new Copy();
      // cp.setOwningTarget(getApplicatonTask().getProject().getDefaultTarget());
      cp.setProject(getApplicatonTask().getProject());
      cp.setTaskName("copy bootstrap");
      cp.setFile(this.getApplicatonTask().getWOEnvironment().bootstrap());
      cp.setTodir(getApplicatonTask().taskDir());
      cp.execute();
    }
  }

  /**
   * Returns a String that consists of paths to the application jar. File
   * separator used is platform dependent and may need to be changed when
   * creating files for multiple platforms.
   */
  protected String buildAppPaths() {
    FileSet fs = null;
    // include zips and jars
    // http://objectstyle.org/jira/secure/ViewIssue.jspa?key=WOL-47
    PatternSet.NameEntry includeJar = null, includeZip = null;
    DirectoryScanner ds = null;
    String[] files = null;
    StringBuffer buf = null;
    try {
      fs = new FileSet();
      fs.setDir(getApplicatonTask().contentsDir());
      includeJar = fs.createInclude();
      includeJar.setName("Resources/Java/**/*.jar");
      includeZip = fs.createInclude();
      includeZip.setName("Resources/Java/**/*.zip");
      ds = fs.getDirectoryScanner(task.getProject());
      files = ds.getIncludedFiles();
      buf = new StringBuffer();

      // prepend the path with Resources/Java (for CompilerProxy support)
      buf.append("APPROOT").append(File.separatorChar).append("Resources").append(File.separatorChar).append("Java").append(File.separatorChar).append(System.getProperty("line.separator"));
      for (int k = 0; k < 2; k++) {

        for (int i = 0; i < files.length; i++) {

          if (k == 0 && files[i].toString().indexOf("webobjects") >= 0) {
            continue;
          }
          if (k == 1 && files[i].toString().indexOf("webobjects") < 0) {
            continue;
          }
          buf.append("APPROOT").append(File.separatorChar).append(files[i]).append(System.getProperty("line.separator"));
        }
      }
      return buf.toString();
    }
    catch (Exception anException) {
      log(anException.getMessage(), Project.MSG_WARN);
    }
    finally {
      fs = null;
      includeJar = null;
      includeZip = null;
      ds = null;
      files = null;
      buf = null;
    }
    return "";
  }

  /**
   * Returns a String that consists of paths of all framework's jar's needed
   * by the application. File separator used is platform dependent and may
   * need to be changed when creating files for multiple platforms.
   */
  protected String buildFrameworkPaths() {
    WOVariables variables = getApplicatonTask().getWOEnvironment().getWOVariables();
    String relativeEmbeddedFrameworksDir = getApplicatonTask().relativeEmbeddedFrameworksDir();
    String result = FrameworkSet.jarsPathForFrameworkSets(task.getProject(), relativeEmbeddedFrameworksDir, getApplicatonTask().getFrameworkSets(), variables);
    return result;
  }

  /**
   * Method buildOtherClassPaths.
   *
   * @return String
   */
  protected String buildOtherClassPaths() {
    StringBuffer buf = new StringBuffer();

    List<?> classpathSets = getApplicatonTask().getOtherClasspath();
    Project project = task.getProject();

    // track included paths to avoid double entries
    HashSet<File> pathSet = new HashSet<File>();

    int size = classpathSets.size();
    try {
      for (int i = 0; i < size; i++) {

        OtherClasspathSet cs = (OtherClasspathSet) classpathSets.get(i);
        cs.collectClassPaths(project, pathSet);
      }
    }
    catch (BuildException be) {
      // paths doesn't exist or are not readable
      log(be.getMessage(), Project.MSG_WARN);
    }
    if (pathSet.size() > 0) {
      File someFiles[] = pathSet.toArray(new File[] {});
      size = someFiles.length;
      for (int i = 0; i < size; i++) {
        // log(": Framework JAR " + (File) someFiles[i],
        // Project.MSG_VERBOSE);
        String fileName = this.getApplicatonTask().getWOEnvironment().getWOVariables().encodePathForFile(someFiles[i]);

        // If it's not a jar file and it doesn't have a trailing '/'.
        // add one in.
        boolean isJar = fileName.endsWith(".jar") || fileName.endsWith(".zip");
        if (isJar == false && fileName.endsWith("/") == false) {
          fileName = fileName + "/";
        }
        buf.append(fileName).append("\r\n");
      }
    }
    return buf.toString();
  }

  /**
   * Prepare mappings for Windows subdirectory.
   */
  private void prepareWindows() {
    File winDir = new File(getApplicatonTask().contentsDir(), "Windows");
    String cp = new File(winDir, "CLSSPATH.TXT").getPath();
    createMappings(cp, woappPlusVersion() + "/Contents/Windows/CLSSPATH.TXT", classpathFilter('\\'));
    String subp = new File(winDir, "SUBPATHS.TXT").getPath();
    createMappings(subp, woappPlusVersion() + "/Contents/Windows/SUBPATHS.TXT");
    // add run script to Win. directory
    String runScript = new File(winDir, getName() + ".cmd").getPath();
    createMappings(runScript, woappPlusVersion() + "/Contents/Windows/appstart.cmd", startupScriptFilter());
    // add run script to top-level directory
    File taskDir = getApplicatonTask().taskDir();
    String startupScriptName = this.getApplicatonTask().startupScriptName;
    if (startupScriptName == null || startupScriptName.length() == 0) {
      startupScriptName = getName();
    }
    String topRunScript = new File(taskDir, startupScriptName + ".cmd").getPath();
    createMappings(topRunScript, woappPlusVersion() + "/Contents/Windows/appstart.cmd", startupScriptFilter());
  }

  /**
   * Prepare mappings for UNIX subdirectory.
   */
  private void prepareUnix() {
    File dir = new File(getApplicatonTask().contentsDir(), "UNIX");
    String cp = new File(dir, "UNIXClassPath.txt").getPath();
    createMappings(cp, woappPlusVersion() + "/Contents/UNIX/UNIXClassPath.txt", classpathFilter('/'));
  }

  /**
   * Prepare mappings for MacOS subdirectory.
   */
  private void prepareMac() {
    File macDir = new File(getApplicatonTask().contentsDir(), "MacOS");
    String cp = new File(macDir, "MacOSClassPath.txt").getPath();
    createMappings(cp, woappPlusVersion() + "/Contents/MacOS/MacOSClassPath.txt", classpathFilter('/'));
    String servercp = new File(macDir, "MacOSXServerClassPath.txt").getPath();
    createMappings(servercp, woappPlusVersion() + "/Contents/MacOS/MacOSXServerClassPath.txt", classpathFilter('/'));
    // add run script to Mac directory
    String runScript = new File(macDir, getName()).getPath();
    createMappings(runScript, woappPlusVersion() + "/Contents/MacOS/appstart", startupScriptFilter());
    // add run script to top-level directory
    File taskDir = getApplicatonTask().taskDir();
    String startupScriptName = this.getApplicatonTask().startupScriptName;
    if (startupScriptName == null || startupScriptName.length() == 0) {
      startupScriptName = getName();
    }
    String topRunScript = new File(taskDir, startupScriptName).getPath();
    createMappings(topRunScript, woappPlusVersion() + "/Contents/MacOS/appstart", startupScriptFilter());
  }

  /**
   * Creates a filter for Classpath helper files.
   */
  private FilterSet classpathFilter(char pathSeparator) {
    FilterSet filter = new FilterSet();
    if (pathSeparator == File.separatorChar) {
      filter.addFilter("APP_JAR", appPaths);
      filter.addFilter("FRAMEWORK_JAR", frameworkPaths);
      filter.addFilter("OTHER_PATHS", otherClasspaths);
    }
    else {
      filter.addFilter("APP_JAR", appPaths.replace(File.separatorChar, pathSeparator));
      filter.addFilter("FRAMEWORK_JAR", frameworkPaths.replace(File.separatorChar, pathSeparator));
      filter.addFilter("OTHER_PATHS", otherClasspaths.replace(File.separatorChar, pathSeparator));
    }

    return filter;
  }

  /**
   * Method getAppClass.
   *
   * @return String
   */
  private String getAppClass() {
    return task.getPrincipalClass();
  }

  /**
   * Returns the WO servlet adaptor for this application.
   * 
   * @return the WO servlet adaptor for this application
   */
  private String getServletAdaptor() {
    return task.getServletAdaptor();
  }

  /**
   * Method createMappings.
   *
   * @param fileName
   * @param template
   * @param filter
   */
  private void createMappings(String fileName, String template, FilterSet filter) {
    FilterSetCollection fsCollection = new FilterSetCollection(filter);
    FilterSet additionalBuildSettingsFilter = additionalBuildSettingsFilter();

    filter.addFilter("APP_CLASS", getAppClass());
    filter.addFilter("JAR_NAME", getJarName());

    if (additionalBuildSettingsFilter != null) {
      fsCollection.addFilterSet(additionalBuildSettingsFilter);
    }

    createMappings(fileName, template, fsCollection);
  }

  /**
   * Method createMappings.
   *
   * @param fileName
   * @param template
   */
  private void createMappings(String fileName, String template) {
    createMappings(fileName, template, (FilterSetCollection) null);
  }

  /**
   * Method createMappings.
   *
   * @param fileName
   * @param template
   * @param filter
   */
  private void createMappings(String fileName, String template, FilterSetCollection filter) {
    templateMap.put(fileName, template);
    filterMap.put(fileName, filter);
  }

  /**
   * Method getApplicatonTask.
   *
   * @return WOApplication
   */
  private WOApplication getApplicatonTask() {
    return (WOApplication) task;
  }

  /**
   * @see org.objectstyle.woproject.ant.ProjectFormat#fileIterator()
   */
  @Override
  public Iterator<String> fileIterator() {
    return templateMap.keySet().iterator();
  }

  /**
   * @see org.objectstyle.woproject.ant.ProjectFormat#templateForTarget(java.lang.String)
   */
  @Override
  public String templateForTarget(String targetName) throws BuildException {
    String template = templateMap.get(targetName);
    if (template == null) {
      throw new BuildException("Invalid target, no template found: " + targetName);
    }
    return template;
  }

  /**
   * @see org.objectstyle.woproject.ant.ProjectFormat#filtersForTarget(java.lang.String)
   */
  @Override
  public FilterSetCollection filtersForTarget(String targetName) throws BuildException {
    if (!filterMap.containsKey(targetName)) {
      throw new BuildException("Invalid target: " + targetName);
    }
    return filterMap.get(targetName);
  }

  /**
   * Method woappPlusVersion returns the template name.
   *
   * @return String
   */
  public String woappPlusVersion() {
    if (this.getApplicatonTask().getWOEnvironment().wo5or51())
      return "woapp";
    return "woapp_52";
  }

  /**
   * Launch scripts configuration
   * @return FilterSet
   */
  private FilterSet additionalBuildSettingsFilter() {
    String jvmOptions = getApplicatonTask().getJvmOptions();
    String jvm = getApplicatonTask().getJVM();
    String jdb = getApplicatonTask().getJDB();
    String jdbOptions = getApplicatonTask().getJDBOptions();
    String javaVersion = getApplicatonTask().getJavaVersion();

    if (jvmOptions != null) {
      FilterSet filter = new FilterSet();
      filter.addFilter("JVM_OPTIONS", jvmOptions);
      filter.addFilter("JVM", jvm);
      filter.addFilter("JDB", jdb);
      filter.addFilter("JDB_OPTIONS", jdbOptions);
      filter.addFilter("JAVA_VERSION", javaVersion);
      return filter;
    }

    return null;
  }

  /**
   *
   */
  @Override
  public void release() {
    super.release();
  }

  /**
   * Returns a FilterSet that can be used to build the startup script file.
   */
  public FilterSetCollection startupScriptFilter() {
    FilterSet filter = new FilterSet();
    String frameworksBaseURL = this.getApplicatonTask().getFrameworksBaseURL();
    if (frameworksBaseURL != null && frameworksBaseURL.length() > 0) {
      frameworksBaseURL = "-WOFrameworksBaseURL " + frameworksBaseURL;
    }
    else {
      frameworksBaseURL = "";
    }
    filter.addFilter("-WOFrameworksBaseURL", frameworksBaseURL);
    return new FilterSetCollection(filter);
  }

  protected String stripPath(String path) {
    path = path.replace("WOROOT", "");
    path = path.replace("APPROOT", "");
    path = path.replace("LOCALROOT", "");
    return path;
  }
  
  /**
   * Returns a FilterSet that can be used to build web.xml file.
   */
  public FilterSetCollection webXMLFilter() {
    //System.out.println("AppFormat.webXMLFilter: appPaths = " + appPaths);
    //System.out.println("AppFormat.webXMLFilter: frameworkPaths = " + frameworkPaths);
    //System.out.println("AppFormat.webXMLFilter: otherClasspaths = " + otherClasspaths);
    FilterSet filter = new FilterSet();
    String WEBINFROOT = "WEBINFROOT";
    List<String> paths = new LinkedList<String>();
    if (appPaths != null && appPaths.length() > 0) {
      appPaths = appPaths.trim();
      for (String appPath : appPaths.split("\n")) {
        paths.add(WEBINFROOT + stripPath(appPath));
      }
    }
    if (frameworkPaths != null && frameworkPaths.length() > 0) {
      frameworkPaths = frameworkPaths.trim();
      for (String frameworkPath : frameworkPaths.split("\n")) {
        paths.add(WEBINFROOT + "/" + getApplicatonTask().getName() + ".woa/Contents" + stripPath(frameworkPath));
      }
    }
    if (otherClasspaths != null && otherClasspaths.length() > 0) {
      otherClasspaths = otherClasspaths.trim();
      for (String otherPath : otherClasspaths.split("\n")) {
        paths.add(WEBINFROOT + stripPath(otherPath));
      }
    }
    
    StringBuffer pathsBuffer = new StringBuffer();
    for (String path : paths) {
      pathsBuffer.append(path);
      pathsBuffer.append("\n");
    }

    WOApplication woappTask = (WOApplication) this.task;
    log(" AppFormat.webXMLFilter().woappTask: " + woappTask, Project.MSG_VERBOSE);
    filter.addFilter("WOROOT", woappTask.getWebXML_WOROOT());
    filter.addFilter("LOCALROOT", woappTask.getWebXML_LOCALROOT());
    filter.addFilter("WOAINSTALLROOT", woappTask.getWebXML_WOAINSTALLROOT());
    filter.addFilter("WOAppMode", woappTask.getWebXML_WOAppMode());
    filter.addFilter("WOClasspath", pathsBuffer.toString());
    filter.addFilter("WOApplicationClass", this.getAppClass());
    filter.addFilter("WOServletAdaptor", this.getServletAdaptor());
    filter.addFilter("WOTagLib", woappTask.getWebXML_WOtaglib());
    String customContent = woappTask.getWebXML_CustomContent();
    if (customContent == null) {
      customContent = "";
    }
    filter.addFilter("CustomContent", customContent);
    return new FilterSetCollection(filter);
  }
}
