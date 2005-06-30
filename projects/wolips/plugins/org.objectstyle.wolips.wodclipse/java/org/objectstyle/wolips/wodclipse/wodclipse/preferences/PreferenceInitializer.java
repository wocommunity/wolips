package org.objectstyle.wolips.wodclipse.wodclipse.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.objectstyle.wolips.wodclipse.WodclipsePlugin;

public class PreferenceInitializer extends AbstractPreferenceInitializer {
  public void initializeDefaultPreferences() {
    IPreferenceStore store = WodclipsePlugin.getDefault().getPreferenceStore();
    store.setDefault(PreferenceConstants.COMPONENT_NAME, "65,0,197");
    store.setDefault(PreferenceConstants.COMPONENT_TYPE, "63,127,95");
    store.setDefault(PreferenceConstants.ASSOCIATION_NAME, "138,23,100");
    store.setDefault(PreferenceConstants.ASSOCIATION_VALUE, "0,65,216");
    store.setDefault(PreferenceConstants.CONSTANT_ASSOCIATION_VALUE, "42,0,255");
    store.setDefault(PreferenceConstants.OPERATOR, "0,0,0");
    store.setDefault(PreferenceConstants.UNKNOWN, "0,0,0");
  }

}
