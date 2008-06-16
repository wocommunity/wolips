package org.objectstyle.wolips.goodies.core.mac;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.refresh.IRefreshMonitor;
import org.eclipse.core.resources.refresh.IRefreshResult;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

public class MacRefreshMonitor implements IRefreshMonitor {
	static {
		System.loadLibrary("MacRefreshProvider");
	}

	private Map<IResource, MonitoredResource> _resources;

	public MacRefreshMonitor() {
		_resources = new HashMap<IResource, MonitoredResource>();
	}

	public synchronized void monitor(IResource resource, IRefreshResult refreshResult) {
		System.out.println("MacRefreshMonitor.monitor: " + resource);
		if (resource != null) {
			IPath resourcePath = resource.getLocation();
			if (resourcePath != null) {
				MonitoredResource monitoredResource = new MonitoredResource(resource, refreshResult);
				monitor(resourcePath.toOSString(), monitoredResource);
				_resources.put(resource, monitoredResource);
			}
		}
	}

	public synchronized void unmonitor(IResource resource) {
		System.out.println("MacRefreshMonitor.unmonitor: " + resource);
		if (resource != null) {
			IPath resourcePath = resource.getLocation();
			if (resourcePath != null) {
				MonitoredResource monitoredResource = _resources.get(resource);
				if (monitoredResource != null) {
					unmonitor(resourcePath.toOSString(), monitoredResource);
					_resources.remove(resource);
				}
			}
		}
	}

	private native void unmonitor(String resourcePath, MonitoredResource monitoredResource);

	private native void monitor(String resourcePath, MonitoredResource monitoredResource);

	protected class MonitoredResource {
		private IResource _resource;

		private IRefreshResult _result;

		@SuppressWarnings("unused")
		private int _fsEventStreamRef;

		@SuppressWarnings("unused")
		private MonitoredResource _globalRef;

		public MonitoredResource(IResource resource, IRefreshResult result) {
			_resource = resource;
			_result = result;
		}

		public IResource getResource() {
			return _resource;
		}

		public IRefreshResult getResult() {
			return _result;
		}

		protected void refresh(IResource resource) {
			if (_result != null && !resource.isSynchronized(IResource.DEPTH_INFINITE)) {
				System.out.println("MacRefreshMonitor.refresh: " + resource);
				_result.refresh(resource);
			}
		}

		protected void pathChanged(String changedPathStr) {
			File f = new File(changedPathStr);
			if (f.exists()) {
				if (f.isDirectory()) {
					IContainer[] containers = _resource.getWorkspace().getRoot().findContainersForLocation(new Path(changedPathStr));
					for (IContainer container : containers) {
						refresh(container);
					}
				} else {
					IFile[] files = _resource.getWorkspace().getRoot().findFilesForLocation(new Path(changedPathStr));
					for (IFile file : files) {
						refresh(file);
					}
				}
			}
		}
	}
}
