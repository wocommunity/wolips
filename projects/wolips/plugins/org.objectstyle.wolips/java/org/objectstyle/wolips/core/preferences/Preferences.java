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
import org.objectstyle.wolips.core.plugin.WOLipsPlugin;

/**
 * @author uli
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class Preferences {

	public static final String PREF_ANT_BUILD_FILE =
		"org.objectstyle.wolips.preference.AntBuildFile";
	public static final String PREF_RUN_WOBUILDER_ON_BUILD =
		"org.objectstyle.wolips.preference.RunWOBuilderOnBuild";
	public static final String PREF_OPEN_WOCOMPONENT_ACTION_INCLUDES_OPEN_HTML =
		"org.objectstyle.wolips.Preference.OpenWOComponentActionIncludesOpenHTML";
	public static final String PREF_SHOW_BUILD_OUTPUT =
		"org.objectstyle.wolips.Preference.ShowBuildOutput";
	public static final String PREF_RUN_ANT_AS_EXTERNAL_TOOL =
		"org.objectstyle.wolips.Preference.RunAntAsExternalTool";
	public static final String PREF_NS_PROJECT_SEARCH_PATH =
		"org.objectstyle.wolips.Preference.NSProjectSearch";
	public static final String PREF_REBUILD_WOBUILD_PROPERTIES_ON_NEXT_LAUNCH =
		"org.objectstyle.wolips.Preference.RebuildWOBuildPropertiesOnNextLaunch";
	public static final String PREF_WOLIPS_VERSION_EARLY_STARTUP =
		"org.objectstyle.wolips.Preference.WOLipsVersionEarlyStartup";
	public static final String PREF_LAUNCH_GLOBAL =
		"org.objectstyle.wolips.Preference.Launch_Global";

	public static boolean FLAG_INCLUDE_EXCLUDE_RULES_CHANGED = false;
	public static final String trueString = "true";
	public static final String falseString = "false";
	//set this sting to a preferences key and call set defaults to set the default value for this preferences key
	private static String SET_DEFAULTS_STRING = null;

	public static void save() {
		IPreferenceStore store = getPreferenceStore();
		if (store instanceof IPersistentPreferenceStore) {
			IPersistentPreferenceStore pstore =
				(IPersistentPreferenceStore) store;
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
				Preferences.PREF_ANT_BUILD_FILE))
			store.setDefault(
				Preferences.PREF_ANT_BUILD_FILE,
				"build.xml");
		if (Preferences.SET_DEFAULTS_STRING == null
			|| Preferences.SET_DEFAULTS_STRING.equals(
				Preferences.PREF_RUN_WOBUILDER_ON_BUILD))
			store.setDefault(
				Preferences.PREF_RUN_WOBUILDER_ON_BUILD,
				Preferences.trueString);
		if (Preferences.SET_DEFAULTS_STRING == null
			|| Preferences.SET_DEFAULTS_STRING.equals(
				Preferences.PREF_RUN_ANT_AS_EXTERNAL_TOOL))
			store.setDefault(
				Preferences.PREF_RUN_ANT_AS_EXTERNAL_TOOL,
				Preferences.falseString);
		if (Preferences.SET_DEFAULTS_STRING == null
			|| Preferences.SET_DEFAULTS_STRING.equals(
				Preferences.PREF_OPEN_WOCOMPONENT_ACTION_INCLUDES_OPEN_HTML))
			store.setDefault(
				Preferences.PREF_OPEN_WOCOMPONENT_ACTION_INCLUDES_OPEN_HTML,
				Preferences.falseString);
		if (Preferences.SET_DEFAULTS_STRING == null
			|| Preferences.SET_DEFAULTS_STRING.equals(
				Preferences.PREF_SHOW_BUILD_OUTPUT))
			store.setDefault(
				Preferences.PREF_SHOW_BUILD_OUTPUT,
				Preferences.falseString);
		if (Preferences.SET_DEFAULTS_STRING == null
			|| Preferences.SET_DEFAULTS_STRING.equals(
				Preferences.PREF_NS_PROJECT_SEARCH_PATH))
			store.setDefault(Preferences.PREF_NS_PROJECT_SEARCH_PATH, "");
		if (Preferences.SET_DEFAULTS_STRING == null
			|| Preferences.SET_DEFAULTS_STRING.equals(
				Preferences.PREF_REBUILD_WOBUILD_PROPERTIES_ON_NEXT_LAUNCH))
			store.setDefault(
				Preferences.PREF_REBUILD_WOBUILD_PROPERTIES_ON_NEXT_LAUNCH,
				PreferencesMessages.getString(Preferences.falseString));
		if (Preferences.SET_DEFAULTS_STRING == null
			|| Preferences.SET_DEFAULTS_STRING.equals(
				Preferences.PREF_WOLIPS_VERSION_EARLY_STARTUP))
			store.setDefault(
				Preferences.PREF_WOLIPS_VERSION_EARLY_STARTUP,
				"0.0.0");
		store.setDefault(
			Preferences.PREF_LAUNCH_GLOBAL,
			PreferencesMessages.getString(Preferences.PREF_LAUNCH_GLOBAL));
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
	private static boolean getBoolean(String key) {
		IPreferenceStore store = getPreferenceStore();
		return (Preferences.trueString.equals(store.getString(key)));
	}
	/**
	 * Method setBoolean.
	 * @param key
	 * @param value
	 */
	private static void setBoolean(String key, boolean value) {
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

	/**
	 * @return
	 */
	public static String getPREF_ANT_BUILD_FILE() {
		return Preferences.getString(Preferences.PREF_ANT_BUILD_FILE);
	}

	/**
	 * @return
	 */
	public static String getPREF_LAUNCH_GLOBAL() {
		return Preferences.getString(Preferences.PREF_LAUNCH_GLOBAL);
	}

	/**
	 * @return
	 */
	public static String getPREF_NS_PROJECT_SEARCH_PATH() {
		return Preferences.getString(Preferences.PREF_NS_PROJECT_SEARCH_PATH);
	}

	/**
	 * @return
	 */
	public static String getPREF_OPEN_WOCOMPONENT_ACTION_INCLUDES_OPEN_HTML() {
		return Preferences.getString(
			Preferences.PREF_OPEN_WOCOMPONENT_ACTION_INCLUDES_OPEN_HTML);
	}

	/**
	 * @return
	 */
	public static boolean getPREF_REBUILD_WOBUILD_PROPERTIES_ON_NEXT_LAUNCH() {
		return Preferences.getBoolean(
			Preferences.PREF_REBUILD_WOBUILD_PROPERTIES_ON_NEXT_LAUNCH);
	}

	/**
	 * @return
	 */
	public static boolean getPREF_RUN_ANT_AS_EXTERNAL_TOOL() {
		return Preferences.getBoolean(
			Preferences.PREF_RUN_ANT_AS_EXTERNAL_TOOL);
	}

	/**
	 * @return
	 */
	public static boolean getPREF_RUN_WOBUILDER_ON_BUILD() {
		return Preferences.getBoolean(Preferences.PREF_RUN_WOBUILDER_ON_BUILD);
	}

	/**
	 * @return
	 */
	public static boolean getPREF_SHOW_BUILD_OUTPUT() {
		return Preferences.getBoolean(Preferences.PREF_SHOW_BUILD_OUTPUT);
	}

	/**
	 * @return
	 */
	public static String getPREF_WOLIPS_VERSION_EARLY_STARTUP() {
		return Preferences.getString(
			Preferences.PREF_WOLIPS_VERSION_EARLY_STARTUP);
	}

	/**
	 * @param string
	 */
	public static void setPREF_ANT_BUILD_FILE(String string) {
		Preferences.setString(Preferences.PREF_ANT_BUILD_FILE, string);
	}

	/**
	 * @param string
	 */
	public static void setPREF_LAUNCH_GLOBAL(String string) {
		Preferences.setString(Preferences.PREF_LAUNCH_GLOBAL, string);
	}

	/**
	 * @param string
	 */
	public static void setPREF_NS_PROJECT_SEARCH_PATH(String string) {
		Preferences.setString(Preferences.PREF_NS_PROJECT_SEARCH_PATH, string);
	}

	/**
	 * @param string
	 */
	public static void setPREF_OPEN_WOCOMPONENT_ACTION_INCLUDES_OPEN_HTML(boolean value) {
		Preferences.setBoolean(
			Preferences.PREF_OPEN_WOCOMPONENT_ACTION_INCLUDES_OPEN_HTML,
			value);
	}

	/**
	 * @param string
	 */
	public static void setPREF_REBUILD_WOBUILD_PROPERTIES_ON_NEXT_LAUNCH(boolean value) {
		Preferences.setBoolean(
			Preferences.PREF_REBUILD_WOBUILD_PROPERTIES_ON_NEXT_LAUNCH,
			value);
	}

	/**
	 * @param string
	 */
	public static void setPREF_RUN_ANT_AS_EXTERNAL_TOOL(boolean value) {
		Preferences.setBoolean(
			Preferences.PREF_RUN_ANT_AS_EXTERNAL_TOOL,
			value);
	}

	/**
	 * @param string
	 */
	public static void setPREF_RUN_WOBUILDER_ON_BUILD(boolean value) {
		Preferences.setBoolean(Preferences.PREF_RUN_WOBUILDER_ON_BUILD, value);
	}

	/**
	 * @param string
	 */
	public static void setPREF_SHOW_BUILD_OUTPUT(boolean value) {
		Preferences.setBoolean(Preferences.PREF_SHOW_BUILD_OUTPUT, value);
	}

	/**
	 * @param string
	 */
	public static void setPREF_WOLIPS_VERSION_EARLY_STARTUP(String string) {
		Preferences.setString(
			Preferences.PREF_WOLIPS_VERSION_EARLY_STARTUP,
			string);
	}

}
