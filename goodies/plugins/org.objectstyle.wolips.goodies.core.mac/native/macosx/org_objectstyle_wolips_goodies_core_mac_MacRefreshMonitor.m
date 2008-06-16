#include "org_objectstyle_wolips_goodies_core_mac_MacRefreshMonitor.h"
#include <CoreServices/CoreServices.h>
#include <Foundation/Foundation.h>

void pathsChanged(ConstFSEventStreamRef streamRef, void *clientCallBackInfo, size_t numEvents, void *changedPaths, const FSEventStreamEventFlags eventFlags[], const FSEventStreamEventId eventIds[]) {
	JavaVM *VMs[1];
	jint numVMs;	
	JNI_GetCreatedJavaVMs(VMs, 1, &numVMs);

	JavaVM *vm = VMs[0];
	JNIEnv *env;
	(*vm)->AttachCurrentThread(vm, (void **)&env, NULL);
	jobject refreshMonitorGlobalRef = (jobject)clientCallBackInfo;
	
	jclass refreshMonitorClass = (*env)->GetObjectClass(env, refreshMonitorGlobalRef);
	jmethodID pathsChangedMethod = (*env)->GetMethodID(env, refreshMonitorClass, "pathChanged", "(Ljava/lang/String;)V");
	
    int pathNum;
    char **paths = changedPaths;
    for (pathNum = 0; pathNum < numEvents; pathNum++) {
		jstring pathStr = (*env)->NewStringUTF(env, paths[pathNum]);
		(*env)->CallVoidMethod(env, refreshMonitorGlobalRef, pathsChangedMethod, pathStr);
	}
	
	(*vm)->DetachCurrentThread(vm);
}

CFMutableArrayRef ModifyMonitoredPaths(FSEventStreamRef fsEventStreamRef) {
	CFMutableArrayRef monitorPaths;
	if (fsEventStreamRef != NULL) {
		CFArrayRef monitoredPaths = FSEventStreamCopyPathsBeingWatched(fsEventStreamRef);
		monitorPaths = CFArrayCreateMutableCopy(kCFAllocatorDefault, 0, monitoredPaths);
		FSEventStreamStop(fsEventStreamRef);
		FSEventStreamInvalidate(fsEventStreamRef);
		FSEventStreamRelease(fsEventStreamRef);
	}
	else {
		monitorPaths = CFArrayCreateMutable(kCFAllocatorDefault, 0, &kCFTypeArrayCallBacks);
	}
	return monitorPaths;
}

FSEventStreamRef MonitorPaths(JNIEnv *env, jobject obj, jobject refreshMonitorGlobalRef, CFMutableArrayRef monitorPaths) {
	CFAbsoluteTime latency = 1.0;
	FSEventStreamContext context = {0, refreshMonitorGlobalRef, NULL, NULL, NULL};
	FSEventStreamRef stream = FSEventStreamCreate(NULL, (FSEventStreamCallback)&pathsChanged, &context, monitorPaths, kFSEventStreamEventIdSinceNow, latency, kFSEventStreamCreateFlagNone);
	CFRelease(monitorPaths);
		
	FSEventStreamScheduleWithRunLoop(stream, CFRunLoopGetCurrent(), kCFRunLoopDefaultMode);
	FSEventStreamStart(stream);	
	return stream;
}

JNIEXPORT jint JNICALL Java_org_objectstyle_wolips_goodies_core_mac_MacRefreshMonitor_unmonitor(JNIEnv *env, jobject obj, jint jfsEventStreamRef, jstring resourcePath, jobject monitoredResource) {
	const char *monitorPathChar = (*env)->GetStringUTFChars(env, resourcePath, JNI_FALSE);
	CFStringRef monitorPath = CFStringCreateWithCString(kCFAllocatorDefault, monitorPathChar, kCFStringEncodingUTF8);	
	(*env)->ReleaseStringUTFChars(env, resourcePath, monitorPathChar);
	
	CFMutableArrayRef monitorPaths = ModifyMonitoredPaths((FSEventStreamRef) jfsEventStreamRef);
	int monitorPathIndex = CFArrayGetFirstIndexOfValue(monitorPaths, CFRangeMake(0, CFArrayGetCount(monitorPaths)), monitorPath);
	if (monitorPathIndex != -1) {
		CFArrayRemoveValueAtIndex(monitorPaths, monitorPathIndex);
	}
	CFRelease(monitorPath);

	jclass monitoredResourceClass = (*env)->FindClass(env, "org/objectstyle/wolips/goodies/core/mac/MacRefreshMonitor$MonitoredResource");
	jfieldID globalRefField = (*env)->GetFieldID(env, monitoredResourceClass, "_globalRef", "Ljava/lang/Object;");
	jobject refreshMonitorGlobalRef = (*env)->GetObjectField(env, obj, globalRefField);
	(*env)->DeleteGlobalRef(env, refreshMonitorGlobalRef);
	
	FSEventStreamRef stream = NULL;
	if (CFArrayGetCount(monitorPaths) > 0) {
		stream = MonitorPaths(env, obj, refreshMonitorGlobalRef, monitorPaths);
	}
	return (jint)stream;
}

JNIEXPORT jint JNICALL Java_org_objectstyle_wolips_goodies_core_mac_MacRefreshMonitor_monitor(JNIEnv *env, jobject obj, jint jfsEventStreamRef, jstring resourcePath, jobject monitoredResource) {
	const char *monitorPathChar = (*env)->GetStringUTFChars(env, resourcePath, JNI_FALSE);
	CFStringRef monitorPath = CFStringCreateWithCString(kCFAllocatorDefault, monitorPathChar, kCFStringEncodingUTF8);	
	(*env)->ReleaseStringUTFChars(env, resourcePath, monitorPathChar);

	CFMutableArrayRef monitorPaths = ModifyMonitoredPaths((FSEventStreamRef) jfsEventStreamRef);
	CFArrayAppendValue(monitorPaths, monitorPath);
	CFRelease(monitorPath);

	jclass monitoredResourceClass = (*env)->FindClass(env, "org/objectstyle/wolips/goodies/core/mac/MacRefreshMonitor$MonitoredResource");
	jfieldID globalRefField = (*env)->GetFieldID(env, monitoredResourceClass, "_globalRef", "Ljava/lang/Object;");
	jobject refreshMonitorGlobalRef = (jobject) (*env)->NewGlobalRef(env, obj);
	(*env)->SetObjectField(env, obj, globalRefField, refreshMonitorGlobalRef);

	FSEventStreamRef stream = MonitorPaths(env, obj, refreshMonitorGlobalRef, monitorPaths);
	return (jint)stream;
}
