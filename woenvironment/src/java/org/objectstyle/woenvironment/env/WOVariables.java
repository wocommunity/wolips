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
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.objectstyle.woenvironment.util.FileStringScanner;

/**
 * @author uli
 * 
 */
public class WOVariables {
  public static final String USER_ROOT = "wo.user.root";

  public static final String USER_FRAMEWORKS = "wo.user.frameworks";

  public static final String LOCAL_ROOT = "wo.local.root";

  public static final String LOCAL_FRAMEWORKS = "wo.local.frameworks";

  public static final String SYSTEM_ROOT = "wo.system.root";

  public static final String SYSTEM_FRAMEWORKS = "wo.system.frameworks";

  public static final String NETWORK_ROOT = "wo.network.root";

  public static final String NETWORK_FRAMEWORKS = "wo.network.frameworks";

  public static final String EXTERNAL_BUILD_ROOT = "wo.external.root";

  public static final String EXTERNAL_BUILD_FRAMEWORKS = "wo.external.frameworks";

  public static final String APPS_ROOT = "wo.apps.root";

  public static final String API_ROOT_KEY = "wo.api.root";

  public static final String BOOTSTRAP_JAR_KEY = "wo.bootstrapjar";

  public static final String WEBOBJECTS_EXTENSIONS = "wo.extensions";

  public static final String WOLIPS_PROPERTIES = "wolips.properties";

  //private static final String WOLIPS_PROPERTIES_FILE_NAME = "wolips.properties";

  private static Map<File, CachedProperties> _cachedProperties = new HashMap<File, CachedProperties>();

  private Properties _wolipsProperties;

  private File _wolipsPropertiesFile;

  public WOVariables(WOVariables variables, Map<Object, Object> existingProperties) {
    init(variables, existingProperties);
  }

  public WOVariables(Map<Object, Object> existingProperties) {
    init(null, existingProperties);
  }

  public void init(WOVariables variables, Map<Object, Object> existingProperties) {
    if (variables == null) {
      _wolipsProperties = new Properties();

      String wolipsPropertiesPath;

      if (existingProperties != null) {
        wolipsPropertiesPath = (String) existingProperties.get(WOVariables.WOLIPS_PROPERTIES);
      }
      else {
        wolipsPropertiesPath = System.getProperty(WOVariables.WOLIPS_PROPERTIES);
      }

      if (wolipsPropertiesPath == null) {
        wolipsPropertiesPath = System.getenv(WOVariables.WOLIPS_PROPERTIES);
      }

      if (wolipsPropertiesPath == null || !new File(wolipsPropertiesPath).isAbsolute()) {
        if (wolipsPropertiesPath == null) {
          String environmentName = null;
          if (existingProperties != null) {
            environmentName = (String) existingProperties.get("wolips.environment");
          }
          if (environmentName != null) {
            wolipsPropertiesPath = "wolips." + environmentName + ".properties";
          }
          else {
            wolipsPropertiesPath = "wolips.properties";
          }
        }

        if (isWindows()) {
          _wolipsPropertiesFile = new File(System.getenv("APPDATA") + "\\WOLips\\" + wolipsPropertiesPath);
        }
        else {
          _wolipsPropertiesFile = new File(userHomeFolder(), "Library/Application Support/WOLips/" + wolipsPropertiesPath);
        }
      }
      else {
        _wolipsPropertiesFile = new File(wolipsPropertiesPath);
      }

      if (isValidWOlipsPropertiesFile()) {
        CachedProperties cachedProperties;
        synchronized (_cachedProperties) {
          cachedProperties = _cachedProperties.get(_wolipsPropertiesFile);
          if (cachedProperties == null) {
            cachedProperties = new CachedProperties(_wolipsPropertiesFile);
            _cachedProperties.put(_wolipsPropertiesFile, cachedProperties);
          }
        }
        cachedProperties.reloadIfNecessary();
        _wolipsProperties = new Properties();
        _wolipsProperties.putAll(cachedProperties.properties());
      }
      else if (existingProperties == null || existingProperties.isEmpty()) {
        createDefaultProperties();
      }
    }
    else {
      _wolipsProperties = new Properties();
      _wolipsProperties.putAll(variables._wolipsProperties);
    }

    if (existingProperties != null) {
      for (Map.Entry<Object, Object> entry : existingProperties.entrySet()) {
        if (entry.getKey() instanceof String && entry.getValue() instanceof String) {
          _wolipsProperties.setProperty((String) entry.getKey(), (String) entry.getValue());
        }
      }
    }
  }

  public void createDefaultProperties() {
    _wolipsProperties = new Properties();
    String nextRoot = System.getenv("NEXT_ROOT");
    if (isWindows()) {
      _wolipsProperties.setProperty(WOVariables.API_ROOT_KEY, "/Developer/ADC%20Reference%20Library/documentation/WebObjects/Reference/API/");
      _wolipsProperties.setProperty(WOVariables.APPS_ROOT, nextRoot + "\\Applications");
      _wolipsProperties.setProperty(WOVariables.BOOTSTRAP_JAR_KEY, nextRoot + "\\Library\\Application\\wotaskd.woa\\WOBootstrap.jar");
      _wolipsProperties.setProperty(WOVariables.LOCAL_ROOT, nextRoot + "\\Local");
      _wolipsProperties.setProperty(WOVariables.LOCAL_FRAMEWORKS, nextRoot + "\\Local\\Library\\Frameworks");
      _wolipsProperties.setProperty(WOVariables.SYSTEM_ROOT, nextRoot);
      _wolipsProperties.setProperty(WOVariables.SYSTEM_FRAMEWORKS, nextRoot + "\\Library\\Frameworks");
      _wolipsProperties.setProperty(WOVariables.NETWORK_ROOT, nextRoot + "\\Network");
      _wolipsProperties.setProperty(WOVariables.NETWORK_FRAMEWORKS, nextRoot + "\\Network\\Library\\Frameworks");
      _wolipsProperties.setProperty(WOVariables.USER_ROOT, System.getProperty("user.home"));
      _wolipsProperties.setProperty(WOVariables.USER_FRAMEWORKS, System.getProperty("user.home") + "\\Library\\Frameworks");
      _wolipsProperties.setProperty(WOVariables.WEBOBJECTS_EXTENSIONS, nextRoot + "\\Extensions");
    }
    else {
      _wolipsProperties.setProperty(WOVariables.API_ROOT_KEY, "/Developer/Documentation/DocSets/com.apple.ADC_Reference_Library.WebObjectsReference.docset/Contents/Resources/Documents/documentation/InternetWeb/Reference/WO542Reference");
      _wolipsProperties.setProperty(WOVariables.APPS_ROOT, "/Library/WebObjects/Applications");
      _wolipsProperties.setProperty(WOVariables.BOOTSTRAP_JAR_KEY, "/System/Library/WebObjects/JavaApplications/wotaskd.woa/WOBootstrap.jar");
      _wolipsProperties.setProperty(WOVariables.LOCAL_ROOT, "/");
      _wolipsProperties.setProperty(WOVariables.LOCAL_FRAMEWORKS, "/Library/Frameworks");
      _wolipsProperties.setProperty(WOVariables.SYSTEM_ROOT, "/System");
      _wolipsProperties.setProperty(WOVariables.SYSTEM_FRAMEWORKS, "/System/Library/Frameworks");
      _wolipsProperties.setProperty(WOVariables.NETWORK_ROOT, "/Network");
      _wolipsProperties.setProperty(WOVariables.NETWORK_FRAMEWORKS, "/Network/Library/Frameworks");
      _wolipsProperties.setProperty(WOVariables.USER_ROOT, System.getProperty("user.home"));
      _wolipsProperties.setProperty(WOVariables.USER_FRAMEWORKS, System.getProperty("user.home") + "/Library/Frameworks");
      _wolipsProperties.setProperty(WOVariables.WEBOBJECTS_EXTENSIONS, "/Library/WebObjects/Extensions");
    }

    try {
      File wolipsPropertiesFolder = _wolipsPropertiesFile.getParentFile();
      if (!wolipsPropertiesFolder.exists()) {
        wolipsPropertiesFolder.mkdirs();
      }
      if (wolipsPropertiesFolder.canWrite()) {
        FileOutputStream wolipsPropertiesStream = new FileOutputStream(_wolipsPropertiesFile);
        try {
          _wolipsProperties.store(wolipsPropertiesStream, null);
        }
        finally {
          wolipsPropertiesStream.close();
        }
        synchronized (_cachedProperties) {
          _cachedProperties.remove(_wolipsPropertiesFile);
        }
      }
    }
    catch (IOException e) {
      throw new RuntimeException("Failed to write " + _wolipsPropertiesFile + ".", e);
    }
  }

  public boolean isValidWOlipsPropertiesFile() {
    return _wolipsPropertiesFile != null && _wolipsPropertiesFile.exists() && !_wolipsPropertiesFile.isDirectory();
  }

  public File wolipsPropertiesFile() {
    return _wolipsPropertiesFile;
  }

  /**
   * Where you store your XCode or ant generated stuff.
   * 
   * @return String
   */
  public String externalBuildRoot() {
    return _wolipsProperties.getProperty(WOVariables.EXTERNAL_BUILD_ROOT);
  }

  public String externalBuildFrameworkPath() {
    return _wolipsProperties.getProperty(WOVariables.EXTERNAL_BUILD_FRAMEWORKS);
  }

  public String localRoot() {
    return _wolipsProperties.getProperty(WOVariables.LOCAL_ROOT);
  }

  public String localFrameworkPath() {
    return _wolipsProperties.getProperty(WOVariables.LOCAL_FRAMEWORKS);
  }

  public String systemRoot() {
    return _wolipsProperties.getProperty(WOVariables.SYSTEM_ROOT);
  }

  public String systemFrameworkPath() {
    return _wolipsProperties.getProperty(WOVariables.SYSTEM_FRAMEWORKS);
  }

  public String networkRoot() {
    return _wolipsProperties.getProperty(WOVariables.NETWORK_ROOT);
  }

  public String networkFrameworkPath() {
    return _wolipsProperties.getProperty(WOVariables.NETWORK_FRAMEWORKS);
  }

  public String appsRoot() {
    return _wolipsProperties.getProperty(WOVariables.APPS_ROOT);
  }

  public String boostrapJar() {
    return _wolipsProperties.getProperty(WOVariables.BOOTSTRAP_JAR_KEY);
  }

  /**
   * Method referenceApi. WOVariables.REFERENCE_API_KEY defined in
   * wolips.properties (key: <code>wo.dir.reference.api</code>)
   * 
   * @return String
   */
  public String referenceApi() {
    return _wolipsProperties.getProperty(WOVariables.API_ROOT_KEY);
  }

  /**
   * Returns the home directory for the current user.
   * 
   * @return
   */
  public String userHomeFolder() {
    String userHome = System.getProperty("user.home");
    if (userHome == null) {
      userHome = System.getenv("USERPROFILE");
    }
    if (userHome == null) {
      System.out.println("WOVariables.userHome: No user home directory found.");
    }
    return userHome;
  }

  /**
   * Method userHome
   * 
   * @return String
   */
  public String userRoot() {
    return _wolipsProperties.getProperty(WOVariables.USER_ROOT);
  }

  /**
   * Method userHome
   * 
   * @return String
   */
  public String userFrameworkPath() {
    return _wolipsProperties.getProperty(WOVariables.USER_FRAMEWORKS);
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
      localRoot = localRoot();
      userHome = userRoot();
      systemRoot = systemRoot();
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
      aPath = convertWindowsPath(path);
      // aPrefix = getAppRootPath();
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
    return (_wolipsProperties != null ? _wolipsProperties.getProperty(aKey) : null);
  }

  public static boolean isWindows() {
    return System.getProperty("os.name").toLowerCase().contains("windows");
  }

  public static class CachedProperties {
    private File _propertiesFile;
    private long _lastLoaded;
    private Properties _properties;

    public CachedProperties(File propertiesFile) {
      _propertiesFile = propertiesFile;
      _properties = new Properties();
    }

    public void reloadIfNecessary() {
      if (_propertiesFile.exists()) {
        long lastModified = _propertiesFile.lastModified();
        if (_lastLoaded != lastModified) {
          reload();
          _lastLoaded = lastModified;
        }
      }
    }

    public Properties properties() {
      return _properties;
    }

    public void reload() {
      try {
        _properties = new Properties();
        _properties.load(new FileInputStream(_propertiesFile));
      }
      catch (IOException e) {
        throw new RuntimeException("Failed to load " + _propertiesFile + ".", e);
      }
    }
  }
}