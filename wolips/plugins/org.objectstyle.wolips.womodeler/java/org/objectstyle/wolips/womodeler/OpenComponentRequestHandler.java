package org.objectstyle.wolips.womodeler;

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.swt.widgets.Display;
import org.objectstyle.wolips.componenteditor.actions.OpenComponentAction;
import org.objectstyle.wolips.womodeler.server.IRequestHandler;
import org.objectstyle.wolips.womodeler.server.Request;
import org.objectstyle.wolips.womodeler.server.Webserver;

public class OpenComponentRequestHandler implements IRequestHandler {
  public void init(Webserver server) throws Exception {
    // DO NOTHING
  }

  public void handle(Request request) throws Exception {
    Map<String, String> params = request.getQueryParameters();
    String appName = params.get("app");
    final IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(appName);
    if (project != null) {
      final String componentName = params.get("component");
      if (componentName != null) {
        Display.getDefault().asyncExec(new Runnable() {
          public void run() {
            OpenComponentAction.openComponentWithTypeNamed(JavaCore.create(project), componentName);
          }
        });
      }
    }
    request.getWriter().println("ok");
  }
}
