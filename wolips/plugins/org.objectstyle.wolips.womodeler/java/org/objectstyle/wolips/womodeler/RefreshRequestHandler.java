package org.objectstyle.wolips.womodeler;

import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.objectstyle.wolips.womodeler.server.IRequestHandler;
import org.objectstyle.wolips.womodeler.server.Request;
import org.objectstyle.wolips.womodeler.server.Webserver;

public class RefreshRequestHandler implements IRequestHandler {
  public void init(Webserver server) throws Exception {
    // DO NOTHING
  }

  public void handle(Request request) throws Exception {
    Map<String, String> params = request.getQueryParameters();
    String pathStr = params.get("path");
    Path path = new Path(pathStr);
    if (path.isAbsolute()) {
      IResource[] resources = ResourcesPlugin.getWorkspace().getRoot().findContainersForLocation(path);
      if (resources.length == 0) {
        resources = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocation(path);
      }
      for (IResource resource : resources) {
        resource.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
      }
    }
    else {
      IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(path);
      if (resource.exists()) {
        resource.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
      }
    }
    request.getWriter().println("ok");
  }
}
