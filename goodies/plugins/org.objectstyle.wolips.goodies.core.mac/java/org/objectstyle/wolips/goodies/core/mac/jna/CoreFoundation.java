package org.objectstyle.wolips.goodies.core.mac.jna;

import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

public interface CoreFoundation extends Library {
	public interface CFAllocationReleaseCallaback extends Callback {
		void callback(Pointer info);
	}

	public interface CFAllocationRetainCallback extends Callback {
		void callback(Pointer info);
	}

	public interface CFAllocatorCopyDescriptionCallBack extends Callback {
		void callback(Pointer info);
	}

	public class CFAllocatorRef extends Structure {
		// empty
	}

	public class CFArrayCallBacks extends Structure {
		public int version;

		public CFArrayRetainCallBack retain;

		public CFArrayReleaseCallBack release;

		public CFArrayCopyDescriptionCallBack copyDescription;

		public CFArrayEqualCallBack equal;
	}

	public interface CFArrayCopyDescriptionCallBack {
		public void callback(Pointer value);
	}

	public interface CFArrayEqualCallBack {
		public void callback(Pointer value1, Pointer value2);
	}

	public class CFArrayRef extends Structure {
		// empty
	}

	public interface CFArrayReleaseCallBack {
		public void callback(Pointer value);
	}

	public interface CFArrayRetainCallBack {
		public void callback(Pointer value);
	}

	public static final int kCFStringEncodingUTF8 = 0x08000100;

	public static final int kCFRunLoopRunFinished = 1;

	public static final int kCFRunLoopRunStopped = 2;

	public static final int kCFRunLoopRunTimedOut = 3;

	public static final int kCFRunLoopRunHandledSource = 4;

	Pointer CFArrayCreate(Pointer allocator, Pointer[] values, int numValues, CFArrayCallBacks callBacks);

	public Pointer CFArrayCreate(Pointer[] values);

	public Pointer CFArrayCreate(String[] stringVals);

	public Pointer CFRetain(Pointer pointer);

	public Pointer CFRunLoopGetCurrent();

	public Pointer CFRunLoopGetMain();

	public void CFRunLoopRun();

	public int CFRunLoopRunInMode(Pointer mode, double interval, boolean returnAfterSourceHandled);

	public void CFRunLoopStop(Pointer pointer);

	Pointer CFStringCreateWithCString(Pointer allocator, String string, int encoding);

	public Pointer CFStringCreateWithCString(String string);

	public String getLastError();

	public String strerror(int errnum);
	
	public class CoreFoundationWrapper implements CoreFoundation {
		private final CoreFoundation coreFoundation;

		public CoreFoundationWrapper() {
			coreFoundation = (CoreFoundation) Native.loadLibrary("CoreFoundation", CoreFoundation.class);
		}

		public Pointer CFArrayCreate(Pointer allocator, Pointer[] values, int numValues, CFArrayCallBacks callBacks) {
			return coreFoundation.CFArrayCreate(allocator, values, numValues, callBacks);
		}

		public Pointer CFArrayCreate(Pointer[] values) {
			return coreFoundation.CFArrayCreate(null, values, values.length, null);
		}

		public Pointer CFArrayCreate(String[] stringVals) {
			Pointer[] values = new Pointer[stringVals.length];
			for (int i = 0; i < stringVals.length; i++) {
				values[i] = CFStringCreateWithCString(stringVals[i]);
			}
			return CFArrayCreate(values);
		}

		public Pointer CFRetain(Pointer pointer) {
			return coreFoundation.CFRetain(pointer);
		}

		public Pointer CFRunLoopGetCurrent() {
			return coreFoundation.CFRunLoopGetCurrent();
		}

		public Pointer CFRunLoopGetMain() {
			return coreFoundation.CFRunLoopGetMain();
		}

		public void CFRunLoopRun() {
			coreFoundation.CFRunLoopRun();
		}

		public int CFRunLoopRunInMode(Pointer mode, double interval, boolean returnAfterSourceHandled) {
			return coreFoundation.CFRunLoopRunInMode(mode, interval, returnAfterSourceHandled);
		}

		public void CFRunLoopStop(Pointer pointer) {
			coreFoundation.CFRunLoopStop(pointer);
		}

		public Pointer CFStringCreateWithCString(Pointer allocator, String string, int encoding) {
			return coreFoundation.CFStringCreateWithCString(allocator, string, encoding);
		}

		public Pointer CFStringCreateWithCString(String string) {
			return CFStringCreateWithCString(null, string, kCFStringEncodingUTF8);
		}

		public String getLastError() {
			return strerror(Native.getLastError());
		}

		public String strerror(int errnum) {
			return coreFoundation.strerror(errnum);
		}
	}
}
