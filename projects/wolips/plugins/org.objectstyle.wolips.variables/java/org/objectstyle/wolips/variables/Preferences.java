/* ====================================================================
 *
 * The ObjectStyle Group Software License, Version 1.0
 *
 * Copyright (c) 2002 - 2004 The ObjectStyle Group
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

package org.objectstyle.wolips.variables;

import java.io.IOException;

import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * @author uli
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class Preferences {

	/**
	 * Comment for <code>PREF_REBUILD_WOBUILD_PROPERTIES_ON_NEXT_LAUNCH</code>
	 */
	public static final String PREF_REBUILD_WOBUILD_PROPERTIES_ON_NEXT_LAUNCH =
		"org.objectstyle.wolips.Preference.RebuildWOBuildPropertiesOnNextLaunch";
	/**
	 * Comment for <code>PREF_WOLIPS_VERSION_EARLY_STARTUP</code>
	 */
	public static final String PREF_WOLIPS_VERSION_EARLY_STARTUP =
		"org.objectstyle.wolips.Preference.WOLipsVersionEarlyStartup";
	/**
	 * Comment for <code>trueString</code>
	 */
	public static final String trueString = "true";
	/**
	 * Comment for <code>falseString</code>
	 */
	public static final String falseString = "false";
	//set this sting to a preferences key and call set defaults to set the default value for this preferences key
	private static String SET_DEFAULTS_STRING = null;

	/**
	 * 
	 */
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
				Preferences.PREF_REBUILD_WOBUILD_PROPERTIES_ON_NEXT_LAUNCH))
			store.setDefault(
				Preferences.PREF_REBUILD_WOBUILD_PROPERTIES_ON_NEXT_LAUNCH,
				Preferences.falseString);
		if (Preferences.SET_DEFAULTS_STRING == null
			|| Preferences.SET_DEFAULTS_STRING.equals(
				Preferences.PREF_WOLIPS_VERSION_EARLY_STARTUP))
			store.setDefault(
				Preferences.PREF_WOLIPS_VERSION_EARLY_STARTUP,
				"0.0.0");
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
		return VariablesPlugin.getDefault().getPreferenceStore();
	}

	/**
	 * @return rebuild wobuild properties on next launch
	 */
	public static boolean getPREF_REBUILD_WOBUILD_PROPERTIES_ON_NEXT_LAUNCH() {
		return Preferences.getBoolean(
			Preferences.PREF_REBUILD_WOBUILD_PROPERTIES_ON_NEXT_LAUNCH);
	}

	/**
	 * @return the previous version of WOLips
	 */
	public static String getPREF_WOLIPS_VERSION_EARLY_STARTUP() {
		return Preferences.getString(
			Preferences.PREF_WOLIPS_VERSION_EARLY_STARTUP);
	}
	/**
	 * @param value
	 */
	public static void setPREF_REBUILD_WOBUILD_PROPERTIES_ON_NEXT_LAUNCH(boolean value) {
		Preferences.setBoolean(
			Preferences.PREF_REBUILD_WOBUILD_PROPERTIES_ON_NEXT_LAUNCH,
			value);
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
