package org.objectstyle.woproject.ant;

import org.objectstyle.woenvironment.env.WOVariables;
import org.objectstyle.woenvironment.frameworks.Dependency;

public class AntDependency extends Dependency {
  private String _jarPath;
  private FrameworkSet _frameworkSet;
  private WOVariables _variables;

  public AntDependency(FrameworkSet frameworkSet, String jarPath, WOVariables variables) {
    _jarPath = jarPath;
    _frameworkSet = frameworkSet;
    _variables = variables;
  }
  
  public FrameworkSet getFrameworkSet() {
    return _frameworkSet;
  }

  public String getJarPath() {
    return _jarPath;
  }
  
  @Override
  public String getProjectFrameworkName() {
    throw new IllegalStateException("This should never be called in ant.");
  }

  @Override
  public String getLocation() {
    return _jarPath.toString();
  }

  @Override
  public String getRawPath() {
    return _jarPath.toString();
  }

  @Override
  public String getSystemRoot() {
    return _variables.systemRoot();
  }

  @Override
  public boolean isProject() {
    return false;
  }

  @Override
  public boolean isWOProject() {
    return false;
  }

}
