package org.objectstyle.wolips.eomodeler;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.objectstyle.wolips.eomodeler.editors.eoentity.EOAttributesConstants;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

  // The plug-in ID
  public static final String PLUGIN_ID = "org.objectstyle.wolips.eomodeler";

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
  public void start(BundleContext context) throws Exception {
    super.start(context);
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
   */
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
   * Returns an image descriptor for the image file at the given
   * plug-in relative path
   *
   * @param path the path
   * @return the image descriptor
   */
  public static ImageDescriptor getImageDescriptor(String path) {
    return imageDescriptorFromPlugin(PLUGIN_ID, path);
  }

  protected void initializeImageRegistry(ImageRegistry _reg) {
    super.initializeImageRegistry(_reg);
    _reg.put(EOAttributesConstants.PRIMARY_KEY, AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "/icons/primaryKey.png"));
    _reg.put(EOAttributesConstants.LOCKING, AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "/icons/locking.png"));
    _reg.put(EOAttributesConstants.CLASS_PROPERTY, AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "/icons/classProperty.png"));
    _reg.put("check", AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/check.png"));
  }
}
