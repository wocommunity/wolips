package org.objectstyle.wolips.goodies.core.mac.jna;

import org.objectstyle.wolips.goodies.core.mac.jna.CoreFoundation.CFAllocationReleaseCallaback;
import org.objectstyle.wolips.goodies.core.mac.jna.CoreFoundation.CFAllocationRetainCallback;
import org.objectstyle.wolips.goodies.core.mac.jna.CoreFoundation.CFAllocatorCopyDescriptionCallBack;

import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

public interface CoreServices extends Library {
	public final CoreServices INSTANCE = (CoreServices) Native.loadLibrary("CoreServices", CoreServices.class);
	
	public interface FSEventStreamCallback extends Callback {
		void callback(Pointer streamRef, Pointer clientCallbackInfo, int numEvents, Pointer eventPaths, Pointer eventFlags, Pointer eventIds);

	}

	public class FSEventStreamContext extends Structure {
		public int version = 0;

		public Pointer info = null;

		public CFAllocationRetainCallback retain = null;

		public CFAllocationReleaseCallaback release = null;

		public CFAllocatorCopyDescriptionCallBack copyDescription = null;
	}

	public class FSEventStreamRef extends Structure {
		// empty
	}

	public static final int kFSEventStreamCreateFlagNone = 0;

	public static final int kFSEventStreamCreateFlagUseCFTypes = 1;

	public static final int kFSEventStreamCreateFlagNoDefer = 2;

	public static final int kFSEventStreamCreateFlagWatchRoot = 4;
	
	public static final int gestaltSystemVersion = ('s'<<24) + ('y'<<16) + ('s'<<8) + 'v';

	public long FSEventsGetCurrentEventId();

	public Pointer FSEventStreamCreate(Pointer allocator, FSEventStreamCallback callback, FSEventStreamContext context, Pointer pathsToWatch, long sinceWhen, double latency, int flags);

	public void FSEventStreamFlushSync(Pointer streamRef);

	public void FSEventStreamInvalidate(Pointer streamRef);

	public void FSEventStreamRelease(Pointer streamRef);

	public void FSEventStreamScheduleWithRunLoop(Pointer streamRef, Pointer runLoop, Pointer runLoopMod);

	public boolean FSEventStreamStart(Pointer streamRef);

	public void FSEventStreamStop(Pointer streamRef);
	
	public int Gestalt(int selector, int[] response);
	
	public static class CoreServicesWrapper implements CoreServices {
		private static final CoreServices coreServices = CoreServices.INSTANCE;
		private static final CoreServicesWrapper wrapper = new CoreServicesWrapper();

		public static CoreServicesWrapper defaultInstance() {
			return wrapper;
		}
		
		private CoreServicesWrapper() {
		}

		public long FSEventsGetCurrentEventId() {
			return coreServices.FSEventsGetCurrentEventId();
		}

		public Pointer FSEventStreamCreate(Pointer allocator, FSEventStreamCallback callback, FSEventStreamContext context, Pointer pathsToWatch, long sinceWhen, double latency, int flags) {
			return coreServices.FSEventStreamCreate(allocator, callback, context, pathsToWatch, sinceWhen, latency, flags);
		}

		public void FSEventStreamFlushSync(Pointer streamRef) {
			coreServices.FSEventStreamFlushSync(streamRef);
		}

		public void FSEventStreamInvalidate(Pointer streamRef) {
			coreServices.FSEventStreamInvalidate(streamRef);
		}

		public void FSEventStreamRelease(Pointer streamRef) {
			coreServices.FSEventStreamRelease(streamRef);
		}

		public void FSEventStreamScheduleWithRunLoop(Pointer streamRef, Pointer runLoop, Pointer runLoopMod) {
			coreServices.FSEventStreamScheduleWithRunLoop(streamRef, runLoop, runLoopMod);
		}

		public boolean FSEventStreamStart(Pointer streamRef) {
			return coreServices.FSEventStreamStart(streamRef);
		}

		public void FSEventStreamStop(Pointer streamRef) {
			coreServices.FSEventStreamStop(streamRef);
		}
		
		public int Gestalt(int selector, int[] response) {
			return coreServices.Gestalt(selector, response);
		}
		
		public int SystemVersion() {
			int[] response = new int[1];
			coreServices.Gestalt(gestaltSystemVersion, response);
			return response[0];
			
		}
	}
}
