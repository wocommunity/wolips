package org.objectstyle.woenvironment.frameworks;

import java.io.File;

public abstract class Dependency {
  public abstract String getProjectFrameworkName();
  
  public abstract boolean isProject();

  public abstract boolean isWOProject();

  public abstract String getSystemRoot();

  public abstract String getRawPath();
  
  public abstract String getLocation();

  public boolean isAppleProvided() {
    String location = getLocation();
    if (location != null) {
      // check user settings (from wobuild.properties)
      String systemRootPath = getSystemRoot();
      if (systemRootPath != null && location.startsWith(systemRootPath.toString())) {
        return location.indexOf("JavaVM") < 0;
      }
      // check maven path (first french version)
      if (location.indexOf("webobjects" + File.separator + "apple") > 0) {
        return true;
      }
      // check maven path
      if (location.indexOf("apple" + File.separator + "webobjects") > 0) {
        return true;
      }
      if (location.indexOf("System" + File.separator + "Library") > 0) {
        return location.indexOf("JavaVM") < 0;
      }
      // check win path
      if (location.indexOf("Apple" + File.separator + "Library") > 0) {
        return true;
      }
    }
    return false;
  }

  public boolean isWoa() {
    String location = getLocation();
    if (location != null) {
      if (location.indexOf(".woa") > 0) {
        return true;
      }
    }
    return false;
  }

  public boolean isBuildProject() {
    String location = getLocation();
    if (location != null) {
      if (location.indexOf(File.separator + "build" + File.separator) > 0) {
        return true;
      }
    }
    return false;
  }

  public boolean isFrameworkJar() {
    String location = getLocation();
    if (location != null) {
      String pattern = "(?i).*?/(\\w+)\\.framework/Resources/Java/.*.jar";
      if (location.replace('\\', '/').matches(pattern)) {
        return true;
      }
    }
    return false;
  }
}
