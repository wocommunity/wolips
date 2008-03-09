/*
 * ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0
 * 
 * Copyright (c) 2006 The ObjectStyle Group and individual authors of the
 * software. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * 3. The end-user documentation included with the redistribution, if any, must
 * include the following acknowlegement: "This product includes software
 * developed by the ObjectStyle Group (http://objectstyle.org/)." Alternately,
 * this acknowlegement may appear in the software itself, if and wherever such
 * third-party acknowlegements normally appear.
 * 
 * 4. The names "ObjectStyle Group" and "Cayenne" must not be used to endorse or
 * promote products derived from this software without prior written permission.
 * For written permission, please contact andrus@objectstyle.org.
 * 
 * 5. Products derived from this software may not be called "ObjectStyle" nor
 * may "ObjectStyle" appear in their names without prior written permission of
 * the ObjectStyle Group.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * OBJECTSTYLE GROUP OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 * 
 * This software consists of voluntary contributions made by many individuals on
 * behalf of the ObjectStyle Group. For more information on the ObjectStyle
 * Group, please see <http://objectstyle.org/>.
 *  
 */
package org.objectstyle.wolips.baseforplugins.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class StringUtils {
	public static String toShortPrettyClassName(String name) {
		if (name == null) {
			return null;
		}
		String shortName;
		int dotIndex = name.lastIndexOf('.');
		if (dotIndex == -1) {
			shortName = name;
		}
		else {
			shortName = name.substring(dotIndex + 1);
		}
		if (shortName.startsWith("EO")) {
			shortName = shortName.substring(2);
		}
		StringBuffer nameBuffer = new StringBuffer(shortName);
		for (int i = 0; i < nameBuffer.length(); i ++) {
			char ch = nameBuffer.charAt(i);
			if (Character.isUpperCase(ch) && i != 0) {
				nameBuffer.insert(i, ' ');
				i ++;
			}
		}
		return nameBuffer.toString();
	}
	
	public static int firstLetterIndex(String _name) {
		int letterIndex = -1;
		if (_name != null) {
			int length = _name.length();
			boolean found = false;
			for (int index = 0; !found && index < length; index++) {
				char ch = _name.charAt(index);
				if (Character.isLetter(ch)) {
					letterIndex = index;
					found = true;
				} else if (ch != '_') {
					found = true;
				}
			}
		}
		return letterIndex;
	}

	public static boolean isUppercaseFirstLetter(String _name) {
		int firstLetterIndex = StringUtils.firstLetterIndex(_name);
		return (firstLetterIndex != -1 && Character.isUpperCase(_name.charAt(firstLetterIndex)));
	}

	public static boolean isLowercaseFirstLetter(String _name) {
		int firstLetterIndex = StringUtils.firstLetterIndex(_name);
		return (firstLetterIndex != -1 && Character.isLowerCase(_name.charAt(firstLetterIndex)));
	}

	public static String toLowercaseFirstLetter(String _name) {
		int firstLetterIndex = StringUtils.firstLetterIndex(_name);
		String name;
		if (firstLetterIndex == -1) {
			name = _name;
		} else {
			StringBuffer sb = new StringBuffer();
			if (firstLetterIndex > 0) {
				sb.append(_name.substring(0, firstLetterIndex));
			}
			sb.append(Character.toLowerCase(_name.charAt(firstLetterIndex)));
			sb.append(_name.substring(firstLetterIndex + 1));
			name = sb.toString();
		}
		return name;
	}

	public static String toUppercaseFirstLetter(String _name) {
		int firstLetterIndex = StringUtils.firstLetterIndex(_name);
		String name;
		if (firstLetterIndex == -1) {
			name = _name;
		} else {
			StringBuffer sb = new StringBuffer();
			if (firstLetterIndex > 0) {
				sb.append(_name.substring(0, firstLetterIndex));
			}
			sb.append(Character.toUpperCase(_name.charAt(firstLetterIndex)));
			sb.append(_name.substring(firstLetterIndex + 1));
			name = sb.toString();
		}
		return name;
	}

	public static boolean isSelectorNameEqual(String _expectedName, String _possibleName) {
		return _expectedName.equals(_possibleName) || (_expectedName + ":").equals(_possibleName);
	}
    
    /**
     * Converts ThisIsATest to this_is_a_test
     * @param camelString the StringWithCaps
     * @return the string_with_underscores
     */
    public static String camelCaseToUnderscore(String camelString) {
    	StringBuffer underscore = new StringBuffer();
    	boolean lastCharacterWasWordBreak = true;
    	for (int i = 0; i < camelString.length(); i ++) {
    		char ch = camelString.charAt(i);
    		if (Character.isUpperCase(ch) && !lastCharacterWasWordBreak) {
    			underscore.append("_");
    			lastCharacterWasWordBreak = true;
    		}
    		else {
    			lastCharacterWasWordBreak = false;
    		}
    		underscore.append(Character.toLowerCase(ch));
    	}
    	return underscore.toString();
    }

	public static String toPlural(String _str) {
		String plural;
		if (_str != null && _str.length() > 0) {
			char ch = _str.charAt(_str.length() - 1);
			StringBuffer pluralBuffer = new StringBuffer(_str);
			if (ch == 's' || ch == 'x') {
				pluralBuffer.append("es");
			} else if (ch == 'y') {
				pluralBuffer.setLength(pluralBuffer.length() - 1);
				pluralBuffer.append("ies");
			} else {
				pluralBuffer.append("s");
			}
			plural = pluralBuffer.toString();
		} else {
			plural = _str;
		}
		return plural;
	}
	
	public static boolean isKeyPath(String str) {
		return str != null && str.matches("^[^.][a-zA-Z0-9_.]+$");
	}

	public static String getErrorMessage(Throwable _t) {
		return StringUtils.getErrorMessage(null, _t);
	}
	
	public static String getErrorMessage(String initialMessage, Throwable _t) {
		StringBuffer messageBuffer = new StringBuffer();
		if (initialMessage != null) {
			messageBuffer.append(initialMessage);
			if (_t != null) {
				messageBuffer.append(" ");
			}
		}
		
		Throwable t = _t;
		while (t != null) {
			String message = t.getMessage();
			if (message == null && !(t instanceof InvocationTargetException)) {
				String name = t.getClass().getName();
				int lastDotIndex = name.lastIndexOf('.');
				name = name.substring(lastDotIndex + 1);
				message = name;
			}

			if (message != null) {
				message = message.trim();
				messageBuffer.append(message);
				if (!message.endsWith(".")) { //$NON-NLS-1$
					messageBuffer.append(".  "); //$NON-NLS-1$
				}
				else if (!message.endsWith("  ")) {
					messageBuffer.append("  ");
				}
			}

			Throwable cause = t.getCause();
			if (t == cause) {
				t = null;
			} else {
				t = cause;
			}
		}
		return messageBuffer.toString();
	}
	
	public static String findUnusedName(String newName, Object obj, String getMethodName) {
		try {
			String safeNewName = newName;
			if (safeNewName == null) {
				safeNewName = "MISSING";
			}
			Method getMethod = obj.getClass().getMethod(getMethodName, String.class);
			boolean unusedNameFound = (getMethod.invoke(obj, safeNewName) == null);
			String unusedName = safeNewName;
			if (!unusedNameFound) {
				int cutoffLength;
				for (cutoffLength = safeNewName.length(); cutoffLength > 0; cutoffLength --) {
					if (!Character.isDigit(safeNewName.charAt(cutoffLength - 1))) {
						break;
					}
				}
				String newWithoutTrailingNumber = safeNewName.substring(0, cutoffLength);
				unusedNameFound = (getMethod.invoke(obj, newWithoutTrailingNumber) == null);
				unusedName = newWithoutTrailingNumber;
				for (int dupeNameNum = 1; !unusedNameFound; dupeNameNum++) {
					unusedName = newWithoutTrailingNumber + dupeNameNum;
					Object existingObject = getMethod.invoke(obj, unusedName);
					unusedNameFound = (existingObject == null);
				}
			}
			return unusedName;
		} catch (Throwable t) {
			throw new RuntimeException("Failed to find unused name for '" + newName + "' with method '" + getMethodName + "'.", t);
		}
	}

}
