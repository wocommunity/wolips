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

package org.objectstyle.wolips.launching.errors;

import org.eclipse.debug.ui.console.IConsole;
import org.eclipse.debug.ui.console.IConsoleLineTracker;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.objectstyle.wolips.launching.LaunchingPlugin;
import org.objectstyle.wolips.launching.exceptionhandler.IExceptionHandler;
import org.objectstyle.wolips.launching.exceptionhandler.internal.ExceptionHandlerWrapper;
import org.objectstyle.wolips.preferences.Preferences;

public class ErrorConsoleLineTracker implements IConsoleLineTracker {

	private IConsole currentConsole;

	private ExceptionHandlerWrapper[] exceptionHandlerWrappers;

	private int[] linesToSkip;
	
	private boolean enabled;

	public ErrorConsoleLineTracker() {
		super();
	}

	public void init(IConsole console) {
		this.currentConsole = console;
		this.exceptionHandlerWrappers = LaunchingPlugin.getDefault().getExceptionHandlerWrapper();
		this.linesToSkip = new int[this.exceptionHandlerWrappers.length];
		this.enabled = Preferences.isShowConsoleExceptionDialogs();
	}

	public void lineAppended(IRegion line) {
		int offset = line.getOffset();
		int length = line.getLength();
		String text = null;
		try {
			text = currentConsole.getDocument().get(offset, length);
			incrementLinesToSkip();
			forwardLineAppended(text);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}

	}

	private void incrementLinesToSkip() {
		for (int i = 0; i < linesToSkip.length; i++) {
			int currentLinesToSkip = linesToSkip[i];
			if (currentLinesToSkip > 0) {
				currentLinesToSkip--;
				linesToSkip[i] = currentLinesToSkip;
			}
		}
	}

	private void forwardLineAppended(String line) {
		if (this.enabled) {
			for (int i = 0; i < exceptionHandlerWrappers.length; i++) {
				ExceptionHandlerWrapper exceptionHandlerWrapper = exceptionHandlerWrappers[i];
				IExceptionHandler exceptionHandler = exceptionHandlerWrapper.getExceptionHandler();
				if (linesToSkip[i] <= 0) {
					linesToSkip[i] = exceptionHandler.lineAppendedToConsole(line, currentConsole);
				}
			}
		}
	}

	public void dispose() {
		this.currentConsole = null;
	}
}
