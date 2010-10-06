package org.objectstyle.wolips.builder.internal;

import org.eclipse.ant.core.IAntPropertyValueProvider;
import org.objectstyle.wolips.preferences.Preferences;

public class WOLipsAntPropertyValueProvider implements IAntPropertyValueProvider {
	public WOLipsAntPropertyValueProvider() {
		System.out.println("WOLipsAntPropertyValueProvider.WOLipsAntPropertyValueProvider: start");
	}
	public String getAntPropertyValue(String antPropertyName) {
		System.out.println("WOLipsAntPropertyValueProvider.getAntPropertyValue: " + antPropertyName);
		String value = null;
		if ("wolips.global.properties".equals(antPropertyName)) {
			value = Preferences.getString(Preferences.PREF_WOLIPS_PROPERTIES_FILE);
		}
		return value;
	}
}
