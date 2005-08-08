package org.objectstyle.wolips.pbserver;

import java.io.IOException;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.ui.IStartup;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class PBServerPlugin extends Plugin implements IStartup {

  //The shared instance.
  private static PBServerPlugin plugin;
  private PBServer myServer;

  /**
   * The constructor.
   */
  public PBServerPlugin() {
    plugin = this;
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
  public static PBServerPlugin getDefault() {
    return plugin;
  }

  public void earlyStartup() {
    System.out.println("PBServerPlugin.start: Starting.");
    myServer = new PBServer();
    try {
      myServer.start(PBServer.DEFAULT_PB_PORT);
    }
    catch (IOException e) {
      e.printStackTrace(System.out);
    }
  }

}
