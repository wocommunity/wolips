/*
 * ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0
 * 
 * Copyright (c) 2006 The ObjectStyle Group and individual authors of the
 * software. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * 3. The end-user documentation included with the redistribution, if any, must
 * include the following acknowlegement: "This product includes software
 * developed by the ObjectStyle Group (http://objectstyle.org/)." Alternately,
 * this acknowlegement may appear in the software itself, if and wherever such
 * third-party acknowlegements normally appear.
 * 
 * 4. The names "ObjectStyle Group" and "Cayenne" must not be used to endorse or
 * promote products derived from this software without prior written permission.
 * For written permission, please contact andrus@objectstyle.org.
 * 
 * 5. Products derived from this software may not be called "ObjectStyle" nor
 * may "ObjectStyle" appear in their names without prior written permission of
 * the ObjectStyle Group.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * OBJECTSTYLE GROUP OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 * 
 * This software consists of voluntary contributions made by many individuals on
 * behalf of the ObjectStyle Group. For more information on the ObjectStyle
 * Group, please see <http://objectstyle.org/>.
 *  
 */
package org.objectstyle.wolips.eogenerator.core.model;

import java.text.CharacterIterator;
import java.text.ParseException;
import java.text.StringCharacterIterator;
import java.util.Enumeration;
import java.util.NoSuchElementException;

public class CommandLineTokenizer implements Enumeration {
	private StringCharacterIterator _iterator;

	private int _state;

	private boolean _wasQuoted;

	public CommandLineTokenizer(String line) {
		_iterator = new StringCharacterIterator(line);
		reset();
	}

	public void reset() {
		_state = 1;
		_iterator.first();
	}

	public boolean hasMoreElements() {
		return hasMoreTokens();
	}

	public boolean hasMoreTokens() {
		return (_iterator.current() != CharacterIterator.DONE);
	}

	public Object nextElement() {
		String token;
		try {
			token = nextToken();
		} catch (ParseException e) {
			e.printStackTrace();
			token = null;
		}
		return token;
	}

	public String nextToken() throws ParseException {
		boolean escapeNext = false;
		boolean wasQuoted = _wasQuoted;
		// 1 = Whitespace, 2 = Text, 3 = Quoted;
		StringBuffer token = new StringBuffer();
		char c = _iterator.current();
		boolean done = false;
		while (!done && c != CharacterIterator.DONE) {
			if (escapeNext) {
				switch (c) {
				case '\n':
					throw new ParseException("Unexception escape '\\' at end of string.", _iterator.getIndex());

				default:
					token.append(c);
					c = _iterator.next();
					break;
				}
				escapeNext = false;
			} else {
				switch (_state) {
				case 1:
					switch (c) {
					case '\n':
					case ' ':
					case '\t':
						c = _iterator.next();
						break;

					case '\"':
						_state = 3;
						c = _iterator.next();
						if (token.length() > 0 || _wasQuoted) {
							done = true;
							_wasQuoted = false;
						}
						_wasQuoted = true;
						break;

					case '\\':
						escapeNext = true;
						c = _iterator.next();
						break;

					default:
						_state = 2;
						if (token.length() > 0 || _wasQuoted) {
							done = true;
							_wasQuoted = false;
						}
						break;
					}
					break;

				case 2:
					switch (c) {
					case ' ':
					case '\t':
					case '\n':
						_state = 1;
						break;

					// case '\"':
					// throw new ParseException("Unexpected quote '\"' in
					// string.",
					// myIterator.getIndex());

					case '\\':
						escapeNext = true;
						c = _iterator.next();
						break;

					default:
						token.append(c);
						c = _iterator.next();
						break;
					}
					break;

				case 3:
					switch (c) {
					case '\"':
						_state = 1;
						c = _iterator.next();
						break;

					case '\\':
						escapeNext = true;
						c = _iterator.next();
						break;

					default:
						token.append(c);
						c = _iterator.next();
						break;
					}
					break;
				}
			}
		}

		if (token.length() <= 0 && !wasQuoted) {
			throw new NoSuchElementException("There are no more tokens on this line.");
		}

		return token.toString();
	}
}
