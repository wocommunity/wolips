/* ====================================================================
 *
 * The ObjectStyle Group Software License, Version 1.0
 *
 * Copyright (c) 2006 The ObjectStyle Group
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

package org.objectstyle.wolips.launching.exceptionhandler;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.objectstyle.wolips.baseforuiplugins.IEditorTarget;
import org.objectstyle.wolips.launching.LaunchingPlugin;
import org.objectstyle.wolips.locate.Locate;
import org.objectstyle.wolips.locate.LocateException;
import org.objectstyle.wolips.locate.LocatePlugin;
import org.objectstyle.wolips.locate.result.LocalizedComponentsLocateResult;
import org.objectstyle.wolips.locate.scope.ComponentLocateScope;

public class NSKeyValueCodingUnknownKeyExceptionHandler extends AbstractConsoleHandler {

	private String fileName = null;

	public int lineAppended(String line) {
		if (fileName == null) {
			if (line != null) {
				String exception = "com.webobjects.foundation.NSKeyValueCoding$UnknownKeyException:";
				int exceptionIndex = line.indexOf(exception);
				if (exceptionIndex >= 0) {
					int startIndex = exceptionIndex + exception.length();
					int endIndex = line.indexOf(' ', startIndex + 1);
					fileName = line.substring(startIndex, endIndex);
					if (fileName.startsWith(" ")) {
						fileName = fileName.substring(1);
					}
					if (fileName.startsWith("[")) {
						fileName = fileName.substring(1);
					}
					if (fileName.startsWith("<")) {
						fileName = fileName.substring(1);
					}
					fileName = this.fileNameWithoutPackage(fileName);
				} else {
					fileName = null;
				}
			}
			return 0;
		}
		if (line != null) {
			String variableOfTheName = "variable of the name";
			int startIndex = line.indexOf(variableOfTheName);
			if (startIndex >= 0) {
				startIndex = startIndex + 2;
			}
			if (startIndex >= 0) {
				int endIndex = line.indexOf(' ', startIndex + variableOfTheName.length());
				String key = line.substring(startIndex -1 + variableOfTheName.length(), endIndex);
				int startIndexWOComponent = line.indexOf("This WOComponent");
				if (startIndexWOComponent >= 0) {
					IJavaProject javaProject = this.getJavaProject();
					if (javaProject != null) {
						IFile wodFile = null;
						try {
							ComponentLocateScope componentLocateScope = ComponentLocateScope.createLocateScope(javaProject.getProject(), fileName);
							LocalizedComponentsLocateResult localizedComponentsLocateResult = new LocalizedComponentsLocateResult();
							Locate locate = new Locate(componentLocateScope, localizedComponentsLocateResult);
							locate.locate();
							wodFile = localizedComponentsLocateResult.getFirstWodFile();
						} catch (CoreException e) {
							LaunchingPlugin.getDefault().log(e);
						} catch (LocateException e) {
							LaunchingPlugin.getDefault().log(e);
						}
						if (wodFile != null) {
							final String errorMessage = line;
							this.selectAndReveal(wodFile, key, IEditorTarget.TARGET_WOD, errorMessage, "Buggy key in WOComponent");
						}
					}
				}
			}
			int startIndexClass = line.indexOf("This class");
			if (startIndexClass >= 0) {
				IJavaProject javaProject = this.getJavaProject();
				if (javaProject != null) {
					IFile[] javaFiles = null;
					try {
						javaFiles = LocatePlugin.getDefault().getJavaLocateResult(fileName, javaProject.getProject()).getDotJava();
					} catch (CoreException e) {
						LaunchingPlugin.getDefault().log(e);
					} catch (LocateException e) {
						LaunchingPlugin.getDefault().log(e);
					}
					// TODO: select java file in the ui
					if (javaFiles != null && javaFiles.length == 1) {
						final String errorMessage = line;
						this.selectAndReveal(javaFiles[0], errorMessage, "Buggy key");
					}
				}
			}
		}
		fileName = null;
		return 5;
	}
}