package org.objectstyle.wolips.wodclipse;

import java.util.ResourceBundle;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class WodclipsePlugin extends AbstractUIPlugin {
  //The shared instance.
  private static WodclipsePlugin plugin;
  private ResourceBundle resourceBundle;

  public static String WODEditorID = "org.objectstyle.wolips.wodclipse.editors.WODEditor";
  public static String ComponentEditorID = "org.objectstyle.wolips.wodclipse.mpe.ComponentEditor";
  /**
   * The constructor.
   */
  public WodclipsePlugin() {
    super();
    plugin = this;
    resourceBundle = ResourceBundle.getBundle("org.objectstyle.wolips.wodclipse.WodclipsePluginResources");
  }

  /**
   * This method is called upon plug-in activation
   */
  public void start(BundleContext context) throws Exception {
    super.start(context);
  }

  /**
   * This method is called when the plug-in is stopped
   */
  public void stop(BundleContext context) throws Exception {
    super.stop(context);
    plugin = null;
  }

  /**
   * Returns the shared instance.
   */
  public static WodclipsePlugin getDefault() {
    return plugin;
  }

  /**
   * Returns an image descriptor for the image file at the given
   * plug-in relative path.
   *
   * @param path the path
   * @return the image descriptor
   */
  public static ImageDescriptor getImageDescriptor(String path) {
    return AbstractUIPlugin.imageDescriptorFromPlugin("org.objectstyle.wolips.wodclipse", path);
  }

  /**
   * Returns the plugin's resource bundle,
   */
  public ResourceBundle getResourceBundle() {
    return this.resourceBundle;
  }
}
