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

	@SuppressWarnings("unused")
	private int _fsEventStreamRef;

	private Map<String, MonitoredResource> _resources;

	public MacRefreshMonitor() {
		_resources = new HashMap<String, MonitoredResource>();
	}

	public synchronized void monitor(IResource resource, IRefreshResult refreshResult) {
		System.out.println("MacRefreshMonitor.monitor: " + resource);
		IPath resourcePath = resource.getLocation();
		if (resourcePath != null) {
			String resourcePathStr = resourcePath.toOSString();
			MonitoredResource monitoredResource = new MonitoredResource(resource, refreshResult);
			_fsEventStreamRef = monitor(_fsEventStreamRef, resourcePathStr, monitoredResource);
			_resources.put(resourcePathStr, monitoredResource);
		}
	}

	public synchronized void unmonitor(IResource resource) {
		System.out.println("MacRefreshMonitor.unmonitor: " + resource);
		IPath resourcePath = resource.getLocation();
		if (resourcePath != null) {
			String resourcePathStr = resourcePath.toOSString();
			MonitoredResource monitoredResource = _resources.get(resourcePathStr);
			if (monitoredResource != null) {
				_fsEventStreamRef = unmonitor(_fsEventStreamRef, resourcePathStr, monitoredResource);
				_resources.remove(resourcePathStr);
			}
		}
	}

	protected void pathChanged(String changedPathStr) {
		MonitoredResource monitoredResource = _resources.get(changedPathStr);
		if (monitoredResource != null) {
			refresh(monitoredResource.getResource(), monitoredResource.getResult());
		} else {
			Path changedPath = new Path(changedPathStr);
			for (String parentPathStr : _resources.keySet()) {
				Path parentPath = new Path(parentPathStr);
				if (parentPath.isPrefixOf(changedPath)) {
					monitoredResource = _resources.get(parentPathStr);
					IRefreshResult refreshResult = monitoredResource.getResult();
					File f = new File(changedPathStr);
					if (f.isDirectory()) {
						IContainer[] containers = monitoredResource.getResource().getWorkspace().getRoot().findContainersForLocation(changedPath);
						for (IContainer container : containers) {
							refresh(container, refreshResult);
						}
					} else {
						IFile[] files = monitoredResource.getResource().getWorkspace().getRoot().findFilesForLocation(changedPath);
						for (IFile file : files) {
							refresh(file, refreshResult);
						}
					}
				}
			}
		}
	}
	
	protected void refresh(IResource resource, IRefreshResult refreshResult) {
		if (!resource.isSynchronized(IResource.DEPTH_INFINITE)) {
			System.out.println("MacRefreshMonitor.refresh: " + resource);
			refreshResult.refresh(resource);
		}
	}

	private native int unmonitor(int fsEventStreamRef, String resourcePath, MonitoredResource monitoredResource);

	private native int monitor(int fsEventStreamRef, String resourcePath, MonitoredResource monitoredResource);

	protected static class MonitoredResource {
		private IResource _resource;

		private IRefreshResult _result;

		@SuppressWarnings("unused")
		private Object _globalRef;

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
	}
}
