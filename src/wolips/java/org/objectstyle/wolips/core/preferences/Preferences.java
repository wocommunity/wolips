/* ====================================================================
 *
 * The ObjectStyle Group Software License, Version 1.0
 *
 * Copyright (c) 2002 The ObjectStyle Group
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

package org.objectstyle.wolips.core.preferences;

import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Vector;

import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.jface.preference.IPreferenceStore;
import org.objectstyle.wolips.core.plugin.IWOLipsPluginConstants;
import org.objectstyle.wolips.core.plugin.WOLipsPlugin;
import org.objectstyle.wolips.core.util.QuotedStringTokenizer;

/**
 * @author uli
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class Preferences {

	public static final String trueString = "true";
	public static final String falseString = "false";
	//set this sting to a preferences key and call set defaults to set the default value for this preferences key
	private static String SET_DEFAULTS_STRING = null;

  public static void save() {
    IPreferenceStore store = getPreferenceStore();
    if (store instanceof IPersistentPreferenceStore) {
      IPersistentPreferenceStore pstore = (IPersistentPreferenceStore)store;
      if (pstore.needsSaving()) {
        try {
          pstore.save();
        } catch (IOException up) {
          // hmm, what should we do?
        }
      }
    }
    
  }

	/**
	 * Method setDefaults.
	 */
	public static void setDefaults() {
		IPreferenceStore store = getPreferenceStore();
		if (Preferences.SET_DEFAULTS_STRING == null
			|| Preferences.SET_DEFAULTS_STRING.equals(
				IWOLipsPluginConstants.PREF_ANT_BUILD_FILE))
			store.setDefault(
				IWOLipsPluginConstants.PREF_ANT_BUILD_FILE,
				org.apache.tools.ant.Main.DEFAULT_BUILD_FILENAME);
		if (Preferences.SET_DEFAULTS_STRING == null
			|| Preferences.SET_DEFAULTS_STRING.equals(
				IWOLipsPluginConstants.PREF_RUN_WOBUILDER_ON_BUILD))
			store.setDefault(
				IWOLipsPluginConstants.PREF_RUN_WOBUILDER_ON_BUILD,
				Preferences.trueString);
		if (Preferences.SET_DEFAULTS_STRING == null
			|| Preferences.SET_DEFAULTS_STRING.equals(
				IWOLipsPluginConstants.PREF_RUN_ANT_AS_EXTERNAL_TOOL))
			store.setDefault(
				IWOLipsPluginConstants.PREF_RUN_ANT_AS_EXTERNAL_TOOL,
				Preferences.falseString);
		if (Preferences.SET_DEFAULTS_STRING == null
			|| Preferences.SET_DEFAULTS_STRING.equals(
				IWOLipsPluginConstants.PREF_MODEL_NAVIGATOR_FILTER))
			store.setDefault(
				IWOLipsPluginConstants.PREF_MODEL_NAVIGATOR_FILTER,
				PreferencesMessages.getString(
					"Preferences.ModelNavigatorFilter.Default"));
		if (Preferences.SET_DEFAULTS_STRING == null
			|| Preferences.SET_DEFAULTS_STRING.equals(
				IWOLipsPluginConstants.PREF_WO_NAVIGATOR_FILTER))
			store.setDefault(
				IWOLipsPluginConstants.PREF_WO_NAVIGATOR_FILTER,
				PreferencesMessages.getString(
					"Preferences.WONavigatorFilter.Default"));
		if (Preferences.SET_DEFAULTS_STRING == null
			|| Preferences.SET_DEFAULTS_STRING.equals(
				IWOLipsPluginConstants.PREF_PRODUCT_NAVIGATOR_FILTER))
			store.setDefault(
				IWOLipsPluginConstants.PREF_PRODUCT_NAVIGATOR_FILTER,
				PreferencesMessages.getString(
					"Preferences.ProductNavigatorFilter.Default"));
		if (Preferences.SET_DEFAULTS_STRING == null
			|| Preferences.SET_DEFAULTS_STRING.equals(
				IWOLipsPluginConstants
					.PREF_OPEN_WOCOMPONENT_ACTION_INCLUDES_OPEN_HTML))
			store.setDefault(
				IWOLipsPluginConstants
					.PREF_OPEN_WOCOMPONENT_ACTION_INCLUDES_OPEN_HTML,
				Preferences.falseString);
		if (Preferences.SET_DEFAULTS_STRING == null
			|| Preferences.SET_DEFAULTS_STRING.equals(
				IWOLipsPluginConstants.PREF_SHOW_BUILD_OUTPUT))
			store.setDefault(
				IWOLipsPluginConstants.PREF_SHOW_BUILD_OUTPUT,
				Preferences.falseString);
		if (Preferences.SET_DEFAULTS_STRING == null
			|| Preferences.SET_DEFAULTS_STRING.equals(
				IWOLipsPluginConstants.PREF_NS_PROJECT_SEARCH_PATH))
			store.setDefault(
				IWOLipsPluginConstants.PREF_NS_PROJECT_SEARCH_PATH,
				"");
		if (Preferences.SET_DEFAULTS_STRING == null
			|| Preferences.SET_DEFAULTS_STRING.equals(
				IWOLipsPluginConstants
					.PREF_REBUILD_WOBUILD_PROPERTIES_ON_NEXT_LAUNCH))
			store.setDefault(
				IWOLipsPluginConstants
					.PREF_REBUILD_WOBUILD_PROPERTIES_ON_NEXT_LAUNCH,
				PreferencesMessages.getString(Preferences.falseString));
		if (Preferences.SET_DEFAULTS_STRING == null
			|| Preferences.SET_DEFAULTS_STRING.equals(
				IWOLipsPluginConstants.PREF_WOLIPS_VERSION_EARLY_STARTUP))
			store.setDefault(
				IWOLipsPluginConstants.PREF_WOLIPS_VERSION_EARLY_STARTUP,
				"0.0.0");
		if (Preferences.SET_DEFAULTS_STRING == null
			|| Preferences.SET_DEFAULTS_STRING.equals(
				IWOLipsPluginConstants.PREF_LOG_LEVEL))
			store.setDefault(
				IWOLipsPluginConstants.PREF_LOG_LEVEL,
				PreferencesMessages.getString("Preferences.LogLevel.Default"));
		if (Preferences.SET_DEFAULTS_STRING == null
			|| Preferences.SET_DEFAULTS_STRING.equals(
				IWOLipsPluginConstants.PREF_PBWO_PROJECT_UPDATE))
			store.setDefault(
				IWOLipsPluginConstants.PREF_PBWO_PROJECT_UPDATE, Preferences.trueString);
		if (Preferences.SET_DEFAULTS_STRING == null
			|| Preferences.SET_DEFAULTS_STRING.equals(
				IWOLipsPluginConstants.PREF_PBWO_PROJECT_INCLUDED_CLASSES))
			store.setDefault(
				IWOLipsPluginConstants.PREF_PBWO_PROJECT_INCLUDED_CLASSES,
				PreferencesMessages.getString(
					IWOLipsPluginConstants.PREF_PBWO_PROJECT_INCLUDED_CLASSES));
		if (Preferences.SET_DEFAULTS_STRING == null
			|| Preferences.SET_DEFAULTS_STRING.equals(
				IWOLipsPluginConstants.PREF_PBWO_PROJECT_EXCLUDED_CLASSES))
			store.setDefault(
				IWOLipsPluginConstants.PREF_PBWO_PROJECT_EXCLUDED_CLASSES,
				PreferencesMessages.getString(
					IWOLipsPluginConstants.PREF_PBWO_PROJECT_EXCLUDED_CLASSES));
		if (Preferences.SET_DEFAULTS_STRING == null
			|| Preferences.SET_DEFAULTS_STRING.equals(
				IWOLipsPluginConstants
					.PREF_PBWO_PROJECT_INCLUDED_WOAPP_RESOURCES))
			store.setDefault(
				IWOLipsPluginConstants
					.PREF_PBWO_PROJECT_INCLUDED_WOAPP_RESOURCES,
				PreferencesMessages.getString(
					IWOLipsPluginConstants
						.PREF_PBWO_PROJECT_INCLUDED_WOAPP_RESOURCES));
		if (Preferences.SET_DEFAULTS_STRING == null
			|| Preferences.SET_DEFAULTS_STRING.equals(
				IWOLipsPluginConstants
					.PREF_PBWO_PROJECT_EXCLUDED_WOAPP_RESOURCES))
			store.setDefault(
				IWOLipsPluginConstants
					.PREF_PBWO_PROJECT_EXCLUDED_WOAPP_RESOURCES,
				PreferencesMessages.getString(
					IWOLipsPluginConstants
						.PREF_PBWO_PROJECT_EXCLUDED_WOAPP_RESOURCES));
		store.setDefault(
			IWOLipsPluginConstants.PREF_LAUNCH_GLOBAL,
			PreferencesMessages.getString(
				IWOLipsPluginConstants.PREF_LAUNCH_GLOBAL));
		Preferences.SET_DEFAULTS_STRING = null;
	}

	/**
	 * Method getString.
	 * @param key
	 * @return String
	 */
	public static String getString(String key) {
		IPreferenceStore store = getPreferenceStore();
		String returnValue = store.getString(key);
		if (returnValue.equals(IPreferenceStore.STRING_DEFAULT_DEFAULT)) {
			Preferences.setDefaults();
			Preferences.SET_DEFAULTS_STRING = key;
			returnValue = store.getString(key);
		}
		return returnValue;
	}
	/**
	 * Method setString.
	 * @param key
	 * @param value
	 */
	public static void setString(String key, String value) {
		IPreferenceStore store = getPreferenceStore();
		store.setValue(key, value);
	}

	/**
	* Method getBoolean.
	* @param key
	* @return boolean
	*/
	public static boolean getBoolean(String key) {
		IPreferenceStore store = getPreferenceStore();
		return (Preferences.trueString.equals(store.getString(key)));
	}
	/**
	 * Method setBoolean.
	 * @param key
	 * @param value
	 */
	public static void setBoolean(String key, boolean value) {
		IPreferenceStore store = getPreferenceStore();
		if (value)
			store.setValue(key, Preferences.trueString);
		else
			store.setValue(key, Preferences.falseString);
	}
	/**
	 * Method getPreferenceStore.
	 * @return IPreferenceStore
	 */
	public static IPreferenceStore getPreferenceStore() {
		return WOLipsPlugin.getDefault().getPreferenceStore();
	}

/*	public static String[] getStringArrayForKey(String key, String separator) {
		String string = Preferences.getString(key);
		if (string == null)
			return new String[0];
		QuotedStringTokenizer stringTokenizer =
			new QuotedStringTokenizer(string, separator);
		Vector vector = new Vector();
		while (stringTokenizer.hasMoreTokens()) {
			vector.add(stringTokenizer.nextToken());
		}
		String[] stringArray = new String[vector.size()];
		for (int i = 0; i < vector.size(); i++) {
			stringArray[i] = (String) vector.elementAt(i);
		}
		return stringArray;
	}
*/

  public static IIncludeInfo[] getIncludeInfoForKey(String key) {
    String string = Preferences.getString(key);
    if (string == null)
      return new IIncludeInfo[0];
    QuotedStringTokenizer stringTokenizer = new QuotedStringTokenizer(string, ',');
    Vector vector = new Vector();
    while (stringTokenizer.hasMoreTokens()) {
      vector.add(stringTokenizer.nextToken());
    }
    IIncludeInfo[] includeInfo = new IIncludeInfo[vector.size()];
    for (int i = 0; i < vector.size(); i++) {
      includeInfo[i] = new IncludeInfo((String) vector.elementAt(i));
    }
    return includeInfo;
  }

  public static String[] getStringArrayForKey(String key) {
    String string = Preferences.getString(key);
    if (string == null)
      return new String[0];
    QuotedStringTokenizer stringTokenizer =
      new QuotedStringTokenizer(string, ',');
    Vector vector = new Vector();
    while (stringTokenizer.hasMoreTokens()) {
      vector.add(stringTokenizer.nextToken());
    }
    String[] stringArray = new String[vector.size()];
    for (int i = 0; i < vector.size(); i++) {
      stringArray[i] = (String) vector.elementAt(i);
    }
    return stringArray;
  }

	public static ILaunchInfo[] getLaunchInfoForKey(String key) {
		String string = Preferences.getString(key);
		return Preferences.getLaunchInfoFrom(string);
	}

	public static ILaunchInfo[] getLaunchInfoFrom(String string) {
		if (string == null)
			return new ILaunchInfo[0];
		StringTokenizer stringTokenizer = new StringTokenizer(string, "<>");
		Vector vector = new Vector();
		while (stringTokenizer.hasMoreElements()) {
			Object token = stringTokenizer.nextElement();
			vector.add(token);
		}
		ILaunchInfo[] launchInfo = new ILaunchInfo[vector.size() / 3];
		int j = 0;
		for (int i = 0; i < launchInfo.length; i++) {
			launchInfo[i] =
				new LaunchInfo(
					(String) vector.elementAt(j),
					(String) vector.elementAt(j + 1),
					(String) vector.elementAt(j + 2));
			j = j + 3;
		}
		return launchInfo;
	}
	public static void setIncludeInfoForKey(String[] includeInfo, String key) {
		StringBuffer value = new StringBuffer();
		for (int i = 0; i < includeInfo.length; i++) {
      String thisOne = includeInfo[i];
      if (null != thisOne) {
        if (-1 != thisOne.indexOf(',')) {
          value.append("\"");
          value.append(thisOne);
          value.append("\"");
        } else {
          value.append(thisOne);
        }
        if (i != (includeInfo.length - 1))
          value.append(",");
      }
		}
		Preferences.setString(key, value.toString());
	}

	public static String LaunchInfoToString(
		String[] parameter,
		String[] arguments,
		boolean[] enabled) {
		String value = "";
		for (int i = 0; i < parameter.length; i++) {
			value += parameter[i];
			value += "<>";
			value += arguments[i];
			value += "<>";
			value += enabled[i];
			if (i != (parameter.length - 1))
				value += "<>";
		}
		return value;
	}
	/**
	 * @param parameter
	 * @param arguments
	 * @param enabled
	 * @param preferencesKey
	 */
	public static void setLaunchInfoForKey(
		String[] parameter,
		String[] arguments,
		boolean[] enabled,
		String key) {
		String value =
			Preferences.LaunchInfoToString(parameter, arguments, enabled);
		Preferences.setString(key, value);
	}
	private static class IncludeInfo implements IIncludeInfo {
		private String pattern;

		public IncludeInfo(String pattern) {
			this.pattern = pattern;
		}
		public String getPattern() {
			return pattern;
		}
	}

	private static class LaunchInfo implements ILaunchInfo {
		public String parameter;
		public String argument;
		public boolean enabled;
		public LaunchInfo(String parameter, String argument, String enabled) {
			this.parameter = parameter;
			this.argument = argument;
			this.enabled =
				(enabled != null && Preferences.trueString.equals(enabled));
		}

		/**
		 * @return
		 */
		public String getArgument() {
			return argument;
		}

		/**
		 * @return
		 */
		public boolean isEnabled() {
			return enabled;
		}

		/**
		 * @return
		 */
		public String getParameter() {
			return parameter;
		}

	}
}
