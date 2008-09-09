/* ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0 
 *
 * Copyright (c) 2002 - 2006 The ObjectStyle Group 
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
package org.objectstyle.woenvironment.env;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import org.objectstyle.woenvironment.util.FileStringScanner;

/**
 * @author uli
 * 
 */
public class WOVariables {
  private static final String USER_ROOT = "wo.user.root";

  private static final String USER_FRAMEWORKS = "wo.user.frameworks";

  private static final String LOCAL_ROOT = "wo.local.root";

  private static final String LOCAL_FRAMEWORKS = "wo.local.frameworks";

  private static final String SYSTEM_ROOT = "wo.system.root";

  private static final String SYSTEM_FRAMEWORKS = "wo.system.frameworks";

  private static final String NETWORK_ROOT = "wo.network.root";

  private static final String NETWORK_FRAMEWORKS = "wo.network.frameworks";

  private static final String EXTERNAL_BUILD_ROOT = "wo.external.root";

  private static final String EXTERNAL_BUILD_FRAMEWORKS = "wo.external.frameworks";

  private static final String APPS_ROOT = "wo.apps.root";

  private static final String API_ROOT_KEY = "wo.api.root";

  private static final String BOOTSTRAP_JAR_KEY = "wo.bootstrapjar";

  private static final String WEBOBJECTS_EXTENSIONS = "wo.extensions";

  private static final String WOLIPS_PROPERTIES = "WOLIPS_PROPERTIES";

  private static final String WOLIPS_PROPERTIES_FILE_NAME = "wolips.properties";

  private Properties wolipsProperties;

  private File wolipsPropertiesFile;

  private Environment environment;

  public WOVariables(Environment environment, WOVariables variables, Map<Object, Object> existingProperties) {
    this.environment = environment;
    this.init(variables, existingProperties);
  }
  
  public WOVariables(Environment environment, Map<Object, Object> existingProperties) {
    this.environment = environment;
    this.init(null, existingProperties);
  }

  public void init(WOVariables variables, Map<Object, Object> existingProperties) {
    if (variables == null) {
      // load properties
      this.wolipsProperties = new Properties();
  
      String wobuildPropertiesPath = System.getProperty(WOVariables.WOLIPS_PROPERTIES);
      if (wobuildPropertiesPath != null) {
        this.wolipsPropertiesFile = new File(wobuildPropertiesPath);
      }
      if (!isValidWOlipsPropertiesFile()) {
        wobuildPropertiesPath = this.environment.getEnvVars().getProperty(WOVariables.WOLIPS_PROPERTIES);
        if (wobuildPropertiesPath != null) {
          this.wolipsPropertiesFile = new File(wobuildPropertiesPath);
        }
      }
  
      if (!isValidWOlipsPropertiesFile()) {
        if (isWindows()) {
          this.wolipsPropertiesFile = new File(System.getenv("APPDATA") + "\\WOLips\\" + WOVariables.WOLIPS_PROPERTIES_FILE_NAME);
        }
        else {
          this.wolipsPropertiesFile = new File(this.environment.userHome(), "Library/Application Support/WOLips/" + WOVariables.WOLIPS_PROPERTIES_FILE_NAME);
        }
      }
  
      if (isValidWOlipsPropertiesFile()) {
        try {
          this.wolipsProperties.load(new FileInputStream(this.wolipsPropertiesFile));
        }
        catch (IOException e) {
          throw new RuntimeException("Failed to configure " + wolipsPropertiesFile + ".", e);
        }
      }
      else if (existingProperties == null || existingProperties.isEmpty()) {
        createDefaultProperties();
      }
    }
    else {
      this.wolipsProperties = new Properties();
      this.wolipsProperties.putAll(variables.wolipsProperties);
    }

    if (existingProperties != null) {
      for (Map.Entry<Object, Object> entry : existingProperties.entrySet()) {
        if (entry.getKey() instanceof String && entry.getValue() instanceof String) {
          this.wolipsProperties.setProperty((String) entry.getKey(), (String) entry.getValue());
        }
      }
    }
  }

  public void createDefaultProperties() {
    this.wolipsProperties = new Properties();
    if (isWindows()) {
      this.wolipsProperties.setProperty(WOVariables.API_ROOT_KEY, "/Developer/ADC%20Reference%20Library/documentation/WebObjects/Reference/API/");
      this.wolipsProperties.setProperty(WOVariables.APPS_ROOT, "C:\\Apple\\Applications");
      this.wolipsProperties.setProperty(WOVariables.BOOTSTRAP_JAR_KEY, "C:\\Apple\\Library\\Application\\wotaskd.woa\\WOBootstrap.jar");
      this.wolipsProperties.setProperty(WOVariables.LOCAL_ROOT, "C:\\Apple\\Local");
      this.wolipsProperties.setProperty(WOVariables.LOCAL_FRAMEWORKS, "C:\\Apple\\Local\\Library\\Frameworks");
      this.wolipsProperties.setProperty(WOVariables.SYSTEM_ROOT, "C:\\Apple");
      this.wolipsProperties.setProperty(WOVariables.SYSTEM_FRAMEWORKS, "C:\\Apple\\Library\\Frameworks");
      this.wolipsProperties.setProperty(WOVariables.NETWORK_ROOT, "C:\\Apple\\Network");
      this.wolipsProperties.setProperty(WOVariables.NETWORK_FRAMEWORKS, "C:\\Apple\\Network\\Library\\Frameworks");
      this.wolipsProperties.setProperty(WOVariables.USER_ROOT, System.getProperty("user.home"));
      this.wolipsProperties.setProperty(WOVariables.USER_FRAMEWORKS, System.getProperty("user.home") + "\\Library\\Frameworks");
      this.wolipsProperties.setProperty(WOVariables.WEBOBJECTS_EXTENSIONS, "C:\\Apple\\Extensions");
    }
    else {
      this.wolipsProperties.setProperty(WOVariables.API_ROOT_KEY, "/Developer/ADC%20Reference%20Library/documentation/WebObjects/Reference/API/");
      this.wolipsProperties.setProperty(WOVariables.APPS_ROOT, "/Library/WebObjects/Applications");
      this.wolipsProperties.setProperty(WOVariables.BOOTSTRAP_JAR_KEY, "/System/Library/WebObjects/JavaApplications/wotaskd.woa/WOBootstrap.jar");
      this.wolipsProperties.setProperty(WOVariables.LOCAL_ROOT, "/");
      this.wolipsProperties.setProperty(WOVariables.LOCAL_FRAMEWORKS, "/Library/Frameworks");
      this.wolipsProperties.setProperty(WOVariables.SYSTEM_ROOT, "/System");
      this.wolipsProperties.setProperty(WOVariables.SYSTEM_FRAMEWORKS, "/System/Library/Frameworks");
      this.wolipsProperties.setProperty(WOVariables.NETWORK_ROOT, "/Network");
      this.wolipsProperties.setProperty(WOVariables.NETWORK_FRAMEWORKS, "/Network/Library/Frameworks");
      this.wolipsProperties.setProperty(WOVariables.USER_ROOT, System.getProperty("user.home"));
      this.wolipsProperties.setProperty(WOVariables.USER_FRAMEWORKS, System.getProperty("user.home") + "/Library/Frameworks");
      this.wolipsProperties.setProperty(WOVariables.WEBOBJECTS_EXTENSIONS, "/Library/WebObjects/Extensions");
    }

    try {
      File wolipsPropertiesFolder = this.wolipsPropertiesFile.getParentFile(); 
      if (!wolipsPropertiesFolder.exists()) {
        wolipsPropertiesFolder.mkdirs();
      }
      if (wolipsPropertiesFolder.canWrite()) {
        FileOutputStream wolipsPropertiesStream = new FileOutputStream(this.wolipsPropertiesFile);
        try {
          this.wolipsProperties.store(wolipsPropertiesStream, null);
        }
        finally {
          wolipsPropertiesStream.close();
        }
      }
    }
    catch (IOException e) {
      throw new RuntimeException("Failed to write " + this.wolipsPropertiesFile + ".", e);
    }
  }

  public boolean isValidWOlipsPropertiesFile() {
    return this.wolipsPropertiesFile != null && this.wolipsPropertiesFile.exists() && !this.wolipsPropertiesFile.isDirectory();
  }

  public File wolipsPropertiesFile() {
    return this.wolipsPropertiesFile;
  }

  /**
   * Where you store your XCode or ant generated stuff.
   * 
   * @return String
   */
  public String externalBuildRoot() {
    return this.wolipsProperties.getProperty(WOVariables.EXTERNAL_BUILD_ROOT);
  }
  
  public String externalBuildFrameworkPath() {
    return this.wolipsProperties.getProperty(WOVariables.EXTERNAL_BUILD_FRAMEWORKS);
  }

  public String localRoot() {
    return this.wolipsProperties.getProperty(WOVariables.LOCAL_ROOT);
  }

  public String localFrameworkPath() {
    return this.wolipsProperties.getProperty(WOVariables.LOCAL_FRAMEWORKS);
  }

  public String systemRoot() {
    return this.wolipsProperties.getProperty(WOVariables.SYSTEM_ROOT);
  }

  public String systemFrameworkPath() {
    return this.wolipsProperties.getProperty(WOVariables.SYSTEM_FRAMEWORKS);
  }

  public String networkRoot() {
    return this.wolipsProperties.getProperty(WOVariables.NETWORK_ROOT);
  }

  public String networkFrameworkPath() {
    return this.wolipsProperties.getProperty(WOVariables.NETWORK_FRAMEWORKS);
  }

  public String appsRoot() {
    return this.wolipsProperties.getProperty(WOVariables.APPS_ROOT);
  }

  public String boostrapJar() {
    return this.wolipsProperties.getProperty(WOVariables.BOOTSTRAP_JAR_KEY);
  }

  /**
   * Method referenceApi. WOVariables.REFERENCE_API_KEY defined in
   * wolips.properties (key: <code>wo.dir.reference.api</code>)
   * 
   * @return String
   */
  public String referenceApi() {
    return this.wolipsProperties.getProperty(WOVariables.API_ROOT_KEY);
  }

  /**
   * Method userHome
   * 
   * @return String
   */
  public String userRoot() {
    return this.wolipsProperties.getProperty(WOVariables.USER_ROOT);
  }

  /**
   * Method userHome
   * 
   * @return String
   */
  public String userFrameworkPath() {
    return this.wolipsProperties.getProperty(WOVariables.USER_FRAMEWORKS);
  }

  /**
   * Method woProjectFileName.
   * 
   * @return String
   */
  public static String woProjectFileName() {
    return "PB.project";
  }

  /**
   * Method webServerResourcesDirName.
   * 
   * @return String
   */
  public static String webServerResourcesDirName() {
    return "WebServerResources";
  }

  /**
   * Method encodePathForFile.
   * 
   * @param aFile
   * @return String
   */
  public String encodePathForFile(File aFile) {
    return encodePath(aFile.getPath());
  }

  public String encodePath(String path) {
    String userHome = null;
    String systemRoot = null;
    String localRoot = null;
    String aPath = null;
    try {
      localRoot = this.localRoot();
      userHome = this.userRoot();
      systemRoot = this.systemRoot();
      int localRootLength = 0;
      int userHomeLength = 0;
      int systemRootLength = 0;
      if (localRoot != null) {
        localRootLength = localRoot.length();
      }
      if (userHome != null) {
        userHomeLength = userHome.length();
      }
      if (systemRoot != null) {
        systemRootLength = systemRoot.length();
      }
      // aPath = aFile.getCanonicalPath();
      // u.k. the CanonicalPath will resolve links this will
      // result in path with /Versions/a in it
      aPath = this.convertWindowsPath(path);
      // aPrefix = this.getAppRootPath();
      // if((aPrefix != null) && (aPrefix.length() > 1) &&
      // (aPath.startsWith(aPrefix))) {
      // return "APPROOT" + aPath.substring(aPrefix.length());
      // }
      if (localRoot != null && aPath.startsWith(localRoot)) {
        boolean otherRoot = false;
        if (localRootLength < userHomeLength && aPath.startsWith(userHome)) {
          otherRoot = true;
        }
        if (localRootLength < systemRootLength && aPath.startsWith(systemRoot)) {
          otherRoot = true;
        }
        if (!otherRoot) {
          if (localRootLength == 1) {// MacOSX
            return "LOCALROOT" + aPath;
          }
          return "LOCALROOT" + aPath.substring(localRootLength);
        }
      }
      if (userHome != null && aPath.startsWith(userHome)) {
        boolean otherRoot = false;
        if (userHomeLength < systemRootLength && aPath.startsWith(systemRoot)) {
          otherRoot = true;
        }
        if (!otherRoot) {
          return "HOMEROOT" + aPath.substring(userHomeLength);
        }
      }
      if (systemRoot != null && aPath.startsWith(systemRoot)) {
        return "WOROOT" + aPath.substring(systemRootLength);
      }
      return aPath;
    }
    catch (Exception anException) {
      System.out.println("Exception occurred during encoding of the path " + anException);
    }
    finally {
      localRoot = null;
      userHome = null;
      systemRoot = null;
      aPath = null;
    }
    return null;
  }

  private String convertWindowsPath(String path) {
    if (path == null || path.length() == 0) {
      return null;
    }
    return FileStringScanner.replace(path, "\\", "/");
  }

  public String getProperty(String aKey) {
    return (wolipsProperties != null ? wolipsProperties.getProperty(aKey) : null);
  }

  public static boolean isWindows() {
    return System.getProperty("os.name").toLowerCase().contains("windows");
  }
}