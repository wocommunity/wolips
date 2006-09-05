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

package org.objectstyle.woenvironment.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author uli
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 */
public class FileStringScanner {

	/**
	 * @see java.lang.Object#Object()
	 */
	public FileStringScanner() {
		super();
	}

	/**
	 * Method fileOpenReplaceWith.
	 * 
	 * @param file
	 * @param replace
	 * @param with
	 * @throws IOException
	 */
	public static void fileOpenReplaceWith(String file, String replace, String with) throws IOException {
		String stringFromFile = null;
		String replacedString = null;
		try {
			stringFromFile = FileStringScanner.stringFromFile(new File(file));
			replacedString = FileStringScanner.replace(stringFromFile, replace, with);
			// if nothing replaced
			if (replacedString != null)
				FileStringScanner.stringToFile(new File(file), replacedString);
		} finally {
			stringFromFile = null;
			replacedString = null;
		}
	}

	/**
	 * Method stringFromFile.
	 * 
	 * @param aFile
	 * @return String
	 * @throws IOException
	 */
	public static String stringFromFile(File aFile) throws IOException {
		FileInputStream fis = null;
		byte[] data = null;
		try {
			int size = (int) aFile.length();
			fis = new FileInputStream(aFile);
			data = new byte[size];
			int bytesRead = 0;
			while (bytesRead < size) {
				bytesRead += fis.read(data, bytesRead, size - bytesRead);
			}
			fis.close();
		} finally {
			fis = null;
			// data = null;
		}
		return new String(data);
	}

	/**
	 * Method stringToFile.
	 * 
	 * @param aFile
	 * @param aString
	 * @throws IOException
	 */
	public static void stringToFile(File aFile, String aString) throws IOException {
		int length = aString.length();
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(aFile);
			fos.write(aString.getBytes(), 0, length);
			fos.close();
		} finally {
			fos = null;
		}
	}

	/**
	 * Method replace.
	 * 
	 * @param text
	 * @param replace
	 * @param with
	 * @return String
	 */
	public static String replace(String text, String replace, String with) {
		if (text == null)
			return null;
		int li = 0;
		int l = replace.length();
		int i = text.indexOf(replace, li);
		if (i < 0)
			return text;
		StringBuffer aWorkString = new StringBuffer(text.length() + 1);
		while (i >= 0) {
			if (i > li)
				aWorkString.append(text.substring(li, i));
			aWorkString.append(with);
			li = i + l;
			i = text.indexOf(replace, li);
		}
		aWorkString.append(text.substring(li));
		return aWorkString.toString();
	}
}
