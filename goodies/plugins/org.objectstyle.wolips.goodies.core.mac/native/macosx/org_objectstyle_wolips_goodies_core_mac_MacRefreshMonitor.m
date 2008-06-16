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
	jobject monitoredResourceGlobalRef = (jobject)clientCallBackInfo;
	
	jclass monitoredResourceClass = (*env)->GetObjectClass(env, monitoredResourceGlobalRef);
	jmethodID pathChangedMethod = (*env)->GetMethodID(env, monitoredResourceClass, "pathChanged", "(Ljava/lang/String;)V");
	
    int pathNum;
    char **paths = changedPaths;
    for (pathNum = 0; pathNum < numEvents; pathNum++) {
		jstring pathStr = (*env)->NewStringUTF(env, paths[pathNum]);
		(*env)->CallVoidMethod(env, monitoredResourceGlobalRef, pathChangedMethod, pathStr);
	}
	
	(*vm)->DetachCurrentThread(vm);
}

JNIEXPORT void JNICALL Java_org_objectstyle_wolips_goodies_core_mac_MacRefreshMonitor_unmonitor(JNIEnv *env, jobject obj, jstring resourcePath, jobject monitoredResource) {
	jclass monitoredResourceClass = (*env)->GetObjectClass(env, monitoredResource);
	jfieldID fsEventStreamRefField = (*env)->GetFieldID(env, monitoredResourceClass, "_fsEventStreamRef", "I");
	FSEventStreamRef stream = (FSEventStreamRef)(*env)->GetIntField(env, monitoredResource, fsEventStreamRefField);
	(*env)->SetIntField(env, monitoredResource, fsEventStreamRefField, 0);

	FSEventStreamStop(stream);
	FSEventStreamInvalidate(stream);
	FSEventStreamRelease(stream);

	jfieldID monitoredResourceGlobalRefField = (*env)->GetFieldID(env, monitoredResourceClass, "_globalRef", "Lorg/objectstyle/wolips/goodies/core/mac/MacRefreshMonitor$MonitoredResource;");
	jobject monitoredResourceGlobalRef = (*env)->GetObjectField(env, monitoredResource, monitoredResourceGlobalRefField);
	(*env)->DeleteGlobalRef(env, monitoredResourceGlobalRef);
	(*env)->SetObjectField(env, monitoredResource, monitoredResourceGlobalRefField, NULL);
}

JNIEXPORT void JNICALL Java_org_objectstyle_wolips_goodies_core_mac_MacRefreshMonitor_monitor(JNIEnv *env, jobject obj, jstring resourcePath, jobject monitoredResource) {
	jclass monitoredResourceClass = (*env)->GetObjectClass(env, monitoredResource);
	jfieldID monitoredResourceGlobalRefField = (*env)->GetFieldID(env, monitoredResourceClass, "_globalRef", "Lorg/objectstyle/wolips/goodies/core/mac/MacRefreshMonitor$MonitoredResource;");
	jobject monitoredResourceGlobalRef = (jobject) (*env)->NewGlobalRef(env, monitoredResource);
	(*env)->SetObjectField(env, monitoredResource, monitoredResourceGlobalRefField, monitoredResourceGlobalRef);
	
	CFAbsoluteTime latency = 1.0;
	FSEventStreamContext context = {0, monitoredResourceGlobalRef, NULL, NULL, NULL};
	CFMutableArrayRef monitorPaths = CFArrayCreateMutable(kCFAllocatorDefault, 0, &kCFTypeArrayCallBacks);

	const char *monitorPathChar = (*env)->GetStringUTFChars(env, resourcePath, JNI_FALSE);
	CFStringRef monitorPath = CFStringCreateWithCString(kCFAllocatorDefault, monitorPathChar, kCFStringEncodingUTF8);	
	(*env)->ReleaseStringUTFChars(env, resourcePath, monitorPathChar);
	CFArrayAppendValue(monitorPaths, monitorPath);
	CFRelease(monitorPath);

	FSEventStreamRef stream = FSEventStreamCreate(NULL, (FSEventStreamCallback)&pathsChanged, &context, monitorPaths, kFSEventStreamEventIdSinceNow, latency, kFSEventStreamCreateFlagNone);
	CFRelease(monitorPaths);
	
	jfieldID fsEventStreamRefField = (*env)->GetFieldID(env, monitoredResourceClass, "_fsEventStreamRef", "I");
	(*env)->SetIntField(env, monitoredResource, fsEventStreamRefField, (jint)stream);
	
	FSEventStreamScheduleWithRunLoop(stream, CFRunLoopGetCurrent(), kCFRunLoopDefaultMode);
	FSEventStreamStart(stream);	
}
