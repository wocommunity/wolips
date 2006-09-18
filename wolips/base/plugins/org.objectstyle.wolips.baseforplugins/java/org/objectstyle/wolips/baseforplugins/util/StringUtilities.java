package org.objectstyle.wolips.baseforplugins.util;

public class StringUtilities {
	public static String toCommandlineParameterFormat(String _parameter, String _value, boolean _escapeSpacesAndQuotes) {
		StringBuffer commandlineFormat = new StringBuffer();
		commandlineFormat.append(_parameter);
		if (!_parameter.endsWith("=")) {
			commandlineFormat.append(" ");
		}
		String value = _value;
		boolean quote = value.indexOf(' ') != -1;
		if (quote && _escapeSpacesAndQuotes) {
			// MS: WO parameters were getting upset when they weren't escaped
			// and quoted and escaped again. What you end up with is:
			// -DWOUserDirectory="/Volumes/mDT\
			// Workspace/runtimeWorkspace/AjaxExample/build/AjaxExample.woa"
			// You would THINK that you don't need to escape the space if you've
			// got the thing in quotes. You would think wrong.
			value = value.replaceAll(" ", "\\\\ "); // MS: Escape spaces
			value = value.replaceAll("\"", "\\\\\""); // MS: Escape quotes
		}
		if (quote) {
			commandlineFormat.append('\"');
		}
		commandlineFormat.append(value);
		if (quote) {
			commandlineFormat.append('\"');
		}
		return commandlineFormat.toString();
	}

}
