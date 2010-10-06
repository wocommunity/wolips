package org.objectstyle.wolips.baseforplugins.util;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class StringUtilities {
	/**
	 * checks if the specified String contains only digits.
	 * 
	 * @param aString
	 *            , the string to check
	 * @return true if the string contains only digits, false otherwise
	 */
	public static boolean isDigitsOnly(String aString) {
		for (int i = aString.length(); i-- > 0;) {
			char c = aString.charAt(i);
			if (!Character.isDigit(c)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * checks if the specified String contains only digits, a minus, or a
	 * decimal
	 * 
	 * @param aString
	 *            , the string to check
	 * @return true if the string contains only digits, false otherwise
	 */
	public static boolean isNumericOnly(String aString) {
		boolean foundDecimal = false;
		for (int i = aString.length(); i-- > 0;) {
			char c = aString.charAt(i);
			if (c == '-') {
				if (i != 0) {
					return false;
				}
			} else if (c == '.') {
				if (!foundDecimal) {
					foundDecimal = true;
				} else {
					return false;
				}
			} else if (!Character.isDigit(c)) {
				return false;
			}
		}
		return true;
	}

	public static String toCommandlineParameterFormat(String parameter, String value, boolean escapeSpacesAndQuotes) {
		return toCommandlineParameterFormat(parameter, value, escapeSpacesAndQuotes, false);
	}
	
	public static String toCommandlineParameterFormat(String parameter, String value, boolean escapeSpacesAndQuotes, boolean equalsSeparator) {
		StringBuffer commandlineFormat = new StringBuffer();
		commandlineFormat.append(parameter);
		if (!parameter.endsWith("=")) {
			if (equalsSeparator) {
				commandlineFormat.append("=");
			}
			else {
				commandlineFormat.append(" ");
			}
		}
		String formattedValue = value;
		boolean quote = formattedValue.indexOf(' ') != -1 || formattedValue.indexOf('-') != -1;
		if (quote && escapeSpacesAndQuotes) {
			// MS: WO parameters were getting upset when they weren't escaped
			// and quoted and escaped again. What you end up with is:
			// -DWOUserDirectory="/Volumes/mDT\
			// Workspace/runtimeWorkspace/AjaxExample/build/AjaxExample.woa"
			// You would THINK that you don't need to escape the space if you've
			// got the thing in quotes. You would think wrong.
			formattedValue = formattedValue.replaceAll(" ", "\\\\ "); // MS: Escape spaces
			formattedValue = formattedValue.replaceAll("\"", "\\\\\""); // MS: Escape quotes
		}
		if (quote) {
			commandlineFormat.append('\"');
		}
		commandlineFormat.append(formattedValue);
		if (quote) {
			commandlineFormat.append('\"');
		}
		return commandlineFormat.toString();
	}

	/**
	 * replace every occurence of oldPart with newPart in origin returns changed
	 * origin (since String is immutable...)
	 * 
	 * @param origin
	 * @param oldPart
	 * @param newPart
	 * @return
	 */

	static public String replace(String origin, String oldPart, String newPart) {
		if ((origin == null) || (origin.length() == 0)) {
			return origin;
		}

		StringBuffer buffer = new StringBuffer(origin);

		// start replacing from the end so we can use indexOf on the original
		// string

		int index;
		int end = origin.length();
		int oldLength = oldPart.length();

		while (end >= 0) {
			index = origin.lastIndexOf(oldPart, end);
			// no more occurences of oldPart
			if (index == -1)
				break;

			end = index - oldLength;

			buffer.replace(index, index + oldLength, newPart);
		}
		return buffer.toString();
	}

	/**
	 * Method arrayListFromCSV.
	 * 
	 * @param csvString
	 * @return ArrayList
	 */
	public static synchronized ArrayList arrayListFromCSV(String csvString) {
		if (csvString == null || csvString.length() == 0) {
			return new ArrayList();
		}
		StringTokenizer valueTokenizer = new StringTokenizer(csvString, ",");
		ArrayList resultList = new ArrayList(valueTokenizer.countTokens());
		while (valueTokenizer.hasMoreElements()) {
			resultList.add(valueTokenizer.nextElement());
		}
		return resultList;
	}

}
