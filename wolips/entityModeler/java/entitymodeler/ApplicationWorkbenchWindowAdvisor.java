package entitymodeler;

import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

  public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
    super(configurer);
  }

  @Override
  public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) {
    return new ApplicationActionBarAdvisor(configurer);
  }

  @Override
  public void preWindowOpen() {
    IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
    configurer.setInitialSize(new Point(1200, 675));
    configurer.setShowCoolBar(true);
    configurer.setShowStatusLine(true);
    configurer.setTitle("Entity Modeler");
  }
  
  @Override
  public void postWindowOpen() {
    super.postWindowOpen();
    getWindowConfigurer().getActionBarConfigurer().getCoolBarManager().setLockLayout(true);
//    IContributionItem[] items = getWindowConfigurer().getActionBarConfigurer().getCoolBarManager().getItems();
//    for (IContributionItem item : items) {
//      if (item instanceof ToolBarContributionItem2) {
//        ToolBarContributionItem2 tbcItem = (ToolBarContributionItem2)item;
//        IContributionItem[] tbItems = tbcItem.getToolBarManager().getItems();
//        for (IContributionItem tbItem : tbItems) {
//          if (tbItem instanceof PluginActionCoolBarContributionItem) {
//            PluginActionCoolBarContributionItem pac = (PluginActionCoolBarContributionItem)tbItem;
//            pac.setMode(PluginActionCoolBarContributionItem.MODE_FORCE_TEXT);
//          }
//        }
//      }
//    }
  }
}
