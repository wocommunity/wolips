/* ====================================================================
 *
 * The ObjectStyle Group Software License, Version 1.0
 *
 * Copyright (c) 2002 The ObjectStyle Group
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

package org.objectstyle.wolips.core.logging;

import java.util.Date;
import java.util.Hashtable;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.objectstyle.wolips.core.plugin.WOLipsPlugin;

/**
 * @author uli
 * @author mnolte
 */
public class WOLipsLog {

public static final String MSG_SEP = " ";
	public static final int TRACE = 0;
	public static final int DEBUG = 1;
	public static final int INFO = 2;
	public static final int WARN = 3;
	public static final int ERROR = 4;
	public static final int FATAL = 5;

	private static final String[] levelNames =
		{ "TRACE", "DEBUG", "INFO", "WARN", "ERROR", "FATAL" };
	private static final Integer[] levelValues =
		{
			new Integer(TRACE),
			new Integer(DEBUG),
			new Integer(INFO),
			new Integer(WARN),
			new Integer(ERROR),
			new Integer(FATAL)};

	private static Hashtable levelToStringDict;

	static {
		levelToStringDict = new Hashtable();
		for (int i = 0; i < levelValues.length; i++) {
			levelToStringDict.put(levelValues[i], levelNames[i]);
		}
	}

	private String name;
	private int level;

	public WOLipsLog(String name, int level) {
		this.name = name;
		this.level = level;
	}

	/**
	 * Prints an IStatus.
	 * @param status
	 */
	public static void log(IStatus status) {
		WOLipsPlugin.getDefault().getLog().log(status);
	}
	/**
	 * Prints a message.
	 * @param message
	 */
	public static void log(String message) {
		WOLipsLog.log(
			new Status(
				IStatus.ERROR,
		WOLipsPlugin.getPluginId(),
				IStatus.ERROR,
				message,
				null));
	}
	/**
	 * Prints a Throwable.
	 * @param e
	 */
	public static void log(Throwable e) {
		WOLipsLog.log(new Status(IStatus.ERROR, WOLipsPlugin.getPluginId(), IStatus.ERROR, "Internal Error", e)); //$NON-NLS-1$
	}
	/**
	 * If WOLips.debug is true this method prints a String to the log.
	 * @param aString
	 */
	public static void debug(String aString) {
		if (WOLipsPlugin.DEBUG)
			WOLipsLog.log(aString);
	}
	/**
	 * If WOLips.debug is true this method prints an Exception to the log.
	 * @param aThrowable
	 */
	public static void debug(Throwable aThrowable) {
		if (WOLipsPlugin.DEBUG);
		WOLipsLog.log(aThrowable);
	}

	///////////////////// Log implementation ///////////////////////////////

	/**
	 * @param message
	 * @param t
	 */
	public void debug(Object message, Throwable t) {
		if (isDebugEnabled()) {
			System.err.println(
				new Date()
					+ MSG_SEP
					+ DEBUG
					+ MSG_SEP
					+ name
					+ MSG_SEP
					+ message);
			t.printStackTrace(System.err);
		}
		WOLipsLog.log(new Status(IStatus.WARNING, WOLipsPlugin.getPluginId(), IStatus.WARNING, name + MSG_SEP + message, t)); //$NON-NLS-1$
	}

	/**
	 * @param message
	 */
	public void debug(Object message) {
		if (isDebugEnabled()) {
			System.err.println(
				new Date()
					+ MSG_SEP
					+ DEBUG
					+ MSG_SEP
					+ name
					+ MSG_SEP
					+ message);
		}
		WOLipsLog.log(new Status(IStatus.WARNING, WOLipsPlugin.getPluginId(), IStatus.WARNING, name + MSG_SEP + message, null)); //$NON-NLS-1$
	}

	/**
	 * @param message
	 * @param t
	 */
	public void error(Object message, Throwable t) {
		if (isDebugEnabled()) {
			System.err.println(
				new Date()
					+ MSG_SEP
					+ ERROR
					+ MSG_SEP
					+ name
					+ MSG_SEP
					+ message);
			t.printStackTrace(System.err);
		}
		WOLipsLog.log(new Status(IStatus.ERROR, WOLipsPlugin.getPluginId(), IStatus.ERROR, name + MSG_SEP + message, t)); //$NON-NLS-1$
	}

	/**
	 * @param message
	 */
	public void error(Object message) {
		if (isDebugEnabled()) {
			System.err.println(
				new Date()
					+ MSG_SEP
					+ ERROR
					+ MSG_SEP
					+ name
					+ MSG_SEP
					+ message);
		}
		WOLipsLog.log(new Status(IStatus.ERROR, WOLipsPlugin.getPluginId(), IStatus.ERROR, name + MSG_SEP + message, null)); //$NON-NLS-1$
	}

	/**
	 * @param message
	 * @param t
	*/
	public void fatal(Object message, Throwable t) {
		if (isDebugEnabled()) {
			System.err.println(
				new Date()
					+ MSG_SEP
					+ FATAL
					
					+ MSG_SEP
					+ name
					+ MSG_SEP
					+ message);
			t.printStackTrace(System.err);
		}
		WOLipsLog.log(new Status(IStatus.ERROR, WOLipsPlugin.getPluginId(), IStatus.ERROR, name + MSG_SEP + message, t)); //$NON-NLS-1$
	}

	/**
	 * @param message
	 */
	public void fatal(Object message) {
		if (isDebugEnabled()) {
			System.err.println(
				new Date()
					+ MSG_SEP
					+ FATAL
					+ MSG_SEP
					+ name
					+ MSG_SEP
					+ message);
		}
		WOLipsLog.log(new Status(IStatus.ERROR, WOLipsPlugin.getPluginId(), IStatus.ERROR, name + MSG_SEP + message, null)); //$NON-NLS-1$
	}

	/**
	 * @param message
	 * @param t
	 */
	public void info(Object message, Throwable t) {
		if (isDebugEnabled()) {
			System.err.println(
				new Date()
					+ MSG_SEP
					+ INFO
					+ MSG_SEP
					+ name
					+ MSG_SEP
					+ message);
			t.printStackTrace(System.err);
		}
		WOLipsLog.log(new Status(IStatus.WARNING, WOLipsPlugin.getPluginId(), IStatus.WARNING, name + MSG_SEP + message, t)); //$NON-NLS-1$
	}

	/**
	 * @param message
	 */
	public void info(Object message) {
		if (isDebugEnabled()) {
			System.err.println(
				new Date()
					+ MSG_SEP
					+ INFO
					+ MSG_SEP
					+ name
					+ MSG_SEP
					+ message);
		}
		WOLipsLog.log(new Status(IStatus.WARNING, WOLipsPlugin.getPluginId(), IStatus.WARNING, name + MSG_SEP + message, null)); //$NON-NLS-1$
	}

	/**
	 * @param message
	 * @param t
	  */
	public void trace(Object message, Throwable t) {
		if (isDebugEnabled()) {
			System.err.println(
				new Date()
					+ MSG_SEP
					+ TRACE
					+ MSG_SEP
					+ name
					+ MSG_SEP
					+ message);
			t.printStackTrace(System.err);
		}
		WOLipsLog.log(new Status(IStatus.WARNING, WOLipsPlugin.getPluginId(), IStatus.WARNING, name + MSG_SEP + message, t)); //$NON-NLS-1$
	}

	/**
	 * @param message
	 */
	public void trace(Object message) {
		if (isDebugEnabled()) {
			System.err.println(
				new Date()
					+ MSG_SEP
					+ TRACE
					+ MSG_SEP
					+ name
					+ MSG_SEP
					+ message);
		}
		WOLipsLog.log(new Status(IStatus.WARNING, WOLipsPlugin.getPluginId(), IStatus.WARNING, name + MSG_SEP + message, null)); //$NON-NLS-1$
	}

	/**
	 * @param message
	 * @param t
	 */
	public void warn(Object message, Throwable t) {
		if (isDebugEnabled()) {
			System.err.println(
				new Date()
					+ MSG_SEP
					+ WARN
					+ MSG_SEP
					+ name
					+ MSG_SEP
					+ message);
			t.printStackTrace(System.err);
		}
		WOLipsLog.log(new Status(IStatus.WARNING, WOLipsPlugin.getPluginId(), IStatus.WARNING, name + MSG_SEP + message, t)); //$NON-NLS-1$
	}

	/**
	 * @param message
	 */
	public void warn(Object message) {
		if (isDebugEnabled()) {
			System.err.println(
				new Date()
					+ MSG_SEP
					+ WARN
					+ MSG_SEP
					+ name
					+ MSG_SEP
					+ message);
		}
		WOLipsLog.log(new Status(IStatus.WARNING, WOLipsPlugin.getPluginId(), IStatus.WARNING, name + MSG_SEP + message, null)); //$NON-NLS-1$
	}

	/**
	 * @return
	 */
	public boolean isDebugEnabled() {
		return level <= DEBUG;
	}

	/**
	 * @return
	 */
	public boolean isErrorEnabled() {
		return level <= ERROR;
	}

	/**
	 * @return
	 */
	public boolean isFatalEnabled() {
		return level == FATAL;
	}

	/**
	 * @return
	 */
	public boolean isInfoEnabled() {
		return level <= INFO;
	}

	/**
	 * @return
	 */
	public boolean isTraceEnabled() {
		return level <= TRACE;
	}

	/**
	 * @return
	 */
	public boolean isWarnEnabled() {
		return level <= WARN;
	}

	/**
	 * Returns the level.
	 * @return int
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * Returns the name.
	 * @return String
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the level.
	 * @param level The level to set
	 */
	public void setLevel(int level) {
		if (level >= TRACE && level <= FATAL)
			this.level = level;
	}

	/**
	 * Sets the name.
	 * @param name The name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

}
