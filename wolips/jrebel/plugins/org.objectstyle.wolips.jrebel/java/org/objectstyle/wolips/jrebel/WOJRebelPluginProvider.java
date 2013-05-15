package org.objectstyle.wolips.jrebel;

import org.eclipse.jdt.core.IJavaProject;
import org.objectstyle.wolips.jrebel.utils.WOProjectUtils;
import org.zeroturnaround.eclipse.IRebelPluginProvider;


public class WOJRebelPluginProvider implements IRebelPluginProvider {
  private static final String[] WOJR = new String[] { "lib/WOJRebel-5.0.jar" };
  private static final String[] WOJR_53 = new String[] { "lib/WOJRebel-53-5.0.jar" };
  private static final String[] EmptyStringArray = new String[0];

  public String[] getPluginResources(IJavaProject project) {
    if (WOProjectUtils.isWOApplication(project)) {
      String version = WOProjectUtils.woVersion(project);
      if (version != null && version.startsWith("5.3")) {
        return WOJR_53;
      } else {
        return WOJR;
      }
    }
    return EmptyStringArray;
  }
}
