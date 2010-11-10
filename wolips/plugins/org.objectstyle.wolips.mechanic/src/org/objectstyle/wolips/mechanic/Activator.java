package org.objectstyle.wolips.mechanic;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class Activator extends AbstractUIPlugin {
  public static final String PLUGIN_ID = "org.objectstyle.wolips.mechanic"; //$NON-NLS-1$

  private static Activator _plugin;

  public void start(BundleContext context) throws Exception {
    super.start(context);
    _plugin = this;
  }

  public void stop(BundleContext context) throws Exception {
    _plugin = null;
    super.stop(context);
  }

  public static Activator getDefault() {
    return _plugin;
  }
}
