package org.objectstyle.wolips.commons.util;

public class StringUtilities {
  public static String toCommandlineParameterFormat(String _parameter, String _value) {
    StringBuffer commandlineFormat = new StringBuffer();
    commandlineFormat.append(_parameter);
    if (!_parameter.endsWith("=")) {
      commandlineFormat.append(" ");
    }
    boolean quote = _value.indexOf(' ') != -1;
    if (quote) {
      commandlineFormat.append('\"');
    }
    commandlineFormat.append(_value);
    if (quote) {
      commandlineFormat.append('\"');
    }
    return commandlineFormat.toString();
  }

}
