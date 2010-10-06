package org.objectstyle.wolips.bindings;

import org.eclipse.core.resources.IProject;
import org.objectstyle.wolips.baseforuiplugins.AbstractBaseUIActivator;
import org.objectstyle.wolips.bindings.preferences.PreferenceConstants;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractBaseUIActivator {
  // The plug-in ID
  public static final String PLUGIN_ID = "org.objectstyle.wolips.bindings";

  // The shared instance
  private static Activator plugin;

  /**
   * The constructor
   */
  public Activator() {
    plugin = this;
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
   */
  @Override
  public void start(BundleContext context) throws Exception {
    super.start(context);
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
   */
  @Override
  public void stop(BundleContext context) throws Exception {
    plugin = null;
    super.stop(context);
  }

  /**
   * Returns the shared instance
   *
   * @return the shared instance
   */
  public static Activator getDefault() {
    return plugin;
  }

  public boolean useInlineBindings(IProject project) {
    return Activator.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.USE_INLINE_BINDINGS_KEY);
  }
  
  public void setUseInlineBindings(IProject project, boolean useInlineBindings) {
    Activator.getDefault().getPreferenceStore().setValue(PreferenceConstants.USE_INLINE_BINDINGS_KEY, useInlineBindings);
  }
  
//  public boolean isWO54(IProject project) {
//    return Activator.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.WO54_KEY);
//  }
}
