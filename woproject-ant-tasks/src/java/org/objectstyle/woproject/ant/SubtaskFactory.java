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

package org.objectstyle.woproject.ant;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Chmod;
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.taskdefs.Jar;
import org.apache.tools.ant.taskdefs.Mkdir;

/**
 * Factory of standard ant tasks that are used as subtasks in other parts of
 * WOProject. This class customizes certain things of returned tasks, like
 * logging levels, etc.
 *
 * @author Andrei Adamchik
 */
public class SubtaskFactory {
	protected Task parent;

	public SubtaskFactory(Task parent) {
		this.parent = parent;
	}

	public void release() {
		parent = null;
	}

	/**
	 * Modifies log level to avoid log flood when using subtasks within a main
	 * task.
	 */
	public static int subtaskLogLevel(int level) {
		if (level == Project.MSG_INFO) {
			return Project.MSG_VERBOSE;
		}
		return level;
	}

	protected void initChildTask(Task t) {
		t.setOwningTarget(parent.getOwningTarget());
		t.setProject(parent.getProject());
		t.setTaskName(parent.getTaskName());
		t.setLocation(parent.getLocation());
	}

	/**
	 * Returns Copy task configured to copy WebObjects resources.
	 */
	public Copy getResourceCopy() {
		ResourcesCopy task = new ResourcesCopy();
		initChildTask(task);
		return task;
	}

	/**
	 * Returns a new Chmod task.
	 */
	public Chmod getChmod() {
		Chmod task = new Chmod();
		initChildTask(task);
		return task;
	}

	/**
	 * Returns Jar task.
	 */
	public Jar getJar() {
		WOJar task = new WOJar();
		initChildTask(task);
		return task;
	}

	/**
	 * Returns Mkdir task.
	 */
	public Mkdir getMkdir() {
		WOMkdir task = new WOMkdir();
		initChildTask(task);
		return task;
	}

	class ResourcesCopy extends Copy {

		public ResourcesCopy() {
			super.mapperElement = new WOMapper(super.getProject(), parent);
		}

		@Override
		public void log(String msg) {
			super.log(msg, subtaskLogLevel(Project.MSG_INFO));
		}

		@Override
		public void log(String msg, int msgLevel) {
			super.log(msg, subtaskLogLevel(msgLevel));
		}
	}

	class WOJar extends Jar {
		@Override
		public void log(String msg) {
			super.log(msg, subtaskLogLevel(Project.MSG_INFO));
		}

		@Override
		public void log(String msg, int msgLevel) {
			super.log(msg, subtaskLogLevel(msgLevel));
		}
	}

	class WOMkdir extends Mkdir {
		@Override
		public void log(String msg) {
			super.log(msg, subtaskLogLevel(Project.MSG_INFO));
		}

		@Override
		public void log(String msg, int msgLevel) {
			super.log(msg, subtaskLogLevel(msgLevel));
		}
	}
}