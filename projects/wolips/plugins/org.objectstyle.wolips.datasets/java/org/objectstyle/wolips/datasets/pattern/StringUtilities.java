/*
 * Created on 28.07.2003
 * StringUtilities -- general utility functions for Strings
 */

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

package org.objectstyle.wolips.datasets.pattern;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author Harald Niesche
 *
 */
public class StringUtilities {

	/**
	 * replace every occurence of oldPart with newPart in origin
	 * returns changed origin (since String is immutable...)
	 * @param origin
	 * @param oldPart
	 * @param newPart
	 * @return
	 */

	static public String replace(
		String origin,
		String oldPart,
		String newPart) {
		if ((origin == null) || (origin.length() == 0)) {
			return origin;
		}

		StringBuffer buffer = new StringBuffer(origin);

		//start replacing from the end so we can use indexOf on the original string

		int index;
		int end = origin.length();
		int oldLength = oldPart.length();

		while (end >= 0) {
			index = origin.lastIndexOf(oldPart, end);
			// no more occurences of oldPart
			if (index == -1)
				break;

			end = index - oldLength;

			buffer.replace(index, index + oldLength, newPart);
		}
		return buffer.toString();
	}

	/**
	 * @param string
	 * @param sep
	 * @return
	 */
	public static String[] smartSplit(String string, char sep) {
		QuotedStringTokenizer tok = new QuotedStringTokenizer(string, sep);
		List tmp = new ArrayList();
		while (tok.hasNext()) {
			tmp.add(tok.nextToken());
		}

		return ((String[]) tmp.toArray(new String[tmp.size()]));
	}

	/**
	 * Method arrayListFromCSV.
	 * @param csvString
	 * @return ArrayList
	 */
	public static synchronized ArrayList arrayListFromCSV(String csvString) {
		if (csvString == null || csvString.length() == 0) {
			return new ArrayList();
		}
		StringTokenizer valueTokenizer = new StringTokenizer(csvString, ",");
		ArrayList resultList = new ArrayList(valueTokenizer.countTokens());
		while (valueTokenizer.hasMoreElements()) {
			resultList.add(valueTokenizer.nextElement());
		}
		return resultList;
	}

}
