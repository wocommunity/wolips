/* ====================================================================
 *
 * The ObjectStyle Group Software License, Version 1.0
 *
 * Copyright (c) 2002 The ObjectStyle Group,
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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Vector;

/**
 * Copyright (c) 2002 The Apache Group, A set of helper methods related to
 * string manipulation.
 *
 * @author <a href="mailto:sbailliez@apache.org">Stephane Bailliez</a>
 */
public final class AntStringUtils {

	/**
	 * Splits up a string into a list of lines. It is equivalent to
	 * <tt>split(data, '\n')</tt>.
	 *
	 * @param data
	 *            the string to split up into lines.
	 * @return the list of lines available in the string.
	 */
	public static Vector<String> lineSplit(String data) {
		return split(data, '\n');
	}

	/**
	 * Splits up a string where elements are separated by a specific character
	 * and return all elements.
	 *
	 * @param data
	 *            the string to split up.
	 * @param ch
	 *            the separator character.
	 * @return the list of elements.
	 */
	public static Vector<String> split(String data, int ch) {
		Vector<String> elems = new Vector<String>();
		int pos = -1;
		int i = 0;
		while ((pos = data.indexOf(ch, i)) != -1) {
			String elem = data.substring(i, pos);
			elems.addElement(elem);
			i = pos + 1;
		}
		elems.addElement(data.substring(i));
		return elems;
	}

	/**
	 * Replace occurrences into a string.
	 *
	 * @param data
	 *            the string to replace occurrences into
	 * @param from
	 *            the occurrence to replace.
	 * @param to
	 *            the occurrence to be used as a replacement.
	 * @return the new string with replaced occurrences.
	 */
	public static String replace(String data, String from, String to) {
		StringBuffer buf = new StringBuffer(data.length());
		int pos = -1;
		int i = 0;
		while ((pos = data.indexOf(from, i)) != -1) {
			buf.append(data.substring(i, pos)).append(to);
			i = pos + from.length();
		}
		buf.append(data.substring(i));
		return buf.toString();
	}

	/**
	 * Convenient method to retrieve the full stacktrace from a given exception.
	 *
	 * @param t
	 *            the exception to get the stacktrace from.
	 * @return the stacktrace from the given exception.
	 */
	public static String getStackTrace(Throwable t) {
		StringWriter sw = null;
		PrintWriter pw = null;
		try {
			sw = new StringWriter();
			pw = new PrintWriter(sw, true);
			t.printStackTrace(pw);
			pw.flush();
			pw.close();
			return sw.toString();
		} finally {
			sw = null;
			pw = null;
		}
	}

}
