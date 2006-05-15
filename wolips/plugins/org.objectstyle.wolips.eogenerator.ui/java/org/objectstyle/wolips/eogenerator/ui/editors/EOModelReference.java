package org.objectstyle.wolips.eogenerator.ui.editors;

import java.io.File;

public class EOModelReference {
  private String myPath;

  public EOModelReference(String _path) {
    myPath = _path;
  }

  public String getPath() {
    return myPath;
  }

  public String getName() {
    String name;
    int separatorIndex = myPath.lastIndexOf(File.separator);
    if (separatorIndex == -1) {
      name = myPath;
    }
    else {
      name = myPath.substring(separatorIndex + 1);
    }
    int dotIndex = name.indexOf('.');
    if (dotIndex != -1) {
      name = name.substring(0, dotIndex);
    }
    return name;
  }
}
