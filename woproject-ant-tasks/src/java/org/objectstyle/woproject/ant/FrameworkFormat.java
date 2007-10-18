/* ====================================================================
 *
 * The ObjectStyle Group Software License, Version 1.0
 *
 * Copyright (c) 2002 - 2005 The ObjectStyle Group
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

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.FilterSet;
import org.apache.tools.ant.types.FilterSetCollection;

/**
 * Subclass of ProjectFormat that defines file copying strategy for
 * WOFrameworks.
 *
 * @author Andrei Adamchik
 */
public class FrameworkFormat extends ProjectFormat {
	public final String INFO_TEMPLATE = "woframework/Info.plist";

	/**
	 * Creates new FrameworkFormat and initializes it with the name of the
	 * project being built.
	 */
	public FrameworkFormat(WOTask task) {
		super(task);
	}

	private WOFramework getFrameworkTask() {
		return (WOFramework) task;
	}

	@Override
	public Iterator fileIterator() {
		String infoFile = new File(task.resourcesDir(), "Info.plist").getPath();
		return stringIterator(infoFile);
	}

	@Override
	public String templateForTarget(String targetName) throws BuildException {
		if (targetName.endsWith("Info.plist")) {
			return INFO_TEMPLATE;
		}
		throw new BuildException("Invalid target: " + targetName);
	}

	@Override
	public FilterSetCollection filtersForTarget(String targetName) throws BuildException {

		if (targetName.endsWith("Info.plist")) {
			return infoFilter(getFrameworkTask().getLibNames());
		}
		throw new BuildException("Invalid target: " + targetName);
	}

	/**
	 * Returns a FilterSet that can be used to build Info.plist file.
	 */
	@Override
	public FilterSetCollection infoFilter(Iterator extLibs) {
		FilterSetCollection filterSetCollection = super.infoFilter(extLibs);
		FilterSet filter = new FilterSet();

		filter.addFilter("EOAdaptorClassName", getFrameworkTask().getEOAdaptorClassName());
		filterSetCollection.addFilterSet(filter);

		return filterSetCollection;
	}

	/**
	 * Returns an iterator with a single String element.
	 */
	private Iterator<String> stringIterator(String str) {
		ArrayList<String> list = new ArrayList<String>(1);
		list.add(str);
		return list.iterator();
	}
}