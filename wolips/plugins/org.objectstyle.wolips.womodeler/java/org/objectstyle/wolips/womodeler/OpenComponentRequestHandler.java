package org.objectstyle.wolips.womodeler;

import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swt.widgets.Display;
import org.objectstyle.wolips.componenteditor.part.ComponentEditor;
import org.objectstyle.wolips.locate.LocatePlugin;
import org.objectstyle.wolips.locate.result.LocalizedComponentsLocateResult;
import org.objectstyle.wolips.womodeler.server.IRequestHandler;
import org.objectstyle.wolips.womodeler.server.Request;
import org.objectstyle.wolips.womodeler.server.Webserver;
import org.objectstyle.wolips.workbenchutilities.WorkbenchUtilitiesPlugin;

public class OpenComponentRequestHandler implements IRequestHandler {
  public void init(Webserver server) throws Exception {
    // DO NOTHING
  }

  public void handle(Request request) throws Exception {
    Map<String, String> params = request.getQueryParameters();
    String appName = params.get("app");
    IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(appName);
    if (project != null) {
      String componentName = params.get("component");
      if (componentName != null) {
        int lastDotIndex = componentName.lastIndexOf('.');
        if (lastDotIndex != -1) {
          componentName = componentName.substring(lastDotIndex + 1);
        }
        LocalizedComponentsLocateResult result = LocatePlugin.getDefault().getLocalizedComponentsLocateResult(project, componentName);
        if (result != null) {
          final IFile wodFile = result.getFirstWodFile();
          if (wodFile != null) {
            Display.getDefault().asyncExec(new Runnable() {
              public void run() {
                WorkbenchUtilitiesPlugin.open(wodFile, ComponentEditor.ID);
              }
            });
          }
        }
      }
    }
    request.getWriter().println("ok");
  }
}
