package org.objectstyle.wolips.wodclipse.core;

import org.objectstyle.wolips.baseforuiplugins.AbstractBaseUIActivator;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractBaseUIActivator {
  // The plug-in ID
  public static final String PLUGIN_ID = "org.objectstyle.wolips.wodclipse.core";

  public static final String TEMPLATE_PROBLEM_MARKER = Activator.PLUGIN_ID + ".problem";

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

  /**
   * Generates a message from a template and parameters.
   * Replace template {0}{1}.. with parameters.
   * 
   * @param message message
   * @param params  parameterd
   * @return generated message
   */
  public static String createMessage(String message, String[] params) {
    if (message != null) {
      for (int i = 0; i < params.length; i++) {
        message = message.replaceAll("\\{" + i + "\\}", params[i]);
      }
    }
    return message;
  }

}
