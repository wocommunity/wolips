package org.objectstyle.wolips.eomodeler;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {
  public static final String CHECK_ICON = "check";
  public static final String EOENTITY_ICON = "eoEntity";
  public static final String EOATTRIBUTE_ICON = "eoAttribute";
  public static final String EORELATIONSHIP_ICON = "eoRelationship";
  public static final String EOFETCHSPEC_ICON = "eoFetchSpec";
  public static final String TO_MANY_ICON = "toMany";
  public static final String TO_ONE_ICON = "toOne";
  public static final String PRIMARY_KEY_ICON = "primaryKey";
  public static final String LOCKING_ICON = "locking";
  public static final String CLASS_PROPERTY_ICON = "classProperty";
  public static final String ALLOW_NULL_ICON = "allowNull";

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
    _reg.put(Activator.PRIMARY_KEY_ICON, AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "/icons/primaryKey.png"));
    _reg.put(Activator.LOCKING_ICON, AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "/icons/locking.png"));
    _reg.put(Activator.CLASS_PROPERTY_ICON, AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "/icons/classProperty.png"));
    _reg.put(Activator.CHECK_ICON, AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/check.png"));
    _reg.put(Activator.EOATTRIBUTE_ICON, AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/eoAttribute.png"));
    _reg.put(Activator.EOENTITY_ICON, AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/eoEntity.png"));
    _reg.put(Activator.EOFETCHSPEC_ICON, AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/eoFetchSpecification.png"));
    _reg.put(Activator.EORELATIONSHIP_ICON, AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/eoRelationship.png"));
    _reg.put(Activator.TO_MANY_ICON, AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/toMany.png"));
    _reg.put(Activator.TO_ONE_ICON, AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/toOne.png"));
  }
}
