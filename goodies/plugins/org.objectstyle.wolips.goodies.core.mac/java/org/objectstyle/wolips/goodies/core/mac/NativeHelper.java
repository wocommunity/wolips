/* ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0 
 *
 * Copyright (c) 2004 - 2006 The ObjectStyle Group 
 * and individual authors of the software.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:  
 *       "This product includes software developed by the 
 *        ObjectStyle Group (http://objectstyle.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "ObjectStyle Group" and "Cayenne" 
 *    must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written 
 *    permission, please contact andrus@objectstyle.org.
 *
 * 5. Products derived from this software may not be called "ObjectStyle"
 *    nor may "ObjectStyle" appear in their names without prior written
 *    permission of the ObjectStyle Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE OBJECTSTYLE GROUP OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the ObjectStyle Group.  For more
 * information on the ObjectStyle Group, please see
 * <http://objectstyle.org/>.
 *
 */

package org.objectstyle.wolips.goodies.core.mac;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;

/**
 * @author ulrich
 * @author Mike Schrag
 */
public class NativeHelper {
	private static URLClassLoader APPLE_CLASS_LOADER;

	static {
		try {
			// Note: This HAS to only be loaded one time or the app will explode
			// because the dylib will have already been loaded
			// in another class loader.
			NativeHelper.APPLE_CLASS_LOADER = URLClassLoader.newInstance(new URL[] { new File("/System/Library/Java").toURL() });
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public static void revealInFinder(IResource resource) {
		try {
			Class nsApplicationClass = NativeHelper.APPLE_CLASS_LOADER.loadClass("com.apple.cocoa.application.NSApplication");
			Method nsApplicationSharedApplicationMethod = nsApplicationClass.getMethod("sharedApplication", null);
			nsApplicationSharedApplicationMethod.invoke(null, null);

			Class nsWorkspaceClass = NativeHelper.APPLE_CLASS_LOADER.loadClass("com.apple.cocoa.application.NSWorkspace");
			Method nsWorkspaceSharedWorkspaceMethod = nsWorkspaceClass.getMethod("sharedWorkspace", null);
			Object nsWorkspace = nsWorkspaceSharedWorkspaceMethod.invoke(null, null);

			Method nsWorkspaceSelectFileMethod = nsWorkspaceClass.getMethod("selectFile", new Class[] { String.class, String.class });
			String resourcePath = resource.getLocation().toOSString();
			nsWorkspaceSelectFileMethod.invoke(nsWorkspace, new Object[] { resourcePath, resourcePath });
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public static void cdInTerminal(IResource resource) {
		try {
			Class nsApplicationClass = NativeHelper.APPLE_CLASS_LOADER.loadClass("com.apple.cocoa.application.NSApplication");
			Method nsApplicationSharedApplicationMethod = nsApplicationClass.getMethod("sharedApplication", null);
			nsApplicationSharedApplicationMethod.invoke(null, null);

			Class nsAppleScriptClass = NativeHelper.APPLE_CLASS_LOADER.loadClass("com.apple.cocoa.foundation.NSAppleScript");
			Constructor nsAppleScriptConstructor = nsAppleScriptClass.getConstructor(new Class[] { String.class });

			Class nsMutableDictionaryClass = NativeHelper.APPLE_CLASS_LOADER.loadClass("com.apple.cocoa.foundation.NSMutableDictionary");
			Constructor nsMutableDictionaryConstructor = nsMutableDictionaryClass.getConstructor(null);
			Object errorsDictionary = nsMutableDictionaryConstructor.newInstance(null);

            String containerPath = getParentOfResource(resource).replaceAll(" ", "\\ ");
			String openInTerminalString = "tell application \"Terminal\"\n do script \"cd " + containerPath + "\"\n activate\nend tell";
			Object nsAppleScript = nsAppleScriptConstructor.newInstance(new Object[] { openInTerminalString });

			Method nsAppleScriptExecuteMethod = nsAppleScriptClass.getMethod("execute", new Class[] { nsMutableDictionaryClass });
			nsAppleScriptExecuteMethod.invoke(nsAppleScript, new Object[] { errorsDictionary });
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private static String getParentOfResource(IResource resource) {
        File file = resource.getLocation().toFile();
        File path = null;
        if (file != null) {
            if (!file.isDirectory() && file.getParentFile() != null) {
                path = file.getParentFile();
            } else {
                path = file;
            }
        }
        return path.getPath();
	}

}