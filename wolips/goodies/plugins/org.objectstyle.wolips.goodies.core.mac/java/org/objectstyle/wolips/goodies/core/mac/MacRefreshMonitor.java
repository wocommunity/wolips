package org.objectstyle.wolips.goodies.core.mac;

import static org.objectstyle.wolips.goodies.core.mac.jna.CoreServices.kFSEventStreamCreateFlagNoDefer;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.refresh.IRefreshMonitor;
import org.eclipse.core.resources.refresh.IRefreshResult;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.objectstyle.wolips.goodies.core.mac.jna.CoreFoundation;
import org.objectstyle.wolips.goodies.core.mac.jna.CoreFoundation.CoreFoundationWrapper;
import org.objectstyle.wolips.goodies.core.mac.jna.CoreServices;
import org.objectstyle.wolips.goodies.core.mac.jna.CoreServices.CoreServicesWrapper;
import org.objectstyle.wolips.goodies.core.mac.jna.CoreServices.FSEventStreamCallback;

import com.sun.jna.NativeLibrary;
import com.sun.jna.Pointer;

public class MacRefreshMonitor extends Job implements IRefreshMonitor {
	final Map<IPath, MonitoredResource> _resources;

    CoreServices coreServices;
    CoreFoundation coreFoundation;
    long currentEvent = -1;
    private MonitorRefreshThread monitorThread;

	public MacRefreshMonitor() {
		super("MacRefreshMonitor");
		setPriority(Job.DECORATE);
		setSystem(true);
        _resources = new ConcurrentHashMap<IPath, MonitoredResource>();
        coreServices = CoreServicesWrapper.defaultInstance();
        coreFoundation = CoreFoundationWrapper.defaultInstance();
        currentEvent = coreServices.FSEventsGetCurrentEventId();
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
        closeStream();
        monitorThread = new MonitorRefreshThread();
        monitorThread.start();
		return Status.OK_STATUS;
	}


	public void monitor(IResource resource, IRefreshResult refreshResult) {
		if (resource != null && refreshResult != null) {
			MonitoredResource monitoredResource = new MonitoredResource(resource, refreshResult);
			_resources.put(MacRefreshMonitor.canonicalPath(resource.getLocation()), monitoredResource);
			schedule();
		}
	}

	public void unmonitor(IResource resource) {
		if (resource != null) {
			_resources.remove(resource.getLocation());
			schedule();
		}
	}

    protected static class MonitoredResource {
		public final IResource _resource;
		public final IRefreshResult _result;

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
			// MS: SVN thrashes the fuck out of the refresher, and because it's always a
			// branch off of your main structure, it doesn't benefit from the refresh 
			// coalescing that happens in RefreshManager. To help out a little bit, we
			// roll up team private members to their nearest non-team-private member so
			// it can be coalesced effectively.
			if (resource != null && resource.isTeamPrivateMember()) {
				IResource refreshResource = resource.getParent();
				while (refreshResource != null && refreshResource.isTeamPrivateMember()) {
					refreshResource = refreshResource.getParent();
				}
				if (refreshResource != null && !resource.isSynchronized(IResource.DEPTH_ONE)) {
					_result.refresh(refreshResource);
				}
			}
			else if (resource != null && !resource.isSynchronized(IResource.DEPTH_ONE)) {
				_result.refresh(resource);
			}
		}

		protected void pathChanged(IPath location) {
			IContainer matchingContainer = _resource.getWorkspace().getRoot().getContainerForLocation(location);
			if (matchingContainer != null) {
				refresh(matchingContainer);
			}
			else {
				IContainer[] matchingContainers = _resource.getWorkspace().getRoot().findContainersForLocationURI(URIUtil.toURI(location.makeAbsolute()));
				if (matchingContainers != null) {
					for (IContainer container : matchingContainers) {
						refresh(container);
					}
				}
			}
		}
	}

    public synchronized void dispose() {
        closeStream();
        currentEvent = -1;
        coreFoundation = null;
        coreServices = null;
    }

    private void closeStream() {
        if (monitorThread != null) {
            monitorThread.cancel();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        dispose();
    }
    
	public static IPath canonicalPath(IPath path) {
		if (path == null)
			return null;
		try {
			final String pathString = path.toOSString();
			final String canonicalPath = new java.io.File(pathString).getCanonicalPath();
			//only create a new path if necessary
			if (canonicalPath.equals(pathString))
				return path;
			return new Path(canonicalPath);
		} catch (IOException e) {
			return path;
		}
	}

	MonitoredResource monitoredResourceForPath(IPath path) {
		IPath aPath = path;
		MonitoredResource resource = _resources.get(aPath);
		while (resource == null && !path.isRoot() && !"/".equals(aPath.toPortableString())) {
			aPath = aPath.removeLastSegments(1);
			resource = _resources.get(aPath);
		}
		return resource;
	}

    class StreamEventCallback implements FSEventStreamCallback {
	        public void callback(Pointer streamRef, Pointer clientCallbackInfo, int numEvents,
	                Pointer eventPaths, Pointer eventFlags, Pointer eventIds) {
	
	            Pointer[] myPaths = eventPaths.getPointerArray(0, numEvents);
	            long[] myEvents = eventIds.getLongArray(0, numEvents);
	            for (int i = 0; i < numEvents; i++) {
	                IPath path = MacRefreshMonitor.canonicalPath(new Path(myPaths[i].getString(0)));
	                MonitoredResource resource = monitoredResourceForPath(path);
	                if (resource != null) {
	                	resource.pathChanged(path);
	                }

	                if ((currentEvent > 0 && currentEvent < myEvents[i])
	                    ||(currentEvent < 0 && currentEvent > myEvents[i])) {
	                	currentEvent = myEvents[i];
	                }
	            }
	        }
	    }

	private class MonitorRefreshThread extends Thread {

    	public MonitorRefreshThread() {
			super("MacRefreshMonitor");
		}
    	
        private Pointer runLoop;
        private Pointer streamRef;
        private StreamEventCallback callBack = new StreamEventCallback();
        private CountDownLatch started = new CountDownLatch(1);

        @Override
        public void run() {
            Pointer runLoopMode = NativeLibrary.getInstance("CoreFoundation").getGlobalVariableAddress(
            	"kCFRunLoopDefaultMode").getPointer(0);
        	try {
            	if (_resources.size() == 0) return;

                Pointer monitorPaths = coreFoundation.CFArrayCreate(resourcePaths());
                streamRef = coreServices.FSEventStreamCreate(null, callBack, null,
                        monitorPaths, currentEvent, 1.0, kFSEventStreamCreateFlagNoDefer);
                runLoop = coreFoundation.CFRunLoopGetCurrent();

                coreServices.FSEventStreamScheduleWithRunLoop(streamRef, runLoop, runLoopMode);
                coreServices.FSEventStreamStart(streamRef);
            } finally {
                started.countDown();
            }
            coreFoundation.CFRunLoopRun();
        }

        private String[] resourcePaths() {
        	List<String> result = new LinkedList<String>();
        	IPath[] paths = _resources.keySet().toArray(new IPath[_resources.size()]);
        	for (int i = 0; i < paths.length; i++) {
        		if (paths[i] != null) {
        			result.add(paths[i].toOSString());
        		}
        	}
        	return result.toArray(new String[result.size()]);
        }
        
        synchronized void cancel() {
            try {
                started.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (runLoop != null) {
                coreFoundation.CFRunLoopStop(runLoop);
                runLoop = null;
            }

            if (streamRef != null) {
                coreServices.FSEventStreamStop(streamRef);
                coreServices.FSEventStreamInvalidate(streamRef);
                coreServices.FSEventStreamRelease(streamRef);
                streamRef = null;
            }
        }
    }
}
