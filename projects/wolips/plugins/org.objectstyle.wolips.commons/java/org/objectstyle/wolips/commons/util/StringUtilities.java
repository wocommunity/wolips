package org.objectstyle.wolips.commons.util;

public class StringUtilities {
  public static String toCommandlineParameterFormat(String _parameter, String _value) {
    StringBuffer commandlineFormat = new StringBuffer();
    commandlineFormat.append(_parameter);
    if (!_parameter.endsWith("=")) {
      commandlineFormat.append(" ");
    }
    commandlineFormat.append(_value);
    return commandlineFormat.toString();
  }

}
