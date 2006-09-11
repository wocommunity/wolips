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

import java.util.Iterator;

/**
 * @author Harald Niesche
 * @version 1.0 QuotedStringTokenizer -- tokenize string ignoring quoted
 *          separators
 * @deprecated Use org.objectstyle.wolips.core.* instead.
 */
public class QuotedStringTokenizer implements Iterator {

	/**
	 * construct a string tokenizer for String <i>string </i>, using ' ' (blank)
	 * as separator
	 * 
	 * @param string
	 */
	public QuotedStringTokenizer(String string) {
		this._s = string;
		this._sep = ' ';
		this._nextStart = 0;
	}

	/**
	 * construct a string tokenizer for String <i>string </i>, using sep as the
	 * separator
	 * 
	 * @param string
	 * @param sep
	 */
	public QuotedStringTokenizer(String string, int sep) {
		this._s = string;
		this._sep = sep;
		this._nextStart = 0;
	}

	/**
	 * checks whether more tokens are available
	 */

	public boolean hasNext() {
		return (this._s.length() >= this._nextStart);
	}

	/**
	 * @return
	 */
	public boolean hasMoreTokens() {
		return (hasNext());
	}

	/**
	 * Iterator interface: obtains the next token as Object
	 */
	public Object next() {
		return nextToken(this._sep);
	}

	/**
	 * Iterator interface: remove current token (skips it, actually)
	 */
	public void remove() {
		nextToken(this._sep);
	}

	/**
	 * obtain current token as String
	 * 
	 * @return
	 */
	public String nextToken() {
		return (nextToken(this._sep));
	}

	/**
	 * obtain current token as String, using sep as separator
	 * 
	 * @param sep
	 * @return
	 */
	public String nextToken(int sep) {
		if (this._s.length() < this._nextStart) {
			return (null);
		}

		if (this._s.length() == this._nextStart) {
			++this._nextStart;
			return ("");
		}

		StringBuffer buffer = new StringBuffer();

		int end;
		int tmp;
		int restStart = this._nextStart;
		if ('"' == this._s.charAt(this._nextStart)) {
			++this._nextStart;
			restStart = this._nextStart;
			tmp = this._s.indexOf('"', this._nextStart);

			while ((tmp != -1) && (tmp + 1 < this._s.length()) && (this._s.charAt(tmp + 1) == '"')) {
				buffer.append(this._s.substring(restStart, tmp + 1));
				restStart = tmp + 2;
				tmp = this._s.indexOf('"', tmp + 2);
			}

			if (tmp == -1) {
				tmp = this._s.length();
			}
		} else {
			tmp = this._nextStart;
		}
		int end1 = this._s.indexOf(sep, tmp);
		int end2 = this._s.length();
		if (-1 == end1) {
			end1 = this._s.length();
		}

		if (end1 < end2) {
			end = end1;
		} else {
			end = end2;
		}

		if (restStart < end) {
			if (this._s.charAt(end - 1) == '"') {
				buffer.append(this._s.substring(restStart, end - 1));
			} else {
				buffer.append(this._s.substring(restStart, end));
			}
		}

		this._nextStart = end + 1; // skip the separator

		String result = buffer.toString();
		if (result == null) {
			result = "";
		}

		return (result);
	}

	private String _s;

	private int _sep;

	private int _nextStart;
}